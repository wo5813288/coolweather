package com.lin.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {
    public static final String WEATHER_KEY = "weather";
    private static final String SHARED_NAME ="m_sharedPreference";
    public static void setSharedPreferenceString(Context context,String key,String content){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_NAME,Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key,content).apply();
    }
    public static String getSharedPreferenceString(Context context,String key,String def){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_NAME,Context.MODE_PRIVATE);
        return  sharedPreferences.getString(key,def);
    }
}
