package com.example.Calayo.helper;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import com.example.Calayo.entities.Item;
import com.example.Calayo.entities.address;

public class tempStorage {
    private ArrayList<Item.addOn> addOnArrayList;
    private ArrayList<address> addressList;
    private address selectedAddress;

    private tempStorage(){
        addOnArrayList = new ArrayList<>();
    }

    private static final class InstanceHolder {
        private static final tempStorage instance = new tempStorage();
    }

    public static tempStorage getInstance() {
        return InstanceHolder.instance;
    }


    public ArrayList<Item.addOn> getAddOnArrayList() {
        return addOnArrayList;
    }

    public void setAddOnArrayList(ArrayList<Item.addOn> addOnArrayList) {
        this.addOnArrayList = addOnArrayList;
    }
    public double getTotalAddOnPrice(){
        double j = 0.0;
        for (Item.addOn i : addOnArrayList){
            j+=i.getAddOnPrice();
        }
        return j;
    }
    public address getSelectedAddress(){
        return this.selectedAddress;
    }
    public void setSelectedAddress(address s){
         this.selectedAddress = s;
    }
}
