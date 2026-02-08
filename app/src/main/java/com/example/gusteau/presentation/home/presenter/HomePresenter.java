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
        view.showLoading();

        disposables.add(
                mealsRepository.getMealOfTheDay()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                meal -> {
                                    view.hideLoading();
                                    currentMealOfDay = meal;
                                    currentMealOfDay.setFavorite(mealsRepository.isDayMealFavorited());
                                    String isfav = meal.isFavorite() ? "true" : "false";
                                    view.showMealOfDay(meal);
                                },
                                error -> {
                                    view.hideLoading();
                                    view.showError("Failed to load meal of the day: " + error.getMessage());
                                }
                        )
        );
    }

    @Override
    public void loadCategories() {
        disposables.add(
                mealsRepository.getAllCategories()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                categories -> {
                                    view.showCategories(categories);
                                },
                                error -> {
                                    view.showError("Failed to load categories: " + error.getMessage());
                                }
                        )
        );
    }

    @Override
    public void loadCountries() {
        disposables.add(
                mealsRepository.getAllAreas()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                areas -> {
                                    view.showCountries(areas);
                                },
                                error -> {
                                    view.showError("Failed to load areas: " + error.getMessage());
                                }
                        )
        );
    }

    @Override
    public void loadIngredients() {
        disposables.add(
                mealsRepository.getAllIngredients()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                ingredients -> {
                                    view.showIngredients(ingredients);
                                },
                                error -> {
                                    view.showError("Failed to load ingredients: " + error.getMessage());
                                }
                        )
        );
    }

    @Override
    public void getUserName() {
        if (view == null) return;

        disposables.add(
                authRepository.getCurrentUser()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                user -> {
                                    if (user.isGuest()) {
                                        view.setUserName("Guest");
                                    } else {
                                        view.setUserName(user.getName());
                                    }
                                },
                                error -> {
                                    view.showError("Failed to get user: " + error.getMessage());
                                }
                        )
        );
    }

    @Override
    public void onMealOfDayClick() {
        if (currentMealOfDay != null) {
            mealsRepository.saveMealDetailsID(currentMealOfDay.getId());
            view.navigateToMealDetails();
        }
    }


    @Override
    public void onMealOfDayFavoriteClick(Meal meal) {

        disposables.add(
                authRepository.isGuestMode()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                isGuest -> {
                                    if (isGuest) {
                                        view.showGuestModeMessage();
                                    } else {
                                        performFavoriteToggle(meal);
                                    }
                                },
                                error -> {
                                    view.showError("Failed to check user status");
                                }
                        )
        );
    }

    private void performFavoriteToggle(Meal meal) {
        boolean newFavoriteStatus = !meal.isFavorite();

        if (newFavoriteStatus) {
            disposables.add(
                    mealsRepository.addFavMeal(meal)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> {
                                        meal.setFavorite(true);
                                        mealsRepository.setDayMealFavorited(true);
                                        view.updateMealOfDayFavoriteStatus(true);
                                    },
                                    error -> {
                                        view.showError("Failed to add to favorites");
                                    }
                            )
            );
        } else {
            disposables.add(
                    mealsRepository.deleteFavMeal(meal.getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> {
                                        meal.setFavorite(false);
                                        mealsRepository.setDayMealFavorited(false);
                                        view.updateMealOfDayFavoriteStatus(false);
                                    },
                                    error -> {
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
        view.setGreeting(greeting);
    }

    @Override
    public void onCategoryClick(String category) {
        mealsRepository.saveCategory(category);
        view.navigateToFIlteredMeals();
    }

    @Override
    public void onCountryClick(String country) {
        mealsRepository.saveCountry(country);
        view.navigateToFIlteredMeals();
    }

    @Override
    public void onIngredientClick(String ingredient) {
        mealsRepository.saveIngredients(ingredient);
        view.navigateToFIlteredMeals();
    }

    @Override
    public void onDestroy() {
        disposables.clear();
    }
}