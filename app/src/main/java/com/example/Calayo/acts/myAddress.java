package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Calayo.R;
import com.example.Calayo.adapters.address_adapter;
import com.example.Calayo.adapters.product_adapt;
import com.example.Calayo.entities.address;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class myAddress extends AppCompatActivity {
    RecyclerView addsRecyclerView;
    address_adapter adapter;
    ArrayList<address> adds = new ArrayList<>();
    private final FirebaseAuth myAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    tempStorage temp = tempStorage.getInstance();
    ExecutorService executor = Executors.newSingleThreadExecutor();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_address);
        Button btn = findViewById(R.id.btn);
        btn.setOnClickListener(view -> finish());

        Button switch_acc = findViewById(R.id.switch_acc);
        switch_acc.setOnClickListener(view -> {
            Intent intent = new Intent(this, addAddress.class); // Replace with actual target
            startActivity(intent);
        });

        addsRecyclerView = findViewById(R.id.Product_Address_Recycler);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        addsRecyclerView.setLayoutManager(layoutManager2);

        adapter = new address_adapter(tempStorage.getInstance().getAddressList(),this);
        addsRecyclerView.setAdapter(adapter);
    }
    @Override
    public void onResume(){
        super.onResume();
        executor.execute(() -> temp.loadAllUserData(() -> runOnUiThread(() -> {
            adapter = new address_adapter(tempStorage.getInstance().getAddressList(),this);
            addsRecyclerView.setAdapter(adapter);
        })));

    }
}
