package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Calayo.R;
import com.example.Calayo.adapters.product_adapt;
import com.example.Calayo.entities.Item;
import com.example.Calayo.helper.tempStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class search extends AppCompatActivity {

    private static final String TAG = "SearchActivity";

    private RecyclerView products;
    private product_adapt productAdapter;
    private ArrayList<Item> items = new ArrayList<>();
    private tempStorage temp;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        try {
            temp = tempStorage.getInstance();
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize tempStorage: " + e.getMessage());
            return;
        }

        // Initialize views
        products = findViewById(R.id.Products_Recycler);
        EditText searchEdit = findViewById(R.id.search);
        Button submit = findViewById(R.id.submit);

        String res = getIntent().getStringExtra("result");
        if (res != null && !res.isEmpty()) {
            searchAndDisplay(res);
        }

        submit.setOnClickListener(v -> {
            // Handle passed intent search result
            String search_res = searchEdit.getText().toString();
            if (!search_res.isEmpty()) {
                searchAndDisplay(search_res);
            }
        });

        initNavigationButtons();

    }

    /**
     * Searches for an item by name and displays it if found.
     */
    private void searchAndDisplay(String query) {
        Item res = temp.searchItem(query);
        items.clear();
        if (res != null) {
            items.add(res);
            updateProductList(items);
        } else {
            Log.d(TAG, "No item found for query: " + query);
        }
    }

    /**
     * Updates the RecyclerView with a new product list.
     */
    private void updateProductList(ArrayList<Item> newItems) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        products.setLayoutManager(layoutManager);
        productAdapter = new product_adapt(newItems, this);
        products.setAdapter(productAdapter);
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
     * Refreshes data when returning to this activity.
     */
    @Override
    protected void onResume() {
        super.onResume();
        try {
            executor.execute(() -> temp.loadAllData(() ->
                    temp.loadAllUserData(() -> runOnUiThread(() -> {
                        if (products != null) {
                            ArrayList<Item> loadedItems = temp.getItemArrayList();
                            if (loadedItems == null) loadedItems = new ArrayList<>();
                            updateProductList(loadedItems);
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
