package com.shinleeholdings.coverstar.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.MyApplication;

public class SharedPreferenceHelper {
    public static final String LOGIN_ID = "LOGIN_ID";
    public static final String LOGIN_PW = "LOGIN_PW";
    public static final String PUSH_ID = "PUSH_ID";
    public static final String ALARM_IS_OFF = "ALARM_IS_OFF";
    public static final String LOGIN_USER_DATA = "LOGIN_USER_DATA";
    public static final String DISPLAY_WIDTH = "DISPLAY_WIDTH";
    public static final String LANGUAGE = "LANGUAGE";

    private static volatile SharedPreferenceHelper instance;
    private SharedPreferences mShared;
    private SharedPreferences.Editor mEditor;

    private static Gson GSON = new Gson();

    public static SharedPreferenceHelper getInstance() {
        return getInstance(MyApplication.getContext());
    }

    public static SharedPreferenceHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferenceHelper(context);
        }

        return instance;
    }

    private SharedPreferenceHelper(Context context) {
        if (mShared == null) {
            String PREFERENCENAME = "PREF_" + AppConstants.APP_NAME;
            mShared = context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
            mEditor = mShared.edit();
        }
    }

    public void removeSharedPreference(@NonNull String key) {
        mEditor.remove(key);
        mEditor.commit();
    }

    public synchronized boolean setSharedPreference(@NonNull String key, String value) {
        mEditor.putString(key, value);
        return mEditor.commit();
    }

    public synchronized boolean setSharedPreference(@NonNull String key, boolean value) {
        mEditor.putBoolean(key, value);
        return mEditor.commit();
    }

    public synchronized boolean setSharedPreference(@NonNull String key, int value) {
        mEditor.putInt(key, value);
        return mEditor.commit();
    }

    public synchronized boolean setSharedPreference(@NonNull String key, long value) {
        mEditor.putLong(key, value);
        return mEditor.commit();
    }

    public synchronized String getStringPreference(@NonNull String key) {
        return mShared.getString(key, "");
    }

    public long getLongPreference(@NonNull String key) {
        return mShared.getLong(key, 0);
    }

    public boolean getBooleanPreference(@NonNull String key) {
        return mShared.getBoolean(key, false);
    }

    public int getIntPreference(@NonNull String key) {
        return mShared.getInt(key, 0);
    }

    public void putObject(@NonNull String key, Object object) {
        String objectString = "";
        if (object != null) {
            objectString = GSON.toJson(object);
        }

        mEditor.putString(key, objectString);
        mEditor.commit();
    }

    public <T> T getObject(@NonNull String key, Class<T> a) {
        try {
            String gson = mShared.getString(key, null);
            return GSON.fromJson(gson, a);
        } catch (Exception e) {
            DebugLogger.e("getObject error : " + e.getMessage());
            return null;
        }
    }
}
