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

import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;

public class splash extends AppCompatActivity {
    private tempStorage temp;
    private FirebaseAuth myAuth;
    private boolean isLoggedIn;
    private ProgressBar progressBar;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable progressRunnable;
    private int progressStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        progressBar = findViewById(R.id.progressBar);

        temp = tempStorage.getInstance();
        myAuth = FirebaseAuth.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        runProgressBar();

        // Start loading data in background
        FirebaseUser currentUser = myAuth.getCurrentUser();
        ExecutorService executor = Executors.newSingleThreadExecutor();

        if (isLoggedIn && currentUser != null) {
            String uid = currentUser.getUid();
            temp.setLoggedin(uid);

            executor.execute(() -> {
                try {
                    temp.loadAllData(() -> temp.loadAllUserData(() -> runOnUiThread(() -> {
                        // Stop progress and go to UserDashboardAct immediately when ready
                        handler.removeCallbacks(progressRunnable);
                        startActivity(new Intent(splash.this, UserDashboardAct.class));
                        finish();
                    })));
                } catch (Exception e) {
                    e.printStackTrace();
                    redirectToLogin();
                }
            });
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            executor.execute(() -> {
                try {
                    temp.loadAllData(() -> runOnUiThread(() -> {
                        handler.removeCallbacks(progressRunnable);
                        redirectToLogin();
                    }));
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(this::redirectToLogin);
                }
            });
        }
    }

    private void runProgressBar() {
        progressStatus = 0;
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (progressStatus < 100) {
                    progressStatus++;
                    progressBar.setProgress(progressStatus);
                    handler.postDelayed(this, 300); // 300ms * 100 = 30 seconds
                } else {
                    // Progress completed: if user data not loaded yet, redirect to login or dashboard?
                    if (!isLoggedIn) {
                        redirectToLogin();
                    } else {
                        // Optional: Do nothing, waiting for data loading or timeout
                    }
                }
            }
        };
        handler.post(progressRunnable);
    }

    private void redirectToLogin() {
        startActivity(new Intent(splash.this, main_act.class));
        finish();
    }
}

