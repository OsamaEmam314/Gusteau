package com.example.gusteau.presentation.search.presenter;

import android.content.Context;

import com.example.gusteau.data.authentication.AuthRepository;
import com.example.gusteau.data.meals.MealsRepository;
import com.example.gusteau.data.model.Meal;
import com.example.gusteau.presentation.search.SearchContract;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class SearchPresenter implements SearchContract.Presenter {

    private static final int SEARCH_DEBOUNCE_DELAY_MS = 500;
    private static final int MIN_SEARCH_LENGTH = 3;

    private final SearchContract.View view;
    private final MealsRepository mealsRepository;
    private final AuthRepository authRepository;
    private final CompositeDisposable disposables;

    private final PublishSubject<String> searchSubject;

    private String currentFilterType = null;
    private String currentSearchQuery = null;

    public SearchPresenter(SearchContract.View view, Context context) {
        this.view = view;
        this.mealsRepository = new MealsRepository(context);
        this.authRepository = new AuthRepository(context);
        this.disposables = new CompositeDisposable();
        this.searchSubject = PublishSubject.create();
        setupRealTimeSearch();
    }

    private void setupRealTimeSearch() {
        Disposable searchDisposable = searchSubject
                .debounce(SEARCH_DEBOUNCE_DELAY_MS, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .filter(query -> {
                    if (query == null || query.trim().isEmpty()) {
                        return true;
                    }
                    return query.trim().length() >= MIN_SEARCH_LENGTH;
                })
                .switchMap(query -> {
                    if (query == null || query.trim().isEmpty()) {
                        return Observable.just(query);
                    } else {
                        return Observable.just(query);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        query -> {
                            if (query == null || query.trim().isEmpty()) {
                                loadInitialData();
                            } else {
                                performSearch(query.trim());
                            }
                        },
                        error -> {
                            view.showError("Search error");
                        }
                );

        disposables.add(searchDisposable);
    }

    @Override
    public void loadInitialData() {
        view.showLoading();
        view.hideEmptyState();
        view.updateResultsHeader("Trending Meals");
        disposables.add(
                mealsRepository.getMealsByFirsrLetter()
                        .flatMap(this::checkFavoritesForMeals)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                meals -> {
                                    view.hideLoading();
                                    if (meals.isEmpty()) {
                                        view.showEmptyState();
                                    } else {
                                        view.hideEmptyState();
                                        view.showSearchResults(meals);
                                    }
                                },
                                error -> {
                                    view.hideLoading();
                                    view.showError("Failed to load meals");
                                    view.showEmptyState();
                                }
                        )
        );
    }

    @Override
    public void onSearchQuerySubmit(String query) {
        if (query == null || query.trim().isEmpty()) {
            loadInitialData();
            return;
        }

        currentSearchQuery = query.trim();
        currentFilterType = null;

        performSearch(currentSearchQuery);
    }



    @Override
    public void onSearchQueryChange(String query) {
        if (query != null && !query.trim().isEmpty()) {
            currentFilterType = null;
            view.uncheckAllChips();
        }

        searchSubject.onNext(query != null ? query : "");

        if (query != null && !query.trim().isEmpty() && query.trim().length() < MIN_SEARCH_LENGTH) {
            view.updateResultsHeader("Type at least " + MIN_SEARCH_LENGTH + " characters to search");
        }
    }
    private void performSearch(String query) {
        view.showLoading();
        view.hideEmptyState();
        view.updateResultsHeader("Search Results for \"" + query + "\"");

        disposables.add(
                mealsRepository.searchMealByName(query)
                        .flatMap(this::checkFavoritesForMeals)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                meals -> {
                                    view.hideLoading();

                                    if (meals.isEmpty()) {
                                        view.showEmptyState();
                                        view.updateResultsHeader("No results for \"" + query + "\"");
                                    } else {
                                        view.hideEmptyState();
                                        view.showSearchResults(meals);
                                    }
                                },
                                error -> {
                                    view.hideLoading();
                                    view.showError("Search failed");
                                    view.showEmptyState();
                                }
                        )
        );
    }

    @Override
    public void onCategoriesChipClick() {
        currentFilterType = "category";

        disposables.add(
                mealsRepository.getAllCategories()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                categories -> {
                                   view.showCategories(categories);
                                },
                                error -> {
                                    view.showError("Failed to load categories");
                                }
                        )
        );
    }

    @Override
    public void onCountryChipClick() {
        currentFilterType = "country";

        disposables.add(
                mealsRepository.getAllAreas()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                countries -> {
                                   view.showCountries(countries);
                                },
                                error -> {
                                    view.showError("Failed to load countries");
                                }
                        )
        );
    }

    @Override
    public void onIngredientsChipClick() {
        currentFilterType = "ingredient";

        disposables.add(
                mealsRepository.getAllIngredients()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                ingredients -> {
                                    view.showIngredients(ingredients);
                                },
                                error -> {
                                    view.showError("Failed to load ingredients");
                                }
                        )
        );
    }

    @Override
    public void onCategorySelected(String category) {
        currentSearchQuery = null;

        view.showLoading();
        view.hideEmptyState();
        view.hideFilters();
        view.clearSearchQuery();
        view.updateResultsHeader(category + " Meals");

        disposables.add(
                mealsRepository.filterByCategory(category)
                        .flatMap(this::checkFavoritesForMeals)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                meals -> {
                                    view.hideLoading();

                                    if (meals.isEmpty()) {
                                        view.showEmptyState();
                                    } else {
                                        view.hideEmptyState();
                                        view.showSearchResults(meals);
                                    }
                                },
                                error -> {
                                    view.hideLoading();
                                    view.showError("Failed to filter meals");
                                    view.showEmptyState();
                                }
                        )
        );
    }

    @Override
    public void onCountrySelected(String country) {
        currentSearchQuery = null;

        view.showLoading();
        view.hideEmptyState();
        view.hideFilters();
        view.clearSearchQuery();
        view.updateResultsHeader(country + " Meals");

        disposables.add(
                mealsRepository.filterByArea(country)
                        .flatMap(this::checkFavoritesForMeals)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                meals -> {
                                    view.hideLoading();

                                    if (meals.isEmpty()) {
                                        view.showEmptyState();
                                    } else {
                                        view.hideEmptyState();
                                        view.showSearchResults(meals);
                                    }
                                },
                                error -> {
                                    view.hideLoading();
                                    view.showError("Failed to filter meals");
                                    view.showEmptyState();
                                }
                        )
        );
    }

    @Override
    public void onIngredientSelected(String ingredient) {
        currentSearchQuery = null;

        view.showLoading();
        view.hideEmptyState();
        view.hideFilters();
        view.clearSearchQuery();
        view.updateResultsHeader("Meals with " + ingredient);

        disposables.add(
                mealsRepository.filterByIngredient(ingredient)
                        .flatMap(this::checkFavoritesForMeals)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                meals -> {
                                    view.hideLoading();

                                    if (meals.isEmpty()) {
                                        view.showEmptyState();
                                    } else {
                                        view.hideEmptyState();
                                        view.showSearchResults(meals);
                                    }
                                },
                                error -> {
                                    view.hideLoading();
                                    view.showError("Failed to filter meals");
                                    view.showEmptyState();
                                }
                        )
        );
    }

    private Single<List<Meal>> checkFavoritesForMeals(List<Meal> meals) {
        return Single.fromCallable(() -> {
            for (Meal meal : meals) {
                try {
                    Meal favMeal = mealsRepository.getFavMealById(meal.getId()).blockingGet();
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