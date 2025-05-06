package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import com.example.Calayo.R;
import com.example.Calayo.adapters.order_adaptor;
import com.example.Calayo.adapters.product_adapt;
import com.example.Calayo.entities.Item;
import com.example.Calayo.entities.Order;
import com.example.Calayo.fragments.userMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserDashboardAct extends AppCompatActivity {
    private  RecyclerView appointmentsView ;
    private order_adaptor Adapt;
    private product_adapt productAdapt;
    private Button btn1, btn2;
    private  ArrayList<Order> orders = new ArrayList<>();
    private  ArrayList<Item> items = new ArrayList<>();
    private RecyclerView recyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth myAuth= FirebaseAuth.getInstance();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_d_board);
        recyclerView = findViewById(R.id.Products_Recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapt = new product_adapt(items,this);
//        appointmentsView.setLayoutManager(new LinearLayoutManager(this));
//        appointmentsView.setAdapter(Adapt);
//        btn2.setOnClickListener(v ->filter("Pending"));
//        btn1.setOnClickListener(v ->filter("Confirmed"));
//        filter("Pending");
//        appointments();
        ImageView home = findViewById(R.id.home);
        home.setOnClickListener(view -> {
            Intent homepage = new Intent(this,UserDashboardAct.class);
            startActivity(homepage);
        });
        ImageView menu = findViewById(R.id.menu);
        menu.setOnClickListener(view -> {
            Intent menupage = new Intent(this,productsAct.class);
            startActivity(menupage);
        });
//        ImageView history = findViewById(R.id.history);
//        menu.setOnClickListener(view -> {
//            Intent menupage = new Intent(this,productsAct.class);
//            startActivity(menupage);
//        });
        ImageView profile = findViewById(R.id.profile);
        profile.setOnClickListener(view -> {
            Intent profilepage = new Intent(this, settingAct.class);
            startActivity(profilepage);
        });
    }

    private void appointments(){
        db.collection("Appointments").get().addOnSuccessListener(queryDocumentSnapshots -> {
            orders.clear();
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Order order = documentSnapshot.toObject(Order.class);
                orders.add(order);
            }
            filter("Confirmed");
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

        // Group appointments by date and pass the grouped data to the adapter
        Map<String, List<Order>> groupedAppointments = groupByDate(filtered);
        Adapt.setAppointments(groupedAppointments, getSupportFragmentManager());
    }

    private Map<String, List<Order>> groupByDate(List<Order> orders) {
        Map<String, List<Order>> grouped = new LinkedHashMap<>();
        for (Order order : orders) {
            String date = order.getAppointmentDate(); // Assuming this method returns the date
            if (!grouped.containsKey(date)) {
                grouped.put(date, new ArrayList<>());
            }
            grouped.get(date).add(order);
        }
        return grouped;
    }

    public void onMenuClicks(View view) {
        userMenu menu = new userMenu();
        menu.show(getSupportFragmentManager(), "MenuDialog");
    }

    @Override
    public void onResume(){
        super.onResume();
    }
}
