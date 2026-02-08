package com.example.gusteau.presentation.home;


import com.example.gusteau.data.model.Category;
import com.example.gusteau.data.model.Country;
import com.example.gusteau.data.model.Ingredients;
import com.example.gusteau.data.model.Meal;
import java.util.List;

public interface HomeContract {

    interface View {
        void setGreeting(String greeting);

        void showLoading();
        void hideLoading();
        void showError(String message);
        void showGuestModeMessage();
        void showMealOfDay(Meal meal);
        void updateMealOfDayFavoriteStatus(boolean isFavorite);
        void showCategories(List<Category> categories);
        void showCountries(List<Country> areas);
        void showIngredients(List<Ingredients> ingredients);

        void navigateToMealDetails();

        void navigateToFIlteredMeals();
        void setUserName(String userName);
    }

    interface Presenter {
        void loadMealOfDay();
        void loadCategories();
        void loadCountries();
        void loadIngredients();
        void getUserName();

        void onMealOfDayClick();
        void onMealOfDayFavoriteClick(Meal meal);
        void onCategoryClick(String category);
        void onCountryClick(String country);
        void onIngredientClick(String ingredient);

        void onDestroy();
    }
}