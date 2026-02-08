package com.example.gusteau.presentation.home.presenter;

import com.example.gusteau.R;
import com.example.gusteau.data.authentication.AuthRepository;
import com.example.gusteau.data.meals.MealsRepository;
import com.example.gusteau.data.model.Country;
import com.example.gusteau.data.model.Ingredients;
import com.example.gusteau.presentation.home.HomeContract;

import android.content.Context;
import android.util.Log;

import com.example.gusteau.data.model.Category;
import com.example.gusteau.data.model.Meal;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomePresenter implements HomeContract.Presenter {

    private static final String TAG = "HomePresenter";

    private final HomeContract.View view;
    private final MealsRepository mealsRepository;
    private final AuthRepository authRepository;
    private final CompositeDisposable disposables;

    private Meal currentMealOfDay;

    public HomePresenter(HomeContract.View view, Context context) {
        this.view = view;
        this.mealsRepository = new MealsRepository(context);
        this.authRepository = new AuthRepository(context);
        this.disposables = new CompositeDisposable();
    }

    @Override
    public void loadMealOfDay() {
        Log.d(TAG, "Loading meal of day");
        view.showLoading();

        disposables.add(
                mealsRepository.getMealOfTheDay()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                meal -> {
                                    Log.d(TAG, "Meal loaded: " + meal.getName());
                                    view.hideLoading();
                                    currentMealOfDay = meal;
                                    view.showMealOfDay(meal);
                                },
                                error -> {
                                    Log.e(TAG, "Error loading meal", error);
                                    view.hideLoading();
                                    view.showError("Failed to load meal of the day: " + error.getMessage());
                                }
                        )
        );
    }

    @Override
    public void loadCategories() {
        Log.d(TAG, "Loading categories");
        disposables.add(
                mealsRepository.getAllCategories()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                categories -> {
                                    Log.d(TAG, "Categories loaded: " + categories.size());
                                    view.showCategories(categories);
                                },
                                error -> {
                                    Log.e(TAG, "Error loading categories", error);
                                    view.showError("Failed to load categories: " + error.getMessage());
                                }
                        )
        );
    }

    @Override
    public void loadCountries() {
        Log.d(TAG, "Loading countries");
        disposables.add(
                mealsRepository.getAllAreas()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                areas -> {
                                    Log.d(TAG, "Countries loaded: " + areas.size());
                                    view.showCountries(areas);
                                },
                                error -> {
                                    Log.e(TAG, "Error loading countries", error);
                                    view.showError("Failed to load areas: " + error.getMessage());
                                }
                        )
        );
    }

    @Override
    public void loadIngredients() {
        Log.d(TAG, "Loading ingredients");
        disposables.add(
                mealsRepository.getAllIngredients()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                ingredients -> {
                                    Log.d(TAG, "Ingredients loaded: " + ingredients.size());
                                    view.showIngredients(ingredients);
                                },
                                error -> {
                                    Log.e(TAG, "Error loading ingredients", error);
                                    view.showError("Failed to load ingredients: " + error.getMessage());
                                }
                        )
        );
    }

    @Override
    public void getUserName() {
        if (view == null) return;
        Log.d(TAG, "Getting user name");

        disposables.add(
                authRepository.getCurrentUser()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                user -> {
                                    Log.d(TAG, "User: " + user.getName() + ", isGuest: " + user.isGuest());
                                    if (user.isGuest()) {
                                        view.setUserName("Guest");
                                    } else {
                                        view.setUserName(user.getName());
                                    }
                                },
                                error -> {
                                    Log.e(TAG, "Error getting user", error);
                                    view.showError("Failed to get user: " + error.getMessage());
                                }
                        )
        );
    }

    @Override
    public void onMealOfDayClick() {
        if (currentMealOfDay != null) {
            Log.d(TAG, "Meal of day clicked: " + currentMealOfDay.getName());
            view.navigateToMealDetails(currentMealOfDay.getId());
        }
    }


    @Override
    public void onMealOfDayFavoriteClick(Meal meal) {
        Log.d(TAG, "Favorite clicked for: " + meal.getName());

        disposables.add(
                authRepository.isGuestMode()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                isGuest -> {
                                    Log.d(TAG, "Is guest: " + isGuest);
                                    if (isGuest) {
                                        Log.d(TAG, "Guest mode - showing message");
                                        view.showGuestModeMessage();
                                    } else {
                                        Log.d(TAG, "Not guest - performing toggle");
                                        performFavoriteToggle(meal);
                                    }
                                },
                                error -> {
                                    Log.e(TAG, "Error checking guest status", error);
                                    view.showError("Failed to check user status");
                                }
                        )
        );
    }

    private void performFavoriteToggle(Meal meal) {
        boolean newFavoriteStatus = !meal.isFavorite();
        Log.d(TAG, "Toggling favorite - new status: " + newFavoriteStatus);

        if (newFavoriteStatus) {
            Log.d(TAG, "Adding to favorites");
            disposables.add(
                    mealsRepository.addFavMeal(meal)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> {
                                        Log.d(TAG, "Added to favorites");
                                        meal.setFavorite(true);
                                        view.updateMealOfDayFavoriteStatus(true);
                                    },
                                    error -> {
                                        Log.e(TAG, "Error adding to favorites", error);
                                        view.showError("Failed to add to favorites");
                                    }
                            )
            );
        } else {
            Log.d(TAG, "Removing from favorites");
            disposables.add(
                    mealsRepository.deleteFavMeal(meal.getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> {
                                        Log.d(TAG, "Removed from favorites");
                                        meal.setFavorite(false);
                                        view.updateMealOfDayFavoriteStatus(false);
                                    },
                                    error -> {
                                        Log.e(TAG, "Error removing from favorites", error);
                                        view.showError("Failed to remove from favorites");
                                    }
                            )
            );
        }
    }

    public void setGreeting() {
        int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        String greeting;

        if (hour < 12) {
            greeting = "Good Morning";
        } else if (hour < 17) {
            greeting = "Good Afternoon";
        } else {
            greeting = "Good Evening";
        }
        Log.d(TAG, "Setting greeting: " + greeting);
        view.setGreeting(greeting);
    }

    @Override
    public void onCategoryClick(String category) {
        Log.d(TAG, "Category clicked: " + category);
        mealsRepository.saveCategory(category);
        view.navigateToFIlteredMeals();
    }

    @Override
    public void onCountryClick(String country) {
        Log.d(TAG, "Country clicked: " + country);
        mealsRepository.saveCountry(country);
        view.navigateToFIlteredMeals();
    }

    @Override
    public void onIngredientClick(String ingredient) {
        Log.d(TAG, "Ingredient clicked: " + ingredient);
        mealsRepository.saveIngredients(ingredient);
        view.navigateToFIlteredMeals();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy - clearing disposables");
        disposables.clear();
    }
}