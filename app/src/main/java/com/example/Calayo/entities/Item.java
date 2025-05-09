package com.example.Calayo.entities;

import java.util.ArrayList;

public class Item {
    public ArrayList<addOn> getAddOns() {
        return addOns;
    }

    public void setAddOns(ArrayList<addOn> addOns) {
        this.addOns = addOns;
    }

    public static class addOn{
        private String addOnName;
        private Double addOnPrice;
        private String itemName;
        private boolean isChecked;
        public addOn(){}
        public addOn(String add, double addOnPrice, String item){
            this.addOnName = add;
            this.addOnPrice = addOnPrice;
            this.itemName = item;
        }

        public String getAddOnName() {
            return addOnName;
        }

        public void setAddOnName(String name) {
            this.addOnName = name;
        }

        public double getAddOnPrice() {
            return addOnPrice;
        }

        public void setAddOnPrice(double price) {
            this.addOnPrice = price;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String item) {
            this.itemName = item;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }

    private  String id ;
    private  String Image;
    private  double price;
    private  String name;
    private int quantity;
    private String description;
    private String ingredients;
    private String category;
    private String type;
    private boolean isFavorite;

    private ArrayList<addOn> addOns;

    // Constructor
    public Item(){}
    public Item(double Price,String image,String Name,int quantity,String description) {
        Image = image;
        price = Price;
        name = Name;
        this.quantity = quantity;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
