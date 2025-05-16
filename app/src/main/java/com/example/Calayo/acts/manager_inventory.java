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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * This activity displays and manages the inventory for an admin.
 * Admin can view, add, or update products from Firestore.
 */
public class manager_inventory extends AppCompatActivity {

    private RecyclerView recyclerView; // The scrollable list UI
    private ArrayList<Item> list; // List of products to display
    private product_adapt Adaptor; // Adapter to bind data to RecyclerView
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // Firebase database

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.products); // Set the layout for this activity

        // Find the RecyclerView in the layout
        recyclerView = findViewById(R.id.Products_Recycler);

        // Initialize product list and adapter
        list = new ArrayList<>();
        Adaptor = new product_adapt(list, this);

        // Set layout manager (how the items are arranged) and connect the adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(Adaptor);

        // Load product data from Firebase
        loadServices();
    }

    // Called again every time this screen becomes active
    @Override
    protected void onResume() {
        super.onResume();
        loadServices(); // Refresh the product list
    }

    // This method is called when the side menu button is clicked
    public void onside(View view) {
        Menu menu = new Menu();
        menu.show(getSupportFragmentManager(), "MenuDialog");
    }

    // This method is called when the "add product" button is clicked
    public void add(View view) {
        product_management dialog = new product_management();
        dialog.show(getSupportFragmentManager(), "AddDialog"); // Show dialog to add new product
    }

    /**
     * Load products from the Firestore "products" collection and display them in the RecyclerView.
     */
    @SuppressLint("NotifyDataSetChanged")
    private void loadServices() {
        db.collection("products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                list.clear(); // Clear the current list to avoid duplicates
                for (QueryDocumentSnapshot d : task.getResult()) {
                    Item newItem = d.toObject(Item.class); // Convert Firestore document into an Item object
                    list.add(newItem); // Add to product list
                    Adaptor.notifyDataSetChanged(); // Notify the adapter to update the UI
                }
            } else {
                Toast.makeText(getApplicationContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
