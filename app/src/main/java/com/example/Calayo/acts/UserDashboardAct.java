package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.*;

import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
    ArrayList<adds> adds = new ArrayList<>();

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
        ImageView address = findViewById(R.id.address);

        address.setOnClickListener(view->{
            Intent homepage = new Intent(this,myAddress.class);
            startActivity(homepage);
        });

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
        for(Item i : getItems()){
            Map<String, Object> data = new HashMap<>();
            data.put("name", i.getName());
            data.put("price", i.getPrice());
            data.put("description", i.getDescription());
            data.put("image", i.getImage());
            data.put("quantity", i.getQuantity());
            data.put("favorite", i.isFavorite());

            db.collection("items").document(i.getName()).get().addOnCompleteListener(task -> {
               if(task.isSuccessful()){
                   DocumentSnapshot d = task.getResult();
                   if(d.exists()){
                       Log.d("Firestore", "Document already exists");
                   }else {
                       db.collection("items").document(i.getName()).set(data);
                   }
               }else {
                   Log.d("Firestore","Failed to get document", task.getException());
               }
            });

        }
        for(Item.addOn l : getItemsAddOns()) {
            Map<String, Object> data2 = new HashMap<>();
            data2.put("ItemName", l.getItemName());
            data2.put("addOnPrice", l.getAddOnPrice());
            data2.put("addOnName", l.getAddOnName());
            db.collection("items").document(l.getAddOnName()).collection("addOns").document(l.getAddOnName()).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    DocumentSnapshot d = task.getResult();
                    if(d.exists()){
                        Log.d("Firestore", "Document already exists");
                    }else {
                        db.collection("items").document(l.getItemName()).collection("addOns").document(l.getAddOnName()).set(data2);
                    }
                }else {
                    Log.d("Firestore","Failed to get document", task.getException());
                }
            });
        }
        products = findViewById(R.id.Products_Recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        products.setLayoutManager(layoutManager);
        products_db();


        // adds

//        for(adds j : data2){
//            Map<String, Object> data = new HashMap<>();
//            data.put("description", j.getDescription());
//            j.setId(uuid.);
//            db.collection("items").document(j.getName()).set(data);
//        }
        addsRecyclerView = findViewById(R.id.Adds_Recycler);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        addsRecyclerView.setLayoutManager(layoutManager2);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(addsRecyclerView);

//         adds_db();
        List<adds> data2 = Arrays.asList(new adds("Get 40% off\nYour First Order"),new adds("Get 10% off\nYour First Order"),new adds("Get 30% off\nYour First Order"),new adds("Get 20% off\nYour First Order"));
        adapter = new AddsADaptor(data2,this);
        addsRecyclerView.setAdapter(adapter);
    }

        private void products_db(){
        db.collection("items").get().addOnSuccessListener(queryDocumentSnapshots -> {
            items.clear();
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Item item = documentSnapshot.toObject(Item.class);
                items.add(item);
            }
            Adapt = new product_adapt(items,this);
            products.setAdapter(Adapt);
        });
    }

    private void adds_db(){
        db.collection("adds").get().addOnSuccessListener(queryDocumentSnapshots -> {
            adds.clear();
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                adds item = documentSnapshot.toObject(adds.class);
                adds.add(item);
            }
            adapter = new AddsADaptor(adds,this);
            addsRecyclerView.setAdapter(adapter);
        });
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
        products_db();
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
    private static ArrayList<Item.addOn> getItemsAddOns() {
        ArrayList<Item.addOn> data = new ArrayList<>();
        Item.addOn newItem = new Item.addOn("Add more pineapple",50,"Pizza");
        Item.addOn newItem2 = new Item.addOn("Spicy",45,"Pizza");
        Item.addOn newItem3 = new Item.addOn("Add more ham",60,"Pizza");
        data.add(newItem);
        data.add(newItem2);
        data.add(newItem3);

        Item.addOn newIte = new Item.addOn("Add more chicken",50,"Chicken Burger");
        Item.addOn newI = new Item.addOn("Spicy",45,"Chicken Burger");
        Item.addOn neww = new Item.addOn("Add more sauce",60,"Chicken Burger");
        data.add(newI);
        data.add(newIte);
        data.add(neww);

        Item.addOn newIte1 = new Item.addOn("Add more patty",50,"Burger");
        Item.addOn newI2 = new Item.addOn("Spicy",45,"Burger");
        Item.addOn neww3 = new Item.addOn("Add more cheese",60,"Burger");
        data.add(newIte1);
        data.add(newI2);
        data.add(neww3);
        return data;
    }
}
