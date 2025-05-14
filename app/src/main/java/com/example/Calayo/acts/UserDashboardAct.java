package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.*;

import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import com.example.Calayo.R;
import com.example.Calayo.adapters.AddsADaptor;
import com.example.Calayo.adapters.address_adapter;
import com.example.Calayo.adapters.product_adapt;
import com.example.Calayo.entities.Item;
import com.example.Calayo.entities.Order;
import com.example.Calayo.entities.adds;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserDashboardAct extends AppCompatActivity {
//    private  RecyclerView ordersView ;
//    private order_adaptor Adapt;
    private RecyclerView products ;
    private product_adapt Adapt;
    private Button btn1, btn2;
    tempStorage temp;
    RecyclerView addsRecyclerView;
    AddsADaptor adapter;
    ArrayList<adds> adds = new ArrayList<>();
    ExecutorService executor = Executors.newSingleThreadExecutor();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_d_board);
        temp = tempStorage.getInstance();
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
        ImageView history = findViewById(R.id.history);
        history.setOnClickListener(view -> {
            Intent menupage = new Intent(this, transactions.class);
            startActivity(menupage);
        });
        ImageView profile = findViewById(R.id.profile);
        profile.setOnClickListener(view -> {
            Intent profilepage = new Intent(this, settingAct.class);
            startActivity(profilepage);
        });

        products = findViewById(R.id.Products_Recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        products.setLayoutManager(layoutManager);
        Adapt = new product_adapt(temp.getItemArrayList(),this);
        products.setAdapter(Adapt);


        addsRecyclerView = findViewById(R.id.Adds_Recycler);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        addsRecyclerView.setLayoutManager(layoutManager2);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(addsRecyclerView);

//         adds_db();
        List<adds> data2 = Arrays.asList(new adds("Get 40% off\nYour First Order"),new adds("Get 10% off\nYour First Order"),new adds("Get 30% off\nYour First Order"),new adds("Get 20% off\nYour First Order"));

        adapter = new AddsADaptor(data2,this);
        addsRecyclerView.setAdapter(adapter);

        adapter = new AddsADaptor(temp.getAddsArrayList(),this);
        addsRecyclerView.setAdapter(adapter);
    }


    @Override
    public void onResume(){
        super.onResume();
        executor.execute(() -> temp.loadAllUserData(()->runOnUiThread(() -> {})));
        executor.execute(() -> temp.loadAllData(()-> runOnUiThread(() -> {
            Adapt = new product_adapt(temp.getItemArrayList(),this);
            products.setAdapter(Adapt);
        })));
    }
}
