package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Calayo.R;
import com.example.Calayo.adapters.addToCartAdapt;
import com.example.Calayo.adapters.product_adapt;
import com.example.Calayo.entities.Order;
import com.example.Calayo.entities.cartItem;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    ExecutorService executor = Executors.newSingleThreadExecutor();

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_to_cart);

        Button back = findViewById(R.id.btnBack);
        TextView units = findViewById(R.id.units);
        CheckBox all = findViewById(R.id.checkboxAll);
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
            executor.execute(() -> temp.loadAllData(() -> runOnUiThread(() -> startActivity(intent))));
        });

// When Select All checkbox is clicked
        all.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update all item selections in the list
            for (cartItem item : temp.getCartItemArrayList()) {
                item.setSelected(isChecked);
                Order newOrder = new Order(item.getImage(), new Date().toString(), new Date().toString(), temp.getLoggedin(), item.getName(), "", "");
                temp.getCheckOutArrayList().add(newOrder);
            }
            adapter.notifyDataSetChanged(); // Refresh the UI
        });

    }
}
