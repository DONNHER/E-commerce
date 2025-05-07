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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.Calayo.R;
import com.google.firebase.auth.FirebaseUser;
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

    private RecyclerView products ;
    private product_adapt Adapt;

    RecyclerView addsRecyclerView;
    AddsADaptor adapter;

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser user = myAuth.getCurrentUser();
        if(user != null){
            String uid = user.getUid();
            db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                if(documentSnapshot.exists()) {
                        Intent intent = new Intent(this, UserDashboardAct.class); // Replace with actual target
                        startActivity(intent);
                        finish();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        recyclerView = findViewById(R.id.appointmentsView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ImageView home = findViewById(R.id.home);
        home.setOnClickListener(view -> {
            Intent homepage = new Intent(this,UserDashboardAct.class);
            startActivity(homepage);
        });
        ImageView menu = findViewById(R.id.foodMenu);
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
        products = findViewById(R.id.Products_Recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        products.setLayoutManager(layoutManager);

        ArrayList<Item> data = getItems();
        Adapt = new product_adapt(data,this);
        products.setAdapter(Adapt);

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

    @NonNull
    private static ArrayList<Item> getItems() {
        ArrayList<Item> data = new ArrayList<>();
        Item newItem = new Item(200,"https://ik.imagekit.io/k4imggmfa/pizza1.png?updatedAt=1746566552587","Pizza",50,"Hawaiian pizza typically uses, mozzarella cheese, ham pizza dough, pizza sauce, mozzarella cheese, ham, and pineapple. Some variations also include bacon or other toppings.");
        Item newItem2 = new Item(180,"https://ik.imagekit.io/k4imggmfa/burger.png?updatedAt=1746566830867","Burger",50,"Hawaiian pizza typically uses, mozzarella cheese, ham pizza dough, pizza sauce, mozzarella cheese, ham, and pineapple. Some variations also include bacon or other toppings.");
        Item newItem3 = new Item(250,"https://ik.imagekit.io/k4imggmfa/hotdogs.png?updatedAt=1746566840760","Chicken Burger",50,"Hawaiian pizza typically uses, mozzarella cheese, ham pizza dough, pizza sauce, mozzarella cheese, ham, and pineapple. Some variations also include bacon or other toppings.");

        data.add(newItem);
        data.add(newItem2);
        data.add(newItem3);
        Item newI= new Item(200,"https://ik.imagekit.io/k4imggmfa/pizza1.png?updatedAt=1746566552587","Pizza",50,"Hawaiian pizza typically uses, mozzarella cheese, ham pizza dough, pizza sauce, mozzarella cheese, ham, and pineapple. Some variations also include bacon or other toppings.");
        Item newIte = new Item(200,"https://ik.imagekit.io/k4imggmfa/burger.png?updatedAt=1746566830867","Burger",50,"Hawaiian pizza typically uses, mozzarella cheese, ham pizza dough, pizza sauce, mozzarella cheese, ham, and pineapple. Some variations also include bacon or other toppings.");
        Item neww = new Item(200,"https://ik.imagekit.io/k4imggmfa/hotdogs.png?updatedAt=1746566840760","Chicken Burger",50,"Hawaiian pizza typically uses, mozzarella cheese, ham pizza dough, pizza sauce, mozzarella cheese, ham, and pineapple. Some variations also include bacon or other toppings.");

        data.add(newItem);
        data.add(newItem2);
        data.add(newItem3);
        data.add(newI);
        data.add(newIte);
        data.add(neww);
        return data;

    }

    public void onMenuClick2(View view) {
        Intent intent = new Intent(view.getContext(), settingAct.class);
        view.getContext().startActivity(intent);

    }
    public void onLogClick(View view) {
        Intent intent = new Intent(view.getContext(), userLoginAct.class);
        view.getContext().startActivity(intent);
    }
    public void onResClick(View view) {
        userRegisterAct dialogFragment = new userRegisterAct();
        Intent intent = new Intent(view.getContext(), userLoginAct.class);
        view.getContext().startActivity(intent);
    }

}
