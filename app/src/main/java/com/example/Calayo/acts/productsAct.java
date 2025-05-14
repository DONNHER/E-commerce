package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Calayo.R;
import com.example.Calayo.adapters.product_adapt;
import com.example.Calayo.entities.Item;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class productsAct extends AppCompatActivity {

    private RecyclerView products;
    private product_adapt Adaptor;
    private final FirebaseAuth myAuth= FirebaseAuth.getInstance();
    tempStorage temp = tempStorage.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_food_menu);
        ImageView home = findViewById(R.id.home);
        ImageView cart = findViewById(R.id.cart);
        cart.setOnClickListener(v -> {
            Intent intent = new Intent(this,AddToCart.class);
            startActivity(intent);
        });

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
            Intent menupage = new Intent(this,transactions.class);
            startActivity(menupage);
        });
        ImageView profile = findViewById(R.id.profile);
        profile.setOnClickListener(view -> {
            Intent profilepage = new Intent(this, settingAct.class);
            startActivity(profilepage);
        });

        //Load products
        //Products
        products = findViewById(R.id.Products_Recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        products.setLayoutManager(layoutManager);

        Adaptor = new product_adapt(temp.getItemArrayList(),this);
        products.setAdapter(Adaptor);
    }
}
