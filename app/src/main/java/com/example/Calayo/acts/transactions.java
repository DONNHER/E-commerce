package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

            ImageView menu = findViewById(R.id.foodMenu);
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
            LinearLayout pending = findViewById(R.id.stepPending);
            TextView pendingText = findViewById(R.id.num);
            LinearLayout approve = findViewById(R.id.stepApproved);
            TextView approveText = findViewById(R.id.num2);
            LinearLayout deliver = findViewById(R.id.stepDelivery);
            TextView deliverText = findViewById(R.id.num3);
            LinearLayout received = findViewById(R.id.stepReceived);
            TextView receivedText = findViewById(R.id.num4);

            if (transactions_recycler == null) {
                Log.e(TAG, "RecyclerView not found in layout (R.id.notification_Recycler)");
                return;
            }
            pendingText.setText(String.valueOf(temp.getPendingArrayList().size()));
            approveText.setText(String.valueOf(temp.getApprovedArrayList().size()));
            deliverText.setText(String.valueOf(temp.getDeliveryArrayList().size()));
            receivedText.setText(String.valueOf(temp.getReceivedArrayList().size()));

            pending.setOnClickListener( v -> {
                LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                transactions_recycler.setLayoutManager(layoutManager);

                ArrayList<Order> pendingList = temp.getPendingArrayList();
                if (pendingList == null) {
                    Log.w(TAG, "Checkout list is null. Using empty list.");
                }

                adaptor = new order_adaptor(pendingList, this);
                transactions_recycler.setAdapter(adaptor);
            });
            approve.setOnClickListener( v -> {
                LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                transactions_recycler.setLayoutManager(layoutManager);

                ArrayList<Order> approvedArrayList = temp.getApprovedArrayList();
                if (approvedArrayList == null) {
                    Log.w(TAG, "Checkout list is null. Using empty list.");
                }

                adaptor = new order_adaptor(approvedArrayList, this);
                transactions_recycler.setAdapter(adaptor);
            });
            deliver.setOnClickListener( v -> {
                LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                transactions_recycler.setLayoutManager(layoutManager);

                ArrayList<Order> deliveryArrayList = temp.getDeliveryArrayList();
                if (deliveryArrayList == null) {
                    Log.w(TAG, "Checkout list is null. Using empty list.");
                }

                adaptor = new order_adaptor(deliveryArrayList, this);
                transactions_recycler.setAdapter(adaptor);
            });
            received.setOnClickListener( v -> {
                LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                transactions_recycler.setLayoutManager(layoutManager);

                ArrayList<Order> receivedArrayList = temp.getReceivedArrayList();
                if (receivedArrayList == null) {
                    Log.w(TAG, "Checkout list is null. Using empty list.");
                }

                adaptor = new order_adaptor(receivedArrayList, this);
                transactions_recycler.setAdapter(adaptor);
            });
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
            transactions_recycler = findViewById(R.id.notification_Recycler);
            LinearLayout pending = findViewById(R.id.stepPending);
            TextView pendingText = findViewById(R.id.num);
            LinearLayout approve = findViewById(R.id.stepApproved);
            TextView approveText = findViewById(R.id.num2);
            LinearLayout deliver = findViewById(R.id.stepDelivery);
            TextView deliverText = findViewById(R.id.num3);
            LinearLayout received = findViewById(R.id.stepReceived);
            TextView receivedText = findViewById(R.id.num4);

            if (transactions_recycler == null) {
                Log.e(TAG, "RecyclerView not found in layout (R.id.notification_Recycler)");
                return;
            }
            pendingText.setText(String.valueOf(temp.getPendingArrayList().size()));
            approveText.setText(String.valueOf(temp.getApprovedArrayList().size()));
            deliverText.setText(String.valueOf(temp.getDeliveryArrayList().size()));
            receivedText.setText(String.valueOf(temp.getReceivedArrayList().size()));

            pending.setOnClickListener( v -> {
                LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                transactions_recycler.setLayoutManager(layoutManager);

                ArrayList<Order> pendingList = temp.getPendingArrayList();
                if (pendingList == null) {
                    Log.w(TAG, "Checkout list is null. Using empty list.");
                }

                adaptor = new order_adaptor(pendingList, this);
                transactions_recycler.setAdapter(adaptor);
            });
            approve.setOnClickListener( v -> {
                LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                transactions_recycler.setLayoutManager(layoutManager);

                ArrayList<Order> approvedArrayList = temp.getApprovedArrayList();
                if (approvedArrayList == null) {
                    Log.w(TAG, "Checkout list is null. Using empty list.");
                }

                adaptor = new order_adaptor(approvedArrayList, this);
                transactions_recycler.setAdapter(adaptor);
            });
            deliver.setOnClickListener( v -> {
                LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                transactions_recycler.setLayoutManager(layoutManager);

                ArrayList<Order> deliveryArrayList = temp.getDeliveryArrayList();
                if (deliveryArrayList == null) {
                    Log.w(TAG, "Checkout list is null. Using empty list.");
                }

                adaptor = new order_adaptor(deliveryArrayList, this);
                transactions_recycler.setAdapter(adaptor);
            });
            received.setOnClickListener( v -> {
                LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                transactions_recycler.setLayoutManager(layoutManager);

                ArrayList<Order> receivedArrayList = temp.getReceivedArrayList();
                if (receivedArrayList == null) {
                    Log.w(TAG, "Checkout list is null. Using empty list.");
                }

                adaptor = new order_adaptor(receivedArrayList, this);
                transactions_recycler.setAdapter(adaptor);
            });
        } catch (Exception e) {
            Log.e(TAG, "Error refreshing adapter onResume: " + e.getMessage(), e);
        }
    }
}
