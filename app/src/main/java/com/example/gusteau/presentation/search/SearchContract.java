package com.example.gusteau.presentation.search;


import com.example.gusteau.data.model.Category;
import com.example.gusteau.data.model.Country;
import com.example.gusteau.data.model.Ingredients;
import com.example.gusteau.data.model.Meal;

import java.util.List;

public interface SearchContract {

    interface View {
        void uncheckAllChips();

        boolean areAnyChipsChecked();

        void showSearchResults(List<Meal> meals);
        void showCategories(List<Category> categories);
        void showCountries(List<Country> countries);
        void showIngredients(List<Ingredients> ingredients);
        void showEmptyState();
        void hideEmptyState();
        void showLoading();
        void hideLoading();
        void showError(String message);
        void updateResultsHeader(String text);
        void updateMealFavoriteStatus(int position, boolean isFavorite);
        void navigateToMealDetails();
        void showGuestModeMessage();
        void clearSearchQuery();
        void hideFilters();

        void showNoInternetDialog();
    }

    interface Presenter {
        void loadInitialData();
        void onSearchQuerySubmit(String query);
        void onSearchQueryChange(String query);
        void onCategoriesChipClick();
        void onCountryChipClick();
        void onIngredientsChipClick();
        void onCategorySelected(String category);
        void onCountrySelected(String country);
        void onIngredientSelected(String ingredient);
        void onMealClick(Meal meal);
        void onFavoriteClick(Meal meal, int position);
        void onDestroy();
    }
}