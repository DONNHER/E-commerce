package com.example.Calayo.acts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Calayo.R;
import com.example.Calayo.fragments.userLoginAct;
import com.google.firebase.auth.FirebaseAuth;
//import com.example.uni.management.SessionManager;

public class settingAct extends AppCompatActivity {

    private final FirebaseAuth myAuth= FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//
//        if (!isSessionActive()) {
//            // Redirect to Login if session is not found
//            Intent intent = new Intent(this, userLoginAct.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            finish();
//            return; // Prevent further execution
//        }

        // Load Main UI for the logged-in user
        setContentView(R.layout.setting_act);
//        Button btnGetStarted = findViewById(R.id.btn_edit_profile);

//         Set the action for the "Get Started" button
//        btnGetStarted.setOnClickListener(v -> {
//            // Action to perform when "Get Started" is clicked
//            // Redirect to Login or next activity, for example
//            Intent intent = new Intent(settingAct.this, userAct.class);
//            startActivity(intent);
//        });
        ImageView home = findViewById(R.id.home);
        home.setOnClickListener(view -> {
            Intent homepage = new Intent(this,UserDashboardAct.class);
            startActivity(homepage);
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
    }

    private void initializeServices() {

//        com.univet.service.PetService petService = new com.univet.service.PetService(dbHelper);
//        com.univet.service.OwnerService ownerService = new com.univet.service.OwnerService(dbHelper);
//        com.univet.service.ServiceService serviceService = new com.univet.service.ServiceService(dbHelper);
//        com.univet.service.AppointmentService appointmentService = new com.univet.service.AppointmentService(dbHelper);
//
//        // Initialize SessionManager
//        SessionManager.initialize(petService, ownerService, serviceService, appointmentService);
    }


    // Check if a session exists
    private boolean isSessionActive() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    private void loadUserRoleUI() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "");

        Intent intent;
        if ("admin".equals(role)) {
            intent = new Intent(this, UserDashboardAct.class);
//        else if ("technician".equals(role)) {
//            intent = new Intent(this, TechnicianDashboardActivity.class);
//        } else {
//            intent = new Intent(this, OwnerDashboardActivity.class);
//        }
            startActivity(intent);
            finish(); // Close MainActivity after redirection

        }
    }
    public void back(View view) {
        finish();
    }
    public void logoutClick(View view) {
        myAuth.signOut();
        Intent intent = new Intent(this, main_act.class); // Replace with actual target
        startActivity(intent);
        finish();
    }
    public void editProfileClick(View view) {
        Intent intent = new Intent(this, userAct.class); // Replace with actual target
        startActivity(intent);
        finish();
    }
//    public void onProductClick(View view) {
//        if(ownerLogin.isLoggedIn()==null){
//            Intent intent = new Intent(this, userLoginAct.class); // Replace with actual target
//            startActivity(intent);
//            finish();
//        }
//        Intent intent = new Intent(this, productsAct.class); // Replace with actual target
//        startActivity(intent);
//        finish();
    }
//    public void onOtherClick(View view) {
//        if(ownerLogin.isLoggedIn()==null){
//            Intent intent = new Intent(this, userLoginAct.class); // Replace with actual target
//            startActivity(intent);
//            finish();
//        }
//        Intent intent = new Intent(this, otherServiceAct.class); // Replace with actual target
//        startActivity(intent);
//        finish();
//    }
//    public void onLogClick(View view) {
//        Intent intent = new Intent(this, userLoginAct.class); // Replace with actual target
//        startActivity(intent);
//        finish();
//    }
//    public void onResClick(View view) {
//        Intent intent = new Intent(this, userRegisterAct.class); // Replace with actual target
//        startActivity(intent);
//        finish();
//    }
//}
