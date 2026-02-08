package com.example.gusteau.presentation.filteredmeals.presenter;

import android.content.Context;
import android.util.Log;

import com.example.gusteau.data.authentication.AuthRepository;
import com.example.gusteau.data.meals.MealsRepository;
import com.example.gusteau.data.model.Meal;
import com.example.gusteau.presentation.filteredmeals.FilteredMealsContract;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FilteredMealsPresenter implements FilteredMealsContract.Presenter {

    private static final String TAG = "FilteredMealsPresenter";

    private final FilteredMealsContract.View view;
    private final MealsRepository mealsRepository;
    private final AuthRepository authRepository;
    private final CompositeDisposable disposables;

    private String filterType;
    private String filterValue;

    public FilteredMealsPresenter(FilteredMealsContract.View view, Context context) {
        this.view = view;
        this.mealsRepository = new MealsRepository(context);
        this.authRepository = new AuthRepository(context);
        this.disposables = new CompositeDisposable();
        retrieveFilterData();
    }

    private void retrieveFilterData() {
        String category = mealsRepository.retriveCategory();
        String country = mealsRepository.retriveCountry();
        String ingredient = mealsRepository.retriveIngredients();

        if (category != null && !category.isEmpty()) {
            filterType = "category";
            filterValue = category;
            view.setToolbarTitle(category);
        } else if (country != null && !country.isEmpty()) {
            filterType = "country";
            filterValue = country;
            view.setToolbarTitle(country);
        } else if (ingredient != null && !ingredient.isEmpty()) {
            filterType = "ingredient";
            filterValue = ingredient;
            view.setToolbarTitle(ingredient);
        } else {
            filterType = "category";
            filterValue = "Beef";
            view.setToolbarTitle("All Meals");
        }
    }

    @Override
    public void loadFilteredMeals() {
        view.showLoading();
        view.hideEmptyState();

        Single<List<Meal>> mealsObservable;
        switch (filterType) {
            case "category":
                mealsObservable = mealsRepository.filterByCategory(filterValue);
                break;
            case "country":
                mealsObservable = mealsRepository.filterByArea(filterValue);
                break;
            case "ingredient":
                mealsObservable = mealsRepository.filterByIngredient(filterValue);
                break;
            default:
                view.hideLoading();
                view.showError("Invalid filter type");
                return;
        }

        disposables.add(
                mealsObservable
                        .flatMap(mealsRepository::checkFavoritesForMeals)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                meals -> {
                                    view.hideLoading();
                                    if (meals.isEmpty()) {
                                        view.showEmptyState();
                                        view.setResultsCount(0);
                                    } else {
                                        view.hideEmptyState();
                                        view.showMeals(meals);
                                        view.setResultsCount(meals.size());
                                    }
                                },
                                error -> {
                                    view.hideLoading();
                                    view.showError("Failed to load meals: " + error.getMessage());
                                    view.showEmptyState();
                                    view.setResultsCount(0);
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
                                        performFavoriteToggle(meal, position);
                                    }
                                },
                                error -> {
                                    view.showError("Failed to check user status");
                                }
                        )
        );
    }

    private void performFavoriteToggle(Meal meal, int position) {
        boolean newFavoriteStatus = !meal.isFavorite();

        if (newFavoriteStatus) {
            disposables.add(
                    mealsRepository.addFavMeal(meal)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> {
                                        if(meal.getId().equals(mealsRepository.getMealOfTheDayId())){
                                            mealsRepository.setDayMealFavorited(true);
                                        }
                                        meal.setFavorite(true);
                                        view.updateMealFavoriteStatus(position, true);
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
                                        if(meal.getId().equals(mealsRepository.getMealOfTheDayId())){
                                            mealsRepository.setDayMealFavorited(false);
                                        }
                                        meal.setFavorite(false);
                                        view.updateMealFavoriteStatus(position, false);
                                    },
                                    error -> {
                                        view.showError("Failed to remove from favorites");
                                    }
                            )
            );
        }
    }

    @Override
    public void onDestroy() {
        disposables.clear();
    }
}