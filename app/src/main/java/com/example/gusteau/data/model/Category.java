package com.example.gusteau.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;


public class Category {
    @SerializedName("idCategory")
    private String id;
    @SerializedName("strCategory")
    private String name;
    @SerializedName("strCategoryThumb")
    private String image;

}
