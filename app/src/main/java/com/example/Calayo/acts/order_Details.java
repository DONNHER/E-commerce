package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
    private  tempStorage temp;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private RecyclerView addOnsRecycler;
    private addOns addOnAdapter;

    private String image, price, desc, itemName;
    private int quantityCount = 1;
    private ProgressBar progressBar;
    private Runnable timeoutRunnable;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_details);

        // Initialize UI elements and set up listeners
        setupUI();
    }

    @SuppressLint("SetTextI18n")
    private void setupUI() {
        // Find views by ID
        Button btnCheckout = findViewById(R.id.checkout);
        progressBar = findViewById(R.id.progressBar);

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
        temp = tempStorage.getInstance();

        // Set layout manager for add-ons list
        addOnsRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve item details from intent and shared preferences
        image = getIntent().getStringExtra("image");
        price = getIntent().getStringExtra("price");
        desc = getIntent().getStringExtra("description");
        SharedPreferences prefs = getSharedPreferences("selected", MODE_PRIVATE);
        itemName = prefs.getString("name", "Item");

        // Display item details in the UI
        nameText.setText(itemName);
        descriptionText.setText(desc);
        totalCostText.setText(price);
        Glide.with(this).load(image).into(pic);
        quantityText.setText(String.valueOf(quantityCount));

        // Store current logged-in user ID
        temp.setLoggedin(myAuth.getCurrentUser().getUid());

        // Load add-ons for the selected item
        loadAddOns(itemName);

        // Navigate to cart screen when cart icon clicked
        cart.setOnClickListener(v -> startActivity(new Intent(this, AddToCart.class)));

        // Decrease quantity when minus icon clicked, minimum 1
        minus.setOnClickListener(v -> {
            if (quantityCount > 1) quantityCount--;
            quantityText.setText(String.valueOf(quantityCount));
        });

        // Increase quantity when add icon clicked
        add.setOnClickListener(v -> {
            quantityCount++;
            quantityText.setText(String.valueOf(quantityCount));
        });

        // Handle checkout process when checkout button clicked
        btnCheckout.setOnClickListener(view -> {
            btnCheckout.setEnabled(false); // Prevent multiple clicks
            progressBar.setVisibility(View.VISIBLE); // Show spinner

            // Create a new cart item with a unique ID
            String cartItemId = UUID.randomUUID().toString();
            cartItem newItem = new cartItem(image, String.valueOf(quantityCount), itemName, new Date(), cartItemId, String.valueOf(Double.parseDouble(price) + temp.getTotalAddOnPrice()),temp.getAddOnArrayList());

            // Add item to local temp storage
            temp.getCartItemArrayList().add(newItem);

            // Prepare Firestore data
            HashMap<String, Object> itemMap = new HashMap<>();
            itemMap.put("image", newItem.getImage());
            itemMap.put("quantity", newItem.getQuantity());
            itemMap.put("name", newItem.getName());
            itemMap.put("date", newItem.getDate());
            itemMap.put("id", newItem.getId());
            itemMap.put("price",newItem.getPrice());
            itemMap.put("addOns", newItem.getAddOns());

            // Setup 30 seconds timeout
            timeoutRunnable = () -> {
                if (progressBar.getVisibility() == View.VISIBLE) {
                    Toast.makeText(order_Details.this,
                            "Loading is taking longer than usual. Please check your connection.",
                            Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    btnCheckout.setEnabled(true);
                }
            };
            handler.postDelayed(timeoutRunnable, 30000); // Run timeout after 30s

            // Save to Firestore in background thread
            executor.execute(() -> db.collection("users")
                    .document(temp.getLoggedin())
                    .collection("Food Cart Items")
                    .document(cartItemId)
                    .set(itemMap)
                    .addOnSuccessListener(unused -> runOnUiThread(() -> {
                        handler.removeCallbacks(timeoutRunnable); // Cancel timeout
                        progressBar.setVisibility(View.GONE);
                        Log.d("Cart", "Item saved to Firestore");

                        // Proceed to checkout screen
                        Intent checkoutIntent = new Intent(this, checkout.class);
                        temp.setSelectedCartItem(newItem);
                        startActivity(checkoutIntent);
                        finish();
                        btnCheckout.setEnabled(true);
                    }))
                    .addOnFailureListener(e -> runOnUiThread(() -> {
                        handler.removeCallbacks(timeoutRunnable); // Cancel timeout
                        progressBar.setVisibility(View.GONE);
                        Log.e("Cart", "Failed to save item", e);
                        Toast.makeText(this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                        btnCheckout.setEnabled(true);
                    }))
            );
        });

        // Close the activity on back button click
        back.setOnClickListener(view -> finish());
    }

    // Load add-ons related to the selected item
    private void loadAddOns(String name) {
        if (name == null || name.isEmpty()) return;

        executor.execute(() -> 
            temp.loadAllData(() -> temp.loadAllUserData(() -> {
                Item item = temp.searchItem(name);
                if (item != null && item.getAddOns() != null) {
                    runOnUiThread(() -> {
                        addOnAdapter = new addOns(item.getAddOns(), this);
                        addOnsRecycler.setAdapter(addOnAdapter);
                    });
                }
            }))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        temp = tempStorage.getInstance();
        // Reload add-ons when coming back to this screen
        SharedPreferences prefs = getSharedPreferences("selected", MODE_PRIVATE);
        String name = prefs.getString("name", null);
        if (name != null) {
            loadAddOns(name);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown(); // Stop the background thread when activity destroyed
    }
}
