package com.mozadevelopment.moza.Database;

public class CartHelperClass {

    String itemId, itemName, itemAmount, itemDescription, itemAnnotation, itemPrice, timestamp;

    public CartHelperClass(){}

    public CartHelperClass(String itemId, String itemName, String itemAmount, String itemDescription, String itemAnnotation, String itemPrice, String timestamp) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemAmount = itemAmount;
        this.itemDescription = itemDescription;
        this.itemAnnotation = itemAnnotation;
        this.itemPrice = itemPrice;
        this.timestamp = timestamp;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(String itemAmount) {
        this.itemAmount = itemAmount;
    }

    public String getItemAnnotation() {
        return itemAnnotation;
    }

    public void setItemAnnotation(String itemAnnotation) {
        this.itemAnnotation = itemAnnotation;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
