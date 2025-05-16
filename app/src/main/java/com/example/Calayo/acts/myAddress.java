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
 * Activity that displays the list of user-saved addresses.
 * Provides options to add a new address or exit the screen.
 * 
 * This class handles loading addresses asynchronously using tempStorage,
 * updates the UI safely, and logs important events or errors.
 */
public class myAddress extends AppCompatActivity {

    private RecyclerView addsRecyclerView;
    private address_adapter adapter;

    // Firebase and helper instances
    private final FirebaseAuth myAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final tempStorage temp = tempStorage.getInstance();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static final String TAG = "myAddressActivity";

    /**
     * Initializes the activity and UI components.
     * Sets content view and calls UI setup with error handling.
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_address);

        try {
            setupUI();
        } catch (Exception e) {
            Log.e(TAG, "Failed to setup UI components", e);
        }
    }

    /**
     * Finds views and sets click listeners for buttons.
     * Performs null checks to avoid crashes.
     */
    private void setupUI() {
        Button btn = findViewById(R.id.btn);
        Button switchAcc = findViewById(R.id.switch_acc);
        addsRecyclerView = findViewById(R.id.Product_Address_Recycler);

        if (btn != null) {
            btn.setOnClickListener(view -> finish()); // Close activity when back pressed
        } else {
            Log.w(TAG, "Back button view not found");
        }

        if (switchAcc != null) {
            switchAcc.setOnClickListener(view -> {
                Log.d(TAG, "Navigating to Add Address screen");
                Intent intent = new Intent(this, addAddress.class);
                startActivity(intent);
            });
        } else {
            Log.w(TAG, "Add Address button view not found");
        }

        if (addsRecyclerView != null) {
            addsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        } else {
            Log.e(TAG, "RecyclerView for addresses not found");
        }
    }

    /**
     * Loads and displays the user's address list each time the activity resumes.
     * Fetches data on a background thread and updates the UI on the main thread.
     */
    @Override
    protected void onResume() {
        super.onResume();

        String uid = temp.getLoggedin();
        if (uid == null) {
            Log.e(TAG, "User not logged in, cannot load addresses.");
            return;
        }

        Log.d(TAG, "Loading addresses for user ID: " + uid);

        executor.execute(() -> {
            try {
                temp.loadAllUserData(() -> runOnUiThread(() -> {
                    ArrayList<address> userAddresses = temp.getAddressList();

                    if (userAddresses == null || userAddresses.isEmpty()) {
                        Log.w(TAG, "No addresses found for user");
                    } else {
                        Log.d(TAG, "Loaded " + userAddresses.size() + " addresses");
                    }

                    if (addsRecyclerView != null) {
                        adapter = new address_adapter(userAddresses, this);
                        addsRecyclerView.setAdapter(adapter);
                    } else {
                        Log.e(TAG, "RecyclerView null when setting adapter");
                    }
                }));
            } catch (Exception e) {
                Log.e(TAG, "Error loading addresses", e);
            }
        });
    }
}
