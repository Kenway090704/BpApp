package com.bemetoy.bp.protocols;


/**
 * @author AlbieLiang
 */
public final class ProtocolConstants {

    public static final String APP_PACKAGE_NAME = "com.bemetoy.bp";

    public static final int CLIENT_VERSION = 0x10101010;//0x1(平台识别：1 android 2 ios)01(主版本号)00（副版本号）00（修正版本号）0（保留）

    public static final int CLIENT_TYPE = 1;

	public static final int MM_INVALID_USER_ID = 0;

    public static final int RSA_PUBLIC_KEY_VERSION = 1;

    public static final int BP_FIX_HEADER_LEN = 18;
    public static final int BP_FIX_HEADER_VERSION = 0x0001;

    public static final String ALPHA_AVATAR_BASE_URL = "http://cdn.gdalpha.com/icon/300/";

//    public static final String HOST_CGI_DEBUG = "http://dev.cgi.zm.auldey.com:6003/bp_app_cgi";
    public static final String HOST_CGI_DEBUG = "http://login.chaozu.net:8000/bp_app_cgi";
    public static final String HOST_CGI_PROD = "http://cgi.zm.auldey.com/bp_app_cgi";

    public static final String ALPHA_FORGET_PWD_DEBUG = "http://passport.gdalpha.com:8081/blapp/";
    public static final String ALPHA_FORGET_PWD_PROD = "http://my.gdalpha.com:8081/";

    public static final String DATABASE_URL_DEBUG = "http://dev.zm.auldey.com/h5/h5/index";
    public static final String DATABASE_URL_PROD = "http://zm.auldey.com/h5/h5/index";

    public static final String U3D_UPDATE_URL_DEBUG = "http://s.bemetoy.com/download/b_project/AndroidAssets/";
    public static final String U3D_UPDATE_URL_PROD = "http://zm.cdn.gdalpha.com/download/AndroidAssets/";

    public static final String DEFAULT_TMALL_URL = "https://auldeywj.tmall.com/";


    public static final String GO_BACK_URL = "http://goback";

    public static final long DEFAULT_DISTANCE = -1; //定位中
    public static final long ERROR_DISTANCE = -2; //定位失败

    public static final String HOST_CGI =  BuildConfig.DEBUG ? ProtocolConstants.HOST_CGI_DEBUG : ProtocolConstants.HOST_CGI_PROD;
    public static final String ALPHA_FORGET_PWD =  BuildConfig.DEBUG ? ProtocolConstants.ALPHA_FORGET_PWD_DEBUG : ProtocolConstants.ALPHA_FORGET_PWD_PROD;
    public static final String U3D_UPDATE_URL =  BuildConfig.DEBUG ? ProtocolConstants.U3D_UPDATE_URL_DEBUG : ProtocolConstants.U3D_UPDATE_URL_PROD;
    public static final String DATABASE_URL = BuildConfig.DEBUG ? ProtocolConstants.DATABASE_URL_DEBUG : ProtocolConstants.DATABASE_URL_PROD;

    public static final int PAGE_SIZE = 20;
    public static final int NEAREST_LOCATION_SIZE = 10;
    public static final int ALPHA_MAX_IMAGE_SIZE = 300;


    public interface IntentParams {
        String USER_LOCATION_PROVINCE = "user.province";
        String USER_LOCATION_CITY = "user.city";
        String USER_LOCATION_DISTRICT = "user.district";
        String USER_ID = "user.id";
        String SESSION_ID = "session.id";
        String USER_NAME = "user.name";
        String USER_AVATAR = "user.avatar";
        String PART_ITEM = "part.item";
        String USER_INFO = "user.info";
        String USER_POSITION = "user.position";
        String GAME_INFO = "game.info";
        String URL_INFO = "url.info";
        String SHOW_BACK = "show.back";
        String ADDRESS_INFO = "address.info";
        String ADDRESS_ID = "address.id";
        String ADDRESS_LATITUDE = "address.latitude";
        String ADDRESS_LONGITUDE = "address.longitude";
        String LOCATION_PROVINCE = "location.province";
        String LOCATION_CITY = "location.city";
        String LOCATION_DISTRICT = "location.district";
        String TASK_INFO = "task.info";
        String GAME_POSITION = "game.position";
        String GAME_ID = "game.id";
        String CAR_INFO = "car.info";
        String CAR_COUNT = "car.count";
        String SCORE_INFO = "score.info";
        String ACHIEVE_INFO = "achieve.info";
        String GAME_IMAGE = "game.image";
        String IS_FRIEND = "is.Friend";
        String OPERATION_LIST = "operation.list";
        String TASK_EXP = "task.exp";
        String TASK_SCORE = "task.score";
        String PART_TYPE = "part.type";
        String ACTION_RESULT = "action.result";
        String BUNDLE_DATA = "bundle.data";
        String ADAPTER_CHOOSE_MODEL = "adapter.model";
        String DOWNLOAD_PATH = "download.path";
    }

    public interface PermissionCode {
        int GPS = 1;
    }

    public interface BroadCastAction {
         String TASK_UPDATE_ACTION = "task.update.action";
         String U3D_LAUNCH_FINISH = "u3d.launch";
    }


    public interface RequestCode {
        int ACTION = 200;
        int ACTION_EDIT = 201;
        int ACTION_CHOOSE_ADDRESS = 202;
        int ACTION_ADD_NEW_ADDRESS = 203;
    }

    public interface ResultCode{
        int ACTION_CANCEL = 299;
        int ACTION_DONE = 300;
        int ACTION_JOIN_GAME = 301;
        int ACTION_CHOOSED_ADDRESS = 302;
        int ACTION_GO_BACK = 303;
    }



    public static final int INVALID_COMMON_HEADER_ERROR = 100001;
    public static final int INVALID_BASE_ERROR_CODE = 1;

    public interface ErrorType {
        int OK = 8000;
        int NETWORK_LOCAL_ERROR = 8002;
        int NETWORK_SVR_ERROR = 8003;
    }

    public interface Register {
        int NICKNAME_MAX_LENGTH = 12;
        int NICKNAME_MIN_LENGTH = 6;

        int PASSWORD_MAX_LENGTH = 12;
        int PASSWORD_MIN_LENGTH = 6;

        int PHONE_NUM_LENGTH = 11;

        int OK = -1;
        int NICKNAME_INVALID = 0;
        int PASSWORD_LENGTH_ERROR = 1;
        int PASSWORD_NOT_MATH = 2;
        int RECOMMENDER_INVALID = 3;
    }

    public interface Address {
        int CONTACT_MAX_LENGTH = 10;
        int DETAIL_ADDRESS_LENGTH = 40;
    }



    public interface VerifyCode {
        int IMAGE = 0;
        int LOGIN_SMS = 1;
        int REGISTER_SMS  = 2;
    }

    public interface LOGIN_METHOD {
        int NONE = -1;
        int ACCOUNT_LOGIN = 0;
        int PHONE_LOGIN = 1;
    }

    public interface GameType {
        int WEEK_GAME = 1;
        int CITY_GAME = 2;
        int PROVINCE_GAME = 3;
        int STATE_GAME = 4;
        int THEME_GAME = 5;
        int TRAINING_GAME = 20;
        int PK_GAME = 21;
    }

    public interface PART_TYPE  {
        int NONE = 0;
        int CAR = 1;
        int PART = 2;
        int RECOMMEND = 3;
    }


    public interface DeviType {
        int Device_Type_Android = 1;
        int Device_Type_IOS = 2;
    }

    public interface CryptAlg {
        int NO_ENCRYPT = 0;
        int RSA_ENCRYPT_WITH_PUBLICKEY = 1;
        int AES_ENCRYPT_WITH_PRIVATEKEY = 2;
    }

    public interface SpeedWayType {
        int Horizontal = 1;
        int VERTICAL = 2;
        int EIGHT_SHAPE = 3;
        int SPIRAL = 4;
    }


    public interface GameStates {
        int NONE = -1; // never hold up any game.
        int COMING_SOON = 0;
        int PLAYING = 1;
        int FINISHED = 2;
    }

    public interface NetScene {
        int NetScene = 1;
    }

    public interface DataTypeLengthInByte {
        short TYPE_SHORT = 2;
        short TYPE_INT = 4;
    }

    public interface RetryPolicy {
        int DEFAULT_TIME_OUT = 3 * 1000;
        int MAX_RETRY_TIMES = 3;
        float DEFAULT_BACK_OFF = 1f;
    }

    public interface Ranking_Tab {
        int STATE = 0;
        int PROVINCE = 1;
        int CITY = 2;
        int WEEK = 3;
    }

    public interface GameStatus {
        int COMING_SOON = 0;
        int ON_GOING = 1;
        int FINISHED = 2;
    }

    public interface TaskType {
        int ONLINE = 1;
        int OFFLINE = 2;
    }

    public interface TaskStatus {
        int UN_FINISH = 0;
        int FINISHED = 1;
        int DONE = 2;
    }

    public interface PageId {
        int GAME = 1;  // 赛事
        int MY_CAR = 2; // 我的赛车
        int FRIENDS = 3;  // 好友
        int MALL = 4; // 积分商城
        int NOTICE = 5; // 公告
        int TMALL = 6; //线上商城
        int DATABASE = 7; // 资料库
        int RANKING = 8; // 排行榜
        int ACTIVATION = 9; // 激活
        int PERSONAL_CENTER = 10; // 个人中心
    }

    public interface MessageType {
        String ADD_REQUEST = "AddFriendRequest";
        String NOTICE = "Notice";
        String NOTICE_GAME = "Notice.Game";
        String NOTICE_REWARD = "Notice.RankReward";
    }


    public interface JsonFiled {
        String NICK_NAME = "nickname";
        String LEVEL_TITLE = "levelTitle";
        String HEAD_ID = "headID";
        String EXP = "exp";
        String MAX_EXP = "maxExp";
        String ACTIVE_CAR = "activatedCars";
        String CAR_ID = "carID";
        String USER_CAR_ID = "carDBID";
        String CAR_NAME = "carName";
        String ENGINE_ID = "engineID";
        String FRONT_ID = "frontID";
        String BATTERY_ID = "batteryID";
        String BACK_ID = "backID";
        String SHELL_ID = "shellID";

        String ID = "id";
        String METHOD = "method";
        String PARAMS = "params";
        String FROM = "from";
        String TO = "to";
        String CONTENT = "content";
        String FROM_NAME = "from_name";
        String TIME_STAMP =  "timestamp";
        String AGREE =  "agree";
        String TITLE = "title";
        String GAME_ID = "game_id";

        String RESULT = "result";
        String ACTION = "action";

        String METHOD_ADD_FRIEND = "AddFriendRequest";
        String METHOD_ADD_FRIEND_RESPONSE = "AddFriendResponse";
        String METHOD_NOTICE = "Notice";

        String ICON_LIST = "icon_list";
        String ICON_URL = "icon_url";
        String ICONS = "icons";
        String CATEGORY = "category";
        String NAME = "name";

        String CODE = "code";
        String ERROR_MSG = "errorMsg";
        String ERROR_TITLE = "errorTitle";
        String AMOUNT = "amount";
    }


    public interface JsonValue {
        int ACTION_PASS = 0;
        int ACTION_IGNORE = 1;
        int ACTION_UN_HANDLE = 2;
        int ALPHA_AVATAR_OK = 0;

        int REQUEST_OK = 0;
        int REQUEST_ERROR = 1;
        int REQUEST_NETWORK_ERROR = 2;
    }


}
