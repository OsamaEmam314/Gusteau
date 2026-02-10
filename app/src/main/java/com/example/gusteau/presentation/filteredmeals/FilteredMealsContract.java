package com.example.gusteau.presentation.filteredmeals;

import com.example.gusteau.data.model.Meal;

import java.util.List;

public interface FilteredMealsContract {

    interface View {
        void showLoading();

        void hideLoading();

        void showError(String message);

        void showMeals(List<Meal> meals);

        void setToolbarTitle(String title);

        void setResultsCount(int count);

        void showEmptyState();

        void hideEmptyState();

        void navigateToMealDetails();


        void updateMealFavoriteStatus(int position, boolean isFavorite);

        void showGuestModeMessage();
    }

    interface Presenter {
        void loadFilteredMeals();

        void onDestroy();

        void onMealClick(Meal meal);

        void onFavoriteClick(Meal meal, int position);
    }
}