package com.bemetoy.stub.util;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.autogen.table.MyMessage;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.stub.account.AccountLogic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tom on 2016/4/26.
 */
public class JsonManager {

    private static final String TAG = "Stub.JsonManager";

    public static final String MESSAGE_TYPE = "systemMessageType";
    public static final int ID_VALUE = 0;



    /**
     * Create add friend json message
     * @param fromId myId
     * @param fromName my nick name
     * @param to the friend
     * @param content the validate message.
     * @return
     */
    public static String createAddFriendJson(long fromId, String fromName, long to, String content) {
        if ((0 > fromId) || (0 > to)) {
            Log.e(TAG, "user id is invalid, fromUserId=" + fromId + ", toUserId=" + to);
            return null;
        }

        JSONObject obj = new JSONObject();
        try {
            obj.put(ProtocolConstants.JsonFiled.ID, ID_VALUE);
            obj.put(ProtocolConstants.JsonFiled.METHOD, ProtocolConstants.JsonFiled.METHOD_ADD_FRIEND);
            JSONObject paramsObj = new JSONObject();
            paramsObj.put(ProtocolConstants.JsonFiled.FROM_NAME, fromName);
            paramsObj.put(ProtocolConstants.JsonFiled.FROM, fromId);
            paramsObj.put(ProtocolConstants.JsonFiled.TO, to);
            paramsObj.put(ProtocolConstants.JsonFiled.CONTENT, content);
            paramsObj.put(ProtocolConstants.JsonFiled.TIME_STAMP, Util.getDateFormat("yyyy-MM-dd hh:mm:ss", System.currentTimeMillis()));
            obj.put(ProtocolConstants.JsonFiled.PARAMS, paramsObj);
        } catch (JSONException e) {
            Log.e(TAG, "create response string error: %s", e.getMessage());
        }

        return obj.toString();
    }

    /**
     * Create a response to the add friend request.
     * @param fromId
     * @param toId
     * @param agree
     * @return
     */
    public static String createResponseToAddRequest(long fromId, long toId, boolean agree) {
        if ((0 > fromId) || (0 > toId)) {
            Log.e(TAG, "user id is invalid, fromUserId=" + fromId + ", toUserId=" + toId);
            return null;
        }

        JSONObject obj = new JSONObject();
        try {
            obj.put(ProtocolConstants.JsonFiled.ID, ID_VALUE);
            obj.put(ProtocolConstants.JsonFiled.METHOD, ProtocolConstants.JsonFiled.METHOD_ADD_FRIEND_RESPONSE);
            JSONObject paramsObj = new JSONObject();
            paramsObj.put(ProtocolConstants.JsonFiled.FROM, fromId);
            paramsObj.put(ProtocolConstants.JsonFiled.TO, toId);
            paramsObj.put(ProtocolConstants.JsonFiled.TIME_STAMP, Util.getDateFormat("yyyy-MM-dd hh:mm:ss", System.currentTimeMillis()));
            paramsObj.put(ProtocolConstants.JsonFiled.AGREE, agree);
            obj.put(ProtocolConstants.JsonFiled.PARAMS, paramsObj);
        } catch (JSONException e) {
            Log.e(TAG, "create response string error: %s", e.getMessage());
        }

        return obj.toString();
    }


    /**
     * Convert the account info to json string
     *
     * {
        "nickname" : "阿里麻麻",
        "levelTitle" : "飚速三星",
        "headID" : 1,
        "exp" : 1051,
        "maxExp" : 2000,
        "activatedCars" : [{
            "carDBID" : 1,
            "carID" : "668101",
            "carName" : "天王巨星",
            "amount" : 1
            "engineID" : -1,
            "frontID" : -1,
            "batteryID" : -1,
            "backID" : -1,
            "shellID" : -1
            },
            {
            "carDBID" : 3,
            "carID" : "668103",
            "carName" : "天王老子",
            "amount" : 1
            "engineID" : -1,
            "frontID" : -1,
            "batteryID" : -1,
            "backID" : -1,
            "shellID" : -1
            }
            ]
        }
     *
     *
     * @param accountInfo
     * @return
     */
    public static String getAccountJson(Racecar.AccountInfo accountInfo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ProtocolConstants.JsonFiled.NICK_NAME, accountInfo.getName());
            jsonObject.put(ProtocolConstants.JsonFiled.EXP, accountInfo.getTotalExp());
            jsonObject.put(ProtocolConstants.JsonFiled.MAX_EXP,accountInfo.getLevelExp());
            jsonObject.put(ProtocolConstants.JsonFiled.LEVEL_TITLE, accountInfo.getLevelName());
            String actualIcon = AccountLogic.getAvatarImageUrl(accountInfo.getIcon());
            jsonObject.put(ProtocolConstants.JsonFiled.HEAD_ID, actualIcon );

            JSONArray carArray = new JSONArray();
            for(Racecar.Car car : accountInfo.getCar41List()) {
                JSONObject carObj = new JSONObject();
                carObj.put(ProtocolConstants.JsonFiled.USER_CAR_ID, car.getUserCarId());
                carObj.put(ProtocolConstants.JsonFiled.CAR_ID, car.getCarBaseId());
                carObj.put(ProtocolConstants.JsonFiled.CAR_NAME, car.getCustomName());
                JSONObject partObj = JsonManager.convertString2Json(car.getPart());
                if(partObj != null) {
                    carObj.put(ProtocolConstants.JsonFiled.SHELL_ID, partObj.getInt(ProtocolConstants.JsonFiled.SHELL_ID));
                    carObj.put(ProtocolConstants.JsonFiled.FRONT_ID, partObj.getInt(ProtocolConstants.JsonFiled.FRONT_ID));
                    carObj.put(ProtocolConstants.JsonFiled.BACK_ID, partObj.getInt(ProtocolConstants.JsonFiled.BACK_ID));
                    carObj.put(ProtocolConstants.JsonFiled.BATTERY_ID, partObj.getInt(ProtocolConstants.JsonFiled.BATTERY_ID));
                    carObj.put(ProtocolConstants.JsonFiled.ENGINE_ID, partObj.getInt(ProtocolConstants.JsonFiled.ENGINE_ID));
                } else {
                    carObj.put(ProtocolConstants.JsonFiled.SHELL_ID, -1);
                    carObj.put(ProtocolConstants.JsonFiled.FRONT_ID, -1);
                    carObj.put(ProtocolConstants.JsonFiled.BACK_ID, -1);
                    carObj.put(ProtocolConstants.JsonFiled.BATTERY_ID, -1);
                    carObj.put(ProtocolConstants.JsonFiled.ENGINE_ID, -1);
                }
                carArray.put(carObj);
            }
            jsonObject.put(ProtocolConstants.JsonFiled.ACTIVE_CAR, carArray);
        } catch (JSONException e) {
            Log.e(TAG, "create response string error: %s", e.getMessage());
        }
        return jsonObject.toString();
    }

    /**
     * Create json data when active the car and get response from the server.
     * @param code
     * @param message
     * @param carId
     * @param userCarId
     * @param amount
     * @return
     */
    public static String createActivationResponse(int code, String message, String title, String carId, int userCarId, int amount) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ProtocolConstants.JsonFiled.CODE, code);
            jsonObject.put(ProtocolConstants.JsonFiled.ERROR_MSG, message);
            jsonObject.put(ProtocolConstants.JsonFiled.ERROR_TITLE, title);
            jsonObject.put(ProtocolConstants.JsonFiled.CAR_ID, carId);
            jsonObject.put(ProtocolConstants.JsonFiled.USER_CAR_ID, userCarId);
            jsonObject.put(ProtocolConstants.JsonFiled.AMOUNT, amount);
        }catch (JSONException e) {
            Log.e(TAG, "create response string error: %s", e.getMessage());
        }
        return jsonObject.toString();
    }

    /**
     * Create general json message for the response.
     * @param code
     * @param msg
     * @return
     */
    public static String createGeneralJson(int code, String msg, String title) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ProtocolConstants.JsonFiled.CODE, code);
            jsonObject.put(ProtocolConstants.JsonFiled.ERROR_MSG, msg);
            jsonObject.put(ProtocolConstants.JsonFiled.ERROR_TITLE, title);
        }catch (JSONException e) {
            Log.e(TAG, "create response string error: %s", e.getMessage());
        }
        return jsonObject.toString();
    }


    public static String createAddFriendResult(int action) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ProtocolConstants.JsonFiled.ACTION, action);
        } catch (JSONException e) {
            Log.e(TAG, "create result json failed : %s", e.getMessage());
        }
        return jsonObject.toString();
    }


    public static JSONObject convertString2Json(String string) {
        if(string == null) {
            Log.e(TAG, "json string is null.");
            return null;
        }

        try {
            return new JSONObject(string);
        } catch (JSONException e) {
            Log.e(TAG, "parse json string error:%s", e.getMessage());
        }
        return null;
    }

    public static MyMessage convertStringToMyMessage(String json){
        MyMessage myMessage = null;
        JSONObject jsonObject = convertString2Json(json);
        try {
            final String method = jsonObject.getString(ProtocolConstants.JsonFiled.METHOD);
            final JSONObject contentObject = jsonObject.getJSONObject(ProtocolConstants.JsonFiled.PARAMS);
            final long fromUserId = contentObject.getLong(ProtocolConstants.JsonFiled.FROM);
            final String fromUsername = contentObject.getString(ProtocolConstants.JsonFiled.FROM_NAME);
            final String content = contentObject.getString(ProtocolConstants.JsonFiled.CONTENT);
            long timestamp = Util.getDate("yyyy-MM-dd hh:mm:ss", contentObject.getString(ProtocolConstants.JsonFiled.TIME_STAMP)).getTime();
            myMessage = new MyMessage();
            myMessage.setMethod(method);
            myMessage.setContent(content);
            myMessage.setFromUserId(fromUserId);
            myMessage.setTimestamp(timestamp);
            myMessage.setFromUsername(fromUsername);
            myMessage.setHasRead(false);
            myMessage.setResult(ProtocolConstants.JsonValue.ACTION_UN_HANDLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return myMessage;
    }


    public static String getFiled(JSONObject object, String name) {
        if(object == null || Util.isNullOrNil(name)) {
            Log.e(TAG, "object or key is null.");
            return null;
        }
        try {
            return object.getString(name);
        } catch (JSONException e) {
            Log.e(TAG, "get field error:%s", e.getMessage());
        }
        return null;
    }

    public static int getInt(JSONObject object, String name, int defaultValue) {
        if(object == null || Util.isNullOrNil(name)) {
            Log.e(TAG, "object or key is null.");
            return defaultValue;
        }
        try {
            return object.getInt(name);
        } catch (JSONException e) {
            Log.e(TAG, "get field error:%s", e.getMessage());
        }
        return defaultValue;
    }




    public static long getLong(JSONObject object, String name, long defaultValue) {
        if(object == null || Util.isNullOrNil(name)) {
            Log.e(TAG, "object or key is null.");
            return defaultValue;
        }
        try {
            return object.getLong(name);
        } catch (JSONException e) {
            Log.e(TAG, "get field error:%s", e.getMessage());
        }
        return defaultValue;
    }


}
