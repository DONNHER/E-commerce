package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Calayo.R;
import com.example.Calayo.adapters.addOns;
import com.example.Calayo.entities.Order;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class checkout  extends AppCompatActivity {
    private double totalCost;
    private tempStorage temp = tempStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private com.example.Calayo.adapters.addOns addOn;
    private RecyclerView addOnsRecycler;
    ExecutorService executor = Executors.newSingleThreadExecutor();

    @SuppressLint({"WrongViewCast", "MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout);
        Button checkout = findViewById(R.id.checkout);
        TextView cost = findViewById(R.id.Price3);
        TextView tCost = findViewById(R.id.totalPrice);
        TextView Name = findViewById(R.id.name);
        TextView Name2 = findViewById(R.id.name2);
        TextView Name3 = findViewById(R.id.name3);
        Button back = findViewById(R.id.btnBack);
        TextView units = findViewById(R.id.units);
        TextView address = findViewById(R.id.address);

        TextView header = findViewById(R.id.header);
        SharedPreferences preferences1 = getSharedPreferences("SelectedAddress", MODE_PRIVATE);
        SharedPreferences preferences2 = getSharedPreferences("typeAddress", MODE_PRIVATE);

        if (preferences1.getString("SelectedAddress", null) == null || Objects.requireNonNull(preferences1.getString("SelectedAddress", null)).isEmpty()) {
            Intent intent = new Intent(this, myAddress.class);
            Toast.makeText(this, "Please select address first.", Toast.LENGTH_LONG).show();
            startActivity(intent);
            recreate();
        }
        address.setText(preferences1.getString("SelectedAddress", "default_value"));
        header.setText(preferences2.getString("typeAddress", "default_value"));

        addOnsRecycler = findViewById(R.id.OrderSummary_Recycler3);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        addOnsRecycler.setLayoutManager(layoutManager);
        addOn = new addOns(temp.getAddOnArrayList(), checkout.this);
        addOnsRecycler.setAdapter(addOn);

        String price = getIntent().getStringExtra("price");
        cost.setText(price);


        String des = getIntent().getStringExtra("quantity");
        units.setText("(" + des + "item):");
        Name3.setText(" " + des + "x");


        totalCost = Integer.parseInt(des) * Double.parseDouble(price);
        String id = getIntent().getStringExtra("id");

        String name = getIntent().getStringExtra("name");
        Name.setText(name);
        Name2.setText(name);

        String pic = getIntent().getStringExtra("image");
        tCost.setText("" + (temp.getTotalAddOnPrice() + totalCost));

        checkout.setOnClickListener(view2 -> {
            String orderItemId = UUID.randomUUID().toString();
            Order newOrder = new Order(pic, new Date().toString(), new Date().toString(), temp.getLoggedin(), name, des, id);

            HashMap<String, Object> orderMap = new HashMap<>();
            orderMap.put("image", pic);
            orderMap.put("Time", newOrder.getAppointmentTime());
            orderMap.put("Date", newOrder.getDate());
            orderMap.put("userName", newOrder.getUserName());
            orderMap.put("productName", newOrder.getProductName());
            orderMap.put("units", des);

            // Save order first
            executor.execute(() -> {
                db.collection("users")
                        .document(temp.getLoggedin())
                        .collection("Orders")
                        .document(orderItemId)
                        .set(orderMap)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Cart", "Order added successfully");

                            // Now delete item from cart
                            db.collection("users")
                                    .document(temp.getLoggedin())
                                    .collection("Food Cart Items")
                                    .document(id)
                                    .delete()
                                    .addOnSuccessListener(unused -> {
                                        Log.d("Cart", "Cart item deleted successfully");

                                        // Update tempStorage and go to OrderSuccessDialog
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Cart", "Failed to delete cart item", e);
                                        Toast.makeText(checkout.this, "Failed to delete cart item", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Cart", "Failed to add order", e);
                            Toast.makeText(checkout.this, "Failed to add order", Toast.LENGTH_SHORT).show();
                        });
            });

            temp.getCheckOutArrayList().add(newOrder);
            temp.deleteItem(id);
            Intent intent = new Intent(checkout.this, OrderSuccessDialog.class);
            startActivity(intent);
            finish();
        });


        back.setOnClickListener(view4 -> finish());

    }

}
