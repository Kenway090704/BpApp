package com.bemetoy.bp.protocols;

/**
 * Created by albieliang on 16/4/7.
 */
public interface PluginConstants {

    interface Plugin {
        String PLUGIN_NAME_APP = "Plugin@App";
        String PLUGIN_NAME_LIB_STUB = "Plugin@LibStub";
        String PLUGIN_NAME_LIB_PERSISTENCE = "Plugin@LibPersistence";
        String PLUGIN_NAME_P_PERSONAL_CENTER = "Plugin@PersonalCenter";
        String PLUGIN_NAME_P_RANKING = "Plugin@Ranking";
        String PLUGIN_NAME_P_GAMES = "Plugin@Games";
        String PLUGIN_NAME_P_MALL = "Plugin@Mall";
        String PLUGIN_NAME_P_FRIENDS = "Plugin@Friends";
        String PLUGIN_NAME_P_TASKS_CENTER = "Plugin@TasksCenter";
        String PLUGIN_NAME_P_NOTICE = "Plugin@Notice";
        String PLUGIN_NAME_P_OPERATIONS = "Plugin@Operations";
        String PLUGIN_NAME_P_MY_CAR = "Plugin@MyCar";
    }

    interface App {
        interface Action {
            int CMD_START_LAUNCHER_UI = 1;
            int CMD_START_HOME_PAGE = 2;
            int CMD_SHOW_LOCATION_DIALOG = 3;
            int CMD_START_LOCATION_CHOOSE_UI = 4;
            int CMD_START_ACTIVATION = 5;
            int CMD_START_TMALL = 6;

        }

        interface DataKey {
            String GET_USER_ID = "App@DataKey@GetUserId";
            String SET_USER_ID = "App@DataKey@SetUserId";
            String GET_APK_DOWNLOAD_ID = "App@DataKey@GetDownloadId";
            String SET_APK_DOWNLOAD_ID = "App@DataKey@SetDownloadId";
            String GET_UPDATE_LOG = "App@DataKey@GetUpdateLog";
            String SET_UPDATE_LOG = "App@DataKey@SetUpdateLog";
            String SET_DOWN_FILE_PATH = "App@DataKey@SetDownloadFilePath";
            String GET_DOWN_FILE_PATH = "App@DataKey@GetDownloadFilePath";
            String GET_DOWN_FILE_MD5 = "App@DataKey@GetDownloadFileMD5";
            String SET_DOWN_FILE_MD5 = "App@DataKey@SetDownloadFileMD5";
        }
    }

    interface PersonalCenter {
        interface Action {
            int CMD_START_PERSONAL_CENTER_UI = 1;
            int CMD_SHOW_NICKNAME_EDIT_DIALOG = 2;
            int CMD_START_ADD_ADDRESS_UI = 3;
            int CMD_START_EDIT_ADDRESS_UI = 4;
            int CMD_START_EDIT_NICKNAME_UI = 5;
            int CMD_START_ADD_ADDRESS_UI_FOR_RESULT = 6;
            int CMD_START_ADDRESS_LIST_UI_FOR_RESULT = 7;
        }

        interface DataKey {
            String GET_USER_ID = "App@DataKey@GetUserId";
            String SET_USER_ID = "App@DataKey@SetUserId";
            String CONTENT = "App@DataKey@Content";
        }
    }

    interface Ranking {
        interface Action {
            int CMD_START_RANKING_UI = 1;
        }
    }

    interface Games {
        interface Action {
            int CMD_START_GAMES_UI = 1;
            int CMD_START_GAME_DETAIL = 2;
            int CMD_START_ADDRESS_DETAIL = 3;
            int CMD_START_MAP_UI = 4;
            int CMD_START_PK_GAME_DETAIL = 5;
        }
    }

    interface Mall {
        interface Action {
            int CMD_START_MALL_UI = 1;
            int CMD_START_PART_UI = 2;
            int CMD_START_ORDERS_UI = 3;
        }
    }

    interface FRIENDS {
        interface Action {
            int CMD_START_FRIENDS_UI = 1;
            int CMD_START_SEARCH_FRIEND_UI = 2;
            int CMD_START_USER_DETAIL = 3;
            int CMD_START_FRIEND_DETAIL = 4;
            int CMD_INIT_FRIENDS_DATA = 5;
            int CMD_QUERY_RELATIONSHIP = 6;
        }
    }

    interface TasksCenter {
        interface Action {
            int CMD_START_TASKS_CENTER_UI = 1;
        }

        interface DataKey {
            String GET_USER_ID = "App@DataKey@GetUserId";
            String SET_USER_ID = "App@DataKey@SetUserId";
        }
    }

    interface Notice {
        interface Action {
            int CMD_START_NOTICE_CENTER_UI = 1;
        }

        interface DataKey {
            String GET_USER_ID = "App@DataKey@GetUserId";
            String SET_USER_ID = "App@DataKey@SetUserId";
        }
    }
    interface Operations {
        interface Action {
            int CMD_START_OPERATIONS_UI = 1;
        }
    }


    interface MyCard {
        interface Action {
            int CMD_START_MYCAR_UI = 1;
        }
    }
}
