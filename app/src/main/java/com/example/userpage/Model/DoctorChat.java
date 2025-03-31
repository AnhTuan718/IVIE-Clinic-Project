package com.example.userpage.Model;

public class DoctorChat {
    private String name;
    private String specialty;
    private String hospital;
    private int imageResource;
    private String status;
    private boolean isOnline;

    public DoctorChat(String name, String specialty, String hospital, int imageResource, String status, boolean isOnline) {
        this.name = name;
        this.specialty = specialty;
        this.hospital = hospital;
        this.imageResource = imageResource;
        this.status = status;
        this.isOnline = isOnline;
    }

    public String getName() {
        return name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getHospital() {
        return hospital;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getStatus() {
        return status;
    }

    public boolean isOnline() {
        return isOnline;
    }
}