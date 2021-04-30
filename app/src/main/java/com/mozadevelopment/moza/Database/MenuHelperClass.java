package com.mozadevelopment.moza.Database;

public class MenuHelperClass {

    private String name, description, price, imageUrl, itemId;

    public MenuHelperClass() {}

    public MenuHelperClass (String name, String description, String price, String imageURL, String itemId){
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageURL;
        this.itemId = itemId;
    }

    public String getItemId() {
        return itemId;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

}
