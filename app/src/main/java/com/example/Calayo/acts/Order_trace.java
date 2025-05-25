package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Calayo.R;
import com.example.Calayo.adapters.addOns;
import com.example.Calayo.adapters.order_adaptor;
import com.example.Calayo.entities.Order;
import com.example.Calayo.helper.tempStorage;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Order_trace  extends AppCompatActivity {

    private static final String TAG = "Order Information";
    private RecyclerView addOnsRecyclerView;
    private addOns addOnAdapter;

    private RecyclerView OrderRecyclerView;
    private order_adaptor orderAdapter;
    private ArrayList<Order> orderSelected;
    private tempStorage temp;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_tracker);

        try {
            temp = tempStorage.getInstance();
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize tempStorage: " + e.getMessage());
            return; // Cannot proceed if temp is not initialized
        }

//        initNavigationButtons();
//        setupOrderInfoRecycler();
    }

    /**
     * Sets up navigation buttons (home, address, profile, etc.)
     */
    private void initNavigationButtons() {
        try {
            ImageView home = findViewById(R.id.home);
            Button track = findViewById(R.id.track);
            ImageView address = findViewById(R.id.address);
            ImageView menu = findViewById(R.id.foodMenu);
            ImageView history = findViewById(R.id.history);
            ImageView profile = findViewById(R.id.profile);
            TextView total = findViewById(R.id.totalPrice);


            total.setText(String.valueOf(temp.getSelectedOrder().getTotalCost()));

            if (home != null) {
                home.setOnClickListener(v -> restartActivity());
            }

            if (address != null) {
                address.setOnClickListener(v -> startActivity(new Intent(this, myAddress.class)));
            }

            if (menu != null) {
                menu.setOnClickListener(v -> startActivity(new Intent(this, productsAct.class)));
            }

            if (history != null) {
                history.setOnClickListener(v -> startActivity(new Intent(this, transactions.class)));
            }

            if (profile != null) {
                profile.setOnClickListener(v -> startActivity(new Intent(this, settingAct.class)));
            }
        } catch (Exception e) {
            Log.e(TAG, "Navigation setup failed: " + e.getMessage());
        }
    }

    /**
     * Sets up the Order Information
     */
    private void setupOrderInfoRecycler() {
        try {
            OrderRecyclerView = findViewById(R.id.Order_Recycler3);
            if (OrderRecyclerView != null) {
                OrderRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                // Setup RecyclerView to display selected add-ons
                addOnsRecyclerView = findViewById(R.id.OrderSummary_Recycler3);
                addOnsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

                Order order = temp.getSelectedOrder();
                if (orderSelected == null ) orderSelected = new ArrayList<>();
                orderSelected.add(order);
                orderAdapter = new order_adaptor(orderSelected, this);
                OrderRecyclerView.setAdapter(orderAdapter);
                addOnAdapter = new addOns(order.getAddOn(), this);
                addOnsRecyclerView.setAdapter(addOnAdapter);
            } else {
                Log.w(TAG, "Products RecyclerView not found.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to set up products RecyclerView: " + e.getMessage());
        }
    }


    /**
     * Refreshes data on resume.
     */
//    @Override
//    protected void onResume() {
//        super.onResume();
//        try {
//            executor.execute(() -> temp.loadAllData(() ->
//                    temp.loadAllUserData(() -> runOnUiThread(() -> {
//                        if (products != null) {
//                            ArrayList<Item> items = temp.getItemArrayList();
//                            if (items == null) items = new ArrayList<>();
//                            productAdapter = new product_adapt(items, this);
//                            products.setAdapter(productAdapter);
//                            Log.d(TAG, "Product list updated.");
//                        }
//                    }))
//            ));
//        } catch (Exception e) {
//            Log.e(TAG, "Error during resume data load: " + e.getMessage());
//        }
//    }

    /**
     * Utility method to restart this activity.
     */
    private void restartActivity() {
        try {
            Intent intent = new Intent(this, UserDashboardAct.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Failed to restart activity: " + e.getMessage());
        }
    }
    /**
     * Redirect to address dashboard
     */
    public void location(View view){
        Intent intent = new Intent(this, myAddress.class);
        startActivity(intent);
    }
}
