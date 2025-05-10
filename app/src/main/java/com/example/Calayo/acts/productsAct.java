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
import com.example.Calayo.fragments.userMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class productsAct extends AppCompatActivity {

    private RecyclerView products;
    private ArrayList<Item> list;
    private product_adapt Adaptor;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth myAuth= FirebaseAuth.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_food_menu);
//        loadServices();
        ImageView home = findViewById(R.id.home);

        ImageView cart = findViewById(R.id.cart);
        cart.setOnClickListener(v -> {
            Intent intent = new Intent(this,AddToCart.class);
            startActivity(intent);
        });

        home.setOnClickListener(view -> {
                    FirebaseUser user = myAuth.getCurrentUser();
                    if (user != null) {
                        String uid = user.getUid();
                        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                Intent intent = new Intent(this, UserDashboardAct.class); // Replace with actual target
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {
                        Intent homepage = new Intent(this, main_act.class);
                        startActivity(homepage);
                    }
                });

        ImageView menu = findViewById(R.id.menu);
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

        //Load products
        //Products
        products = findViewById(R.id.Products_Recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        products.setLayoutManager(layoutManager);

        ArrayList<Item> data = getItems();
        Adaptor = new product_adapt(data,this);
        products.setAdapter(Adaptor);


    }

    @Override
    protected void onResume() {
        super.onResume();
//        loadServices();
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
    @NonNull
    private static ArrayList<Item> getItems() {
        ArrayList<Item> data = new ArrayList<>();
        Item newItem = new Item(200,"https://ik.imagekit.io/k4imggmfa/pizza1.png?updatedAt=1746566552587","Pizza",50,"Hawaiian pizza typically uses, mozzarella cheese, ham pizza dough, pizza sauce, mozzarella cheese, ham, and pineapple. Some variations also include bacon or other toppings.");
        Item newItem2 = new Item(200,"https://ik.imagekit.io/k4imggmfa/burger.png?updatedAt=1746566830867","Burger",50,"Hawaiian pizza typically uses, mozzarella cheese, ham pizza dough, pizza sauce, mozzarella cheese, ham, and pineapple. Some variations also include bacon or other toppings.");
        Item newItem3 = new Item(200,"https://ik.imagekit.io/k4imggmfa/burger(1).png?updatedAt=1746566911926","Chicken Burger",50,"Hawaiian pizza typically uses, mozzarella cheese, ham pizza dough, pizza sauce, mozzarella cheese, ham, and pineapple. Some variations also include bacon or other toppings.");

        data.add(newItem);
        data.add(newItem2);
        data.add(newItem3);
        return data;
    }
}
