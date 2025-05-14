package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Calayo.R;
import com.example.Calayo.adapters.order_adaptor;
import com.example.Calayo.entities.Item;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class transactions extends AppCompatActivity {
    private RecyclerView transactions_recycler;
    private ArrayList<Item> list;
    private order_adaptor Adaptor;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth myAuth= FirebaseAuth.getInstance();

    private tempStorage temp = tempStorage.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transactions);

        ImageView home = findViewById(R.id.home);
        home.setOnClickListener(view -> {
            if (temp.getLoggedin() == null) {
                Intent homepage = new Intent(this, main_act.class);
                startActivity(homepage);
            } else {
                Intent intent = new Intent(this, UserDashboardAct.class); // Replace with actual target
                startActivity(intent);
            }
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
            if(myAuth.getCurrentUser() == null) {
                Intent login = new Intent(this, userLoginAct.class);
                startActivity(login);
            }else {
                Intent profilepage = new Intent(this, settingAct.class);
                startActivity(profilepage);
            }
        });
        //Products
        transactions_recycler = findViewById(R.id.notification_Recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        transactions_recycler.setLayoutManager(layoutManager);

        Adaptor = new order_adaptor(temp.getCheckOutArrayList(),this);
        transactions_recycler.setAdapter(Adaptor);

    }
    public void onResume(){
        super.onResume();
        Adaptor = new order_adaptor(temp.getCheckOutArrayList(),this);
        transactions_recycler.setAdapter(Adaptor);

    }
}
