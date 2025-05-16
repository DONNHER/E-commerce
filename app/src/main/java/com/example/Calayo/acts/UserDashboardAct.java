package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.Calayo.R;
import com.example.Calayo.adapters.AddsADaptor;
import com.example.Calayo.adapters.product_adapt;
import com.example.Calayo.entities.Item;
import com.example.Calayo.entities.adds;
import com.example.Calayo.helper.tempStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * UserDashboardAct displays the main dashboard with products and ads.
 * Handles navigation and refreshes data upon resume.
 */
public class UserDashboardAct extends AppCompatActivity {

    private static final String TAG = "UserDashboardAct";

    private RecyclerView products;
    private product_adapt productAdapter;

    private RecyclerView addsRecyclerView;
    private AddsADaptor addsAdapter;

    private tempStorage temp;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_d_board);

        try {
            temp = tempStorage.getInstance();
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize tempStorage: " + e.getMessage());
            return; // Cannot proceed if temp is not initialized
        }

        initNavigationButtons();
        setupProductsRecycler();
        setupAddsRecycler();
    }

    /**
     * Sets up navigation buttons (home, address, profile, etc.)
     */
    private void initNavigationButtons() {
        try {
            ImageView home = findViewById(R.id.home);
            ImageView address = findViewById(R.id.address);
            ImageView menu = findViewById(R.id.foodMenu);
            ImageView history = findViewById(R.id.history);
            ImageView profile = findViewById(R.id.profile);

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
     * Sets up the products RecyclerView with items.
     */
    private void setupProductsRecycler() {
        try {
            products = findViewById(R.id.Products_Recycler);
            if (products != null) {
                products.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

                ArrayList<Item> items = temp.getItemArrayList();
                if (items == null) items = new ArrayList<>();
                productAdapter = new product_adapt(items, this);
                products.setAdapter(productAdapter);
            } else {
                Log.w(TAG, "Products RecyclerView not found.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to set up products RecyclerView: " + e.getMessage());
        }
    }

    /**
     * Sets up the horizontal ads carousel.
     */
    private void setupAddsRecycler() {
        try {
            addsRecyclerView = findViewById(R.id.Adds_Recycler);
            if (addsRecyclerView != null) {
                addsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

                SnapHelper snapHelper = new LinearSnapHelper();
                snapHelper.attachToRecyclerView(addsRecyclerView);

                addsAdapter = new AddsADaptor(temp.getAddsArrayList(), this);
                addsRecyclerView.setAdapter(addsAdapter);
            } else {
                Log.w(TAG, "Adds RecyclerView not found.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to set up ads RecyclerView: " + e.getMessage());
        }
    }

    /**
     * Refreshes data on resume.
     */
    @Override
    protected void onResume() {
        super.onResume();
        try {
            executor.execute(() -> temp.loadAllData(() ->
                    temp.loadAllUserData(() -> runOnUiThread(() -> {
                        if (products != null) {
                            ArrayList<Item> items = temp.getItemArrayList();
                            if (items == null) items = new ArrayList<>();
                            productAdapter = new product_adapt(items, this);
                            products.setAdapter(productAdapter);
                            Log.d(TAG, "Product list updated.");
                        }
                    }))
            ));
        } catch (Exception e) {
            Log.e(TAG, "Error during resume data load: " + e.getMessage());
        }
    }

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
}
