package com.hackcrisis.shopsafeuser.Data;

public class ProductDetails {

    private String productId, productName, productCategory, productSubCategory;
    private float weightPerPkt, price;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductSubCategory() {
        return productSubCategory;
    }

    public void setProductSubCategory(String productSubCategory) {
        this.productSubCategory = productSubCategory;
    }

    public float getWeightPerPkt() {
        return weightPerPkt;
    }

    public void setWeightPerPkt(float weightPerPkt) {
        this.weightPerPkt = weightPerPkt;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
