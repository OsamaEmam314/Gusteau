package com.example.gusteau.data.meals;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gusteau.data.meals.datasource.local.MealLocalDataSource;
import com.example.gusteau.data.meals.datasource.local.MealSharedPrefrenceLocalDataSource;
import com.example.gusteau.data.meals.datasource.local.PlannedMealLocalDataSource;
import com.example.gusteau.data.meals.datasource.remote.FireStoreLocalDataSource;
import com.example.gusteau.data.meals.datasource.remote.RemoteMealDataSource;
import com.example.gusteau.data.model.Category;
import com.example.gusteau.data.model.Country;
import com.example.gusteau.data.model.FireStoreFavMeal;
import com.example.gusteau.data.model.FireStorePlannedMeal;
import com.example.gusteau.data.model.Ingredients;
import com.example.gusteau.data.model.Meal;
import com.example.gusteau.data.model.PlannedMeal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MealsRepository {
    private static final String TAG = "MealsRepository";

    private final RemoteMealDataSource remoteDataSource;
    private final MealLocalDataSource localDataSource;
    private final PlannedMealLocalDataSource plannedMealLocalDataSource;
    private final MealSharedPrefrenceLocalDataSource sharedPrefrenceLocalDataSource;
    private final FireStoreLocalDataSource fireStoreLocalDataSource;

    public MealsRepository(Context context) {
        this.remoteDataSource = new RemoteMealDataSource();
        this.localDataSource = new MealLocalDataSource(context);
        this.fireStoreLocalDataSource = new FireStoreLocalDataSource();
        this.plannedMealLocalDataSource = new PlannedMealLocalDataSource(context);
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
    public Completable insertPlannedMeal(PlannedMeal meal) {
        return plannedMealLocalDataSource.insertMeal(meal);
    }
    public Completable deletePlannedMeal(PlannedMeal meal) {
        return plannedMealLocalDataSource.deleteMeal(meal);
    }
    public Single<List<PlannedMeal>> getPlannedMealsByDay(String dayDate) {
        return plannedMealLocalDataSource.getMealsByDay(dayDate);
    }
    public Single<List<PlannedMeal>> getPlannedMealsByDayAndType(String dayDate, String mealType) {
        return plannedMealLocalDataSource.getMealsByDayAndType(dayDate, mealType);
    }
    public Single<List<PlannedMeal>> getAllPlannedMeals() {
        return plannedMealLocalDataSource.getAllPlannedMeals();
    }
    public Completable deletePlannedMealsByDayAndType(String dayDate, String mealType) {
        return plannedMealLocalDataSource.deleteByDayAndType(dayDate, mealType);
    }
    public Completable cleanupOldMeals(String thresholdDate) {
        return plannedMealLocalDataSource.cleanupOldMeals(thresholdDate);
    }

    public String getMealOfTheDayId() {
        return sharedPrefrenceLocalDataSource.getMealOfTheDayId();
    }
    public Single<Meal> getMealOfTheDay() {
        String todayDate = sharedPrefrenceLocalDataSource.getTodayDate();
        String currentDateFormatted = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        if (todayDate == null || todayDate.isEmpty() || !todayDate.equals(currentDateFormatted)) {
            return remoteDataSource.getRandomMeal()
                    .doOnSuccess(meal -> {
                        sharedPrefrenceLocalDataSource.saveTodayDate(currentDateFormatted);
                        sharedPrefrenceLocalDataSource.saveMealOfTheDayId(meal.getId());
                        sharedPrefrenceLocalDataSource.setDayMealFavorited(false);
                    });
        } else {

            String mealId = sharedPrefrenceLocalDataSource.getMealOfTheDayId();

            if (mealId == null || mealId.isEmpty()) {

                return remoteDataSource.getRandomMeal()
                        .doOnSuccess(meal -> {
                            sharedPrefrenceLocalDataSource.saveMealOfTheDayId(meal.getId());
                            sharedPrefrenceLocalDataSource.setDayMealFavorited(false);
                        });
            } else {
                Log.d(TAG, "Getting meal by ID: " + mealId);
                return remoteDataSource.getMealById(mealId);
            }
        }
    }
    public boolean isDayMealFavorited() {
        return sharedPrefrenceLocalDataSource.isDayMealFavorited();
    }
    public Completable clearAllUserData() {
        return Completable.mergeArray(
                localDataSource.deleteAll(),
                plannedMealLocalDataSource.deleteAll()
        ).doOnComplete(() -> {
            sharedPrefrenceLocalDataSource.clearData();
        });
    }

    public void setDayMealFavorited(boolean favorited) {
        sharedPrefrenceLocalDataSource.setDayMealFavorited(favorited);
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

    public void saveCountry(String country) {
        sharedPrefrenceLocalDataSource.saveCountry(country);
        sharedPrefrenceLocalDataSource.saveCategory(null);
        sharedPrefrenceLocalDataSource.saveIngredients(null);
    }

    public void saveIngredients(String ingredients) {
        sharedPrefrenceLocalDataSource.saveIngredients(ingredients);
        sharedPrefrenceLocalDataSource.saveCategory(null);
        sharedPrefrenceLocalDataSource.saveCountry(null);
    }

    public void saveCategory(String category) {
        sharedPrefrenceLocalDataSource.saveCategory(category);
        sharedPrefrenceLocalDataSource.saveCountry(null);
        sharedPrefrenceLocalDataSource.saveIngredients(null);
    }

    public String retriveCountry() {
        String country = sharedPrefrenceLocalDataSource.getCountry();
        return country;
    }

    public String retriveIngredients() {
        String ingredients = sharedPrefrenceLocalDataSource.getIngredients();
        return ingredients;
    }

    public String retriveCategory() {
        String category = sharedPrefrenceLocalDataSource.getCategory();
        return category;
    }
    public void saveMealDetailsID(String mealID){
        sharedPrefrenceLocalDataSource.saveMeal(mealID);
    }
    public String getMealDetailsID(){
        return sharedPrefrenceLocalDataSource.getMeal();

    }

    public Single<List<Meal>> checkFavoritesForMeals(List<Meal> meals) {
        return Single.fromCallable(() -> {

            for (Meal meal : meals) {
                try {
                    Meal favMeal = getFavMealById(meal.getId()).blockingGet();
                    if (favMeal != null) {
                        meal.setFavorite(true);
                    }
                } catch (Exception e) {
                    meal.setFavorite(false);
                }
            }

            return meals;
        });
    }
    public Single<Meal> checkFavoritesForMeal(Meal meal) {
        return Single.fromCallable(() -> {
                try {
                    Meal favMeal = getFavMealById(meal.getId()).blockingGet();
                    if (favMeal != null) {
                        meal.setFavorite(true);
                    }
                } catch (Exception e) {
                    meal.setFavorite(false);
                }
            return meal;
        });
    }
    @NonNull
    private static String getFlagUrl(@Nullable String title) {
        if (title == null) return "";
        String countryCode = getCountryCode(title);
        String size = countryCode.equals("tn") ? "64" : "128";
        return "https://www.themealdb.com/images/icons/flags/big/" + size + "/" + countryCode + ".png";
    }



    public Completable restoreFavoritesFromFirestore(String userId) {
        Log.d(TAG, "Restoring Favorites for User: " + userId);

        return fireStoreLocalDataSource.getFavMeals(userId)
                .doOnSuccess(list -> Log.d(TAG, "Firestore returned " + list.size() + " favorite items"))
                .flatMapObservable(Observable::fromIterable)
                .flatMapSingle(firestoreMeal ->
                        remoteDataSource.getMealById(firestoreMeal.getMealId())
                                .subscribeOn(Schedulers.io())
                                .map(meal -> {
                                    meal.setFavorite(true);
                                    return meal;
                                })
                                .doOnSuccess(meal -> Log.d(TAG, "Downloaded details for: " + meal.getName()))
                                .onErrorResumeNext(throwable -> {
                                    Log.e(TAG, "Failed to download meal details: " + firestoreMeal.getMealId(), throwable);
                                    return Single.just(new Meal());
                                })
                )
                .filter(meal -> meal.getId() != null)
                .flatMapCompletable(apiMeal ->
                        localDataSource.insertMeal(apiMeal)
                                .doOnComplete(() -> Log.d(TAG, "Inserted into Room: " + apiMeal.getName()))
                );
    }

    public Completable restorePlannedMealsFromFirestore(String userId) {
        Log.d(TAG, "Restoring Planned Meals for User: " + userId);

        return fireStoreLocalDataSource.getPlannedMeals(userId)
                .doOnSuccess(list -> Log.d(TAG, "Firestore returned " + list.size() + " planned items"))
                .flatMapObservable(Observable::fromIterable)
                .flatMapSingle(firestorePlan ->
                        remoteDataSource.getMealById(firestorePlan.getMealId())
                                .subscribeOn(Schedulers.io())
                                .map(apiMeal -> new PlannedMeal(
                                        apiMeal.getId(),
                                        apiMeal.getName(),
                                        apiMeal.getImageUrl(),
                                        apiMeal.getCategory(),
                                        apiMeal.getArea(),
                                        firestorePlan.getDayDate(),
                                        firestorePlan.getMealType()
                                ))
                                .doOnSuccess(meal -> Log.d(TAG, "Downloaded plan: " + meal.getMealName()))
                                .onErrorResumeNext(throwable -> {
                                    Log.e(TAG, "Failed to download plan details: " + firestorePlan.getMealId(), throwable);
                                    return Single.just(new PlannedMeal());
                                })
                )
                .filter(plannedMeal -> plannedMeal.getMealId() != null)
                .flatMapCompletable(plannedMeal ->
                        plannedMealLocalDataSource.insertMeal(plannedMeal)
                                .doOnComplete(() -> Log.d(TAG, "Inserted Plan into Room: " + plannedMeal.getMealName()))
                );
    }
    public Completable uploadFavoritesToFirestore(String userId) {
        return localDataSource.getAllMeals()
                .map(meals -> {
                    List<FireStoreFavMeal> fireStoreList = new ArrayList<>();
                    for (Meal meal : meals) {
                        fireStoreList.add(new FireStoreFavMeal(meal.getId(), userId));
                    }
                    return fireStoreList;
                })
                .flatMapCompletable(list -> fireStoreLocalDataSource.updateAllFavMeals(userId, list));
    }


    public Completable uploadPlannedMealsToFirestore(String userId) {
        return plannedMealLocalDataSource.getAllPlannedMeals()
                .map(plannedMeals -> {
                    List<FireStorePlannedMeal> fireStoreList = new ArrayList<>();
                    for (PlannedMeal meal : plannedMeals) {
                        fireStoreList.add(new FireStorePlannedMeal(
                                meal.getMealId(),
                                userId,
                                meal.getDayDate(),
                                meal.getMealType()
                        ));
                    }
                    return fireStoreList;
                })
                .flatMapCompletable(list -> fireStoreLocalDataSource.updateAllPlannedMeals(userId, list));
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