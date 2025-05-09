package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Calayo.R;
import com.example.Calayo.adapters.addOns;
import com.example.Calayo.entities.Item;
import com.example.Calayo.entities.address;
import com.example.Calayo.fragments.OrderSuccessDialog;
import com.example.Calayo.fragments.userRegisterAct;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class checkout  extends AppCompatActivity {
    private double totalCost ;
    private tempStorage temp = tempStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private com.example.Calayo.adapters.addOns addOn;
    private RecyclerView addOnsRecycler;

    @SuppressLint({"WrongViewCast", "MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout);
        GridLayout checkout = findViewById(R.id.checkout);
        TextView cost = findViewById(R.id.Price3);
        TextView tCost = findViewById(R.id.totalPrice);
        TextView Name = findViewById(R.id.name);
        TextView Name2 = findViewById(R.id.name2);
        TextView Name3= findViewById(R.id.name3);
        Button back = findViewById(R.id.btnBack);
        TextView units = findViewById(R.id.units);
        TextView address = findViewById(R.id.address);

        TextView header = findViewById(R.id.header);
        if(temp.getSelectedAddress()==null){
            Intent intent = new Intent(this, myAddress.class);
            Toast.makeText(this,"Please select address first.",Toast.LENGTH_LONG).show();
            startActivity(intent);
            recreate();
        }
        address.setText(temp.getSelectedAddress().getFullAddress());
        header.setText(temp.getSelectedAddress().getName());

        addOnsRecycler = findViewById(R.id.OrderSummary_Recycler3);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        addOnsRecycler.setLayoutManager(layoutManager);
        addOn = new addOns(temp.getAddOnArrayList(), checkout.this);
        addOnsRecycler.setAdapter(addOn);

        String price = getIntent().getStringExtra("price");
        cost.setText(price);


        String des = getIntent().getStringExtra("quantity");
        units.setText("("+des+"item):");
        Name3.setText(" "+des+"x");



        totalCost =  Integer.parseInt(des) * Double.parseDouble(price);

        String name = getIntent().getStringExtra("name");
        Name.setText(name);
        Name2.setText(name);

        tCost.setText(""+(temp.getTotalAddOnPrice()+ totalCost));

        checkout.setOnClickListener(view2 -> {
            Intent intent = new Intent(this,OrderSuccessDialog.class);
            startActivity(intent);
            finish();
        });

        back.setOnClickListener(view4 -> finish());

    }
}
