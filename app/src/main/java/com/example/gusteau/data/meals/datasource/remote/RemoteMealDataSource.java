package com.example.gusteau.data.meals.datasource.remote;

import com.example.gusteau.data.model.Category;
import com.example.gusteau.data.model.Country;
import com.example.gusteau.data.model.Ingredients;
import com.example.gusteau.data.model.Meal;
import com.example.gusteau.data.model.MealResponse;
import com.example.gusteau.data.network.MealClient;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class RemoteMealDataSource {
    private MealApiService mealApiService;

    public RemoteMealDataSource() {
        this.mealApiService = MealClient.getApiServices();
    }
    public Single<Meal> getRandomMeal() {
        return mealApiService.getRandomMeal()
                .map(response -> response.getMeals().get(0));
    }
    public Single<Meal> getMealById(String id) {
        return mealApiService.getMealById(id)
                .map(response -> response.getMeals().get(0));
    }
    public Single<List<Category>> getAllCategories() {
        return mealApiService.getAllCategories()
                .map(response -> response.getCategories());
    }
    public Single<List<Ingredients>> getAllIngredients() {
        return mealApiService.getAllIngredients()
                .map(response -> response.getIngredients());
    }
    public Single<List<String>> getAllAreas() {
        return mealApiService.getAllAreas()
                .map(response -> {
                    List<String> areas = new ArrayList<>();
                    for(Country country : response.getAreas()){
                        areas.add(country.getName());
                    }
                    return areas;
                });
    }

    public Single<List<Meal>> filterByCategory(String category) {
        return mealApiService.filterByCategory(category)
                .map(response -> response.getMeals());
    }

    public Single<List<Meal>> filterByArea(String area) {
        return mealApiService.filterByArea(area)
                .map(response -> response.getMeals());
    }

    public Single<List<Meal>> filterByIngredient(String ingredient) {
        return mealApiService.filterByIngredient(ingredient)
                .map(response -> response.getMeals());
    }
    public Single<List<Meal>> searchMealByName(String name) {
        return mealApiService.searchMealByName(name)
                .map(response -> response.getMeals());
    }

    public Single<List<Meal>> getMealsByFirstLetter(String letter) {
        return mealApiService.getMealsByFirstLetter(letter)
                .map(response -> response.getMeals());
    }

}














