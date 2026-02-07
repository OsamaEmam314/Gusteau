package com.example.gusteau.data.meals.datasource.remote;

import com.example.gusteau.data.model.Category;
import com.example.gusteau.data.model.CategoryResponse;
import com.example.gusteau.data.model.CountryResponse;
import com.example.gusteau.data.model.Ingredients;
import com.example.gusteau.data.model.IngredientsResponse;
import com.example.gusteau.data.model.Meal;
import com.example.gusteau.data.model.MealResponse;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealApiService {
    String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";
    @GET("random.php")
    Single<MealResponse> getRandomMeal();
    @GET("lookup.php")
    Single<MealResponse> getMealById(@Query("i") String id);
    @GET("categories.php")
    Single<CategoryResponse> getAllCategories();
    @GET("list.php?a=list")
    Single<CountryResponse> getAllAreas();
    @GET("list.php?i=list")
    Single<IngredientsResponse> getAllIngredients();
    @GET("filter.php")
    Single<MealResponse> filterByCategory(@Query("c") String category);

    @GET("filter.php")
    Single<MealResponse> filterByArea(@Query("a") String area);

    @GET("filter.php")
    Single<MealResponse> filterByIngredient(@Query("i") String ingredient);
    @GET("search.php")
    Single<MealResponse> searchMealByName(@Query("s") String name);

    @GET("search.php")
    Single<MealResponse> getMealsByFirstLetter(@Query("f") String letter);

}