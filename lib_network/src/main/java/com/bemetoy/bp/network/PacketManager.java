package com.bemetoy.bp.network;


import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.EncryptUtil;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.sdk.utils.ZlibUtil;
import com.google.protobuf.InvalidProtocolBufferException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Tom on 2016/3/14.
 */
public class PacketManager {

    private static final String TAG = "Network.PacketManager";

    /**
     * Package the request,it will append the fixed header length;
     *
     * @param request
     * @return
     */
    public static byte[] pack(final IRequest request) {
        if (Util.isNull(request)) {
            Log.e(TAG, "request is null");
            return new byte[0];
        }

        // pack
        byte[] body = null;
        try {
            body = request.getRequestBody();
        } catch (Exception e) {
            Log.e(TAG, "%s", e.toString());
            return new byte[0];
        }

        if (Util.isNullOrNil(body)) {
            Log.e(TAG, "body is null or nil");
            return new byte[0];
        }

        // 压缩
        int compressType = Racecar.CompressAlg.BM_NO_COMPRESS_VALUE;
        byte[] bodyCompressed = ZlibUtil.compress(body);
        if (Util.isNullOrNil(bodyCompressed)) {
            Log.e(TAG, "compress failed");
            bodyCompressed = body;
        } else {
            compressType = Racecar.CompressAlg.BM_ZLIB_COMPRESS_VALUE;
        }

        // 加密
        int cryptType = ProtocolConstants.CryptAlg.NO_ENCRYPT;
        byte[] bodyEncrypt = new byte[0];
        switch (request.getCmdId()) {
            case Racecar.CmdId.REGISTER_VALUE:
            case Racecar.CmdId.CHECK_USER_VALUE:
            case Racecar.CmdId.CHECK_CAR_ID_VALUE:
            case Racecar.CmdId.RESET_PASSWD_VALUE:
            case Racecar.CmdId.GET_PHONE_VERIFY_VALUE:
            case Racecar.CmdId.GET_IMAGE_VERIFY_VALUE:
            case Racecar.CmdId.AOFEI_LOGIN_VALUE:
            case Racecar.CmdId.AOFEI_REGISTER_VALUE:
            case Racecar.CmdId.CHECK_VERSION_VALUE:
            case Racecar.CmdId.GET_ADDR_DATA_VALUE:
                cryptType = ProtocolConstants.CryptAlg.RSA_ENCRYPT_WITH_PUBLICKEY;
                bodyEncrypt = EncryptUtil.rsaPublicEncrypt(bodyCompressed);
                break;
            default:
                byte[] sessionKey = request.getSessionKey();//TODO set session key;
                if (Util.isNullOrNil(sessionKey)) {
                    Log.i(TAG, "session key is null");
                    cryptType = ProtocolConstants.CryptAlg.NO_ENCRYPT;
                    bodyEncrypt = bodyCompressed;
                } else {
                    cryptType = ProtocolConstants.CryptAlg.AES_ENCRYPT_WITH_PRIVATEKEY;
                    bodyEncrypt = EncryptUtil.aesCbcEncrypt(sessionKey, bodyCompressed);
                }

                break;
        }

        // 添加可变包头
        Racecar.CommonHeader.Builder build = Racecar.CommonHeader.newBuilder();
        build.setCompressAlogrithm(compressType);
        build.setCryptoAlogrithm(cryptType);
        build.setErrorCode(0);
        build.setLengthBeforeCompress(body.length);
        build.setRSAPublicKeyVersion(ProtocolConstants.RSA_PUBLIC_KEY_VERSION);
        // TODO:set UIN;
        build.setUserId(PluginStubBus.getInt(PluginConstants.Plugin.PLUGIN_NAME_APP, PluginConstants.App.DataKey.GET_USER_ID, 0));
        byte[] commonHeader = build.build().toByteArray();
        if (Util.isNullOrNil(commonHeader)) {
            Log.e(TAG, "commonHeader is null or nil");
            return new byte[0];
        }

        byte[] variableHeaderLength = Util.shortToByteArray((short) commonHeader.length);

        byte[] ret = new byte[bodyEncrypt.length + commonHeader.length + ProtocolConstants.DataTypeLengthInByte.TYPE_SHORT];
        System.arraycopy(variableHeaderLength, 0, ret, 0, variableHeaderLength.length);
        System.arraycopy(commonHeader, 0, ret, variableHeaderLength.length, commonHeader.length);
        System.arraycopy(bodyEncrypt, 0, ret, variableHeaderLength.length + commonHeader.length, bodyEncrypt.length);


        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(bos);
        try {
            out.writeInt((int) request.getCmdId());
            out.write(ret);
            out.close();
            byte[] result = bos.toByteArray();

            return result;

        } catch (IOException e) {
            Log.e(TAG, "Add fixed header fail, reason:%s");
        }

        return null;
    }


    /**
     * The method will return response from the inputStream.
     *
     * @param data
     * @return
     */
    public static Response upPack(byte[] data, IRequest request) {

        final Response response = new Response();

        try {
            byte[] cmdIDBytes = new byte[ProtocolConstants.DataTypeLengthInByte.TYPE_INT];
            System.arraycopy(data, 0, cmdIDBytes, 0, ProtocolConstants.DataTypeLengthInByte.TYPE_INT);
            final int cmdID = Util.byteArray2Int(cmdIDBytes, -1);

            Log.e(TAG, "cmdID = %d", cmdID);

            // read all
            final int toRead = data.length - ProtocolConstants.DataTypeLengthInByte.TYPE_INT;
            Log.d(TAG, "body len = %d ", toRead);
            byte[] body = new byte[toRead];
            if (toRead > 0) {
                System.arraycopy(data, ProtocolConstants.DataTypeLengthInByte.TYPE_INT, body, 0, toRead);
            }

            byte[] variableLengthByteArray = new byte[ProtocolConstants.DataTypeLengthInByte.TYPE_SHORT];
            System.arraycopy(body, 0, variableLengthByteArray, 0, variableLengthByteArray.length);
            short variableLength = Util.byteArray2Short(variableLengthByteArray, (short) -1);
            if (-1 == variableLength) {
                Log.e(TAG, "byteArray2Short failed!!!");
                return response;
            }

            Log.d(TAG, "variable length = %d", variableLength);

            if (variableLength + ProtocolConstants.DataTypeLengthInByte.TYPE_SHORT > body.length) {
                Log.e(TAG, "variableLength + DataTypeLengthInByte.TYPE_SHORT (%d) > aData.length (%d)", variableLength + ProtocolConstants.DataTypeLengthInByte.TYPE_SHORT, body.length);
                return response;
            }

            byte[] commonHeaderByteArray = new byte[variableLength];
            System.arraycopy(body, ProtocolConstants.DataTypeLengthInByte.TYPE_SHORT, commonHeaderByteArray, 0, commonHeaderByteArray.length);

            Racecar.CommonHeader commonHeader = null;
            try {
                commonHeader = Racecar.CommonHeader.parseFrom(commonHeaderByteArray);
            } catch (InvalidProtocolBufferException e) {
                Log.e(TAG, "%s", e.toString());
                return response;
            }

            response.setCommonHeader(commonHeader);

            Log.i(TAG, "CommonHeader parse successful, error code = %d, cryptoAlogrithm = %d, rSAPublicKeyVersion = %d, compressAlogrithm = %d, userId = %d, lengthBeforeCompress = %d", commonHeader.getErrorCode(),
                    commonHeader.getCryptoAlogrithm(), commonHeader.getRSAPublicKeyVersion(), commonHeader.getCompressAlogrithm(), commonHeader.getUserId(), commonHeader.getLengthBeforeCompress());

            if (variableLength + ProtocolConstants.DataTypeLengthInByte.TYPE_SHORT == body.length) {
                Log.e(TAG, "resp body is null");
                return response;
            }

            byte[] realBody = new byte[body.length - ProtocolConstants.DataTypeLengthInByte.TYPE_SHORT - variableLength];
            System.arraycopy(body, ProtocolConstants.DataTypeLengthInByte.TYPE_SHORT + variableLength, realBody, 0, realBody.length);

            Log.d(TAG, "receviced data length = %d, commondHead length = %d, real body length = %d", body.length, variableLength, realBody.length);

            // 解密  TODO set key
            byte[] realBodyDecode = realBody;
            switch (commonHeader.getCryptoAlogrithm()) {
                case Racecar.CryptAlg.NO_ENCRYPT_VALUE:
                    realBodyDecode = realBody;
                    break;
                case Racecar.CryptAlg.AES_ENCRYPT_WITH_PRIVATEKEY_VALUE: {
                    byte[] key = null;
                    switch (request.getCmdId()) {
                        case Racecar.CmdId.REGISTER_VALUE:
                        case Racecar.CmdId.CHECK_USER_VALUE:
                        case Racecar.CmdId.CHECK_CAR_ID_VALUE:
                        case Racecar.CmdId.RESET_PASSWD_VALUE:
                        case Racecar.CmdId.GET_PHONE_VERIFY_VALUE:
                        case Racecar.CmdId.GET_IMAGE_VERIFY_VALUE:
                        case Racecar.CmdId.AOFEI_LOGIN_VALUE:
                        case Racecar.CmdId.AOFEI_REGISTER_VALUE:
                            key = request.getAesKey();
                            break;
                        default:
                            key = request.getSessionKey();
                            break;
                    }

                    if (Util.isNullOrNil(key)) {
                        Log.e(TAG, "invalid key");
                        return response;
                    }

                    realBodyDecode = EncryptUtil.aesCbcDecrypt(key, realBody);
                    break;

                }
                default:
                    Log.e(TAG, "unknow encrypto method, %d", commonHeader.getCryptoAlogrithm());
                    return response;
            }

            if(Util.isNullOrNil(realBodyDecode)) {
                Log.e(TAG, "decrypt the response failed");
                return response;
            }


            // 解压缩
            byte[] realBodyUncompress = realBodyDecode;
            switch (commonHeader.getCompressAlogrithm()) {
                case Racecar.CompressAlg.BM_ZLIB_COMPRESS_VALUE:
                    realBodyUncompress = ZlibUtil.decompress(realBodyDecode);
                    if (Util.isNullOrNil(realBodyUncompress)) {
                        Log.e(TAG, "uncompress failed!!!");
                        return response;
                    }

                    break;
                case Racecar.CompressAlg.BM_NO_COMPRESS_VALUE:
                    realBodyUncompress = realBodyDecode;
                    break;
                default:
                    Log.e(TAG, "unknow compress method, %d", commonHeader.getCompressAlogrithm());
                    return response;
            }
            response.setRealBody(realBodyUncompress);
        } catch (Exception e) {
            Log.e(TAG,"unpack the response failed:%s",e.getMessage());
        }

        return response;
    }

}
