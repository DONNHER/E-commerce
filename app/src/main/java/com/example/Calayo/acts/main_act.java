package com.example.Calayo.acts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.example.Calayo.entities.Order;
import com.example.Calayo.entities.adds;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The main activity for the food ordering app.
 * Displays banners, product list, and navigational controls.
 * Also manages login/profile redirection logic.
 */
public class main_act extends AppCompatActivity {

    private static final String TAG = "main_act";

    private RecyclerView products;
    private RecyclerView addsRecyclerView;

    private product_adapt productAdapter;
    private AddsADaptor addsAdapter;

    private final ArrayList<Order> orders = new ArrayList<>();
    private final ArrayList<Item> items = new ArrayList<>();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth myAuth = FirebaseAuth.getInstance();
    private final tempStorage temp = tempStorage.getInstance();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private boolean isLoggedIn = false;

    /**
     * Initializes UI components, event handlers, and default data on activity creation.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            initNavigation();
            setupProductRecycler();
            setupAddRecycler();
        } catch (Exception e) {
            Log.e(TAG, "onCreate failed: ", e);
        }
    }

    /**
     * Initializes navigation icon click listeners.
     */
    private void initNavigation() {
        TextView seeAll = findViewById(R.id.seeAll);
        ImageView home = findViewById(R.id.home);
        ImageView menu = findViewById(R.id.foodMenu);
        ImageView history = findViewById(R.id.history);
        ImageView profile = findViewById(R.id.profile);

        if (seeAll != null) {
            seeAll.setOnClickListener(view -> startActivity(new Intent(this, productsAct.class)));
        }

        if (home != null) {
            home.setOnClickListener(view -> startActivity(new Intent(this, main_act.class)));
        }

        if (menu != null) {
            menu.setOnClickListener(view -> startActivity(new Intent(this, productsAct.class)));
        }

        if (history != null) {
            history.setOnClickListener(view -> startActivity(new Intent(this, transactions.class)));
        }

        if (profile != null) {
            profile.setOnClickListener(view -> {
                Intent intent = isLoggedIn ? new Intent(this, settingAct.class) : new Intent(this, userLoginAct.class);
                startActivity(intent);
            });
        }
    }

    /**
     * Sets up vertical product list RecyclerView.
     */
    private void setupProductRecycler() {
        products = findViewById(R.id.Products_Recycler);
        if (products != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            products.setLayoutManager(layoutManager);
        } else {
            Log.e(TAG, "Products RecyclerView not found");
        }
    }

    /**
     * Sets up horizontal promotional adds RecyclerView.
     */
    private void setupAddRecycler() {
        addsRecyclerView = findViewById(R.id.Adds_Recycler);
        if (addsRecyclerView != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            addsRecyclerView.setLayoutManager(layoutManager);

            SnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(addsRecyclerView);
            addsAdapter = new AddsADaptor(temp.getAddsArrayList(), this);
            addsRecyclerView.setAdapter(addsAdapter);
        } else {
            Log.e(TAG, "Adds RecyclerView not found");
        }
    }

    /**
     * Navigates to login activity.
     */
    public void onLogClick(View view) {
        startActivity(new Intent(this, userLoginAct.class));
    }

    /**
     * Navigates to registration activity.
     */
    public void onResClick(View view) {
        startActivity(new Intent(this, userRegisterAct.class));
    }

    /**
     * Loads products from tempStorage when activity resumes.
     */
    @Override
    protected void onResume() {
        super.onResume();
        executor.execute(() -> {
            try {
                temp.loadAllData(() -> runOnUiThread(() -> {
                    ArrayList<Item> loadedItems = temp.getItemArrayList();
                    if (loadedItems == null || loadedItems.isEmpty()) {
                        Log.w(TAG, "No items found to display.");
                    }

                    if (products != null) {
                        productAdapter = new product_adapt(loadedItems, this);
                        products.setAdapter(productAdapter);
                        Log.d(TAG, "Product adapter set with " + loadedItems.size() + " items.");
                    } else {
                        Log.e(TAG, "Products RecyclerView is null in onResume");
                    }
                }));
            } catch (Exception e) {
                Log.e(TAG, "Error loading data in onResume: ", e);
            }
        });
    }
}
