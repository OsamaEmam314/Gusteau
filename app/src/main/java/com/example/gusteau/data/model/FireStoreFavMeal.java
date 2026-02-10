package com.example.gusteau.data.model;

public class FireStoreFavMeal {
    private String mealId;
    private String userId;

    public FireStoreFavMeal() {
    }

    public FireStoreFavMeal(String mealId, String userId) {
        this.mealId = mealId;
        this.userId = userId;
    }

    ;

    public String getMealId() {
        return mealId;
    }

    public void setMealId(String mealId) {
        this.mealId = mealId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
