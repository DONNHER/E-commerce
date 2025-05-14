package com.example.Calayo.acts;

import com.example.Calayo.adapters.AddsADaptor;
import com.example.Calayo.adapters.address_adapter;
import com.example.Calayo.adapters.product_adapt;
import com.example.Calayo.entities.Item;
import com.example.Calayo.entities.Order;
import com.example.Calayo.entities.adds;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Calayo.helper.tempStorage;
import com.google.firebase.auth.FirebaseAuth;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.Calayo.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class main_act extends AppCompatActivity {
//    private RecyclerView appointmentsView ;
//    private order_adaptor Adapt;
    private Button btn1, btn2;
    private final ArrayList<Order> orders = new ArrayList<>();
    private RecyclerView recyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private  ArrayList<Item> items = new ArrayList<>();

    private RecyclerView products ;
    private product_adapt Adapt;

    RecyclerView addsRecyclerView;
    AddsADaptor adapter;
    tempStorage temp = tempStorage.getInstance();
    FirebaseAuth myAuth;
    private boolean isLoggedIn;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            if(isLoggedIn) {
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
    @Override
    public void onResume(){
        super.onResume();
        executor.execute(() -> temp.loadAllUserData(() -> runOnUiThread(() -> {
            Adapt = new product_adapt(temp.getItemArrayList(),this);
            products.setAdapter(Adapt);
        })));
    }
}
