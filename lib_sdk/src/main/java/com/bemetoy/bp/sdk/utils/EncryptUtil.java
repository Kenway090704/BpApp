package com.bemetoy.bp.sdk.utils;

/**
 * Created by Tom on 2016/3/14.
 */

@SuppressWarnings("JniMissingFunction")
public class EncryptUtil {

    static {
        System.loadLibrary("stlport_shared");
        System.loadLibrary("bemetoy");
    }
    public static native byte[] aesCbcEncrypt(byte[] key, byte[] content);
    public static native byte[] aesCbcDecrypt(byte[] key, byte[] content);
    public static native byte[] rsaPublicEncrypt(byte[] content);

}
