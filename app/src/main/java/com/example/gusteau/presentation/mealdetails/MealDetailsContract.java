package com.example.gusteau.presentation.mealdetails;

import com.example.gusteau.data.model.DayInfo;
import com.example.gusteau.data.model.Meal;

import java.util.List;

public interface MealDetailsContract {

    interface View {
        void showMealDetails(Meal meal);

        void showLoading();

        void hideLoading();

        void showError(String message);

        void updateFavoriteButton(boolean isFavorite);

        void showWeekPlannerDialog(List<DayInfo> days);

        void showGuestModeMessage();

        void showMealAddedToPlan(String day, String mealType);

        void showVideoNotAvailable();
    }

    interface Presenter {
        void loadMealDetails(String mealId);

        void onFavoriteClick();

        void onCalendarClick();

        void onMealTypeSelected(String dayDate, String mealType);

        void onDestroy();
    }


}