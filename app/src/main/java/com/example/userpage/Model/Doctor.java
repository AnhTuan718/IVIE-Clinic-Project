package com.example.userpage.Model;

public class Doctor {
    private String name;
    private String workplace;
    private int imageResId;

    public Doctor(String name, String workplace, int imageResId) {
        this.name = name;
        this.workplace = workplace;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public String getWorkplace() {
        return workplace;
    }

    public int getImageResId() {
        return imageResId;
    }
}
