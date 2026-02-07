package com.example.gusteau.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.gusteau.data.meals.datasource.local.MealDao;
import com.example.gusteau.data.model.Category;
import com.example.gusteau.data.model.Country;
import com.example.gusteau.data.model.Ingredients;
import com.example.gusteau.data.model.Meal;

@Database(entities = {Meal.class }, version = 1, exportSchema = false)
public abstract class MealDatabase extends RoomDatabase {
    public abstract MealDao mealDao();


    private static volatile MealDatabase instance;
    public static synchronized MealDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    MealDatabase.class,
                    "meal_database"
            ).build();
        }
        return instance;
    }


}
