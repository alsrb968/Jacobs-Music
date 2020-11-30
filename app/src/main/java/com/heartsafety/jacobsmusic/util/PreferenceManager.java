package com.heartsafety.jacobsmusic.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 데이터 저장 및 로드 클래스
 */
public class PreferenceManager {
    public static final String PREFERENCES_NAME = "last_memory";

    private static final String DEFAULT_VALUE_STRING = "";
    private static final boolean DEFAULT_VALUE_BOOLEAN = false;
    private static final int DEFAULT_VALUE_INT = -1;
    private static final long DEFAULT_VALUE_LONG = -1L;
    private static final float DEFAULT_VALUE_FLOAT = -1F;

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    /**
     * String 값 저장
     * @param context   Context
     * @param key       Key
     * @param value     String
     */
    public static void setString(Context context, String key, String value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * boolean 값 저장
     * @param context   Context
     * @param key       Key
     * @param value     boolean
     */
    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * int 값 저장
     * @param context   Context
     * @param key       Key
     * @param value     int
     */
    public static void setInt(Context context, String key, int value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * long 값 저장
     * @param context   Context
     * @param key       Key
     * @param value     long
     */
    public static void setLong(Context context, String key, long value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * float 값 저장
     * @param context   Context
     * @param key       Key
     * @param value     float
     */
    public static void setFloat(Context context, String key, float value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    /**
     * String 값 로드
     * @param context   Context
     * @param key       Key
     * @return          String
     */
    public static String getString(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        return prefs.getString(key, DEFAULT_VALUE_STRING);
    }

    /**
     * boolean 값 로드
     * @param context   Context
     * @param key       Key
     * @return          boolean
     */
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        return prefs.getBoolean(key, DEFAULT_VALUE_BOOLEAN);
    }

    /**
     * int 값 로드
     * @param context   Context
     * @param key       Key
     * @return          int
     */
    public static int getInt(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        return prefs.getInt(key, DEFAULT_VALUE_INT);
    }

    /**
     * long 값 로드
     * @param context   Context
     * @param key       Key
     * @return          long
     */
    public static long getLong(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        return prefs.getLong(key, DEFAULT_VALUE_LONG);
    }

    /**
     * float 값 로드
     * @param context   Context
     * @param key       Key
     * @return          float
     */
    public static float getFloat(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        return prefs.getFloat(key, DEFAULT_VALUE_FLOAT);
    }

    /**
     * 키 값 삭제
     * @param context   Context
     * @param key       Key
     */
    public static void removeKey(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.remove(key);
        edit.apply();
    }

    /**
     * 모든 저장 데이터 삭제
     * @param context   Context
     */
    public static void clear(Context context) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.apply();
    }
}
