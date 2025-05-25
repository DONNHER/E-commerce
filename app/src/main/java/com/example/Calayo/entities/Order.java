package com.example.Calayo.entities;

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

    // Constructor
    public Order(){}
    public Order(String image, String Date, String Time, String name,String productName,String units,String id) {
        this.productName = productName;
        this.Date = Date;
        this.Time = Time;
        this.userName =name;
        this.image = image;
        this.units = units;
        this.id = id;
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
}
