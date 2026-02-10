package com.example.gusteau.data.authentication;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.example.gusteau.data.authentication.datasource.local.SharedPrefrenceLocalSource;
import com.example.gusteau.data.authentication.datasource.remote.FirebaseDataSource;
import com.example.gusteau.data.model.User;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public class AuthRepository {

    private final FirebaseDataSource firebaseDataSource;
    private final SharedPrefrenceLocalSource localDataSource;

    public AuthRepository(Context context) {
        this.firebaseDataSource = new FirebaseDataSource();
        this.localDataSource = new SharedPrefrenceLocalSource(context);
    }

    public Single<User> registerWithEmail(String name, String email, String password) {
        return firebaseDataSource.registerWithEmail(name, email, password)
                .flatMap(user -> localDataSource.saveUserToPreferences(user)
                        .andThen(Single.just(user)));
    }

    public Single<Pair<User, Boolean>> signInWithGoogle(GoogleIdTokenCredential credential) {
        return firebaseDataSource.signInWithGoogle(credential)
                .flatMap(user -> localDataSource.saveUserToPreferences(user.first)
                        .andThen(Single.just(user)));
    }

    public Single<User> signInWithEmail(String email, String password) {
        return firebaseDataSource.signInWithEmail(email, password)
                .flatMap(user -> localDataSource.saveUserToPreferences(user)
                        .andThen(Single.just(user)));
    }

    public Completable logout() {
        return firebaseDataSource.logout()
                .andThen(localDataSource.clearUserPreferences());
    }

    public Completable loginAsGuest() {

        return firebaseDataSource.logout()
                .andThen(localDataSource.clearUserPreferences())
                .andThen(localDataSource.setGuestMode());
    }

    public Single<User> getCurrentUser() {
        return localDataSource.isGuest()
                .flatMap(isGuest -> {
                    if (isGuest) {
                        User guestUser = new User("guest_id", "Guest", "", true);
                        return Single.just(guestUser);
                    } else {
                        return localDataSource.getUserFromPreferences()
                                .switchIfEmpty(Maybe.defer(() -> {
                                    User guestUser = new User("guest_id", "Guest", "", true);
                                    return localDataSource.saveUserToPreferences(guestUser)
                                            .andThen(Maybe.just(guestUser));
                                }))
                                .toSingle();
                    }
                });
    }


    public Single<Boolean> isGuestMode() {
        return localDataSource.isGuest();
    }


    public Single<Boolean> isUserLoggedIn() {
        return localDataSource.isUserLoggedIn();
    }
}