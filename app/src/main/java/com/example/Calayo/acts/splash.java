package com.example.Calayo.acts;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Calayo.R;
import com.example.Calayo.helper.tempStorage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class splash extends AppCompatActivity {
        tempStorage temp = tempStorage.getInstance();
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.splash);
            // Start the main activity
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> temp.loadAllData(() -> runOnUiThread(() -> {
                Intent intent = new Intent(splash.this, main_act.class);
                startActivity(intent);
                finish();
            })));
        }
    }
