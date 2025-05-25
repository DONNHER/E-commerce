package com.example.Calayo.entities;

import java.util.ArrayList;

public class Order {
    private  String productName;
    private String Date;
    private String Time;
    private String status;
    private  String paymentMethod = "COD";
    private double totalCost = 0;
    private String createdAt;
    private String updatedAt;
    private String id ;
    private String image;
    private String serviceID;
    private String userName;
    private String units;
    private String storeName;
    private cartItem item;
    private ArrayList<Item.addOn> addOn;

    // Constructor
    public Order(){}

    public Order(Double totalCost,String image, String Date, String Time, String name, String productName, String units, String id, String storeName, cartItem item, ArrayList<Item.addOn> addOn) {
        this.totalCost = totalCost;
        this.productName = productName;
        this.Date = Date;
        this.Time = Time;
        this.userName =name;
        this.image = image;
        this.units = units;
        this.id = id;
        this.storeName = storeName;
        this.item = item;
        this.addOn = addOn;
        status = "Pending";
    }

    public String getDate() { return Date; }
    public void setDate(String appointmentDate) { this.Date = appointmentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return this.paymentMethod; }
    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public  String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getAppointmentTime() {
        return Time;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.Time = appointmentTime;
    }
    public String getId() {
        return id;
    }
    public void setId(String  id){
        this.id = id;
    }

    public String getServiceID() {
        return serviceID;
    }

    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String Name) {
        this.userName = Name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public ArrayList<Item.addOn> getAddOn() {
        return addOn;
    }

    public void setAddOn(ArrayList<Item.addOn> addOn) {
        this.addOn = addOn;
    }

    public cartItem getItem() {
        return item;
    }

    public void setItem(cartItem item) {
        this.item = item;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}
