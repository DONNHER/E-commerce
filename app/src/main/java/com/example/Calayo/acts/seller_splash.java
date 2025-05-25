package com.example.Calayo.acts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Calayo.R;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class seller_splash extends AppCompatActivity {
    private tempStorage temp;
    private FirebaseAuth myAuth;
    private boolean isLoggedIn;
    private ProgressBar progressBar;
    private Handler handler = new Handler();
    private Runnable timeoutRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Uri data = getIntent().getData();
        if (data != null &&
                "manager.portal".equals(data.getScheme()) &&
                "login".equals(data.getHost())) {

            progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE); // Show spinner immediately

            temp = tempStorage.getInstance();
            myAuth = FirebaseAuth.getInstance();

            SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
            isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

            FirebaseUser currentUser = myAuth.getCurrentUser();
            ExecutorService executor = Executors.newSingleThreadExecutor();

            // Setup 30 seconds timeout to handle slow loading or network issues
            timeoutRunnable = () -> {
                if (progressBar.getVisibility() == View.VISIBLE) {
                    Toast.makeText(seller_splash.this,
                            "Loading is taking longer than usual. Please check your connection.",
                            Toast.LENGTH_LONG).show();
                    // Optional: Redirect to login after timeout if needed
                    redirectToLogin();
                }
            };
            handler.postDelayed(timeoutRunnable, 30000); // 30 seconds

            if (isLoggedIn && currentUser != null) {
                String uid = currentUser.getUid();
                temp.setLoggedin(uid);

                executor.execute(() -> {
                    try {
                        temp.loadAllData(() -> temp.loadAllUserData(() -> runOnUiThread(() -> {
                            // Cancel timeout and hide spinner on success
                            handler.removeCallbacks(timeoutRunnable);
                            progressBar.setVisibility(View.GONE);

                            startActivity(new Intent(seller_splash.this, ManagerDashB.class));
                            finish();
                        })));
                    } catch (Exception e) {
                        Log.e("Splash", "Error loading user data", e);
                        runOnUiThread(() -> {
                            handler.removeCallbacks(timeoutRunnable);
                            progressBar.setVisibility(View.GONE);
                            redirectToLogin();
                        });
                    }
                });
            } else {
                // Not logged in - clear flag and load generic data
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();

                executor.execute(() -> {
                    try {
                        temp.loadAllData(() -> runOnUiThread(() -> {
                            handler.removeCallbacks(timeoutRunnable);
                            progressBar.setVisibility(View.GONE);
                            redirectToLogin();
                        }));
                    } catch (Exception e) {
                        Log.e("Splash", "Error loading data", e);
                        runOnUiThread(() -> {
                            handler.removeCallbacks(timeoutRunnable);
                            progressBar.setVisibility(View.GONE);
                            redirectToLogin();
                        });
                    }
                });
            }
        }
    }

    private void redirectToLogin() {
        startActivity(new Intent(seller_splash.this, seller_login.class));
        finish();
    }
}
