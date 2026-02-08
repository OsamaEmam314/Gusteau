package com.example.gusteau.presentation.favorites.presenter;


import android.content.Context;
import android.util.Log;

import com.example.gusteau.data.authentication.AuthRepository;
import com.example.gusteau.data.meals.MealsRepository;
import com.example.gusteau.data.model.Meal;
import com.example.gusteau.presentation.favorites.FavoritesContract;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FavoritesPresenter implements FavoritesContract.Presenter {


    private final FavoritesContract.View view;
    private final MealsRepository mealsRepository;
    private final AuthRepository authRepository;
    private final CompositeDisposable disposables;

    public FavoritesPresenter(FavoritesContract.View view, Context context) {
        this.view = view;
        this.mealsRepository = new MealsRepository(context);
        this.authRepository = new AuthRepository(context);
        this.disposables = new CompositeDisposable();
    }

    @Override
    public void loadFavorites() {
        view.showLoading();
        view.hideEmptyState();
        disposables.add(
                authRepository.isGuestMode()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                isGuest -> {
                                    if (isGuest) {
                                        view.hideLoading();
                                        view.showEmptyState();
                                    } else {
                                        loadFavoritesFromDatabase();
                                    }
                                },
                                error -> {
                                    view.hideLoading();
                                    view.showError("Failed to check user status");
                                }
                        )
        );
    }
    private void loadFavoritesFromDatabase() {
        disposables.add(
                mealsRepository.getFavMeals()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                meals -> {
                                    view.hideLoading();
                                    if (meals.isEmpty()) {
                                        view.showEmptyState();
                                    } else {
                                        for (Meal meal : meals) {
                                            meal.setFavorite(true);
                                        }
                                        view.hideEmptyState();
                                        view.showFavorites(meals);
                                    }
                                },
                                error -> {
                                    view.hideLoading();
                                    view.showError("Failed to load favorites: " + error.getMessage());
                                    view.showEmptyState();
                                }
                        )
        );
    }

    @Override
    public void onMealClick(Meal meal) {
        mealsRepository.saveMealDetailsID(meal.getId());
        view.navigateToMealDetails();
    }

    @Override
    public void onFavoriteClick(Meal meal, int position) {

        disposables.add(
                authRepository.isGuestMode()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                isGuest -> {
                                    if (isGuest) {
                                        view.showGuestModeMessage();
                                    } else {
                                        removeFavorite(meal, position);
                                    }
                                },
                                error -> {
                                    view.showError("Failed to check user status");
                                }
                        )
        );
    }

    private void removeFavorite(Meal meal, int position) {
        disposables.add(
                mealsRepository.deleteFavMeal(meal.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    meal.setFavorite(false);
                                    if(meal.getId().equals(mealsRepository.getMealOfTheDayId())){
                                        mealsRepository.setDayMealFavorited(false);
                                    }
                                    view.removeMealFromList(position);
                                },
                                error -> {
                                    view.showError("Failed to remove from favorites");
                                }
                        )
        );
    }

    @Override
    public void onDestroy() {
        disposables.clear();
    }
}