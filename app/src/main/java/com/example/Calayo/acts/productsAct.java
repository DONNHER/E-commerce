package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Calayo.R;
import com.example.Calayo.adapters.product_adapt;
import com.example.Calayo.entities.Item;
import com.example.Calayo.fragments.userMenu;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class productsAct extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Item> list;
    private product_adapt Adaptor;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.products);
        recyclerView = findViewById(R.id.Products_Recycler);
        list = new ArrayList<>();
        Adaptor = new product_adapt(list,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(Adaptor);
        loadServices();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadServices();
    }

    public void onside(View view) {
        userMenu menu = new userMenu();
        menu.show(getSupportFragmentManager(), "MenuDialog");
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadServices() {
        db.collection("products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                list.clear();
                for (QueryDocumentSnapshot d : task.getResult()) {
                    Item newItem = d.toObject(Item.class);
                    list.add(newItem);
                    Adaptor.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(getApplicationContext(), "error:2", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
