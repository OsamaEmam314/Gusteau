package com.example.gusteau.presentation.register;

import com.example.gusteau.data.model.User;

public interface RegisterContract {
    interface View {
        void showLoading();
        void hideLoading();
        void showError(String message);
        void showNameError(String error);
        void showEmailError(String error);
        void showPasswordError(String error);
        void showConfirmPasswordError(String error);
        void navigateToLogin();
        void clearErrors();
    }
    interface Presenter {
        void signUpWithEmail(String name, String email, String password, String confirmPassword);
        void navigateToLogin();
        void onDestroy();
    }

}
