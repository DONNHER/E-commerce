package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import com.example.Calayo.R;
import com.example.Calayo.adapters.AddsADaptor;
import com.example.Calayo.adapters.order_adaptor;
import com.example.Calayo.adapters.product_adapt;
import com.example.Calayo.entities.Item;
import com.example.Calayo.entities.Order;
import com.example.Calayo.entities.adds;
import com.example.Calayo.fragments.userMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserDashboardAct extends AppCompatActivity {
//    private  RecyclerView ordersView ;
//    private order_adaptor Adapt;
    private RecyclerView products ;
    private product_adapt Adapt;
    private Button btn1, btn2;
    private  ArrayList<Order> orders = new ArrayList<>();
    private  ArrayList<Item> items = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth myAuth= FirebaseAuth.getInstance();
    RecyclerView addsRecyclerView;
    AddsADaptor adapter;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_d_board);
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
        //Products
        products = findViewById(R.id.Products_Recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        products.setLayoutManager(layoutManager);

        ArrayList<Item> data = getItems();
        Adapt = new product_adapt(data,this);
        products.setAdapter(Adapt);
        // adds
        addsRecyclerView = findViewById(R.id.Adds_Recycler);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        addsRecyclerView.setLayoutManager(layoutManager2);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(addsRecyclerView);

        List<adds> data2 = Arrays.asList(new adds("Get 40% off\nYour First Order"),new adds("Get 10% off\nYour First Order"),new adds("Get 30% off\nYour First Order"),new adds("Get 20% off\nYour First Order"));
        adapter = new AddsADaptor(data2,this);
        addsRecyclerView.setAdapter(adapter);
    }

//    private void appointments(){
//        db.collection("Appointments").get().addOnSuccessListener(queryDocumentSnapshots -> {
//            orders.clear();
//            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                Order order = documentSnapshot.toObject(Order.class);
//                orders.add(order);
//            }
//            filter("Confirmed");
//            filter("Confirmed");
//        });
//    }

//    private void filter(String status) {
//        ArrayList<Order> filtered = new ArrayList<>();
//        for (Order order : orders) {
//            if (order.getStatus().equals(status)) {
//                filtered.add(order);
//            }
//        }
//
//        // Group appointments by date and pass the grouped data to the adapter
//        Map<String, List<Order>> groupedAppointments = groupByDate(filtered);
//        Adapt.setAppointments(groupedAppointments, getSupportFragmentManager());
//    }

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
    @NonNull
    private static ArrayList<Item> getItems() {
        ArrayList<Item> data = new ArrayList<>();
        Item newItem = new Item(200,"https://ik.imagekit.io/k4imggmfa/pizza1.png?updatedAt=1746566552587","Pizza",50,"Hawaiian pizza typically uses, mozzarella cheese, ham pizza dough, pizza sauce, mozzarella cheese, ham, and pineapple. Some variations also include bacon or other toppings.");
        Item newItem2 = new Item(200,"https://ik.imagekit.io/k4imggmfa/burger.png?updatedAt=1746566830867","Burger",50,"Hawaiian pizza typically uses, mozzarella cheese, ham pizza dough, pizza sauce, mozzarella cheese, ham, and pineapple. Some variations also include bacon or other toppings.");
        Item newItem3 = new Item(200,"https://ik.imagekit.io/k4imggmfa/hotdogs.png?updatedAt=1746566840760","Chicken Burger",50,"Hawaiian pizza typically uses, mozzarella cheese, ham pizza dough, pizza sauce, mozzarella cheese, ham, and pineapple. Some variations also include bacon or other toppings.");

        data.add(newItem);
        data.add(newItem2);
        data.add(newItem3);

        return data;
    }
}
