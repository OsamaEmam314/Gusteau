package com.example.gusteau.data.authentication.datasource.remote;


import com.google.firebase.auth.FirebaseAuth;


import com.example.gusteau.data.model.User;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class FirebaseDataSource  {
    private final FirebaseAuth firebaseAuth;

    public FirebaseDataSource() {
        this.firebaseAuth = FirebaseAuth.getInstance();
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
                                emitter.onSuccess(mapFirebaseUserToUser(firebaseUser));
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
                                emitter.onSuccess(mapFirebaseUserToUser(firebaseUser));
                            } else {
                                emitter.onError(new Exception("User does not exist"));
                            }
                        })
                        .addOnFailureListener(emitter::onError)
        );
    }

    public Single<User> getCurrentUser() {
        return Single.create(emitter -> {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                emitter.onSuccess(mapFirebaseUserToUser(firebaseUser));
            } else {
                emitter.onError(new Exception("No user logged in"));
            }
        });
    }

    public Completable logout() {
        return Completable.create(emitter -> {
            firebaseAuth.signOut();
            emitter.onComplete();
        });
    }

    private User mapFirebaseUserToUser(FirebaseUser firebaseUser) {
        String uid = firebaseUser.getUid();
        String name = firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "";
        String email = firebaseUser.getEmail() != null ? firebaseUser.getEmail() : "";
        return new User(uid, name, email, false);
    }
}