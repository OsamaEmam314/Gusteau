package com.example.gusteau.presentation.home.presenter;

import com.example.gusteau.data.authentication.AuthRepository;
import com.example.gusteau.data.meals.MealsRepository;
import com.example.gusteau.data.model.Country;
import com.example.gusteau.data.model.Ingredients;
import com.example.gusteau.presentation.home.HomeContract;

import android.content.Context;
import com.example.gusteau.data.model.Category;
import com.example.gusteau.data.model.Meal;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomePresenter implements HomeContract.Presenter {

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
                                categories -> view.showCategories(categories),
                                error -> view.showError("Failed to load categories: " + error.getMessage())
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
                                areas -> view.showCountries(areas),
                                error -> view.showError("Failed to load areas: " + error.getMessage())
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
                                ingredients -> view.showIngredients(ingredients),
                                error -> view.showError("Failed to load ingredients: " + error.getMessage())
                        )
        );
    }

    @Override
    public void getUserName() {
        disposables.add(
          authRepository.getCurrentUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        user -> view.setUserName(user.getName()),
                        error -> view.showError("Failed to get user name: " + error.getMessage())
                )
        );
    }

    @Override
    public void onMealOfDayClick() {
        if (currentMealOfDay != null) {
            view.navigateToMealDetails(currentMealOfDay.getId());
        }
    }
    @Override
    public void onMealOfDayFavoriteClick(Meal meal) {
        if (authRepository.isGuestMode()) {
            view.showGuestModeMessage();
            return;
        }
        boolean newFavoriteStatus = !meal.isFavorite();

        if (newFavoriteStatus) {
            disposables.add(
                    mealsRepository.addFavMeal(meal)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> {
                                        meal.setFavorite(true);
                                        view.updateMealOfDayFavoriteStatus(true);
                                    },
                                    error -> view.showError("Failed to add to favorites")
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
                                        view.updateMealOfDayFavoriteStatus(false);
                                    },
                                    error -> view.showError("Failed to remove from favorites")
                            )
            );
        }
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