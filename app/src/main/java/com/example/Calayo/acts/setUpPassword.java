package com.example.Calayo.acts;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Calayo.R;

public class setUpPassword extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_set);
        // Start the main activity
        new Handler().postDelayed(()->{
            startActivity(new Intent(this, start_act.class));
            finish(); // Close the splash activity
        },3000);
    }
}
