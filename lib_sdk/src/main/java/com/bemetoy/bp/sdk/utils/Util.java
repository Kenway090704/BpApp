package com.bemetoy.bp.sdk.utils;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;

import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.tools.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * The universal utility.
 * 
 * @author AlbieLiang
 * 
 */
public class Util {

	private static final String TAG = "utils.Util";

	/**
	 * It will return a format time string as 'mm:ss'
	 * 
	 * @param time
	 * @return
	 */
	public static String getFormatTime(int time) {
		return getFormatTime("mm:ss", time);
	}

	/**
	 * It will exchange the time to a format string. <br>
	 * 
	 * @param format
	 * @param time
	 * @return
	 */
	public static String getFormatTime(String format, int time) {
		if (format == null || format.equals("")) {
			throw new IllegalArgumentException("formater is null.");
		}
		if (format.equalsIgnoreCase("mm:ss")) {

			int minute = ((int) time) / 60;
			int second = ((int) time) % 60;
			String timeMsg;
			if (minute < 10)
				timeMsg = "0" + minute + ":";
			else
				timeMsg = minute + ":";
			if (second < 10)
				timeMsg += "0" + second;
			else
				timeMsg += second;
			return timeMsg;
		}
		return "00:00";
	}

	/**
	 * <pre>
	 *                     yyyy-MM-dd 1969-12-31
	 *                     yyyy-MM-dd 1970-01-01
	 *               yyyy-MM-dd HH:mm 1969-12-31 16:00
	 *               yyyy-MM-dd HH:mm 1970-01-01 00:00
	 *              yyyy-MM-dd HH:mmZ 1969-12-31 16:00-0800
	 *              yyyy-MM-dd HH:mmZ 1970-01-01 00:00+0000
	 *       yyyy-MM-dd HH:mm:ss.SSSZ 1969-12-31 16:00:00.000-0800
	 *       yyyy-MM-dd HH:mm:ss.SSSZ 1970-01-01 00:00:00.000+0000
	 *     yyyy-MM-dd'T'HH:mm:ss.SSSZ 1969-12-31T16:00:00.000-0800
	 *     yyyy-MM-dd'T'HH:mm:ss.SSSZ 1970-01-01T00:00:00.000+0000
	 * </pre>
	 * 
	 * @param format
	 * @param msec
	 * @return
	 */
	public static String getDateFormat(String format, long msec) {
		return (new SimpleDateFormat(format)).format(new Date(msec));
	}

	public static long getDateMsec(String formatDate) {
		long msec = 0;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatDate);
		try {
			Date date = simpleDateFormat.parse(formatDate);
			msec = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return msec;
	}

	public static Date getDate(String format, String formatDate) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = simpleDateFormat.parse(formatDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * @param beginDate
	 * @param endDate
     * @return
     */
	public static String getMonthAndDayDuration(String beginDate, String endDate) {
			if(isNullOrNil(beginDate) || isNullOrNil(endDate)) {
				Log.e(TAG, "beginDate and endDate should not be null");
				return null;
			}

			StringBuilder stringBuilder = new StringBuilder();
			Calendar beginCalendar = Calendar.getInstance();
			beginCalendar.setTime(Util.getDate("yyyy-MM-dd hh:mm:ss",beginDate));
			stringBuilder.append(beginCalendar.get(Calendar.MONTH) + 1);
			stringBuilder.append("月");
			stringBuilder.append(beginCalendar.get(Calendar.DAY_OF_MONTH));
			stringBuilder.append("日");

			stringBuilder.append("-");

			Calendar endCalendar = Calendar.getInstance();
			endCalendar.setTime(Util.getDate("yyyy-MM-dd hh:mm:ss",endDate));
			stringBuilder.append(endCalendar.get(Calendar.MONTH) + 1);
			stringBuilder.append("月");
			stringBuilder.append(endCalendar.get(Calendar.DAY_OF_MONTH));
			stringBuilder.append("日");
			return stringBuilder.toString();
	}



	public static byte[] shortToByteArray(short s) {
		byte[] targets = new byte[ProtocolConstants.DataTypeLengthInByte.TYPE_SHORT];
		for (int i = 0; i < ProtocolConstants.DataTypeLengthInByte.TYPE_SHORT; i++) {
			int offset = (targets.length - 1 - i) * 8;
			targets[i] = (byte) ((s >>> offset) & 0xff);
		}
		return targets;
	}

	public static short byteArray2Short(final byte[] aByteArray, short aDefault) {
		if (ProtocolConstants.DataTypeLengthInByte.TYPE_SHORT != aByteArray.length) {
			Log.e(TAG, "error param: aByteArray.length = %d", aByteArray.length);
			return aDefault;
		}

		short ret = 0;
		ret |= ((short) aByteArray[1] & 0x00FF);
		ret |= (((short) aByteArray[0] << 8) & 0xFF00);

		return ret;
	}

    public static int byteArray2Int(final byte[] aByteArray, int aDefault) {
        if (ProtocolConstants.DataTypeLengthInByte.TYPE_INT != aByteArray.length) {
            Log.e(TAG, "error param: aByteArray.length = %d", aByteArray.length);
            return aDefault;
        }

        int value= 0;
        for (int i = 0; i < 4; i++) {
            value += (aByteArray[i] & 0xFF) << (8 * (3 - i));
        }
        return value;
    }


	/**
	 * The method check the string whether is null or nil or normally.
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNullOrNil(String str) {
		return str == null || str.length() == 0;
	}

	/**
	 * The method check the string whether is null or blank.
	 * @param str
	 * @return
     */
	public static boolean isNullOrBlank(String str) {
		return str == null || str.trim().length() == 0;
	}

	/**
	 * The method check the byte array is valid or not
	 * @param object
	 * @return
	 */
	public static boolean isNullOrNil(final byte[] object) {
		if ((object == null) || (object.length <= 0)) {
			return true;
		}
		return false;
	}

	/**
	 * The method check the object is null or not
	 * @param aObject
	 * @return
	 */
	public static boolean isNull(Object aObject) {
		return null == aObject;
	}

	/**
	 * The method will return 0 when the given i is null.
	 * 
	 * @param i
	 * @return
	 */
	public static Integer nullAsNil(Integer i) {
		if (i == null) {
			return 0;
		}
		return i;
	}

	public static String nullAsNil(String str) {
		if (str == null) {
			return "";
		}
		return str;
	}

	/**
	 * The enum is used as a mark to convert storage unit.
	 * 
	 * @author AlbieLiang
	 * 
	 */
	public static enum StorageUnit {
		STORAGE_UNIT_BYTE, STORAGE_UNIT_KB, STORAGE_UNIT_MB, STORAGE_UNIT_GB
	}

	/**
	 * The method will convert the byteSize to a new unit.
	 * 
	 * @param unit
	 * @param byteSize
	 * @return
	 */
	public static long convert(StorageUnit unit, long byteSize) {

		switch (unit) {
		case STORAGE_UNIT_BYTE:
			return byteSize;// Byte
		case STORAGE_UNIT_KB:
			return byteSize / 1024;// KB
		case STORAGE_UNIT_GB:
			return byteSize / 1024 / 1024 / 1024;// GB
		case STORAGE_UNIT_MB:
		default:
			return byteSize / 1024 / 1024;// MB
		}
	}

	/**
	 * Get the unit string.
	 * 
	 * @param unit
	 * @param byteSize
	 * @return
	 */
	public static String getUnitString(StorageUnit unit, long byteSize) {
		float b = 0;
		float k = 0;
		float m = 0;
		float g = 0;
		switch (unit) {
		case STORAGE_UNIT_GB:
			g = ((float) byteSize) / 1024 / 1024 / 1024;// GB
			return String.format("%.2fG", g);
		case STORAGE_UNIT_MB:
			m = ((float) byteSize) / 1024 / 1024;// MB
			return String.format("%.2fM", m);
		case STORAGE_UNIT_KB:
			k = ((float) byteSize) / 1024;// KB
			return String.format("%.2fK", k);
		case STORAGE_UNIT_BYTE:
			b = ((float) byteSize);// Byte
		}
		return String.format("%.2fB", b);
	}

	/**
	 * Change the first chat to upper.
	 * 
	 * @param src
	 * @return
	 */
	public static String changeFirstChatToUpper(String src) {
		if (src == null || src.equals("")) {
			return src;
		}
		if (Character.isUpperCase(src.charAt(0)))
			return src;
		else {
			return (new StringBuilder())
					.append(Character.toUpperCase(src.charAt(0)))
					.append(src.substring(1)).toString();
		}
	}

	/**
	 * Get the suffix of the given file name.
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getSuffix(String fileName) {
		if (isNullOrNil(fileName)) {
			return null;
		}
		int lastIndex = fileName.lastIndexOf(".");
		if (lastIndex == -1 || fileName.length() == (lastIndex + 1)) {
			return null;
		}
		return fileName.substring(lastIndex + 1);
	}

	/**
	 * Check whether the string suffix is match the given.
	 * 
	 * @param str
	 * @param suffix
	 * @return
	 */
	public static boolean isMatchSuffix(String str, String suffix) {
		if (isNullOrNil(str) || isNullOrNil(suffix)) {
			return false;
		}
		return suffix.equals(getSuffix(str));
	}

	/**
	 * Check whether the string suffix is match the given when ignore case.
	 * 
	 * @param str
	 * @param suffix
	 * @return
	 */
	public static boolean isMatchSuffixIgnoreCase(String str, String suffix) {
		if (isNullOrNil(str) || isNullOrNil(suffix)) {
			return false;
		}
		return suffix.equalsIgnoreCase(getSuffix(str));
	}

	/**
	 * Remove the suffix of the string
	 * 
	 * @param str
	 * @return
	 */
	public static String removeSuffix(String str) {
		if (isNullOrNil(str)) {
			return str;
		}
		int lastIndex = str.lastIndexOf(".");
		if (lastIndex == -1) {
			return str;
		}
		return str.substring(0, lastIndex);
	}

	/**
	 * Get the text height with the given paint.
	 * 
	 * @param paint
	 * @return
	 */
	public static float getTextHeight(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
		return fm.bottom - fm.top;
	}

	/**
	 * Get the text width with the given paint, you can invoked the function
	 * {@link Paint#measureText(String)} instead.
	 * 
	 * @param paint
	 * @param text
	 * @return
	 */
	public static float getTextWidth(Paint paint, String text) {
		return paint.measureText(text);
	}

	/**
	 * Get the text bounds with the given paint.
	 * 
	 * @param paint
	 * @param text
	 * @return
	 */
	public static Rect getTextBounds(Paint paint, String text) {
		Rect r = new Rect();
		paint.getTextBounds(text, 0, text.length() - 1, r);
		return r;
	}

	/**
	 * Check whether the value of the flag in the bit set is 0 or not.
	 * 
	 * @param bitset
	 * @param flag
	 * @return
	 */
	public static final boolean checkBitSetValue(int bitset, int flag) {
		return (bitset & flag) != 0;
	}
	
	/**
	 * Set the value of the flag in the bit set.
	 * 
	 * @param bitset
	 * @param flag
	 * @param value
	 * @return
	 */
	public static final int setBitSetValue(int bitset, int flag, boolean value) {
		if (value) {
			bitset |= flag;
		} else {
			bitset &= (~flag);
		}
		return bitset;
	}

	/**
	 * The class is used to change the color value.
	 * 
	 * @author AlbieLiang
	 * 
	 */
	public static final class color {

		public static int getNegativeColor(int color) {
			return Color.argb(225 - Color.alpha(color), 225 - Color.red(color),
					225 - Color.green(color), 225 - Color.blue(color));
		}

		public static int getBrighterColor(int color) {
			return getBrighter(color, 30);
		}

		public static int getDarkerColor(int color) {
			return getDarker(color, 30);
		}

		public static int getDoubleDarkerColor(int color) {
			return getDarker(color, 60);
		}

		public static int getDarker(int color, int dark) {
			if (dark < 0) {
				return color;
			}
			int a = Color.alpha(color) < dark ? 255 : Color.alpha(color) - dark;
			int r = Color.red(color) < dark ? 255 : Color.red(color) - dark;
			int g = Color.green(color) < dark ? 255 : Color.green(color) - dark;
			int b = Color.blue(color) < dark ? 255 : Color.blue(color) - dark;
			return Color.argb(a, r, g, b);
		}

		public static int getBrighter(int color, int bright) {
			if (bright < 0) {
				return color;
			}
			int a = Color.alpha(color) > bright ? 255 : Color.alpha(color)
					+ bright;
			int r = Color.red(color) > bright ? 255 : Color.red(color) + bright;
			int g = Color.green(color) > bright ? 255 : Color.green(color)
					+ bright;
			int b = Color.blue(color) > bright ? 255 : Color.blue(color)
					+ bright;
			return Color.argb(a, r, g, b);
		}
	}

	public static String getStack(Throwable throwable) {
		if (throwable == null) {
			return "";
		}
		ByteArrayOutputStream baos = null;
		PrintStream ps = null;
		try {
			baos = new ByteArrayOutputStream();
			ps = new PrintStream(baos);
			throwable.printStackTrace(ps);
			return baos.toString();
		} catch (Exception e) {
			return "";
		} finally {
			if (ps != null) {
				ps.close();
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static final byte[] buildInAESKey = { (int) 0x00, (int) 0x01, (int) 0x02, (int) 0x03, (int) 0x04, (int) 0x05, (int) 0x06, (int) 0x07, (int) 0x08, (int) 0x09, (int) 0x0a, (int) 0x0b, (int) 0x0c, (int) 0x0d, (int) 0x0e,
			(int) 0x0f };

	public static byte[] geneAESKey() {
		KeyGenerator kg = null;
		try {
			kg = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, "%s", e.toString());
			return buildInAESKey;
		}

		// can be 128,192,256
		kg.init(128);
		SecretKey sk = kg.generateKey();
		if (Util.isNull(sk)) {
			Log.e(TAG, "sk is null");
			return buildInAESKey;
		}

		byte[] ret = sk.getEncoded();
		if (Util.isNullOrNil(ret)) {
			Log.e(TAG, "generate aes key fail!!!, ret is null or nil");
			return buildInAESKey;
		}

		Log.d(TAG, "build aes ramdon key successful, length = %d", ret.length);
		return ret;
	}

	public static String contactString(String [] strings, String split) {
		if(isNull(strings) || strings.length == 0 ) {
			Log.e(TAG, "strings is null or nil");
			return null;
		}

		final StringBuffer sb = new StringBuffer();
		for(int i = 0 ; i <= strings.length - 2; i++) {

			if(!isNullOrNil(strings[i])) {
				sb.append(strings[i]);
			}
			if(!isNullOrNil(split)) {
				sb.append(split);
			}
		}

		if(!isNullOrNil(strings[strings.length - 1])) {
			sb.append(strings[strings.length - 1]);
		}
		return sb.toString();
	}

	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}


	/**
	 *
	 * @param time
	 * @return
     */
	public static String millSecToTime(double time) {
		time = time * 1000;
		String timeStr = null;
		int minute = 0;
		int second = 0;
		double millSec = 0;
		if (time <= 0)
			return "00'00\"00";
		else {
			millSec = time % 1000;
			second = (int)time / 1000;
			if (second < 60) {
				timeStr = unitFormat(0) + "'" + unitFormat(second) + "\"" + unitFormat((int)millSec/10);
			} else {
				minute = second / 60;
				second %= 60;
				timeStr = unitFormat(minute) + "'" + unitFormat(second) + "\"" + unitFormat((int)millSec/10) + "";
			}
		}
		return timeStr;
	}


	/**
	 *
	 * @param time
	 * @return
	 */
	public static String millSecToSecond(double time) {
		time = time * 1000;
		String timeStr = null;
		int second = 0;
		double millSec = 0;
		if (time <= 0)
			return "00'00\"00";
		else {
			millSec = time % 1000;
			second = (int)time / 1000;
			timeStr = unitFormat(second) + "\"" + unitFormat((int)millSec/10);
		}
		return timeStr;
	}

	public static String unitFormat(int i) {
		return String.format("%02d", i);
	}

	public static String unitFormat(double d) {
		return new DecimalFormat("00.00").format(d);
	}

}
