package com.example.gusteau.presentation.splash;

public interface SplashContract {
    interface View{
        void setIsLoggedIn(boolean isLoggedIn);
    }
    interface Presenter {
        void checkLoggedIn();
        void onDestroy();
    }

}
