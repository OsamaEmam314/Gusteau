package com.example.gusteau.data.model;

public class DayInfo {
    private final String displayName;
    private final String date;

    public DayInfo(String displayName, String date) {
        this.displayName = displayName;
        this.date = date;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDate() {
        return date;
    }
}