package com.bemetoy.bp.sdk.utils;

import java.util.Map;

/**
 * @author AlbieLiang
 *
 */
public class GetValueUtils {

	/**
	 * Get int value from {@link Map} with the given key, the default value will be used if the value do not do not exists.
	 * 
	 * @param map
	 * @param key
	 * @param def
	 * @return
	 */
	public static final int getInt(Map<String, Object> map, String key, int def) {
		int value = def;
		if (map != null && !Util.isNullOrNil(key)) {
			Object o = map.get(key);
			if (o instanceof Integer) {
				value = (Integer) o;
			}
		}
		return value;
	}
	
	/**
	 * Get String value from {@link Map} with the given key, the default value will be used if the value do not do not exists.
	 * 
	 * @param map
	 * @param key
	 * @param def
	 * @return
	 */
	public static final String getString(Map<String, Object> map, String key, String def) {
		String value = def;
		if (map != null && !Util.isNullOrNil(key)) {
			Object o = map.get(key);
			if (o instanceof String) {
				value = (String) o;
			}
		}
		return value;
	}
	
	/**
	 * Get Long value from {@link Map} with the given key, the default value will be used if the value do not do not exists.
	 * 
	 * @param map
	 * @param key
	 * @param def
	 * @return
	 */
	public static final long getLong(Map<String, Object> map, String key, long def) {
		long value = def;
		if (map != null && !Util.isNullOrNil(key)) {
			Object o = map.get(key);
			if (o instanceof Long) {
				value = (Long) o;
			}
		}
		return value;
	}
	
	/**
	 * Get Double value from {@link Map} with the given key, the default value will be used if the value do not do not exists.
	 * 
	 * @param map
	 * @param key
	 * @param def
	 * @return
	 */
	public static final double getDouble(Map<String, Object> map, String key, double def) {
		double value = def;
		if (map != null && !Util.isNullOrNil(key)) {
			Object o = map.get(key);
			if (o instanceof Double) {
				value = (Double) o;
			}
		}
		return value;
	}

	/**
	 * return a default Integer if source Integer is null.
	 * 
	 * @param value
	 * @param def
	 * @return
	 */
	public static int getInt(Integer value, int def) {
		if (value != null) {
			return value;
		}
		return def;
	}

	/**
	 * Parse the string to a integer ,it will return the default value when the
	 * string is null or nil.
	 * 
	 * @param value
	 * @param def
	 * @return
	 */
	public static int parseInt(String value, int def) {
		if (!Util.isNullOrNil(value)) {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
			}
		}
		return def;
	}

	/**
	 * return a default string if source string is null.
	 * 
	 * @param str
	 * @param def
	 * @return
	 */
	public static String getString(String str, String def) {
		if (str != null) {
			return str;
		}
		return def;
	}

}
