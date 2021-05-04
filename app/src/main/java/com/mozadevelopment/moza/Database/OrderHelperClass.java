package com.mozadevelopment.moza.Database;

public class OrderHelperClass {

    String timestamp, address, annotation, payment, price, status;

    public OrderHelperClass(String timestamp, String address, String annotation, String payment, String price, String status) {
        this.timestamp = timestamp;
        this.address = address;
        this.annotation = annotation;
        this.payment = payment;
        this.price = price;
        this.status = status;
    }

    public OrderHelperClass() {

    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
