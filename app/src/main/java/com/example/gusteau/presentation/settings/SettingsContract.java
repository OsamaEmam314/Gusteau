package com.example.gusteau.presentation.settings;

public interface SettingsContract {
    interface View {

        void showAboutDialog();

        void navigateToLogin();

        void showError(String message);

        void showLoading();

        void hideLoading();

        void setUserData(String name, String email);
    }

    interface Presenter {

        void logout();

        void backingUp();

        void about();

        void loadUserData();

        void onDestroy();
    }
}
