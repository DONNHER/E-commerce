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
        tempStorage temp;
        FirebaseAuth myAuth;
        private boolean isLoggedIn;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.splash);

            temp = tempStorage.getInstance();
            SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
            isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            myAuth = FirebaseAuth.getInstance();

            ExecutorService executor = Executors.newSingleThreadExecutor();

            if (isLoggedIn && myAuth.getCurrentUser() != null) {
                // Valid session, proceed to dashboard
                temp.setLoggedin(myAuth.getCurrentUser().getUid());
                executor.execute(() -> temp.loadAllData(() ->
                        temp.loadAllUserData(() -> runOnUiThread(() -> {
                            Intent intent = new Intent(splash.this, UserDashboardAct.class);
                            startActivity(intent);
                            finish();
                        }))
                ));
            } else {
                // Invalid session, redirect to login
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();

                executor.execute(() -> temp.loadAllData(() -> runOnUiThread(() -> {
                    Intent intent = new Intent(splash.this, main_act.class);
                    startActivity(intent);
                    finish();
                })));
            }
        }
}
