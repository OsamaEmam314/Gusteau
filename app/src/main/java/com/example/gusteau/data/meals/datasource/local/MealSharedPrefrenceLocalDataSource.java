package com.example.gusteau.data.meals.datasource.local;

import android.content.Context;
import android.content.SharedPreferences;

public class MealSharedPrefrenceLocalDataSource {
    private static final String PREFS_NAME = "meals";
    private static final String TodayDate = "meals";
    private static final String MEAL_OF_THE_DAY_ID = "meal_of_the_day_id";
    private SharedPreferences sharedPreferences;
    public MealSharedPrefrenceLocalDataSource(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    public void saveTodayDate(String date) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TodayDate, date);
        editor.apply();
    }
    public String getTodayDate() {
        return sharedPreferences.getString(TodayDate, null);
    }
    public void saveMealOfTheDayId(String id) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MEAL_OF_THE_DAY_ID, id);
        editor.apply();
    }
    public String getMealOfTheDayId() {
        return sharedPreferences.getString(MEAL_OF_THE_DAY_ID, null);
    }


}
