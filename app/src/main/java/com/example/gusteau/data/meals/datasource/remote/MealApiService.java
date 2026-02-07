package com.example.gusteau.data.meals.datasource.remote;

import com.example.gusteau.data.model.Category;
import com.example.gusteau.data.model.Ingredients;
import com.example.gusteau.data.model.Meal;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealApiService {
    String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";
    @GET("random.php")
    Single<Meal> getRandomMeal();
    @GET("list.php?c=list")
    Single<List<Category>> getAllCategories();
    @GET("list.php?a=list")
    Single<List<String>> getAllAreas();
    @GET("list.php?i=list")
    Single<List<Ingredients>> getAllIngredients();
    @GET("filter.php")
    Single<List<Meal>> filterByCategory(@Query("c") String category);
    @GET("filter.php")
    Single<List<Meal>> filterByArea(@Query("a") String area);
    @GET("filter.php")
    Single<List<Meal>> filterByIngredient(@Query("i") String ingredient);
    @GET("search.php")
    Single<List<Meal>> searchMealByName(@Query("s") String name);
    @GET("lookup.php")
    Single<Meal> getMealById(@Query("i") String id);
}