package com.example.gusteau.data.authentication.datasource.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.gusteau.data.model.User;

public class SharedPrefrenceLocalSource {
    private static final String PREFS_NAME = "gusteau_prefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_IS_GUEST = "is_guest";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private final SharedPreferences sharedPreferences;

    public SharedPrefrenceLocalSource(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public boolean isGuest() {
        return sharedPreferences.getBoolean(KEY_IS_GUEST, false);
    }

    public User getUserFromPreferences() {
        boolean isGuest = sharedPreferences.getBoolean(KEY_IS_GUEST, false);

        if (isGuest) {
            return new User("guest_id", "Guest", "", true);
        }

        String uid = sharedPreferences.getString(KEY_USER_ID, null);
        if (uid == null) {
            return null;
        }

        String name = sharedPreferences.getString(KEY_USER_NAME, "");
        String email = sharedPreferences.getString(KEY_USER_EMAIL, "");
        return new User(uid, name, email, false);
    }

    public void saveUserToPreferences(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, user.getUid());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putBoolean(KEY_IS_GUEST, user.isGuest());
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public void setGuestMode() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_GUEST, true);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_NAME);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_USER_EMAIL);
        editor.apply();
    }

    public void clearUserPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}