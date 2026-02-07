package com.example.gusteau.data.authentication;

import android.content.Context;

import com.example.gusteau.data.authentication.datasource.local.SharedPrefrenceLocalSource;
import com.example.gusteau.data.authentication.datasource.remote.FirebaseDataSource;
import com.example.gusteau.data.model.User;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

import io.reactivex.rxjava3.core.Completable;
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
                .doOnSuccess(localDataSource::saveUserToPreferences);
    }

    public Single<User> signInWithGoogle(GoogleIdTokenCredential credential) {
        return firebaseDataSource.signInWithGoogle(credential)
                .doOnSuccess(localDataSource::saveUserToPreferences);
    }
    public Single<User> signInWithEmail(String email, String password) {
        return firebaseDataSource.signInWithEmail(email, password)
                .doOnSuccess(localDataSource::saveUserToPreferences);
    }
    public Completable logout() {
        return firebaseDataSource.logout()
                .doOnComplete(localDataSource::clearUserPreferences);
    }
    public Single<User> getCurrentUser() {
        return firebaseDataSource.getCurrentUser()
                .onErrorResumeNext(throwable -> {
                    User cachedUser = localDataSource.getUserFromPreferences();
                    if (cachedUser != null) {
                        return Single.just(cachedUser);
                    } else {
                        return Single.error(throwable);
                    }
                });
    }
    public void saveUserToPreferences(User user) {
        localDataSource.saveUserToPreferences(user);
    }


    public boolean isGuestMode() {
        return localDataSource.getUserFromPreferences().isGuest();

    }
    public boolean isUserLoggedIn() {
        return localDataSource.isUserLoggedIn();
    }
}