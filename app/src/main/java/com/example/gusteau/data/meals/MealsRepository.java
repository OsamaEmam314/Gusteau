package com.example.gusteau.data.meals;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gusteau.data.meals.datasource.local.MealLocalDataSource;
import com.example.gusteau.data.meals.datasource.local.MealSharedPrefrenceLocalDataSource;
import com.example.gusteau.data.meals.datasource.remote.RemoteMealDataSource;
import com.example.gusteau.data.model.Category;
import com.example.gusteau.data.model.Country;
import com.example.gusteau.data.model.Ingredients;
import com.example.gusteau.data.model.Meal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class MealsRepository {
    private final RemoteMealDataSource remoteDataSource;
    private final MealLocalDataSource localDataSource;
    private final MealSharedPrefrenceLocalDataSource sharedPrefrenceLocalDataSource;
    public MealsRepository(Context context) {
        this.remoteDataSource = new RemoteMealDataSource();
        this.localDataSource = new MealLocalDataSource(context);
        this.sharedPrefrenceLocalDataSource = new MealSharedPrefrenceLocalDataSource(context);
    }

    public Single<List<Meal>> getFavMeals(){
        return localDataSource.getAllMeals();
    }
    public Single<Meal> getFavMealById(String id) {
        return localDataSource.getMealById(id);
    }
    public Completable addFavMeal(Meal meal) {
        return localDataSource.insertMeal(meal);
    }
    public Completable deleteFavMeal(String id) {
        return localDataSource.deleteMeal(id);
    }

    public Single<Meal> getMealOfTheDay() {
        String todayDate = sharedPrefrenceLocalDataSource.getTodayDate();
        String currentDateFormatted = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        if (todayDate == null || !todayDate.equals(currentDateFormatted)) {
            return remoteDataSource.getRandomMeal()
                    .doOnSuccess(meal -> {
                        sharedPrefrenceLocalDataSource.saveTodayDate(currentDateFormatted);
                        sharedPrefrenceLocalDataSource.saveMealOfTheDayId(meal.getId());
                    });
        } else {
            String mealId = sharedPrefrenceLocalDataSource.getMealOfTheDayId();
            if (mealId == null) {
                return remoteDataSource.getRandomMeal()
                        .doOnSuccess(meal -> sharedPrefrenceLocalDataSource.saveMealOfTheDayId(meal.getId()));
            } else {
                return localDataSource.getMealById(mealId);
            }
        }

    }
    public Single<List<Category>> getAllCategories() {
        return remoteDataSource.getAllCategories();

    }

    public Single<List<Ingredients>> getAllIngredients() {
        return remoteDataSource.getAllIngredients();
    }
    public Single<List<Country>> getAllAreas() {
        return remoteDataSource.getAllAreas()
                .map(countriesNames -> {
                    List<Country> countries = new ArrayList<>();
                    for (String name : countriesNames) {
                        countries.add(new Country(name, getFlagUrl(name)));
                    }
                    return countries;
                });
    }
    public Single<List<Meal>> filterByCategory(String category) {
        return remoteDataSource.filterByCategory(category);
    }
    public Single<List<Meal>> filterByArea(String area) {
        return remoteDataSource.filterByArea(area);
    }
    public Single<List<Meal>> filterByIngredient(String ingredient) {
        return remoteDataSource.filterByIngredient(ingredient);
    }
    public Single<List<Meal>> searchMealByName(String name) {
        return remoteDataSource.searchMealByName(name);
    }
    public Single<Meal> getMealById(String id) {
        return remoteDataSource.getMealById(id);
    }
    public Single<List<Meal>> getMealsByFirsrLetter() {
        Random random = new Random();
        int randomLetter = random.nextInt(26);
        char randomChar = (char) ('a' + randomLetter);
        return remoteDataSource.getMealsByFirstLetter(String.valueOf(randomChar));
    }
    @NonNull
    private static String getFlagUrl(@Nullable String title) {
        if (title == null) return "";
        String countryCode = getCountryCode(title);
        String size = countryCode.equals("tn") ? "64" : "128";
        return "https://www.themealdb.com/images/icons/flags/big/" + size + "/" + countryCode + ".png";
    }

    @NonNull
    private static String getCountryCode(@NonNull String title) {
        switch (title) {
            case "American":
                return "us";
            case "British":
                return "gb";
            case "Algerian":
                return "dz";
            case "Argentinian":
                return "ar";
            case "Australian":
                return "au";
            case "Canadian":
                return "ca";
            case "Chinese":
                return "cn";
            case "Croatian":
                return "hr";
            case "Dutch":
                return "nl";
            case "Egyptian":
                return "eg";
            case "Filipino":
                return "ph";
            case "French":
                return "fr";
            case "Greek":
                return "gr";
            case "Indian":
                return "in";
            case "Irish":
                return "ie";
            case "Italian":
                return "it";
            case "Jamaican":
                return "jm";
            case "Japanese":
                return "jp";
            case "Kenyan":
                return "ke";
            case "Malaysian":
                return "my";
            case "Mexican":
                return "mx";
            case "Moroccan":
                return "ma";
            case "Norwegian":
                return "no";
            case "Polish":
                return "pl";
            case "Portuguese":
                return "pt";
            case "Russian":
                return "ru";
            case "Saudi Arabian":
                return "sa";
            case "Slovakian":
                return "sk";
            case "Spanish":
                return "es";
            case "Syrian":
                return "sy";
            case "Thai":
                return "th";
            case "Tunisian":
                return "tn";
            case "Turkish":
                return "tr";
            case "Ukrainian":
                return "ua";
            case "Uruguayan":
                return "uy";
            case "Vietnamese":
                return "vn";
            case "Venezulan":
                return "ve";
            default:
                return "unknown";
        }
    }

}
