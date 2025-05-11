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

public class myAddress extends AppCompatActivity {
    RecyclerView addsRecyclerView;
    address_adapter adapter;
    ArrayList<address> adds = new ArrayList<>();
    private final FirebaseAuth myAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_address);
        Button btn = findViewById(R.id.btn);
        btn.setOnClickListener(view -> finish());

        Button switch_acc = findViewById(R.id.switch_acc);
        switch_acc.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
            sharedPreferences.edit().remove("isLoggedIn").apply();
            myAuth.signOut();
            Intent intent = new Intent(this, main_act.class); // Replace with actual target
            startActivity(intent);
            finish();
        });

        addsRecyclerView = findViewById(R.id.Product_Address_Recycler);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        addsRecyclerView.setLayoutManager(layoutManager2);


//        address data = new address("P2", " Palma", "Kibawe", "Bukidnon", "8720","home");
//
//        address data2 = new address("P18", " Musuan", "Maramag", "Bukidnon", "8714","work");
//
//        adds.add(data);
//        adds.add(data2);
//        for (address l : adds) {
//            Map<String, Object> mydata2 = new HashMap<>();
//            mydata2.put("street", l.getStreet());
//            mydata2.put("baranggay", l.getBaranggay());
//            mydata2.put("city", l.getCity());
//            mydata2.put("province", l.getProvince());
//            mydata2.put("code", l.getCode());
//            mydata2.put("name", l.getName());
//
//            db.collection("users").document(myAuth.getCurrentUser().getUid()).collection("address").document(l.getName()).get().addOnCompleteListener(task -> {
//                if(task.isSuccessful()){
//                    DocumentSnapshot d = task.getResult();
//                    if(d.exists()){
//                        Log.d("Firestore", "Document already exists");
//                    }else {
//                        db.collection("users").document(myAuth.getCurrentUser().getUid()).collection("address").document(l.getName()).set(mydata2);
//                    }
//                }else {
//                    Log.d("Firestore","Failed to get document", task.getException());
//                }
//            });
//        }

        adapter = new address_adapter(tempStorage.getInstance().getAddressList(),this);
        addsRecyclerView.setAdapter(adapter);
    }
    @Override
    public void onResume(){
        super.onResume();
        adapter = new address_adapter(tempStorage.getInstance().getAddressList(),this);
        addsRecyclerView.setAdapter(adapter);
    }
}
