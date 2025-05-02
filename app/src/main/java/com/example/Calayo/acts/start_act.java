package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Calayo.R;

public class start_act extends AppCompatActivity {

    private ImageView swipeThumb;
    private FrameLayout swipeFrame;
    private float dX;
    private boolean isConfirmed = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_act);

        swipeThumb = findViewById(R.id.swipe_thumb);
        swipeFrame = findViewById(R.id.swipe_frame);

        swipeThumb.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dX = view.getX() - motionEvent.getRawX();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float newX = motionEvent.getRawX() + dX;
                    float maxX = swipeFrame.getWidth() - swipeThumb.getWidth();

                    if (newX >= 0 && newX <= maxX) {
                        view.setX(newX);
                    }

                    if (newX >= maxX - 10 && !isConfirmed) {
                        isConfirmed = true;
                        Intent intent = new Intent(this, main_act.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(this, "Confirmed!", Toast.LENGTH_SHORT).show();
                        view.performClick();  // Important for accessibility warning
                    }
                    return true;

                case MotionEvent.ACTION_UP:
                    if (!isConfirmed) {
                        view.animate().x(0).setDuration(300).start();
                    }
                    return true;
            }
            return false;
        });
    }
}
