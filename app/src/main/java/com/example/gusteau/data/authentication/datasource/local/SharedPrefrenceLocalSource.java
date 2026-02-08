package com.example.gusteau.data.authentication.datasource.local;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.gusteau.data.model.User;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;


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

    public Single<Boolean> isUserLoggedIn() {
        return Single.fromCallable(() ->
                sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
        );
    }

    public Single<Boolean> isGuest() {
        return Single.fromCallable(() ->
                sharedPreferences.getBoolean(KEY_IS_GUEST, false)
        );
    }
    public Maybe<User> getUserFromPreferences() {
        return Maybe.fromCallable(() -> {
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
        });
    }

    public Completable saveUserToPreferences(User user) {
        return Completable.fromAction(() -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_USER_ID, user.getUid());
            editor.putString(KEY_USER_NAME, user.getName());
            editor.putString(KEY_USER_EMAIL, user.getEmail());
            editor.putBoolean(KEY_IS_GUEST, user.isGuest());
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.commit();
        });
    }

    public Completable setGuestMode() {
        return Completable.fromAction(() -> {
            sharedPreferences.edit()
                    .clear()
                    .putBoolean(KEY_IS_GUEST, true)
                    .putBoolean(KEY_IS_LOGGED_IN, true)
                    .commit();
        });
    }

    public Completable clearUserPreferences() {
        return Completable.fromAction(() -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();
        });
    }
}