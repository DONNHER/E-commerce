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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager Dashboard Activity:
 * This screen allows the admin/manager to view all customer appointments and filter them by status.
 */
public class ManagerDashB extends AppCompatActivity {

    private RecyclerView recyclerView; // Scrollable list of appointments
    private order_adaptor Adapt; // Adapter that shows each appointment
    private Button btn1, btn2; // Filter buttons (Confirmed / Pending)
    private ArrayList<Order> orders = new ArrayList<>(); // All orders from Firebase

    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // Database instance
    private final FirebaseAuth myAuth = FirebaseAuth.getInstance(); // Auth to get logged-in manager

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_dash); // Load layout for the dashboard

        recyclerView = findViewById(R.id.appointmentsView4); // Find RecyclerView in layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Arrange list vertically
        recyclerView.setAdapter(Adapt); // Attach adapter (will update this later)

        // Display the manager's name
        TextView name = findViewById(R.id.name);
        name.setText("Hi, " + myAuth.getCurrentUser().getDisplayName());

        // TODO: Make sure btn1 and btn2 are initialized from layout!
        // Example:
        // btn1 = findViewById(R.id.btnConfirmed);
        // btn2 = findViewById(R.id.btnPending);

        // Load appointments and display only the "Confirmed" ones by default
        filter("Confirmed");

        // Set button actions to filter the list based on status
        btn2.setOnClickListener(v -> filter("Pending"));
        btn1.setOnClickListener(v -> filter("Confirmed"));

        // Load appointment data from Firebase
        services();
    }

    // Load all appointments from Firebase
    private void services() {
        db.collection("Appointments").get().addOnSuccessListener(queryDocumentSnapshots -> {
            orders.clear(); // Remove old data
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Order appt = documentSnapshot.toObject(Order.class); // Convert to Order object
                appt.setId(documentSnapshot.getId()); // Store Firestore document ID
                orders.add(appt); // Add to order list
            }
            filter("Confirmed"); // Show confirmed orders by default
        });
    }

    // Filters the appointments list by status and groups by date
    private void filter(String status) {
        ArrayList<Order> filtered = new ArrayList<>();

        // Only add orders with the selected status
        for (Order order : orders) {
            if (order.getStatus().equals(status)) {
                filtered.add(order);
            }
        }

        // Group filtered orders by date
        Map<String, List<Order>> grouped = groupByDate(filtered);

        // Update the adapter with grouped data
        // Note: Adapt must be initialized first!
        // Adapt.setAppointments(grouped, getSupportFragmentManager());
    }

    // Group orders by date for easier viewing
    private Map<String, List<Order>> groupByDate(List<Order> orders) {
        Map<String, List<Order>> grouped = new LinkedHashMap<>();
        for (Order order : orders) {
            String date = order.getDate(); // Use appointment date
            if (!grouped.containsKey(date)) {
                grouped.put(date, new ArrayList<>());
            }
            grouped.get(date).add(order);
        }
        return grouped;
    }

    // Called when user clicks the side menu button
    public void onMenuClick(View view) {
        Menu menu = new Menu();
        menu.show(getSupportFragmentManager(), "MenuDialog");
    }

    // Called when user clicks the inventory button
    public void onInventoryClick(View view) {
        Intent intent = new Intent(this, manager_inventory.class);
        startActivity(intent); // Open inventory management screen
    }

    // When the user returns to this screen, refresh data
    @Override
    public void onResume() {
        super.onResume();
        services(); // Reload appointments
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
