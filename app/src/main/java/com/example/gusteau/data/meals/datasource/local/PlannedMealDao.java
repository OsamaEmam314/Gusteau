package com.example.gusteau.data.meals.datasource.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.gusteau.data.model.PlannedMeal;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface PlannedMealDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(PlannedMeal meal);

    @Delete
    Completable delete(PlannedMeal meal);

    @Query("SELECT * FROM planned_meals WHERE dayDate = :dayDate AND mealType = :mealType ORDER BY timestamp DESC")
    Single<List<PlannedMeal>> getMealsByDayAndType(String dayDate, String mealType);

    @Query("SELECT * FROM planned_meals WHERE dayDate = :dayDate ORDER BY " +
            "CASE mealType " +
            "WHEN 'Breakfast' THEN 1 " +
            "WHEN 'Lunch' THEN 2 " +
            "WHEN 'Dinner' THEN 3 " +
            "WHEN 'Snack' THEN 4 " +
            "END, timestamp DESC")
    Single<List<PlannedMeal>> getMealsByDay(String dayDate);

    @Query("SELECT * FROM planned_meals ORDER BY dayDate ASC, " +
            "CASE mealType " +
            "WHEN 'Breakfast' THEN 1 " +
            "WHEN 'Lunch' THEN 2 " +
            "WHEN 'Dinner' THEN 3 " +
            "WHEN 'Snack' THEN 4 " +
            "END")
    Single<List<PlannedMeal>> getAllPlannedMeals();

    @Query("DELETE FROM planned_meals WHERE dayDate = :dayDate AND mealType = :mealType")
    Completable deleteByDayAndType(String dayDate, String mealType);

    @Query("DELETE FROM planned_meals WHERE dayDate < :date")
    Completable deleteOldMeals(String date);
}