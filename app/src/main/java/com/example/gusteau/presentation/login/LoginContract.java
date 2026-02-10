package com.example.gusteau.presentation.login;

import com.example.gusteau.data.model.User;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;

public interface LoginContract {
    interface View {
        void showLoading();

        void hideLoading();

        void showError(String message);

        void showEmailError(String error);

        void showPasswordError(String error);

        void navigateToRegister();

        void navigateToHome();

        void clearErrors();

        void navigateToOnBoarding();
    }

    interface Presenter {
        void logInWithEmail(String email, String password);

        void logInWithGoogle(GoogleIdTokenCredential credential);

        void navigateToRegister();

        void navigateToHome();

        void onDestroy();

        void guestLogin();
    }
}
