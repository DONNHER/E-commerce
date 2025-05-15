package com.example.Calayo.acts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Calayo.R;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class splash extends AppCompatActivity {
        tempStorage temp = tempStorage.getInstance();
        FirebaseAuth myAuth;
        private boolean isLoggedIn;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.splash);
            // Start the main activity
            ExecutorService executor = Executors.newSingleThreadExecutor();
            SharedPreferences sharedPreferences  = getSharedPreferences("user", MODE_PRIVATE);
            isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

            if (isLoggedIn) {
                executor.execute(() ->{
                    myAuth = FirebaseAuth.getInstance();
                    if(temp.getLoggedin() == null) {
                        temp.setLoggedin(myAuth.getCurrentUser().getUid());
                    }
                    temp.loadAllUserData(() -> runOnUiThread(() -> {
                        Intent intent = new Intent(splash.this, UserDashboardAct.class);
                        startActivity(intent);
                        finish();
                    }));
                });
                return;
            }
            executor.execute(() -> temp.loadAllData(() -> runOnUiThread(() -> {
            Intent intent = new Intent(splash.this, main_act.class);
            startActivity(intent);
            finish();
             })));
        }
    }
