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

public class ManagerDashB extends AppCompatActivity {
    private RecyclerView recyclerView ;
    private order_adaptor Adapt;
    private Button btn1, btn2;

    private ArrayList<Order> orders = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth myAuth= FirebaseAuth.getInstance();
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_dash);
        recyclerView = findViewById(R.id.appointmentsView4);
        Adapt = new order_adaptor();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(Adapt);
        TextView name = findViewById(R.id.name);
        name.setText("Hi, " + myAuth.getCurrentUser().getDisplayName());
        filter("Confirmed");
        btn2.setOnClickListener(v ->filter("Pending"));
        btn1.setOnClickListener(v ->filter("Confirmed"));
        services();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    public void onMenuClick(View view) {
        Menu menu = new Menu();
        menu.show(getSupportFragmentManager(), "MenuDialog");
    }
    private void services(){
        db.collection("Appointments").get().addOnSuccessListener(queryDocumentSnapshots -> {
            orders.clear();
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Order appt = documentSnapshot.toObject(Order.class);
                    appt.setId(documentSnapshot.getId());  // ✅ Fix: Set the document ID manually
                    orders.add(appt);
            }
            filter("Confirmed");
        });
    }



    private void filter(String status) {
        ArrayList<Order> filtered = new ArrayList<>();
        for (Order order : orders) {
            if (order.getStatus().equals(status)) {
                filtered.add(order);
            }
        }

        // Group appointments by date
        Map<String, List<Order>> grouped = groupByDate(filtered);
        Adapt.setAppointments(grouped,getSupportFragmentManager());
    }

    private Map<String, List<Order>> groupByDate(List<Order> orders) {
        Map<String, List<Order>> grouped = new LinkedHashMap<>();
        for (Order order : orders) {
            String date = order.getAppointmentDate(); // Assuming getDate() returns the date as a string
            if (!grouped.containsKey(date)) {
                grouped.put(date, new ArrayList<>());
            }
            grouped.get(date).add(order);
        }
        return grouped;
    }

    @Override
    public void onResume(){
       super.onResume();
       services();
    }
    public void onInventoryClick(View view){
        Intent intent = new Intent(this, manager_inventory.class); // Replace with actual target
        startActivity(intent);
    }
}
