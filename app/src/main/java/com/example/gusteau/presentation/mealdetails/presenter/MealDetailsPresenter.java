package com.example.gusteau.presentation.mealdetails.presenter;

import android.content.Context;

import com.example.gusteau.data.authentication.AuthRepository;
import com.example.gusteau.data.meals.MealsRepository;
import com.example.gusteau.data.model.DayInfo;
import com.example.gusteau.data.model.Meal;
import com.example.gusteau.data.model.PlannedMeal;
import com.example.gusteau.presentation.mealdetails.MealDetailsContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MealDetailsPresenter implements MealDetailsContract.Presenter {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private final MealDetailsContract.View view;
    private final MealsRepository mealsRepository;
    private final AuthRepository authRepository;
    private final CompositeDisposable disposables;
    private Meal currentMeal;
    private boolean isFavorite = false;

    public MealDetailsPresenter(MealDetailsContract.View view, Context context) {
        this.view = view;
        this.mealsRepository = new MealsRepository(context);
        this.authRepository = new AuthRepository(context);
        this.disposables = new CompositeDisposable();
    }

    @Override
    public void loadMealDetails(String mealId) {
        view.showLoading();

        disposables.add(
                mealsRepository.getMealById(mealId)
                        .flatMap(meal -> {
                            return mealsRepository.checkFavoritesForMeal(meal);
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                meal -> {
                                    currentMeal = meal;
                                    isFavorite = meal.isFavorite();
                                    view.hideLoading();
                                    view.showMealDetails(meal);
                                    view.updateFavoriteButton(isFavorite);
                                    if (meal.getYoutubeUrl() == null || meal.getYoutubeUrl().isEmpty()) {
                                        view.showVideoNotAvailable();
                                    }
                                },
                                error -> {
                                    view.hideLoading();
                                    view.showError("Failed to load meal details");
                                }
                        )
        );
    }

    @Override
    public void onFavoriteClick() {
        if (currentMeal == null) {
            return;
        }
        disposables.add(
                authRepository.isGuestMode()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                isGuest -> {
                                    if (isGuest) {
                                        view.showGuestModeMessage();
                                    } else {
                                        toggleFavorite();
                                    }
                                },
                                error -> {
                                    view.showError("Failed to check user status");
                                }
                        )
        );
    }

    private void toggleFavorite() {
        if (isFavorite) {
            disposables.add(
                    mealsRepository.deleteFavMeal(currentMeal.getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> {
                                        isFavorite = false;
                                        currentMeal.setFavorite(false);
                                        if (currentMeal.getId().equals(mealsRepository.getMealOfTheDayId())) {
                                            mealsRepository.setDayMealFavorited(false);
                                        }
                                        view.updateFavoriteButton(false);
                                    },
                                    error -> {
                                        view.showError("Failed to remove from favorites");
                                    }
                            )
            );
        } else {
            disposables.add(
                    mealsRepository.addFavMeal(currentMeal)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> {
                                        isFavorite = true;
                                        currentMeal.setFavorite(true);
                                        if (currentMeal.getId().equals(mealsRepository.getMealOfTheDayId())) {
                                            mealsRepository.setDayMealFavorited(true);
                                        }
                                        view.updateFavoriteButton(true);
                                    },
                                    error -> {
                                        view.showError("Failed to add to favorites");
                                    }
                            )
            );
        }
    }

    @Override
    public void onCalendarClick() {
        if (currentMeal == null) {
            return;
        }

        disposables.add(
                authRepository.isGuestMode()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                isGuest -> {
                                    if (isGuest) {
                                        view.showGuestModeMessage();
                                    } else {
                                        showWeekPlannerDialog();
                                    }
                                },
                                error -> {
                                    view.showError("Failed to check user status");
                                }
                        )
        );
    }

    private void showWeekPlannerDialog() {
        List<DayInfo> days = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        SimpleDateFormat dayNameFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            String displayName;
            if (i == 0) {
                displayName = "Today";
            } else if (i == 1) {
                displayName = "Tomorrow";
            } else {
                displayName = dayNameFormat.format(calendar.getTime());
            }

            String date = dateFormat.format(calendar.getTime());
            days.add(new DayInfo(displayName, date));


            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        view.showWeekPlannerDialog(days);
    }

    @Override
    public void onMealTypeSelected(String dayDate, String mealType) {
        if (currentMeal == null) {
            return;
        }


        PlannedMeal plannedMeal = new PlannedMeal(
                currentMeal.getId(),
                currentMeal.getName(),
                currentMeal.getImageUrl(),
                currentMeal.getCategory(),
                currentMeal.getArea(),
                dayDate,
                mealType
        );

        disposables.add(
                mealsRepository.insertPlannedMeal(plannedMeal)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    String dayDisplay = getDayDisplayName(dayDate);
                                    view.showMealAddedToPlan(dayDisplay, mealType);
                                },
                                error -> {
                                    view.showError("Failed to add meal to plan");
                                }
                        )
        );
    }

    private String getDayDisplayName(String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            Calendar targetCalendar = Calendar.getInstance();
            targetCalendar.setTime(dateFormat.parse(date));

            Calendar todayCalendar = Calendar.getInstance();
            Calendar tomorrowCalendar = Calendar.getInstance();
            tomorrowCalendar.add(Calendar.DAY_OF_MONTH, 1);

            if (isSameDay(targetCalendar, todayCalendar)) {
                return "Today";
            }

            if (isSameDay(targetCalendar, tomorrowCalendar)) {
                return "Tomorrow";
            }

            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            return dayFormat.format(targetCalendar.getTime());

        } catch (Exception e) {
            return date;
        }
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    @Override
    public void onDestroy() {
        disposables.clear();
    }

    public String getMealId() {
        return mealsRepository.getMealDetailsID();
    }
}