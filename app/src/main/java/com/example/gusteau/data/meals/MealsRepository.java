package com.example.gusteau.data.meals;

import android.content.Context;

import com.example.gusteau.data.meals.datasource.local.MealLocalDataSource;
import com.example.gusteau.data.meals.datasource.local.MealSharedPrefrenceLocalDataSource;
import com.example.gusteau.data.meals.datasource.remote.RemoteMealDataSource;

public class MealsRepository {
    private final RemoteMealDataSource remoteDataSource;
    private final MealLocalDataSource localDataSource;
    private final MealSharedPrefrenceLocalDataSource sharedPrefrenceLocalDataSource;
    public MealsRepository(Context context) {
        this.remoteDataSource = new RemoteMealDataSource();
        this.localDataSource = new MealLocalDataSource(context);
        this.sharedPrefrenceLocalDataSource = new MealSharedPrefrenceLocalDataSource(context);
    }

}
