package com.example.gusteau.data.meals.datasource.remote;

import com.example.gusteau.data.model.Category;
import com.example.gusteau.data.model.Ingredients;
import com.example.gusteau.data.model.Meal;
import com.example.gusteau.data.network.MealClient;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class RemoteMealDataSource {
    private MealApiService mealApiService;

    public RemoteMealDataSource() {
        this.mealApiService = MealClient.getApiServices();
    }
    public Single<Meal> getRandomMeal() {
        return mealApiService.getRandomMeal();
    }
    public Single<List<Category>> getAllCategories() {
        return mealApiService.getAllCategories();
    }
    public Single<List<Ingredients>> getAllIngredients() {
        return mealApiService.getAllIngredients();
    }
    public Single<List<String>> getAllAreas() {
        return mealApiService.getAllAreas();
    }

    public Single<List<Meal>> filterByCategory(String category) {
        return mealApiService.filterByCategory(category);
    }
    public Single<List<Meal>> filterByArea(String area) {
        return mealApiService.filterByArea(area);
    }
    public Single<List<Meal>> filterByIngredient(String ingredient) {
        return mealApiService.filterByIngredient(ingredient);
    }
    public Single<List<Meal>> searchMealByName(String name) {
        return mealApiService.searchMealByName(name);
    }
    public Single<Meal> getMealById(String id) {
        return mealApiService.getMealById(id);
    }

    public Single<List<Meal>> getMealsByFirstLetter(String letter) {
        return mealApiService.getMealsByFirstLetter(letter);
    }

}
