package com.example.gusteau.presentation.favorites;

import com.example.gusteau.data.model.Meal;

import java.util.List;

public interface FavoritesContract {

    interface View {
        void showFavorites(List<Meal> favorites);

        void showEmptyState();

        void hideEmptyState();

        void showLoading();

        void hideLoading();

        void showError(String message);

        void updateMealFavoriteStatus(int position, boolean isFavorite);

        void removeMealFromList(int position);

        void navigateToMealDetails();

        void showGuestModeMessage();
    }

    interface Presenter {
        void loadFavorites();

        void onMealClick(Meal meal);

        void onFavoriteClick(Meal meal, int position);

        void onDestroy();
    }
}