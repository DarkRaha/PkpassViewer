package com.dr.util.libs;

import org.json.JSONObject;

/**
 * 
 * @author Verma Rahul
 * 
 */
public class JsonUtil {

	/**
	 * 
	 * @param obj
	 * @param key
	 * @return
	 */
	public static String getString(JSONObject obj, String key) {
		try {
			return obj.getString(key);
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * Try get value of string field of the json object, if no filed than
	 * default value will be returned.
	 * 
	 * @param obj
	 * @param key
	 * @param defaultvalue
	 * @return
	 */
	public static String getString(JSONObject obj, String key,
			String defaultvalue) {
		try {
			return obj.getString(key);
		} catch (Exception e) {

		}
		return defaultvalue;
	}

	/**
	 * Try get value of integer field of the json object, if no filed than
	 * default value will be returned.
	 * 
	 * @param obj
	 * @param key
	 * @param defaultvalue
	 * @return
	 */
	public static int getInt(JSONObject obj, String key, int defaultvalue) {
		try {
			return obj.getInt(key);
		} catch (Exception e) {

		}

		return defaultvalue;
	}

	/**
	 * Try get value of float field of the json object, if no filed than default
	 * value will be returned.
	 * 
	 * @param obj
	 * @param key
	 * @param defaultvalue
	 * @return
	 */
	public static float getFloat(JSONObject obj, String key, float defaultvalue) {
		try {
			return (float) obj.getDouble(key);
		} catch (Exception e) {

		}

		return defaultvalue;
	}

}
