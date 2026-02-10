package com.example.gusteau.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IngredientsResponse {
    @SerializedName("meals")
    private List<Ingredients> ingredients;

    public List<Ingredients> getIngredients() {
        return ingredients;
    }
}