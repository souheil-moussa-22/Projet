package com.projetAndroid.projet.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "user_session";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_TYPE = "user_type";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(String username, String userType) {
        prefs.edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_USER_TYPE, userType)
                .apply();
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public String getUserType() {
        return prefs.getString(KEY_USER_TYPE, null);
    }

    public boolean isLoggedIn() {
        return getUsername() != null;
    }

    public void logout(Context context) {
        prefs.edit().clear().apply();
    }
}