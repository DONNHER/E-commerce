package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Calayo.R;
import com.example.Calayo.adapters.order_adaptor;
import com.example.Calayo.entities.Item;
import com.example.Calayo.entities.Order;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Activity to display a user's transaction history in a RecyclerView.
 * Handles navigation between home, menu, profile, and transaction pages.
 */
public class transactions extends AppCompatActivity {

    private static final String TAG = "TransactionsActivity";

    private RecyclerView transactions_recycler;
    private order_adaptor adaptor;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth myAuth = FirebaseAuth.getInstance();
    private final tempStorage temp = tempStorage.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transactions);

        setupNavigation();
        setupRecyclerView();
    }

    /**
     * Sets up click listeners for bottom navigation bar.
     */
    private void setupNavigation() {
        try {
            ImageView home = findViewById(R.id.home);
            if (home != null) {
                home.setOnClickListener(view -> {
                    Intent intent = new Intent(this,
                            temp.getLoggedin() == null ? main_act.class : UserDashboardAct.class);
                    startActivity(intent);
                });
            }

            ImageView menu = findViewById(R.id.menu);
            if (menu != null) {
                menu.setOnClickListener(view -> {
                    startActivity(new Intent(this, productsAct.class));
                });
            }

            ImageView history = findViewById(R.id.history);
            if (history != null) {
                history.setOnClickListener(view -> {
                    startActivity(new Intent(this, transactions.class));
                });
            }

            ImageView profile = findViewById(R.id.profile);
            if (profile != null) {
                profile.setOnClickListener(view -> {
                    Intent intent = new Intent(this,
                            myAuth.getCurrentUser() == null ? userLoginAct.class : settingAct.class);
                    startActivity(intent);
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up navigation: " + e.getMessage(), e);
        }
    }

    /**
     * Sets up the RecyclerView with a list of transaction items.
     */
    private void setupRecyclerView() {
        try {
            transactions_recycler = findViewById(R.id.notification_Recycler);

            if (transactions_recycler == null) {
                Log.e(TAG, "RecyclerView not found in layout (R.id.notification_Recycler)");
                return;
            }

            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            transactions_recycler.setLayoutManager(layoutManager);

            ArrayList<Order> checkoutList = temp.getCheckOutArrayList();
            if (checkoutList == null) {
                checkoutList = new ArrayList<>();
                Log.w(TAG, "Checkout list is null. Using empty list.");
            }

            adaptor = new order_adaptor(checkoutList, this);
            transactions_recycler.setAdapter(adaptor);

        } catch (Exception e) {
            Log.e(TAG, "Error setting up RecyclerView: " + e.getMessage(), e);
        }
    }

    /**
     * Refreshes the adapter on resume to show any updated data.
     */
    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (transactions_recycler != null) {
                ArrayList<Order> updatedList = temp.getCheckOutArrayList();
                if (updatedList == null) {
                    updatedList = new ArrayList<>();
                    Log.w(TAG, "Updated checkout list is null onResume. Using empty list.");
                }
                adaptor = new order_adaptor(updatedList, this);
                transactions_recycler.setAdapter(adaptor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error refreshing adapter onResume: " + e.getMessage(), e);
        }
    }
}
