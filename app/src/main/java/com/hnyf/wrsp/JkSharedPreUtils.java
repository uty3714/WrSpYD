package com.hnyf.wrsp;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * 偏好数据存储工具类
 */
public class JkSharedPreUtils {

    private static final String fileName = "jk_sp";//

    private static SharedPreferences getSharedPreferences() {
        return AppGlobals.getApplication()
                .getSharedPreferences(fileName,
                        Context.MODE_PRIVATE);
    }

    /**
     * 清除sp里所有的数据
     */
    public static void clearPreference() {
        SharedPreferences pref = getSharedPreferences();
        pref.edit().clear().apply();
    }
    /**
     * 设置String 类型数据
     * @param key key
     * @param value value
     */
    public static void setPreferenceString(String key, String value) {
        SharedPreferences pref = getSharedPreferences();
        pref.edit().putString(key, value).apply();
    }

    /**
     * 设置String 类型数据
     * @param key key
     * @param defaultValue 默认值
     * @return long
     */
    public static String getPreferenceString(String key,String defaultValue){
        SharedPreferences pref = getSharedPreferences();
        return pref.getString(key, defaultValue);
    }

    /**
     * 设置Integer 类型数据
     * @param key key
     * @param value value
     */
    public static void setPreferenceInteger(String key, int value) {
        SharedPreferences pref = getSharedPreferences();
        pref.edit().putInt(key, value).apply();
    }

    /**
     * 设置Integer 类型数据
     * @param key key
     * @param defaultValue 默认值
     * @return long
     */
    public static int getPreferenceInteger(String key,int defaultValue){
        SharedPreferences pref = getSharedPreferences();
        return pref.getInt(key, defaultValue);
    }

    /**
     * 设置Boolean 类型数据
     * @param key key
     * @param value value
     */
    public static void setPreferenceBoolean(String key, boolean value) {
        SharedPreferences pref = getSharedPreferences();
        pref.edit().putBoolean(key, value).apply();
    }

    /**
     * 获取Boolean 类型数据
     * @param key key
     * @param defaultValue 默认值
     * @return long
     */
    public static boolean getPreferenceBoolean(String key,boolean defaultValue){
        SharedPreferences pref = getSharedPreferences();
        return pref.getBoolean(key, defaultValue);
    }

    /**
     * 设置Float 类型数据
     * @param key key
     * @param value value
     */
    public static void setPreferenceFloat(String key, float value) {
        SharedPreferences pref = getSharedPreferences();
        pref.edit().putFloat(key, value).apply();
    }
    /**
     * 获取Float 类型数据
     * @param key key
     * @param defaultValue 默认值
     * @return long
     */
    public static float getPreferenceFloat(String key,float defaultValue){
        SharedPreferences pref = getSharedPreferences();
        return pref.getFloat(key, defaultValue);
    }

    /**
     * 设置Long 类型数据
     * @param key key
     * @param value value
     */
    public static void setPreferenceLong(String key, long value) {
        SharedPreferences pref = getSharedPreferences();
        pref.edit().putFloat(key, value).apply();
    }

    /**
     * 获取Long类型数据
     * @param key key
     * @param defaultValue 默认值
     * @return long
     */
    public static long getPreferenceLong(String key,long defaultValue){
        SharedPreferences pref = getSharedPreferences();
        return pref.getLong(key, defaultValue);
    }

}
