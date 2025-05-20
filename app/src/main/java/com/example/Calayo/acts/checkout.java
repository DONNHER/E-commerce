package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Calayo.R;
import com.example.Calayo.adapters.addOns;
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

/**
 * Checkout activity where the user confirms their order and makes a purchase.
 */
public class checkout extends AppCompatActivity {
    private double totalCost;
    private final tempStorage temp = tempStorage.getInstance(); // Shared temporary data across app
    private final FirebaseFirestore db = FirebaseFirestore.getInstance(); // Firestore instance
    private addOns addOn;
    private RecyclerView addOnsRecycler;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(); // Background thread

    @SuppressLint({"WrongViewCast", "MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout); // Set the layout

        // Get UI elements
        Button checkoutBtn = findViewById(R.id.checkout);
        Button backBtn = findViewById(R.id.btnBack);
        TextView costText = findViewById(R.id.Price3);
        TextView totalCostText = findViewById(R.id.totalPrice);
        TextView nameText = findViewById(R.id.name);
        TextView nameText2 = findViewById(R.id.name2);
        TextView nameText3 = findViewById(R.id.name3);
        TextView unitsText = findViewById(R.id.units);
        TextView addressText = findViewById(R.id.address);
        TextView headerText = findViewById(R.id.header);

        // Retrieve selected address from SharedPreferences
        SharedPreferences preferences1 = getSharedPreferences("SelectedAddress", MODE_PRIVATE);
        SharedPreferences preferences2 = getSharedPreferences("typeAddress", MODE_PRIVATE);
        String addressStr = preferences1.getString("SelectedAddress", "");
        String addressType = preferences2.getString("typeAddress", "");

        // If no address is selected, prompt user and redirect
        if (addressStr == null || addressStr.isEmpty()) {
            Toast.makeText(this, "Please select address first.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, myAddress.class));
            finish();
            return;
        }

        // Show selected address
        addressText.setText(addressStr);
        headerText.setText(addressType);

        // Setup RecyclerView to display selected add-ons
        addOnsRecycler = findViewById(R.id.OrderSummary_Recycler3);
        addOnsRecycler.setLayoutManager(new LinearLayoutManager(this));
        addOn = new addOns(temp.getAddOnArrayList(), checkout.this);
        addOnsRecycler.setAdapter(addOn);

        // Get data from the previous activity using Intent
        ???
        // DIri boss ilisi tanan gamita ang attribute sa cartItem
        cartItem cartItem;
        String priceStr = intent.getStringExtra("price");
        String quantityStr = intent.getStringExtra("quantity");
        String name = intent.getStringExtra("name");
        String id = intent.getStringExtra("id");
        String image = intent.getStringExtra("image");

        // If any data is missing, return to previous screen
        if (priceStr == null || quantityStr == null || name == null || id == null || image == null) {
            Toast.makeText(this, "Invalid data received. Please go back.", Toast.LENGTH_LONG).show();
            Log.e("checkout", "Missing intent data");
            finish();
            return;
        }

        try {
            // Parse and calculate total cost
            double price = Double.parseDouble(priceStr);
            int quantity = Integer.parseInt(quantityStr);
            totalCost = price * quantity;

            // Display order summary
            costText.setText(priceStr);
            unitsText.setText("(" + quantity + " item):");
            nameText3.setText(" " + quantity + "x");
            nameText.setText(name);
            nameText2.setText(name);
            totalCostText.setText(String.valueOf(temp.getTotalAddOnPrice() + totalCost));
        } catch (NumberFormatException e) {
            // If parsing fails, return to previous screen
            Toast.makeText(this, "Failed to parse order data.", Toast.LENGTH_SHORT).show();
            Log.e("checkout", "NumberFormatException", e);
            finish();
            return;
        }

        // Handle the checkout button click
        checkoutBtn.setOnClickListener(view -> {
            // Generate a unique ID for the order
            String orderItemId = UUID.randomUUID().toString();

            // Create an Order object
            Order newOrder = new Order(
                    image,
                    new Date().toString(),
                    new Date().toString(),
                    temp.getLoggedin(),
                    name,
                    quantityStr,
                    id
            );

            // Create a map to store in Firestore
            HashMap<String, Object> orderMap = new HashMap<>();
            orderMap.put("image", image);
            orderMap.put("Time", newOrder.getAppointmentTime());
            orderMap.put("Date", newOrder.getDate());
            orderMap.put("userName", newOrder.getUserName());
            orderMap.put("productName", newOrder.getProductName());
            orderMap.put("units", quantityStr);

            // Perform Firebase operations on background thread
            executor.execute(() -> {
                db.collection("users")
                        .document(temp.getLoggedin())
                        .collection("Orders")
                        .document(orderItemId)
                        .set(orderMap)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Cart", "Order added successfully");

                            // Remove the item from the cart
                            db.collection("users")
                                    .document(temp.getLoggedin())
                                    .collection("Food Cart Items")
                                    .document(id)
                                    .delete()
                                    .addOnSuccessListener(unused -> Log.d("Cart", "Cart item deleted successfully"))
                                    .addOnFailureListener(e -> {
                                        Log.e("Cart", "Failed to delete cart item", e);
                                        runOnUiThread(() -> Toast.makeText(checkout.this, "Failed to delete cart item", Toast.LENGTH_SHORT).show());
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Cart", "Failed to add order", e);
                            runOnUiThread(() -> Toast.makeText(checkout.this, "Failed to add order", Toast.LENGTH_SHORT).show());
                        });
            });

            // Update local temp storage
            temp.getCheckOutArrayList().add(newOrder);
            temp.deleteItem(id);

            // Go to success screen
            startActivity(new Intent(checkout.this, OrderSuccessDialog.class));
            finish();
        });

        // Handle back button click
        backBtn.setOnClickListener(view -> finish());
    }
}
