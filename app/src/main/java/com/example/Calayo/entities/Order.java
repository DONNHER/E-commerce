package com.example.Calayo.entities;

public class Order {
    private  String productName;
    private String appointmentDate;
    private String appointmentTime;
    private String status = "Pending";
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
    public Order(String image, String appointmentDate, String appointmentTime, String name,String productName,String units) {
        this.productName = productName;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.userName =name;
        this.image = image;
        this.units = units;
    }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }

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
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
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
