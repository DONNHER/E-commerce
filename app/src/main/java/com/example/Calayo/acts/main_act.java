package com.example.Calayo.acts;

import com.example.Calayo.entities.Order;
import com.example.Calayo.fragments.userLoginAct;
import com.example.Calayo.fragments.userRegisterAct;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Calayo.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class main_act extends AppCompatActivity {
//    private RecyclerView appointmentsView ;
//    private order_adaptor Adapt;
    private Button btn1, btn2;
    private final ArrayList<Order> orders = new ArrayList<>();
    private RecyclerView recyclerView;
//    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth myAuth= FirebaseAuth.getInstance();

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser user = myAuth.getCurrentUser();
        if(user != null){
//            String uid = user.getUid();
//            db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
//                if(documentSnapshot.exists()) {
//                    if("user".equals(documentSnapshot.getString("role"))){
                        Intent intent = new Intent(this, UserDashboardAct.class); // Replace with actual target
                        startActivity(intent);
                        finish();
//                    }
//                }
//            });
        }
        else{
            Intent intent = new Intent(this, splash.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        recyclerView = findViewById(R.id.appointmentsView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
