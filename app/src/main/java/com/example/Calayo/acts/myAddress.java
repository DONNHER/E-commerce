package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Calayo.R;
import com.example.Calayo.adapters.address_adapter;
import com.example.Calayo.entities.address;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Displays a list of user-saved addresses in a RecyclerView.
 * Allows navigation to add a new address or exit the activity.
 *
 * Responsibilities:
 * - Load user data from tempStorage
 * - Populate RecyclerView with address list
 * - Launch add address activity
 *
 * Handles:
 * - Null safety for logged-in user and address list
 * - Logging errors and steps for debug
 */
public class myAddress extends AppCompatActivity {

    private RecyclerView addsRecyclerView;
    private address_adapter adapter;

    // Firebase and helpers
    private final FirebaseAuth myAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final tempStorage temp = tempStorage.getInstance();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static final String TAG = "myAddressActivity";

    /**
     * Entry point for the activity lifecycle
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_address);

        try {
            setupUI();
        } catch (Exception e) {
            Log.e(TAG, "UI setup failed", e);
        }
    }

    /**
     * Initializes UI components and click listeners.
     * Wraps each listener in null checks and error-safe blocks.
     */
    private void setupUI() {
        Button btn = findViewById(R.id.btn);
        Button switchAcc = findViewById(R.id.switch_acc);
        addsRecyclerView = findViewById(R.id.Product_Address_Recycler);

        if (btn != null) {
            btn.setOnClickListener(view -> finish());
        } else {
            Log.w(TAG, "Back button not found");
        }

        if (switchAcc != null) {
            switchAcc.setOnClickListener(view -> {
                Log.d(TAG, "Navigating to add address screen");
                Intent intent = new Intent(this, addAddress.class);
                startActivity(intent);
            });
        } else {
            Log.w(TAG, "Switch account button not found");
        }

        if (addsRecyclerView != null) {
            addsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        } else {
            Log.e(TAG, "RecyclerView not initialized");
        }
    }

    /**
     * Refreshes the address list when the activity is resumed.
     * Handles logged-in user check and null-safe data binding.
     */
    @Override
    protected void onResume() {
        super.onResume();

        String uid = temp.getLoggedin();
        if (uid == null) {
            Log.e(TAG, "User is not logged in. Cannot load addresses.");
            return;
        }

        Log.d(TAG, "Loading addresses for UID: " + uid);

        executor.execute(() -> {
            try {
                temp.loadAllUserData(() -> runOnUiThread(() -> {
                    ArrayList<address> userAddresses = temp.getAddressList();
                    if (userAddresses == null || userAddresses.isEmpty()) {
                        Log.w(TAG, "No addresses found.");
                    } else {
                        Log.d(TAG, "Loaded " + userAddresses.size() + " addresses.");
                    }

                    if (addsRecyclerView != null) {
                        adapter = new address_adapter(userAddresses, this);
                        addsRecyclerView.setAdapter(adapter);
                    } else {
                        Log.e(TAG, "RecyclerView is null during resume");
                    }
                }));
            } catch (Exception e) {
                Log.e(TAG, "Error loading address data", e);
            }
        });
    }
}
