package com.example.gusteau.presentation.plan;

import com.example.gusteau.data.model.PlannedMeal;

import java.util.List;

public interface PlanContract {

    interface View {
        void updateWeekDays(String[] dayNumbers, String[] dayNames, int selectedDayIndex);
        void showBreakfastMeals(List<PlannedMeal> meals);
        void showLunchMeals(List<PlannedMeal> meals);
        void showDinnerMeals(List<PlannedMeal> meals);
        void showSnackMeals(List<PlannedMeal> meals);
        void showLoading();
        void hideLoading();
        void showError(String message);
        void showMealDeleted(String mealType);
        void navigateToMealDetails();
        void navigateToSearchForMeal();
        void showGuestModeMessage();
    }

    interface Presenter {
        void loadWeekDays();
        void onDaySelected(int dayIndex);
        void loadMealsForSelectedDay();
        void onAddMeal();
        void onMealClick(PlannedMeal meal);
        void onDeleteMealClick(PlannedMeal meal);
        void onDestroy();
    }
}