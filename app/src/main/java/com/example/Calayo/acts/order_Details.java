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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.Calayo.R;
import com.example.Calayo.adapters.addOns;
import com.example.Calayo.entities.Item;
import com.example.Calayo.entities.cartItem;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class order_Details extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth myAuth = FirebaseAuth.getInstance();
    private final tempStorage temp = tempStorage.getInstance();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private RecyclerView addOnsRecycler;
    private addOns addOnAdapter;

    private String image, price, desc, itemName;
    private int quantityCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_details);

        setupUI();
    }

    @SuppressLint("SetTextI18n")
    private void setupUI() {
        Button btnCheckout = findViewById(R.id.checkout);
        Button back = findViewById(R.id.back);
        ImageView minus = findViewById(R.id.minus);
        ImageView add = findViewById(R.id.add);
        ImageView cart = findViewById(R.id.cart);
        ImageView pic = findViewById(R.id.image_order);
        TextView quantityText = findViewById(R.id.units);
        TextView nameText = findViewById(R.id.name);
        TextView descriptionText = findViewById(R.id.description);
        TextView totalCostText = findViewById(R.id.priceOrder);
        addOnsRecycler = findViewById(R.id.OrderDetails);

        addOnsRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Get data
        image = getIntent().getStringExtra("image");
        price = getIntent().getStringExtra("price");
        desc = getIntent().getStringExtra("description");

        SharedPreferences prefs = getSharedPreferences("selected", MODE_PRIVATE);
        itemName = prefs.getString("name", "Item");

        nameText.setText(itemName);
        descriptionText.setText(desc);
        totalCostText.setText(price);
        Glide.with(this).load(image).into(pic);
        quantityText.setText(String.valueOf(quantityCount));

        temp.setLoggedin(myAuth.getCurrentUser().getUid());

        loadAddOns(itemName);

        cart.setOnClickListener(v -> startActivity(new Intent(this, AddToCart.class)));

        minus.setOnClickListener(v -> {
            if (quantityCount > 1) quantityCount--;
            quantityText.setText(String.valueOf(quantityCount));
        });

        add.setOnClickListener(v -> {
            quantityCount++;
            quantityText.setText(String.valueOf(quantityCount));
        });

        btnCheckout.setOnClickListener(view -> {
            String cartItemId = UUID.randomUUID().toString();
            cartItem newItem = new cartItem(
                    image,
                    String.valueOf(quantityCount),
                    itemName,
                    new Date(),
                    cartItemId,
                    price
            );

            // Avoid duplicate rapid taps
            btnCheckout.setEnabled(false);

            temp.getCartItemArrayList().add(newItem);

            executor.execute(() -> {
                HashMap<String, Object> itemMap = new HashMap<>();
                itemMap.put("image", newItem.getImage());
                itemMap.put("quantity", newItem.getQuantity());
                itemMap.put("name", newItem.getName());
                itemMap.put("date", newItem.getDate());
                itemMap.put("id", cartItemId);

                db.collection("users")
                        .document(temp.getLoggedin())
                        .collection("Food Cart Items")
                        .document(cartItemId)
                        .set(itemMap)
                        .addOnSuccessListener(unused -> runOnUiThread(() -> {
                            Log.d("Cart", "Item added to Firestore");
                            Intent checkoutIntent = new Intent(this, checkout.class);
                            checkoutIntent.putExtra("quantity", newItem.getQuantity());
                            checkoutIntent.putExtra("name", newItem.getName());
                            checkoutIntent.putExtra("price", newItem.getPrice());
                            checkoutIntent.putExtra("image", newItem.getImage());
                            startActivity(checkoutIntent);
                            btnCheckout.setEnabled(true); // re-enable
                        }))
                        .addOnFailureListener(e -> runOnUiThread(() -> {
                            Log.e("Cart", "Failed to add item", e);
                            Toast.makeText(this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                            btnCheckout.setEnabled(true); // re-enable
                        }));
            });
        });

        back.setOnClickListener(view -> finish());
    }

    private void loadAddOns(String name) {
        if (name == null || name.isEmpty()) return;

        executor.execute(() -> temp.loadAllData(() -> temp.loadAllUserData(() -> {
            Item item = temp.searchItem(name);
            if (item != null && item.getAddOns() != null) {
                runOnUiThread(() -> {
                    addOnAdapter = new addOns(item.getAddOns(), this);
                    addOnsRecycler.setAdapter(addOnAdapter);
                });
            }
        })));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("selected", MODE_PRIVATE);
        String name = prefs.getString("name", null);
        if (name != null) {
            loadAddOns(name);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown(); // Clean up background thread
    }
}
