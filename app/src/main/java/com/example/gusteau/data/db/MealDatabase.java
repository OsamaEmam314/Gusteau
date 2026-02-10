package com.example.gusteau.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.gusteau.data.meals.datasource.local.MealDao;

import com.example.gusteau.data.meals.datasource.local.PlannedMealDao;
import com.example.gusteau.data.model.Meal;
import com.example.gusteau.data.model.PlannedMeal;

@Database(entities = {Meal.class, PlannedMeal.class }, version = 3, exportSchema = false)
public abstract class MealDatabase extends RoomDatabase {
    public abstract MealDao mealDao();
    public abstract PlannedMealDao plannedMealDao();



    private static volatile MealDatabase instance;
    public static synchronized MealDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    MealDatabase.class,
                    "meal_database"
            ).fallbackToDestructiveMigration().build();
        }
        return instance;
    }


}
