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
import com.example.Calayo.adapters.check_list_adapter;
import com.example.Calayo.entities.Order;
import com.example.Calayo.entities.cartItem;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class checkout  extends AppCompatActivity {
    private double totalCost ;
    private tempStorage temp = tempStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private check_list_adapter adapter;
    private RecyclerView addOnsRecycler;
    ExecutorService executor = Executors.newSingleThreadExecutor();

    @SuppressLint({"WrongViewCast", "MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout);
        GridLayout checkout = findViewById(R.id.checkout);
        TextView tCost = findViewById(R.id.totalPrice);
        Button back = findViewById(R.id.btnBack);
        TextView address = findViewById(R.id.address);

        TextView header = findViewById(R.id.header);
        SharedPreferences preferences1 = getSharedPreferences("SelectedAddress",MODE_PRIVATE);
        SharedPreferences preferences2 = getSharedPreferences("typeAddress",MODE_PRIVATE);

        if(preferences1.getString("SelectedAddress",null) == null || Objects.requireNonNull(preferences1.getString("SelectedAddress", null)).isEmpty()){
            Intent intent = new Intent(this, myAddress.class);
            Toast.makeText(this,"Please select address first.",Toast.LENGTH_LONG).show();
            startActivity(intent);
            recreate();
        }
        address.setText(preferences1.getString("SelectedAddress","default_value"));
        header.setText(preferences2.getString("typeAddress","default_value"));

        addOnsRecycler = findViewById(R.id.OrderSummary_Recycler3);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        addOnsRecycler.setLayoutManager(layoutManager);

        adapter = new check_list_adapter(temp.getCartItemArrayList(), checkout.this);
        addOnsRecycler.setAdapter(adapter);


        totalCost =  Integer.parseInt("123") * Double.parseDouble("142");
        String id = getIntent().getStringExtra("id");

        String pic = getIntent().getStringExtra("image");
        tCost.setText(""+(temp.getTotalAddOnPrice()+ totalCost));

        checkout.setOnClickListener(view2 -> {
            for(cartItem c : temp.getCartItemArrayList()) {
                if (c.isSelected()) {
                    String orderItemId = UUID.randomUUID().toString();
                    Order newOrder = new Order(pic, new Date().toString(), new Date().toString(), temp.getLoggedin(), c.getName(), c.getQuantity(), id);

                    HashMap<String, Object> orderMap = new HashMap<>();
                    orderMap.put("image", pic);
                    orderMap.put("Time", newOrder.getAppointmentTime());
                    orderMap.put("Date", newOrder.getDate());
                    orderMap.put("userName", newOrder.getUserName());
                    orderMap.put("productName", newOrder.getProductName());
                    orderMap.put("units", c.getQuantity());

                    // Save order first
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
                                            temp.getCheckOutArrayList().add(newOrder);
                                            temp.deleteItem(id);
                                            Intent intent = new Intent(checkout.this, OrderSuccessDialog.class);
                                            startActivity(intent);
                                            finish();
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
                }
            }
        });
        back.setOnClickListener(view4 -> finish());

    }
}
