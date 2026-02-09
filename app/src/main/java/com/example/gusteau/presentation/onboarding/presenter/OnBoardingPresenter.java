package com.example.gusteau.presentation.onboarding.presenter;

import android.content.Context;
import android.util.Log;

import com.example.gusteau.R;
import com.example.gusteau.data.authentication.AuthRepository;
import com.example.gusteau.presentation.onboarding.OnBoardingContract;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OnBoardingPresenter implements OnBoardingContract.Presenter {

    private static final String TAG = "OnboardingPresenter";

    private final OnBoardingContract.View view;
    private final AuthRepository authRepository;
    private final CompositeDisposable disposables;

    private int totalPages = 3;
    private int currentPage = 0;

    public OnBoardingPresenter(OnBoardingContract.View view, Context context) {
        this.view = view;
        this.authRepository = new AuthRepository(context);
        this.disposables = new CompositeDisposable();
    }

    @Override
    public void loadOnboardingPages() {
        List<OnBoardingContract.OnboardingPage> pages = new ArrayList<>();

        pages.add(new OnBoardingContract.OnboardingPage(
                R.drawable.ic_search,
                "Discover Delicious Recipes",
                "Explore thousands of recipes from around the world. Find your next favorite meal with our smart search.",
                R.color.primary
        ));

        pages.add(new OnBoardingContract.OnboardingPage(
                R.drawable.ic_calendar,
                "Plan Your Week",
                "Organize your meals for the entire week. Never wonder \"what's for dinner?\" again.",
                R.color.primary
        ));

        pages.add(new OnBoardingContract.OnboardingPage(
                R.drawable.ic_favorite,
                "Save Your Favorites",
                "Bookmark your favorite recipes and access them anytime, even offline.",
                R.color.primary
        ));

        totalPages = pages.size();
        view.showOnboardingPages(pages);
        view.updatePageIndicator(0);
        view.showSkipButton();
        view.hideGetStartedButton();
    }

    @Override
    public void onPageChanged(int position) {
        currentPage = position;

        view.updatePageIndicator(position);
        if (position == totalPages - 1) {
            view.hideSkipButton();
            view.showGetStartedButton();
        } else {
            view.showSkipButton();
            view.hideGetStartedButton();
        }
    }

    @Override
    public void onSkipClicked() {
        completeOnboarding();
    }

    @Override
    public void onGetStartedClicked() {
        completeOnboarding();
    }

    private void completeOnboarding() {
        view.navigateToHome();
    }

    @Override
    public void onDestroy() {
        disposables.clear();
    }
}