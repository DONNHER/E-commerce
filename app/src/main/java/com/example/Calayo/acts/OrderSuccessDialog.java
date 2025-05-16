package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Calayo.R;
import com.example.Calayo.entities.user;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Displays a confirmation screen after a successful order.
 * Shows user name, date, and time.
 */
public class OrderSuccessDialog extends AppCompatActivity {

    private static final String TAG = "OrderSuccessDialog";

    private FirebaseAuth myAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private TextView name, date, time;
    private ImageView backBtn;
    private Button homeBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_successs);

        initializeViews();
        loadUserName();
        setDateTime();
        setListeners();
    }

    /**
     * Initializes all views from layout.
     */
    private void initializeViews() {
        name = findViewById(R.id.name);
        date = findViewById(R.id.Date);
        time = findViewById(R.id.time);
        backBtn = findViewById(R.id.back);
        homeBtn = findViewById(R.id.home);
    }

    /**
     * Loads the logged-in user's name from Firestore.
     */
    private void loadUserName() {
        if (myAuth.getCurrentUser() == null) {
            Log.e(TAG, "No authenticated user");
            name.setText("Guest");
            return;
        }

        db.collection("users")
                .document(myAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        user newUser = doc.toObject(user.class);
                        if (newUser != null && newUser.getName() != null) {
                            name.setText(newUser.getName());
                        } else {
                            name.setText("User");
                        }
                    } else {
                        name.setText("User");
                        Log.w(TAG, "User document does not exist");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch user", e);
                    name.setText("User");
                });
    }

    /**
     * Sets the current date and time in their respective TextViews.
     */
    private void setDateTime() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Date now = new Date();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

            date.setText(dateFormat.format(now));
            time.setText(timeFormat.format(now));
        } else {
            date.setText("N/A");
            time.setText("N/A");
        }
    }

    /**
     * Sets up button listeners for navigation.
     */
    private void setListeners() {
        homeBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, UserDashboardAct.class));
            finish();
        });

        backBtn.setOnClickListener(v -> finish());
    }
}
