package com.example.Calayo.entities;

public class address {
    private String street;
    private String baranggay;
    private String city;
    private String province;
    private String code;
    private String name;
    public address(){
    }
    public address(String street, String baranggay, String city, String province, String code, String name){
        this.street = street;
        this.city = city;
        this.province = province;
        this.code = code;
        this.baranggay = baranggay;
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public String getFullAddress(){
        return this.street +", "+ this.city +", "+this.province +", "+this.code;
    }

    public String getBaranggay() {
        return baranggay;
    }

    public void setBaranggay(String baranggay) {
        this.baranggay = baranggay;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
