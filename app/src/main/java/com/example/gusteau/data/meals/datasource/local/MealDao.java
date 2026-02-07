package com.example.gusteau.data.meals.datasource.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.gusteau.data.model.Meal;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface MealDao {
    @Query("SELECT * FROM meals")
    Single<List<Meal>> getAllMeals();
    @Query("SELECT * FROM meals WHERE id = :id")
    Single<Meal> getMealById(String id);
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMeal(Meal meal);
    @Query("DELETE FROM meals WHERE id = :id")
    void deleteMeal(String id);

}
