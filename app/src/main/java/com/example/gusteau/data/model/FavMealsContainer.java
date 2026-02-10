package com.example.gusteau.data.model;

import com.example.gusteau.data.model.FireStoreFavMeal;

import java.util.List;

public class FavMealsContainer {
    private List<FireStoreFavMeal> meals;

    public FavMealsContainer() {
    }

    public FavMealsContainer(List<FireStoreFavMeal> meals) {
        this.meals = meals;
    }

    public List<FireStoreFavMeal> getMeals() {
        return meals;
    }

    public void setMeals(List<FireStoreFavMeal> meals) {
        this.meals = meals;
    }
}
