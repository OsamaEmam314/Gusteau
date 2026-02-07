package com.example.gusteau.data.model;


import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CountryResponse {
    @SerializedName("meals")
    private List<Country> areas;

    public List<Country> getAreas() {
        return areas;
    }
}