package com.mozadevelopment.moza.Database;

public class MenuHelperClass {

    private String name, description, price, image;

    public MenuHelperClass() {}

    public MenuHelperClass (String name, String price, String image, String description){
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
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

    public void setImage(String image) {
        this.image = image;
    }
}
