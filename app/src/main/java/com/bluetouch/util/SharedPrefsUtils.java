package com.bluetouch.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.HashSet;
import java.util.Set;

///////////////////////////////////////////
///sheredpreference를 이용하기위한 함수////
///////////////////////////////////////////
public class SharedPrefsUtils { ///////////////////// 위에서도 설명했지만 preference를 간단하게 이용하기 위해 만든함수.
    private SharedPrefsUtils() {    //생성자
    }

    public static String getStringPreference(Context context, String key) {                                     ///스트링값 얻어오기 위한 함수
        String value = null;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getString(key, null);
        }
        return value;
    }

    public static boolean setStringPreference(Context context, String key, String value) {              ///스트링값 설정하기 위한 함수
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, value);
            return editor.commit();
        }
        return false;
    }

    public static int getIntegerPreference(Context context, String key, int defaultValue) {              ///인트값 불러오기 위한 함수
        int value = defaultValue;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getInt(key, defaultValue);
        }
        return value;
    }

    public static boolean setIntegerPreference(Context context, String key, int value) {                ///인트값 설정하기 위한 함수
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(key, value);
            return editor.commit();
        }
        return false;
    }

    public static boolean setLoginPreference(Context context, String id, String password) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null && !TextUtils.isEmpty(id)) {
            SharedPreferences.Editor editor = preferences.edit();
            Set<String> myArrayList= new HashSet<String>();
            myArrayList.add(id);
            myArrayList.add(password);

            editor.putStringSet(id, myArrayList);

            return editor.commit();
        }
        return false;

    }
    public static Set<String> getLoginPreference(Context context, String id) {                                     ///스트링값 얻어오기 위한 함수
        Set<String> myArrayList= new HashSet<String>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            myArrayList = preferences.getStringSet(id,null);
        }
        return myArrayList;
    }
}
