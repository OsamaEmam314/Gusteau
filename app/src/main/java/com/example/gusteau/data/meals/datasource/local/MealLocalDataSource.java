package com.example.gusteau.data.meals.datasource.local;

import android.content.Context;

import com.example.gusteau.data.db.MealDatabase;
import com.example.gusteau.data.model.Meal;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class MealLocalDataSource {
    private MealDao mealDao;
    public MealLocalDataSource(Context context) {
        this.mealDao = MealDatabase.getInstance(context).mealDao();
    }
    public Single<List<Meal>> getAllMeals() {
        return mealDao.getAllMeals();
    }
    public Single<Meal> getMealById(String id) {
        return mealDao.getMealById(id);
    }
    public void insertMeal(Meal meal) {
        mealDao.insertMeal(meal);
    }
    public void deleteMeal(String id) {
        mealDao.deleteMeal(id);
    }

}
