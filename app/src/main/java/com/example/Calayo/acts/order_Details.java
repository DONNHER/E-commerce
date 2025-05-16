package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.Calayo.R;
import com.example.Calayo.adapters.addOns;
import com.example.Calayo.adapters.product_adapt;
import com.example.Calayo.entities.Item;
import com.example.Calayo.entities.cartItem;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class order_Details extends AppCompatActivity {
    private String date, time, name2, services;
    private double totalCost;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private addOns addOn;
    private ArrayList<Item.addOn> addOnsItems = new ArrayList<>();
    private RecyclerView addOnsRecycler;

    private tempStorage  temp;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    FirebaseAuth myAuth = FirebaseAuth.getInstance();

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
        String cartItemId = UUID.randomUUID().toString();
        ImageView cart = findViewById(R.id.cart);
        temp = tempStorage.getInstance();
        cart.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddToCart.class);
            startActivity(intent);
        });


        minus.setOnClickListener(v -> {
            int unit = Integer.parseInt(quantity.getText().toString().trim());
            if (unit > 1) {  // Only decrease if the unit is greater than 1
                unit--;
                quantity.setText("   " + unit + "   ");
            } else {
                quantity.setText("   1   ");  // Prevent negative or zero quantity
            }
        });

        add.setOnClickListener(v -> {
            int unit = Integer.parseInt(quantity.getText().toString().trim());
            unit++;
            quantity.setText("   " + unit + "   ");
        });

        addOnsRecycler = findViewById(R.id.OrderDetails);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        addOnsRecycler.setLayoutManager(layoutManager);



        String price = getIntent().getStringExtra("price");
        tCost.setText(price);

        String des = getIntent().getStringExtra("description");
        description.setText(des);

        SharedPreferences sharedPreferences = getSharedPreferences("selected", MODE_PRIVATE);
        String name = sharedPreferences.getString("name", null);
        Name.setText(name);

        String image = getIntent().getStringExtra("image");
        Glide.with(this)
                .load(image)
                .into(pic);
        temp.setLoggedin(myAuth.getCurrentUser().getUid());
        executor.execute(() -> temp.loadAllData(()->temp.loadAllUserData(() ->{} )));
        addOn = new addOns(temp.searchItem(name).getAddOns(), this);
        addOnsRecycler.setAdapter(addOn);


        btncheckout.setOnClickListener(view2 -> {
             // This will create a unique ID for each item
            Intent intent = new Intent(this, checkout.class);
            intent.putExtra("quantity", quantity.getText().toString().trim());
            intent.putExtra("name", name);
            intent.putExtra("price", price);
            intent.putExtra("image", image);
            cartItem newItem = new cartItem(image, quantity.getText().toString().trim(), name, new Date(),cartItemId,price);
            temp.getCartItemArrayList().add(newItem);
            intent.putExtra(temp.getLoggedin(),cartItemId);

// Inside the executor.execute method

            executor.execute(() -> {
                // Generate a new UUID for the cart item
                // Create the HashMap for Firestore
                HashMap<String, Object> itemMap = new HashMap<>();
                itemMap.put("image", newItem.getImage());
                itemMap.put("quantity", newItem.getQuantity());
                itemMap.put("name", newItem.getName());
                itemMap.put("date", newItem.getDate());
                itemMap.put("id", cartItemId);// or format if n   eeded
                // Use the generated UUID as the document ID
                db.collection("users")
                        .document(temp.getLoggedin())
                        .collection("Food Cart Items")
                        .document(cartItemId) // Use UUID here
                        .set(itemMap)
                        .addOnSuccessListener(documentReference -> Log.d("Cart", "Added successfully"))
                        .addOnFailureListener(e -> Log.e("Cart", "Error adding", e));
            });
            temp.getTotalAddOnPrice();
            // Ensure UI updates happen on the main thread
            runOnUiThread(() ->
                    startActivity(intent));
        });
        back.setOnClickListener(view4 -> finish());
    }
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("selected", MODE_PRIVATE);
        String name = sharedPreferences.getString("name", null);
        temp.setLoggedin(myAuth.getCurrentUser().getUid());
        executor.execute(() -> temp.loadAllData(() -> temp.loadAllUserData(() -> {
        })));
        addOn = new addOns(temp.searchItem(name).getAddOns(), this);
        addOnsRecycler.setAdapter(addOn);
    }
}
