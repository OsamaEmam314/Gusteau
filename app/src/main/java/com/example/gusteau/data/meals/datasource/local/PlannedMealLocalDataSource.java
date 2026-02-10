package com.example.gusteau.data.meals.datasource.local;

import android.content.Context;

import com.example.gusteau.data.db.MealDatabase;
import com.example.gusteau.data.model.PlannedMeal;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlannedMealLocalDataSource {

    private final PlannedMealDao plannedMealDao;

    public PlannedMealLocalDataSource(Context context) {
        this.plannedMealDao = MealDatabase.getInstance(context).plannedMealDao();
    }

    public Completable insertMeal(PlannedMeal meal) {
        return plannedMealDao.insert(meal);
    }

    public Completable deleteMeal(PlannedMeal meal) {
        return plannedMealDao.delete(meal);
    }

    public Single<List<PlannedMeal>> getMealsByDay(String dayDate) {
        return plannedMealDao.getMealsByDay(dayDate);
    }

    public Single<List<PlannedMeal>> getMealsByDayAndType(String dayDate, String mealType) {
        return plannedMealDao.getMealsByDayAndType(dayDate, mealType);
    }

    public Single<List<PlannedMeal>> getAllPlannedMeals() {
        return plannedMealDao.getAllPlannedMeals();
    }

    public Completable deleteByDayAndType(String dayDate, String mealType) {
        return plannedMealDao.deleteByDayAndType(dayDate, mealType);
    }

    public Completable cleanupOldMeals(String thresholdDate) {
        return plannedMealDao.deleteOldMeals(thresholdDate);
    }

    public Completable deleteAll() {
        return plannedMealDao.deleteAll();
    }

}