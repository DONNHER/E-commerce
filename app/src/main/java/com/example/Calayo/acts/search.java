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
import android.widget.Toast;

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
    private boolean isLoggedIn = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        // Initialize views
        products = findViewById(R.id.Products_Recycler);
        EditText searchEdit = findViewById(R.id.search);
        Button submit = findViewById(R.id.submit);
        temp = tempStorage.getInstance();

        if ( temp.getSearchResult() != null) {
            items.clear();
            items.add(temp.getSearchResult());
            updateProductList(items);
        }

        submit.setOnClickListener(v -> {
            // Handle passed intent search result
            String search_res = searchEdit.getText().toString();
            if (!search_res.isEmpty()) {
                searchAndDisplay(search_res);
            }
        });

        try {
            temp = tempStorage.getInstance();
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize tempStorage: " + e.getMessage());
            return;
        }

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
                profile.setOnClickListener(view -> {
                    Intent intent = isLoggedIn ? new Intent(search.this, settingAct.class) : new Intent(search.this, userLoginAct.class);
                    startActivity(intent);
            });
        }
        } catch (Exception e) {
            Log.e(TAG, "Navigation setup failed: " + e.getMessage());
        }
    }

    /**
     * Utility method to restart this activity.
     */
    private void restartActivity() {
        try {
            Intent intent = isLoggedIn ? new Intent(search.this, UserDashboardAct.class) : new Intent(search.this, main_act.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Failed to restart activity: " + e.getMessage());
        }
    }
}
