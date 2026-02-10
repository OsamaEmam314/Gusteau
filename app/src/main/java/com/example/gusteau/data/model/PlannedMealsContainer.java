package com.example.gusteau.data.model;

import java.util.List;

public class PlannedMealsContainer {
    private List<FireStorePlannedMeal> meals;

    public PlannedMealsContainer() {
    }

    public PlannedMealsContainer(List<FireStorePlannedMeal> meals) {
        this.meals = meals;
    }

    public List<FireStorePlannedMeal> getMeals() {
        return meals;
    }

    public void setMeals(List<FireStorePlannedMeal> meals) {
        this.meals = meals;
    }
}