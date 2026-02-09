package com.example.gusteau.presentation.plan;

import com.example.gusteau.data.model.PlannedMeal;

import java.util.List;

public interface PlanContract {

    interface View {
        void showBreakfastMeals(List<PlannedMeal> meals);
        void showLunchMeals(List<PlannedMeal> meals);
        void showDinnerMeals(List<PlannedMeal> meals);
        void showSnackMeals(List<PlannedMeal> meals);
        void showLoading();
        void hideLoading();
        void showError(String message);
        void updateWeekDays(String[] dayNumbers, int selectedDayIndex);
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