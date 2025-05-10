package com.example.Calayo.acts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.Calayo.R;
import com.example.Calayo.adapters.AddsADaptor;
import com.example.Calayo.adapters.addToCartAdapt;
import com.example.Calayo.adapters.product_adapt;
import com.example.Calayo.entities.Order;
import com.example.Calayo.entities.adds;
import com.example.Calayo.fragments.userLoginAct;
import com.example.Calayo.fragments.userRegisterAct;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddToCart extends AppCompatActivity {
    //    private RecyclerView appointmentsView ;
//    private order_adaptor Adapt;
    private Button btn1, btn2;
    private final ArrayList<Order> orders = new ArrayList<>();
    private RecyclerView recyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth myAuth= FirebaseAuth.getInstance();
    private tempStorage temp = tempStorage.getInstance();
    private RecyclerView products ;
    private product_adapt Adapt;

    RecyclerView cartRecyclerView;
    addToCartAdapt adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_to_cart);

        Button back = findViewById(R.id.btnBack);
        TextView units = findViewById(R.id.units);

        units.setText(temp.getCartItemArrayList().size()+"");
        back.setOnClickListener(view -> finish());
        Button checkout = findViewById(R.id.checkout);

        back.setOnClickListener(view -> {
            finish();
        });

        //Products
        cartRecyclerView = findViewById(R.id.CartItems_Recycler3);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        cartRecyclerView.setLayoutManager(layoutManager);

        if(!temp.getCartItemArrayList().isEmpty()){
            adapter = new addToCartAdapt(temp.getCartItemArrayList(),this);
            cartRecyclerView.setAdapter(adapter);
        }
        checkout.setOnClickListener(view2 ->{
            Intent intent = new Intent(this, checkout.class);
            startActivity(intent);
        });
    }
}
