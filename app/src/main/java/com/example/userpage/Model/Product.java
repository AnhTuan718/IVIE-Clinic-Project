package com.example.userpage.Model;

import java.io.Serializable;

public class Product implements Serializable {
    private String productName;
    private double price;
    private int imageResource;

    public Product(String productName, double price, int imageResource) {
        this.productName = productName;
        this.price = price;
        this.imageResource = imageResource;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
}
