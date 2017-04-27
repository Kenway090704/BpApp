
package com.bemetoy.bp.sdk.utils;

import com.bemetoy.bp.sdk.tools.Log;

import java.util.Map;
import java.util.Random;

/**
 * 
 * @author AlbieLiang
 *
 */
public class ValueOptUtils {

	private static final String TAG = "Util.ValueOptUtils";
	
	/**
	 * Generate a random string.
	 * 
	 * @param length
	 * @param src
	 * @return
	 */
	public static final String genRandomString(int length, String src) {
		if (Util.isNullOrNil(src) || length <= 0) {
			Log.e(TAG, "Target string length(%d) or source string(%s) is illegaled.", length, src);
			return null;
		}
		int count = src.length();
		int index;
		Random random = new Random();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; i++) {
			index = random.nextInt(count);
			builder.append(src.charAt(index));
		}
		return builder.toString();
	}

	/**
	 * Generate a random string base on
	 * 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'.
	 * 
	 * @param length
	 * @return
	 * 
	 * @see #genRandomString(int, String)
	 */
	public static final String genRandomString(int length) {
		return genRandomString(length, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
	}
	
	/**
	 * Generate a random JavaScript variable string.
	 * 
	 * @param length
	 * @return
	 */
	public static final String genRandomJsVariable(int length) {
		String str = "_$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		int count = str.length();
		int index;
		Random random = new Random();
		StringBuilder builder = new StringBuilder();
		index = random.nextInt(count - 10);
		builder.append(str.charAt(index));
		for (int i = 1; i < length; i++) {
			index = random.nextInt(count);
			builder.append(str.charAt(index));
		}
		return builder.toString();
	}
	
	/**
	 * Returns a pseudo-random uniformly distributed int in the range [min, max].
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static final int getRandomInt(int min, int max) {
		if (max < min) {
			return min;
		}
		Random random = new Random();
		return random.nextInt(max - min + 2) + min;
	}
	
	private static final char[] HEX_SEEDS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * Convert bytes to hex.
	 * 
	 * @param bytes
	 * @return
	 */
	public static String bytes2Hex(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		int len = bytes.length;
		char[] str = new char[len * 2];
		int k = 0;
		for (int i = 0; i < len; i++) {
			byte b = bytes[i];
			str[k++] = HEX_SEEDS[b >>> 4 & 0x0f];
			str[k++] = HEX_SEEDS[b & 0x0f];
		}
		return new String(str);
	}

	/**
	 * Convert integer to hex string. Example : 0x123489ef to "123489ef".
	 * 
	 * @param i
	 * @return
	 */
	public static String int2Hex(int i) {
		StringBuilder builder = new StringBuilder();
		builder.append(HEX_SEEDS[(i >> 28) & 0x0f]);
		builder.append(HEX_SEEDS[(i >> 24) & 0x0f]);
		builder.append(HEX_SEEDS[(i >> 20) & 0x0f]);
		builder.append(HEX_SEEDS[(i >> 16) & 0x0f]);
		builder.append(HEX_SEEDS[(i >> 12) & 0x0f]);
		builder.append(HEX_SEEDS[(i >> 8) & 0x0f]);
		builder.append(HEX_SEEDS[(i >> 4) & 0x0f]);
		builder.append(HEX_SEEDS[i & 0x0f]);
		return builder.toString();
	}

	/**
	 * Parse hex string to a integer value. If the given string are invalid Hex
	 * string that 0 will be return.
	 * 
	 * @param hex
	 * @return
	 */
	public static int hex2Int(String hex) {
		if (hex == null || hex.length() < 8) {
			return 0;
		}
		// To lower case
		String hexStr = hex.substring(0, 8).toLowerCase();
		int r = 0;
		int offset = 0;
		for (int i = 0; i < hexStr.length(); i++) {
			char c = hexStr.charAt(0);
			int value = c - '0';
			r <<= offset;
			if (value >= 0 && value <= 9) {
				r |= (value & 0x0f);
			} else {
				value = c - 'a';
				if (value >= 0 && value <= 5) {
					r |= ((value + 10) & 0x0f);
				} else {
					return 0;
				}
			}
			offset += 4;
		}
		return r;
	}

	/**
	 * Convert long to Hex string
	 * 
	 * @param l
	 * @return
	 */
	public static String long2Hex(long l) {
		StringBuilder builder = new StringBuilder();
		builder.append(HEX_SEEDS[(int) ((l >> 60) & 0x0f)]);
		builder.append(HEX_SEEDS[(int) ((l >> 56) & 0x0f)]);
		builder.append(HEX_SEEDS[(int) ((l >> 52) & 0x0f)]);
		builder.append(HEX_SEEDS[(int) ((l >> 48) & 0x0f)]);
		builder.append(HEX_SEEDS[(int) ((l >> 44) & 0x0f)]);
		builder.append(HEX_SEEDS[(int) ((l >> 40) & 0x0f)]);
		builder.append(HEX_SEEDS[(int) ((l >> 36) & 0x0f)]);
		builder.append(HEX_SEEDS[(int) ((l >> 32) & 0x0f)]);
		builder.append(HEX_SEEDS[(int) ((l >> 28) & 0x0f)]);
		builder.append(HEX_SEEDS[(int) ((l >> 24) & 0x0f)]);
		builder.append(HEX_SEEDS[(int) ((l >> 20) & 0x0f)]);
		builder.append(HEX_SEEDS[(int) ((l >> 16) & 0x0f)]);
		builder.append(HEX_SEEDS[(int) ((l >> 12) & 0x0f)]);
		builder.append(HEX_SEEDS[(int) ((l >> 8) & 0x0f)]);
		builder.append(HEX_SEEDS[(int) ((l >> 4) & 0x0f)]);
		builder.append(HEX_SEEDS[(int) (l & 0x0f)]);
		return builder.toString();
	}

	/**
	 * Convert Hex string to a long value. If the given string are invalid Hex
	 * string that 0 will be return.
	 * 
	 * @param hex
	 * @return
	 */
	public static long hex2Long(String hex) {
		if (hex == null || hex.length() < 16) {
			return 0;
		}
		// To lower case
		String hexStr = hex.substring(0, 16).toLowerCase();
		long r = 0;
		int offset = 0;
		for (int i = 0; i < hexStr.length(); i++) {
			char c = hexStr.charAt(0);
			int value = c - '0';
			r <<= offset;
			if (value >= 0 && value <= 9) {
				r |= (value & 0x0f);
			} else {
				value = c - 'a';
				if (value >= 0 && value <= 5) {
					r |= ((value + 10) & 0x0f);
				} else {
					return 0;
				}
			}
			offset += 4;
		}
		return r;
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] string2Bytes(String str) {
		byte[] result = null;
		if (str != null) {
			int length = str.length();
			result = new byte[length << 1];
			for (int i = 0, index = 0; i < length; i++) {
				char c = str.charAt(i);
				result[index++] = (byte) ((c >> 8) & 0xFF);
				result[index++] = (byte) (c & 0xFF);
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param builder
	 * @param fromStr
	 * @param toStr
	 */
	public static void replaceAll(StringBuilder builder, String fromStr, String toStr) {
		if (builder == null || Util.isNullOrNil(fromStr)) {
			Log.e(TAG, "builder or fromStr is null or nil.");
			return;
		}
		int index = -1;
		while ((index = builder.indexOf(fromStr)) != -1) {
			builder.replace(index, index + fromStr.length(), toStr);
		}
	}

	public static final String DEFAULT_SEPARATOR = ",";

	/**
	 * Chain an string array.The default separator is {@link #DEFAULT_SEPARATOR}.
	 * 
	 * @param name
	 * @return
	 * 
	 * @see #chain(String, String...)
	 */
	public static String chainWithDefSeparator(String... name) {
		return chain(DEFAULT_SEPARATOR, name);
	}

	/**
	 * Chain an string array with the given separator.
	 * 
	 * @param separator
	 * @param name
	 * @return
	 * 
	 * @see #chainWithDefSeparator(String...)
	 * @see #join(String, Object...)
	 */
	public static String chain(String separator, String... name) {
		return join(separator, (Object[]) name);
	}

	/**
	 * Join the object array with the given separator to a string.
	 * 
	 * @param separator
	 * @param name
	 * @return
	 */
	public static String join(String separator, Object...name) {
		if (name == null || name.length == 0) {
			return null;
		}
		final String sep = Util.nullAsNil(separator);
		StringBuilder builder = new StringBuilder();
		builder.append(name[0]);
		for (int i = 1; i < name.length; i++) {
			builder.append(sep);
			builder.append(name[i]);
		}
		return builder.toString();
	}
	
	/**
	 * Unchain the String to an string array.The default separator is ','.
	 * 
	 * @param chain
	 * @return
	 */
	public static String[] unchain(String chain) {
		return unchain(chain, DEFAULT_SEPARATOR);
	}

	/**
	 * Unchain the String to an string array with the given separator.
	 * 
	 * @param separator
	 * @param chain
	 * @return
	 */
	public static String[] unchain(String separator, String chain) {
		if (Util.isNullOrNil(chain) || Util.isNullOrNil(separator)) {
			return null;
		}
		String[] r = chain.split(separator);
		return r;
	}

	/**
	 * Returns a localized formatted string, using the supplied format and
	 * arguments, using the user's default locale.
	 * 
	 * @param format
	 * @param args
	 * @return
	 * @see String#format(String, Object...)
	 */
	public static String format(String format, Object... args) {
		String msg = format;
		if (!Util.isNullOrNil(format)) {
			msg = (args == null || args.length == 0) ? format : String.format(format, args);
		}
		return msg;
	}

	/**
	 * 
	 * @param map
	 * @param clazz
	 * @return
	 */
	public static boolean isMatchKeyType(Map<Object, Object> map, Class<?> clazz) {
		if (map == null || map.isEmpty()) {
			return false;
		}
		for (Object key : map.keySet()) {
			if (key.getClass() != clazz) {
				return false;
			}
		}
		return true;
	}
}
