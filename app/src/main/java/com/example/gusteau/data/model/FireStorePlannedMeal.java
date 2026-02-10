package com.example.gusteau.data.model;

public class FireStorePlannedMeal {
    private String mealId;
    private String userId;
    private String dayDate;
    private String mealType;

    public FireStorePlannedMeal() {
    }

    public FireStorePlannedMeal(String mealId, String userId, String dayDate, String mealType) {
        this.mealId = mealId;
        this.userId = userId;
        this.dayDate = dayDate;
        this.mealType = mealType;
    }

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

    public String getDayDate() {
        return dayDate;
    }

    public void setDayDate(String dayDate) {
        this.dayDate = dayDate;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }
}