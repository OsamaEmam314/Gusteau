package com.example.gusteau.presentation.main;

public interface MainContract {
    interface View{

        void showGuestModeMessage();

        void showError(String failedToCheckUserStatus);
        void setGuestStatus(boolean isGuest);

    }
    interface presenter{
        void checkUserStatus();

        void onDestroy();
    }
}
