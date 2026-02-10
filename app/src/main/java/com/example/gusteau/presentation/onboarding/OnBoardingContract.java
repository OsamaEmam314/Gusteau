package com.example.gusteau.presentation.onboarding;

import java.util.List;

public interface OnBoardingContract {

    interface View {
        void showOnboardingPages(List<OnboardingPage> pages);

        void updatePageIndicator(int position);

        void showSkipButton();

        void hideSkipButton();

        void showGetStartedButton();

        void hideGetStartedButton();

        void navigateToHome();
    }

    interface Presenter {
        void loadOnboardingPages();

        void onPageChanged(int position);

        void onSkipClicked();

        void onGetStartedClicked();

        void onDestroy();
    }

    class OnboardingPage {
        private final int imageRes;
        private final String title;
        private final String description;
        private final int backgroundColor;

        public OnboardingPage(int imageRes, String title, String description, int backgroundColor) {
            this.imageRes = imageRes;
            this.title = title;
            this.description = description;
            this.backgroundColor = backgroundColor;
        }

        public int getImageRes() {
            return imageRes;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public int getBackgroundColor() {
            return backgroundColor;
        }
    }
}