package com.example.Calayo.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.Calayo.R;
import com.example.Calayo.acts.AddToCart;
import com.example.Calayo.acts.checkout;
import com.example.Calayo.adapters.AddsADaptor;
import com.example.Calayo.adapters.addOns;
import com.example.Calayo.adapters.product_adapt;
import com.example.Calayo.entities.Item;
import com.example.Calayo.entities.cartItem;
import com.example.Calayo.helper.tempStorage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class order_Details extends AppCompatActivity {
    private String date, time, name2, services;
    private double totalCost;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private addOns addOn;
    private ArrayList<Item.addOn> addOnsItems = new ArrayList<>();
    private RecyclerView addOnsRecycler;

    private tempStorage temp = tempStorage.getInstance();

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_details);
        Button btncheckout = findViewById(R.id.checkout);
        TextView description = findViewById(R.id.description);
        TextView tCost = findViewById(R.id.priceOrder);
        TextView Name = findViewById(R.id.name);
        Button back = findViewById(R.id.back);
        ImageView pic = findViewById(R.id.image_order);
        ImageView minus = findViewById(R.id.minus);
        ImageView add = findViewById(R.id.add);
        TextView quantity = findViewById(R.id.units);

        ImageView cart = findViewById(R.id.cart);
        cart.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddToCart.class);
            startActivity(intent);
        });


        minus.setOnClickListener(v -> {
                int unit = Integer.parseInt(quantity.getText().toString().trim());
                if (unit < 0) {
                    quantity.setText("1");
                }
                if (unit > 1) {
                    unit--;
                    quantity.setText("   " + unit + "   ");
                }
            });
        add.setOnClickListener(v -> {
                    int unit = Integer.parseInt(quantity.getText().toString().trim());
                    unit++;
                    quantity.setText("   " + unit + "   ");
                });

        addOnsRecycler =findViewById(R.id.OrderDetails);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        String price = getIntent().getStringExtra("price");
        tCost.setText(price);

        String des = getIntent().getStringExtra("description");
        description.setText(des);
        name2= getIntent().getStringExtra("name");
        Name.setText(name2);

        String image = getIntent().getStringExtra("image");
        Glide.with(this)
                .load(image)
                .into(pic);

        String name = getIntent().getStringExtra("name");

        db.collection("items").document(name).collection("addOns").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                addOnsItems.clear();
                for (QueryDocumentSnapshot d : task.getResult()) {
                    Item.addOn newItem = d.toObject(Item.addOn.class);
                    addOnsItems.add(newItem);
                }
                addOn = new addOns(addOnsItems,order_Details.this);
                addOnsRecycler.setAdapter(addOn);
                addOnsRecycler.setLayoutManager(layoutManager);
            }
    });


        btncheckout.setOnClickListener(view2 ->{
            Intent intent = new Intent(this, checkout.class);
            intent.putExtra("quantity",quantity.getText().toString().trim());
            intent.putExtra("name", name2);
            intent.putExtra("price",price);
            intent.putExtra("image",image);
            cartItem newItem = new cartItem(image,quantity.getText().toString().trim(),name,new Date());
            int i = temp.findNodeInsertion(newItem);
            temp.getCartItemArrayList().add(i,newItem);
            Toast.makeText(this, ""+temp.getCartItemArrayList().size(), Toast.LENGTH_SHORT).show();
            startActivity(intent);
        });
        back.setOnClickListener(view4 -> finish());
    }

}
