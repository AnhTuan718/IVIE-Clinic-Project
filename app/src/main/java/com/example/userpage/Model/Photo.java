package com.example.userpage.Model;


import java.io.Serializable;

public class Photo implements Serializable {

    public Photo(int resourceID) {
        this.resourceID = resourceID;
    }

    public int getResourceID() {
        return resourceID;
    }

    public void setResourceID(int resourceID) {
        this.resourceID = resourceID;
    }

    private int resourceID;
}

