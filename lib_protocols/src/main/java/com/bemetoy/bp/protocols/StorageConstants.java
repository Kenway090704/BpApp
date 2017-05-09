package com.bemetoy.bp.protocols;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * 
 * @author AlbieLiang
 * 
 */
public class StorageConstants {

	public static final String DATA_ROOT;
	public static final String DATAROOT_MOBILEMEM_PATH;
	public static final String DATAROOT_PUBLIC_PATH;
	public static final String USER_DATA_PATH;
	public static final String ACCOUNT_DATA_PATH;
	public static final String COMM_SETTING_PATH;
	public static final String APK_DOWN_LOAD_PATH;

	static {
		Context context = EnvirenmentArgsHolder.getContext();
		if (context == null) {
			throw new RuntimeException("ApplicationContext not initialized.");
		}
		DATA_ROOT = context.getFilesDir().getParentFile().getAbsolutePath() + "/";
		DATAROOT_MOBILEMEM_PATH = DATA_ROOT + "BpApp";
		USER_DATA_PATH = DATA_ROOT + FileName.USER_FILE;
		ACCOUNT_DATA_PATH = DATA_ROOT + FileName.DATA_FILE;//+acc.data
		COMM_SETTING_PATH = DATA_ROOT + FileName.MISC_FILE;

		if(context.getExternalFilesDir("apk") != null) {
			APK_DOWN_LOAD_PATH = context.getExternalFilesDir("apk").getAbsolutePath();
		} else {
			// currently shared storage is not available.
			File downLoadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			if(!downLoadPath.exists()) {
				downLoadPath.mkdirs();
				APK_DOWN_LOAD_PATH = downLoadPath.getAbsolutePath();
			} else {
				APK_DOWN_LOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ "BpApp";
			}
		}

		try {
			final File f = new File(DATAROOT_MOBILEMEM_PATH);
			if (!f.exists()) {
				f.mkdirs();
			}
		} catch (Error e) {
			// ignored
		}

		DATAROOT_PUBLIC_PATH = DATA_ROOT + "files/public/";
	}

	/**
	 * The default avatar was storage in the assets folder.
	 */
	public static final String DEFAULT_NOR_AVATAR_PATH = Assets.DEFALUT_AVATAR;
	
	/**
	 * The default icon was storage in the assets folder.
	 */
	public static final String DEFAULT_NOR_ICON_PATH = Assets.DEFALUT_ICON;

	/**
	 * SDCard storage path constants.
	 * 
	 * @author AlbieLiang
	 * 
	 */
	public interface SDCard {
		// TODO : That may has more than one SDCard in the Device.
		String STORAGE_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
		
		String APP_STORAGE_ROOT = STORAGE_ROOT + File.separator + "BpApp";
		
		String IMAGE_STORAGE_DIR = APP_STORAGE_ROOT + File.separator + "image";
		String AVATAR_STORAGE_DIR = APP_STORAGE_ROOT + File.separator + "avatar";
		String VIDEO_STORAGE_DIR = APP_STORAGE_ROOT + File.separator + "video";
		String AUDIO_STORAGE_DIR = APP_STORAGE_ROOT + File.separator + "audio";
		String LOG_STORAGE_DIR = APP_STORAGE_ROOT + File.separator + "log";
		String CRASH_STORAGE_DIR = APP_STORAGE_ROOT + File.separator + "crash";
        String INFO_STORAGE_DIR = APP_STORAGE_ROOT + File.separator + "info";

		/**
		 * Download root directory.
		 */
		String DOWNLOAD_STORAGE_ROOT = APP_STORAGE_ROOT + File.separator + "download";
		
		String AUDIO_DOWNLOAD_STORAGE_DIR = DOWNLOAD_STORAGE_ROOT + File.separator + "song";
		String APK_DOWNLOAD_STORAGE_DIR = DOWNLOAD_STORAGE_ROOT + File.separator + "apk";
		String IMAGE_DOWNLOAD_STORAGE_DIR = DOWNLOAD_STORAGE_ROOT + File.separator + "image";

		String APK_DOWNLOAD_STORAGE_DIR_FOR_6 = DATAROOT_MOBILEMEM_PATH + File.separator  + "apk";
		
		/**
		 * Data root directory
		 */
		String DATA_STORAGE_DIR = STORAGE_ROOT + File.separator + "BpApp" + File.separator + "data";

	}

	/**
	 * Assets storage path Constants.
	 * 
	 * @author AlbieLiang
	 * 
	 */
	public interface Assets {

		String AVATAR_DIR = "avatar";
		String JSAPI_DIR = "jsapi";
		String SOUND_DIR = "sound";
		String EMOJI_DIR = "emoji";
		String IMAGE_DIR = "image";
		String HTML_DIR = "html";

		String DEFALUT_AVATAR = AVATAR_DIR + File.separator + "default_nor_avatar.png";
		String DEFALUT_ICON = AVATAR_DIR + File.separator + "default_nor_icon.png";

		String HOME_PAGE_ = IMAGE_DIR + File.separator + "";
	}

	/**
	 * The constants of preference of the application.
	 * 
	 * @author AlbieLiang
	 * 
	 */
	public interface Preferences {
		String COMMON_PREF_XML = ProtocolConstants.APP_PACKAGE_NAME + "_preferences";
		String PLAYER_STATUS_DATA_PREF_XML = "player_status_data";
		String K_PLAY_MODE = "play_mode";
		String K_PLAYLIST_ID = "playlist_id";
		String K_SONG_ID = "song_id";
		String K_IS_AUTO_PLAY = "is_auto_play";
		String K_IS_AUTO_LOGIN = "is_auto_login";
		String K_SKIN_ID = "skin_id";
	}

    public interface FileName {
        String DATA_FILE = "acc.data";
        String SETTING_FILE = "setting.data";
		String USER_FILE = "user.data";
		String MISC_FILE = "misc.data";
		String UPDATE_LOG_FILE = "update.log";
    }

    public interface Info_Key {
        int USER_NAME = 0;
        int MD5_PASSWORD = 1;
		int SYNC_KEY = 2;
		int HDHEAD_IMG_URL = 3;
		int PROVINCE = 4;
		int CITY = 5;
		int OLD_NAME = 6;
		int SCORE = 7;
		int LEVEL = 8;
		int EXP = 9;
		int NEXT_LEVEL_UP = 10;
//		int ADDRESS_DETAIL = 11;
//		int CONTACT = 12;
//		int MOBILE = 13;
		int ADDRESS = 14;
		int CART_COUNT = 15;
		int PARTS_COUNT = 16;
		int NATIONAL_RANK =17;

		int PROVINCE_RANK = 18;
		int CITY_RANK = 19;
		int WEEKLY_RANK = 20;
		int RANK_PERCENT = 21;
		int NAME = 22;
		int SPEED = 23;
		int DISTRICT = 24;
		int CARS = 25;
		int LEVEL_NAME = 26;
		int TOTAL_EXP = 27;
	}

	public interface SETTING_KEY {
		int LAST_CITY = 1;
		int LAST_DISTRICT = 2;
		int LAST_PROVINCE = 3;
		int RANK_RULE = 4;
		int AVATAR_BASE_URL = 5;
		int REGION_VERSION = 6;
	}

}
