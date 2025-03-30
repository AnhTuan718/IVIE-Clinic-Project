package com.example.userpage.Model;

public class CartItem {
    private String productName;
    private int price;
    private int quantity;
    private int imageResource;

    public CartItem(String productName, int price, int quantity, int imageResource) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.imageResource = imageResource;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
}
