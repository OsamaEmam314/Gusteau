package com.example.gusteau.data.Authentication;

import android.content.Context;

import com.example.gusteau.data.Authentication.local.SharedPrefrenceLocalSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.example.gusteau.data.model.User;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthRepository {
    private final FirebaseAuth firebaseAuth;
    SharedPrefrenceLocalSource sharedPrefrenceLocalSource;

    public AuthRepository(Context context) {
        this.firebaseAuth = FirebaseAuth.getInstance();
        sharedPrefrenceLocalSource = new SharedPrefrenceLocalSource(context);
    }
    public Single<User> registerWithEmail(String name, String email, String password) {
        return Single.create(emitter ->
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> {
                            FirebaseUser firebaseUser = authResult.getUser();
                            if (firebaseUser != null) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();
                                firebaseUser.updateProfile(profileUpdates)
                                        .addOnSuccessListener(aVoid -> {
                                            User user = mapFirebaseUserToUser(firebaseUser);
                                            user.setName(name);
                                            saveUserToPreferences(user);
                                            emitter.onSuccess(user);
                                        })
                                        .addOnFailureListener(emitter::onError);
                            } else {
                                emitter.onError(new Exception("User is null"));
                            }
                        })
                        .addOnFailureListener(e -> {
                            if (e instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                                emitter.onError(new Exception("This email is already registered. Please log in instead."));
                            } else {
                                emitter.onError(e);
                            }
                        })
        );
    }
    public Single<User> signInWithGoogle(GoogleIdTokenCredential googleIdTokenCredential) {
        AuthCredential credential = GoogleAuthProvider.getCredential(googleIdTokenCredential.getIdToken(), null);
        return Single.create(emitter ->
                firebaseAuth.signInWithCredential(credential)
                        .addOnSuccessListener(authResult -> {
                            FirebaseUser firebaseUser = authResult.getUser();
                            if (firebaseUser != null) {
                                User user = mapFirebaseUserToUser(firebaseUser);
                                saveUserToPreferences(user);
                                emitter.onSuccess(user);
                            } else {
                                emitter.onError(new Exception("Google registration failed: User is null"));
                            }
                        })
                        .addOnFailureListener(e -> {
                            if (e instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                                emitter.onError(new Exception("An account with this email already exists using a different sign-in method."));
                            } else {
                                emitter.onError(e);
                            }
                        })
        );
    }
    public Single<User> signInWithEmail(String email, String password) {
        return Single.create(emitter ->
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> {
                            FirebaseUser firebaseUser = authResult.getUser();
                            if (firebaseUser != null) {
                                User user = mapFirebaseUserToUser(firebaseUser);
                                saveUserToPreferences(user);
                                emitter.onSuccess(user);
                            } else {
                                emitter.onError(new Exception("User does not exist"));
                            }
                        })
                        .addOnFailureListener(emitter::onError)
        );
    }
    public Completable logout() {
        return Completable.create(emitter -> {
            firebaseAuth.signOut();
            clearUserPreferences();
            emitter.onComplete();
        });
    }
    private User mapFirebaseUserToUser(FirebaseUser firebaseUser) {
        String uid = firebaseUser.getUid();
        String name = firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "";
        String email = firebaseUser.getEmail() != null ? firebaseUser.getEmail() : "";
        return new User(uid, name, email,false);
    }
    public void clearUserPreferences() {
        sharedPrefrenceLocalSource.clearUserPreferences();
    }
    public void saveUserToPreferences(User user) {
        sharedPrefrenceLocalSource.saveUserToPreferences(user);
    }
    public User getUserFromPreferences() {
        return sharedPrefrenceLocalSource.getUserFromPreferences();
    }
    public boolean isUserLoggedIn() {
        return sharedPrefrenceLocalSource.isUserLoggedIn()
                && firebaseAuth.getCurrentUser() != null;
    }

    public Single<User> getCurrentUser() {
        return Single.create(emitter -> {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                User user = mapFirebaseUserToUser(firebaseUser);
                emitter.onSuccess(user);
            } else {
                User cachedUser = getUserFromPreferences();
                if (cachedUser != null) {
                    emitter.onSuccess(cachedUser);
                } else {
                    emitter.onError(new Exception("No user logged in"));
                }
            }
        });
    }

}
