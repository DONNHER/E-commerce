package com.example.Calayo.acts;

import com.example.Calayo.adapters.AddsADaptor;
import com.example.Calayo.adapters.order_adaptor;
import com.example.Calayo.adapters.product_adapt;
import com.example.Calayo.entities.Item;
import com.example.Calayo.entities.Order;
import com.example.Calayo.entities.adds;
import com.example.Calayo.fragments.userLoginAct;
import com.example.Calayo.fragments.userRegisterAct;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.Calayo.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class main_act extends AppCompatActivity {
//    private RecyclerView appointmentsView ;
//    private order_adaptor Adapt;
    private Button btn1, btn2;
    private final ArrayList<Order> orders = new ArrayList<>();
    private RecyclerView recyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth myAuth= FirebaseAuth.getInstance();

    private  ArrayList<Item> items = new ArrayList<>();

    private RecyclerView products ;
    private product_adapt Adapt;

    RecyclerView addsRecyclerView;
    AddsADaptor adapter;

    @Override
    public void onStart(){
        super.onStart();
        SharedPreferences sharedPreferences  = getSharedPreferences("user",MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false);
        if(isLoggedIn){
            Intent intent = new Intent(this, UserDashboardAct.class); // Replace with actual target
            startActivity(intent);
            finish();
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        recyclerView = findViewById(R.id.appointmentsView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TextView seeAll = findViewById(R.id.seeAll);
        seeAll.setOnClickListener( view ->{
            Intent menupage = new Intent(this,productsAct.class);
            startActivity(menupage);
        });

        ImageView home = findViewById(R.id.home);
        home.setOnClickListener(view -> {
            Intent homepage = new Intent(this, main_act.class);
            startActivity(homepage);
        });
        ImageView menu = findViewById(R.id.foodMenu);
        menu.setOnClickListener(view -> {
            Intent menupage = new Intent(this,productsAct.class);
            startActivity(menupage);
        });
        ImageView history = findViewById(R.id.history);
        history.setOnClickListener(view -> {
            Intent menupage = new Intent(this,transactions.class);
            startActivity(menupage);
        });
        ImageView profile = findViewById(R.id.profile);
        profile.setOnClickListener(view -> {
            if(myAuth.getCurrentUser() == null) {
                Intent login = new Intent(this, userLoginAct.class);
                startActivity(login);
            }else {
                Intent profilepage = new Intent(this, settingAct.class);
                startActivity(profilepage);
            }
        });
        //Products
        products = findViewById(R.id.Products_Recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        products.setLayoutManager(layoutManager);
        products_db();

        // Force a re-layout to update the RecyclerView's height


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

    public void onLogClick(View view) {
        Intent intent = new Intent(view.getContext(), userLoginAct.class);
        view.getContext().startActivity(intent);
    }
    public void onResClick(View view) {
        Intent intent = new Intent(view.getContext(), userRegisterAct.class);
        view.getContext().startActivity(intent);
    }
    private void products_db() {
        db.collection("items").get().addOnSuccessListener(queryDocumentSnapshots -> {
            items.clear();
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Item item = documentSnapshot.toObject(Item.class);
                items.add(item);
            }
            Adapt = new product_adapt(items, this);
            products.setAdapter(Adapt);
        });
    }
    @Override
    public void onResume(){
        super.onResume();
        products_db();
    }
}
