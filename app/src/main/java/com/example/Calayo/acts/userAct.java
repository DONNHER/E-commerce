package com.example.Calayo.acts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Calayo.R;
import com.example.Calayo.fragments.userLoginAct;
import com.example.Calayo.fragments.userRegisterAct;

public class userAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load Main UI for the logged-in user
        setContentView(R.layout.user_act);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    public void back(View view) {
        finish();
    }

    public void onBtnClick(View view) {
        Intent intent = new Intent(this, settingAct.class); // Replace with actual target
        startActivity(intent);
        finish();
    }
    public void onLogClick(View view) {
        Intent intent = new Intent(this, userLoginAct.class); // Replace with actual target
        startActivity(intent);
        finish();
    }
    public void onResClick(View view) {
        Intent intent = new Intent(this, userRegisterAct.class); // Replace with actual target
        startActivity(intent);
        finish();
    }
}
