package com.example.gusteau.data.Authentication;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.example.gusteau.data.model.User;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
public class AuthRepository {
    private static final String PREFS_NAME = "gusteau_prefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_IS_GUEST = "is_guest";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private final FirebaseAuth firebaseAuth;
    private final SharedPreferences sharedPreferences;

    public AuthRepository(Context context) {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
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
    public Single<User> signInWithGoogle(AuthCredential credential) {
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
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
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
    public User getUserFromPreferences() {
        String uid = sharedPreferences.getString(KEY_USER_ID, null);
        if (uid == null) {
            return null;
        }
        String name = sharedPreferences.getString(KEY_USER_NAME, "");
        String email = sharedPreferences.getString(KEY_USER_EMAIL, "");
        boolean isGuest = sharedPreferences.getBoolean(KEY_IS_GUEST, false);

        return new User(uid, name, email, isGuest);
    }
    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
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
