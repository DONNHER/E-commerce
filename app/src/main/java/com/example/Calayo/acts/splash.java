package com.example.Calayo.acts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Calayo.R;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class splash extends AppCompatActivity {
    private tempStorage temp;
    private FirebaseAuth myAuth;
    private boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        try {
            temp = tempStorage.getInstance();
            myAuth = FirebaseAuth.getInstance();

            SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
            isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            FirebaseUser currentUser = myAuth.getCurrentUser();

            if (isLoggedIn && currentUser != null) {
                String uid = currentUser.getUid();
                temp.setLoggedin(uid);

                executor.execute(() -> {
                    try {
                        temp.loadAllData(() -> temp.loadAllUserData(() -> runOnUiThread(() -> {
                            Intent intent = new Intent(splash.this, UserDashboardAct.class);
                            startActivity(intent);
                            finish();
                        })));
                    } catch (Exception e) {
                        Log.e("Splash", "Error during user data load", e);
                        redirectToLogin();
                    }
                });
            } else {
                // Invalid session, clear login and go to main_act
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();

                executor.execute(() -> {
                    try {
                        temp.loadAllData(() -> runOnUiThread(this::redirectToLogin));
                    } catch (Exception e) {
                        Log.e("Splash", "Error loading data for login redirect", e);
                        runOnUiThread(this::redirectToLogin);
                    }
                });
            }

        } catch (Exception e) {
            Log.e("Splash", "Critical error in onCreate", e);
            redirectToLogin();
        }
    }

    private void redirectToLogin() {
        Intent intent = new Intent(splash.this, main_act.class);
        startActivity(intent);
        finish();
    }
}
