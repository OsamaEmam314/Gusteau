package com.example.gusteau.presentation.plan.presenter;

import android.content.Context;
import android.util.Log;

import com.example.gusteau.data.authentication.AuthRepository;
import com.example.gusteau.data.meals.MealsRepository;
import com.example.gusteau.data.model.PlannedMeal;
import com.example.gusteau.presentation.plan.PlanContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlanPresenter implements PlanContract.Presenter {

    private static final String TAG = "PlanPresenter";

    private final PlanContract.View view;
    private final AuthRepository authRepository;
    private final CompositeDisposable disposables;
    private final MealsRepository mealRepository;

    private Calendar currentWeekStart;
    private int selectedDayIndex = 0;
    private String[] weekDates;

    public PlanPresenter(PlanContract.View view, Context context) {
        this.view = view;
        this.authRepository = new AuthRepository(context);
        this.disposables = new CompositeDisposable();
        currentWeekStart = Calendar.getInstance();
        currentWeekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        this.mealRepository = new MealsRepository(context);
    }

    @Override
    public void loadWeekDays() {

        weekDates = new String[7];
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault());

        String[] dayNumbers = new String[7];

        String todayDate = dateFormat.format(calendar.getTime());

        for (int i = 0; i < 7; i++) {
            weekDates[i] = dateFormat.format(calendar.getTime());
            dayNumbers[i] = dayFormat.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        view.updateWeekDays(dayNumbers, 0);
        selectedDayIndex = 0;
        cleanupOldPlans(todayDate);
    }

    private void cleanupOldPlans(String thresholdDate) {
        disposables.add(
                mealRepository.cleanupOldMeals(thresholdDate)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> Log.d(TAG, "Old meals cleanup completed successfully."),
                                error -> Log.e(TAG, "Failed to cleanup old meals", error)
                        )
        );
    }
    @Override
    public void onDaySelected(int dayIndex) {
        selectedDayIndex = dayIndex;
        loadMealsForSelectedDay();
    }

    @Override
    public void loadMealsForSelectedDay() {
        if (weekDates == null || selectedDayIndex >= weekDates.length) {
            return;
        }

        String selectedDate = weekDates[selectedDayIndex];

        view.showLoading();
        loadMealsByType(selectedDate, "Breakfast");

        loadMealsByType(selectedDate, "Lunch");

        loadMealsByType(selectedDate, "Dinner");
        loadMealsByType(selectedDate, "Snack");
    }

    private void loadMealsByType(String date, String mealType) {
        disposables.add(
                mealRepository.getPlannedMealsByDayAndType(date, mealType)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                meals -> {
                                    view.hideLoading();

                                    switch (mealType) {
                                        case "Breakfast":
                                            view.showBreakfastMeals(meals);
                                            break;
                                        case "Lunch":
                                            view.showLunchMeals(meals);
                                            break;
                                        case "Dinner":
                                            view.showDinnerMeals(meals);
                                            break;
                                        case "Snack":
                                            view.showSnackMeals(meals);
                                            break;

                                    }
                                },
                                error -> {
                                    view.hideLoading();
                                    List<PlannedMeal> emptyList = new ArrayList<>();
                                    switch (mealType) {
                                        case "Breakfast":
                                            view.showBreakfastMeals(emptyList);
                                            break;
                                        case "Lunch":
                                            view.showLunchMeals(emptyList);
                                            break;
                                        case "Dinner":
                                            view.showDinnerMeals(emptyList);
                                            break;
                                    }
                                }
                        )
        );
    }

  @Override
    public void onAddMeal() {
        checkGuestAndNavigate();
    }


    private void checkGuestAndNavigate() {
        disposables.add(
                authRepository.isGuestMode()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                isGuest -> {
                                    if (isGuest) {
                                        view.showGuestModeMessage();
                                    } else {
                                        view.navigateToSearchForMeal();
                                    }
                                },
                                error -> {
                                    view.showError("Failed to check user status");
                                }
                        )
        );
    }

    @Override
    public void onMealClick(PlannedMeal meal) {
        mealRepository.saveMealDetailsID(meal.getMealId());
        view.navigateToMealDetails();
    }

    @Override
    public void onDeleteMealClick(PlannedMeal meal) {

        disposables.add(
                mealRepository.deletePlannedMeal(meal)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    view.showMealDeleted(meal.getMealType());
                                    loadMealsForSelectedDay();
                                },
                                error -> {
                                    view.showError("Failed to delete meal");
                                }
                        )
        );
    }

    @Override
    public void onDestroy() {
        disposables.clear();
    }
}