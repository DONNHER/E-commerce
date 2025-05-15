package com.example.Calayo.entities;

import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

public class cartItem  {
    private String image;
    private String quantity;
    private String name;
    private Date date;
    private String id;
    private boolean isSelected;
    private String price;
    public cartItem(){}
    public cartItem(String image, String quantity, String name, Date date, String id, String price){
        this.image = image;
        this.quantity = quantity;
        this.name = name;
        this.date = date;
        this.id = id;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String pic) {
        this.image = pic;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
