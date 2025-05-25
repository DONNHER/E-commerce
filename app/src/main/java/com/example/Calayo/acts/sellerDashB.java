package com.example.Calayo.acts;

import static android.graphics.Color.TRANSPARENT;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Calayo.R;
import com.example.Calayo.adapters.Rider_order_adapt;
import com.example.Calayo.adapters.rider_order_delivered;
import com.example.Calayo.helper.tempStorage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class sellerDashB  extends AppCompatActivity {

    private static final String TAG = "RiderDashboardAct";

    private RecyclerView order;
    private RecyclerView pending_order;
    private RecyclerView approved_order;
    private RecyclerView delivery_order;
    private rider_order_delivered deliveredAdapt;
    private Rider_order_adapt Adapter;
    private tempStorage temp;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
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
        setupOrderRecycler();
    }

    /**
     * Sets up navigation buttons (home, address, profile, etc.)
     */
    @SuppressLint("ResourceAsColor")
    private void initNavigationButtons() {
        try {
            ImageView home = findViewById(R.id.home);
            ImageView profile = findViewById(R.id.profile);
            TextView otw = findViewById(R.id.tabStandard2);
            TextView delivered = findViewById(R.id.tabStandard);
            otw.setOnClickListener(v->{
                order.setVisibility(VISIBLE);
                delivered_order.setVisibility(GONE);
                otw.setBackgroundColor(R.color.white);
                delivered.setBackgroundColor(TRANSPARENT);
            });
            delivered.setOnClickListener(v->{
                order.setVisibility(GONE);
                delivered_order.setVisibility(VISIBLE);
                otw.setBackgroundColor(TRANSPARENT);
                delivered.setBackgroundColor(R.color.white);
            });

            if (home != null) {
                home.setOnClickListener(v -> restartActivity());
            }

            if (profile != null) {
                profile.setOnClickListener(v -> startActivity(new Intent(this, settingAct.class)));
            }
        } catch (Exception e) {
            Log.e(TAG, "Navigation setup failed: " + e.getMessage());
        }
    }

    /**
     * Sets up the orders
     */
    private void setupOrderRecycler() {
        try {
            order = findViewById(R.id.Order_Recycler);
            pending_order = findViewById(R.id.Order_P_Recycler);
            approved_order = findViewById(R.id.Order_A_Recycler);
            delivery_order = findViewById(R.id.Order_OTW_Recycler);

            if (order != null) {
                order.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                Adapter = new Rider_order_adapt(temp.getPendingArrayList(), this);
                order.setAdapter(Adapter);
                delivery_order.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                deliveredAdapt = new rider_order_delivered(temp.getReceivedArrayList(), this);
                order.setAdapter(deliveredAdapt);

                pending_order.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                Adapter = new Rider_order_adapt(temp.getPendingArrayList(), this);
                order.setAdapter(Adapter);
                approved_order.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                deliveredAdapt = new rider_order_delivered(temp.getReceivedArrayList(), this);
                order.setAdapter(deliveredAdapt);
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
                        order = findViewById(R.id.Products_Recycler);
                        delivery_order = findViewById(R.id.stepDelivery);

                        if (order != null) {
                            order.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                            Adapter = new Rider_order_adapt(temp.getPendingArrayList(), this);
                            order.setAdapter(Adapter);
                            delivery_order.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                            deliveredAdapt = new rider_order_delivered(temp.getPendingArrayList(), this);
                            order.setAdapter(deliveredAdapt);
                        } else {
                            Log.w(TAG, "Adds RecyclerView not found.");
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
