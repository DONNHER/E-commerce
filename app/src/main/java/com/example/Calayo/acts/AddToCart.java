package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Calayo.R;
import com.example.Calayo.adapters.addToCartAdapt;
import com.example.Calayo.entities.Order;
import com.example.Calayo.entities.cartItem;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Displays cart items and allows user to proceed to checkout.
 * Handles Select All logic and updates order preview.
 */
public class AddToCart extends AppCompatActivity {

    private static final String TAG = "AddToCart";

    private RecyclerView cartRecyclerView;
    private addToCartAdapt adapter;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth myAuth = FirebaseAuth.getInstance();
    private final tempStorage temp = tempStorage.getInstance();

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_to_cart);

        Button backBtn = findViewById(R.id.btnBack);
        Button checkoutBtn = findViewById(R.id.checkout);
//        TextView unitsTextView = findViewById(R.id.units);
//        CheckBox selectAllCheckbox = findViewById(R.id.checkboxAll);
        cartRecyclerView = findViewById(R.id.CartItems_Recycler3);

        // Back button
        backBtn.setOnClickListener(view -> finish());

        // Initialize cart RecyclerView
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<cartItem> cartItems = temp.getCartItemArrayList();

        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
        } else {
            adapter = new addToCartAdapt(cartItems, this);
            cartRecyclerView.setAdapter(adapter);
//            unitsTextView.setText(String.valueOf(cartItems.size()));
        }

        // Checkout button
        checkoutBtn.setOnClickListener(view -> {
            if (temp.getSelectedCartItem() == null) {
                Toast.makeText(this, "Please select an item to checkout", Toast.LENGTH_SHORT).show();
                return;
            }else if (cartItems.isEmpty()) {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, checkout.class);
            executor.execute(() -> temp.loadAllData(() ->
                    runOnUiThread(() -> startActivity(intent)))
            );
        });

//        // Select All checkbox logic
//        selectAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (cartItems == null) return;
//
//            temp.getCheckOutArrayList().clear(); // Clear previous selections
//
//            for (cartItem item : cartItems) {
//                item.setSelected(isChecked);
//                if (isChecked) {
//                    Order order = new Order(
//                            item.getImage(),
//                            new Date().toString(),
//                            new Date().toString(),
//                            temp.getLoggedin(),
//                            item.getName(),
//                            "", ""
//                    );
//                    temp.getCheckOutArrayList().add(order);
//                }
//            }
//
//            adapter.notifyDataSetChanged(); // Update UI
//            Log.d(TAG, (isChecked ? "Selected" : "Deselected") + " all cart items");
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown(); // Clean up thread
    }
}
