package com.example.gusteau.data.model;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

public class Ingredients {
    @SerializedName("idIngredient")
    private String id;
    @SerializedName("strIngredient")
    private String name;
    @SerializedName("strType")
    private String image;
}
