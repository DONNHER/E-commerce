package com.example.Calayo.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.Calayo.R;
import com.example.Calayo.acts.AdminDashB;
import com.example.Calayo.acts.UserDashboardAct;
import com.example.Calayo.entities.Item;
import com.example.Calayo.entities.user;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class OrderSuccessDialog  extends AppCompatActivity {
    private FirebaseAuth myAuth= FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_successs);
        TextView name = findViewById(R.id.name);
        TextView date = findViewById(R.id.Date);
        TextView time = findViewById(R.id.time);
        ImageView btn= findViewById(R.id.back);

        Button home= findViewById(R.id.home);

        db.collection("users").document(myAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                   DocumentSnapshot d = task.getResult();
                   user newUser = d.toObject(user.class);
                   name.setText(newUser.getName());
                }
            }
        });

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Date now = new Date();
//            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
//            @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("KK:mm a");
//            String formattedDate = dateFormat.format(now);
//            String formattedTime = timeFormat.format(now);
//            time.setText(formattedTime);
//            date.setText(formattedDate);
//        }
        home.setOnClickListener(v -> {
            Intent backhome = new Intent(this,UserDashboardAct.class);
            startActivity(backhome);
            finish();
        });

        btn.setOnClickListener(v -> finish());

    }

}
