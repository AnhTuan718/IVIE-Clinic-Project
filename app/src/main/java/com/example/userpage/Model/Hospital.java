package com.example.userpage.Model;

public class Hospital {
    private String name;
    private String address;
    private int imageResId;

    public Hospital(int imageResId, String address, String name) {
        this.imageResId = imageResId;
        this.address = address;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}
