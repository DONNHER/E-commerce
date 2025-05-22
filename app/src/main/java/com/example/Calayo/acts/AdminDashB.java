package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Calayo.R;
import com.example.Calayo.adapters.order_adaptor;
import com.example.Calayo.entities.Order;
import com.example.Calayo.fragments.Menu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdminDashB extends AppCompatActivity {

    private RecyclerView recyclerView;
    private order_adaptor Adapt;
    private Button btnConfirmed, btnPending, btnAll;
    private ArrayList<Order> orders = new ArrayList<>();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth myAuth = FirebaseAuth.getInstance();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admind_dash);

        recyclerView = findViewById(R.id.appointmentsView_admin);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Adapt = new order_adaptor();
        recyclerView.setAdapter(Adapt);

        TextView name = findViewById(R.id.name);
        if (myAuth.getCurrentUser() != null) {
            name.setText("Admin: " + myAuth.getCurrentUser().getDisplayName());
        }

        btnConfirmed = findViewById(R.id.btnConfirmed_admin);
        btnPending = findViewById(R.id.btnPending_admin);
        btnAll = findViewById(R.id.btnAll_admin);
        

        btnConfirmed.setOnClickListener(v -> filter("Confirmed"));
        btnPending.setOnClickListener(v -> filter("Pending"));
        btnAll.setOnClickListener(v -> filter(null));

        services(); // Load all orders
    }

    private void services() {
        db.collection("Appointments").get().addOnSuccessListener(queryDocumentSnapshots -> {
            orders.clear();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                Order order = doc.toObject(Order.class);
                if (order != null) {
                    order.setId(doc.getId());
                    orders.add(order);
                }
            }
            filter(null); // Show all orders by default
        });
    }

    private void filter(String status) {
        ArrayList<Order> filtered = new ArrayList<>();
        for (Order order : orders) {
            if (status == null || order.getStatus().equals(status)) {
                filtered.add(order);
            }
        }
        Map<String, List<Order>> grouped = groupByDate(filtered);
        Adapt.setAppointments(grouped, getSupportFragmentManager());
    }

    private Map<String, List<Order>> groupByDate(List<Order> orders) {
        Map<String, List<Order>> grouped = new LinkedHashMap<>();
        for (Order order : orders) {
            String date = order.getDate();
            if (!grouped.containsKey(date)) {
                grouped.put(date, new ArrayList<>());
            }
            grouped.get(date).add(order);
        }
        return grouped;
    }

    public void onMenuClick(View view) {
        Menu menu = new Menu();
        menu.show(getSupportFragmentManager(), "MenuDialog");
    }

    public void onInventoryClick(View view) {
        Intent intent = new Intent(this, manager_inventory.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        services();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
