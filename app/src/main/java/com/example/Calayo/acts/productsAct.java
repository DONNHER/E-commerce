package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Calayo.R;
import com.example.Calayo.adapters.product_adapt;
import com.example.Calayo.entities.Item;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Displays a list of products in a RecyclerView for the user to browse.
 * Includes navigation controls to cart, history, profile, and home.
 */
public class productsAct extends AppCompatActivity {

    private static final String TAG = "productsAct";

    private RecyclerView productsRecycler;
    private product_adapt adapter;
    private FirebaseAuth myAuth;
    private tempStorage temp;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_food_menu);

        // Initialize Firebase and tempStorage
        myAuth = FirebaseAuth.getInstance();
        temp = tempStorage.getInstance();

        // Initialize UI and logic
        setupNavigation();
        setupRecyclerView();
        loadProducts();

        EditText searchEdit = findViewById(R.id.search);
        Button submit = findViewById(R.id.submit);

        submit.setOnClickListener(v -> {
            // Handle passed intent search result
            String search_res = searchEdit.getText().toString();
            if (!search_res.isEmpty()) {
                Intent intent = new Intent(productsAct.this , search.class);
                temp.setSearchResult(temp.searchItem(search_res));
                startActivity(intent);
            }
        });
    }

    /**
     * Set up the bottom navigation bar.
     */
    private void setupNavigation() {
        ImageView home = findViewById(R.id.home);
        ImageView cart = findViewById(R.id.cart);
        ImageView menu = findViewById(R.id.foodMenu);
        ImageView history = findViewById(R.id.history);
        ImageView profile = findViewById(R.id.profile);

        if (cart != null) {
            cart.setOnClickListener(v -> {
                startActivity(new Intent(this, AddToCart.class));
            });
        }

        if (home != null) {
            home.setOnClickListener(view -> {
                if (temp != null && temp.getLoggedin() == null) {
                    startActivity(new Intent(this, main_act.class));
                } else {
                    startActivity(new Intent(this, UserDashboardAct.class));
                }
            });
        }

        if (menu != null) {
            menu.setOnClickListener(view -> {
                startActivity(new Intent(this, productsAct.class));
            });
        }

        if (history != null) {
            history.setOnClickListener(view -> {
                startActivity(new Intent(this, transactions.class));
            });
        }

        if (profile != null) {
            profile.setOnClickListener(view -> {
                startActivity(new Intent(this, settingAct.class));
            });
        }
    }

    /**
     * Initializes the RecyclerView for displaying product items.
     */
    private void setupRecyclerView() {
        productsRecycler = findViewById(R.id.Products_Recycler);
        if (productsRecycler == null) {
            Log.e(TAG, "RecyclerView (R.id.Products_Recycler) not found in layout");
            Toast.makeText(this, "Something went wrong. Please restart the app.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        productsRecycler.setLayoutManager(layoutManager);
    }

    /**
     * Loads products from tempStorage and sets them to the adapter.
     */
    private void loadProducts() {
        if (temp == null) {
            Log.e(TAG, "tempStorage instance is null");
            Toast.makeText(this, "Unable to load products.", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Item> items = temp.getItemArrayList();
        if (items == null || items.isEmpty()) {
            Log.w(TAG, "No items found in tempStorage");
            Toast.makeText(this, "No products available.", Toast.LENGTH_SHORT).show();
        }

        adapter = new product_adapt(items, this);
        productsRecycler.setAdapter(adapter);
    }
}
