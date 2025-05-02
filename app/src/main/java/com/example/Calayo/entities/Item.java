package com.example.Calayo.entities;

public class Item {

    private  String id ;
    private  String Image;
    private  double price;
    private  String name;
    private int quantity;


    // Constructor
    public Item(){}
    public Item(double Price,String image,String Name,int quantity) {
        Image = image;
        price = Price;
        name = Name;
        this.quantity = quantity;
    }

    public  String getName() {
        return name;
    }

    public  void setName(String name) {
        this.name = name;
    }


    // Getters and Setters
    public String getId() {
        return id;
    }
    public  double getPrice() {
        return price;
    }

    public  void setPrice(double Price) {
        price = Price;
    }


    @Override
    public String toString() {
        return "";
    }

    public  String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }


    public void setId(String id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
