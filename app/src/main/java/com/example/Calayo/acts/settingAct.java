package com.example.Calayo.acts;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Calayo.R;
import com.example.Calayo.helper.Firebase;
import com.example.Calayo.helper.tempStorage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
//import com.example.uni.management.SessionManager;

public class settingAct extends AppCompatActivity {

    private final FirebaseAuth myAuth = FirebaseAuth.getInstance();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityResultLauncher<Intent> galleryLauncher;
    private Uri selectedImageUri;
    private ImageView imageView;
    tempStorage temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_act);
        imageView = findViewById(R.id.image);
        ImageView imageEdit = findViewById(R.id.edit);
        imageEdit.setOnClickListener(v -> openGallery());
        TextView changePass = findViewById(R.id.changePass);
        TextView changeEmail = findViewById(R.id.changeEmail);

        changePass.setOnClickListener(v -> {
            Intent intent = new Intent(this, change_pass.class);
            startActivity(intent);
        });
        changePass.setOnClickListener(v -> {
            Intent intent = new Intent(this, change_pass.class);
            startActivity(intent);
        });


        ImageView home = findViewById(R.id.home);

        home.setOnClickListener(view -> {
            FirebaseUser user = myAuth.getCurrentUser();
            if (user != null) {
                String uid = user.getUid();
                db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Intent intent = new Intent(this, UserDashboardAct.class); // Replace with actual target
                        startActivity(intent);
                        finish();
                    }
                });
            } else {
                Intent homepage = new Intent(this, main_act.class);
                startActivity(homepage);
            }
        });
        ImageView menu = findViewById(R.id.foodMenu);
        menu.setOnClickListener(view -> {
            Intent menupage = new Intent(this, productsAct.class);
            startActivity(menupage);
        });
        ImageView history = findViewById(R.id.history);
        history.setOnClickListener(view -> {
            Intent menupage = new Intent(this, transactions.class);
            startActivity(menupage);
        });
        ImageView profile = findViewById(R.id.profile);
        profile.setOnClickListener(view -> {
            Intent profilepage = new Intent(this, settingAct.class);
            startActivity(profilepage);
        });
    }


    // Check if a session exists
    private boolean isSessionActive() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    private void loadUserRoleUI() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "");

        Intent intent;
        if ("admin".equals(role)) {
            intent = new Intent(this, UserDashboardAct.class);
//        else if ("technician".equals(role)) {
//            intent = new Intent(this, TechnicianDashboardActivity.class);
//        } else {
//            intent = new Intent(this, OwnerDashboardActivity.class);
//        }
            startActivity(intent);
            finish(); // Close MainActivity after redirection

        }


        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select an image.", Toast.LENGTH_SHORT).show();
        } else {
            // Generate a unique filename for the image (e.g., using the current timestamp)
            String fileName = "service_" + System.currentTimeMillis() + ".jpg";

            // Call the Firebase.upload method
            Firebase.upload(this, selectedImageUri, fileName, new Firebase.UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    // Prepare data to be stored in Firestore
                    Map<String, Object> data = new HashMap<>();
                    data.put("image", selectedImageUri);
                    DocumentReference reference = db.collection("users").document(temp.getLoggedin());
                    reference.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            CollectionReference document = reference.collection("image");
                            document.add(data).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    recreate();
                                }
                            });
                        }
                        Toast.makeText(settingAct.this, "Error", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(settingAct.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        imageView.setImageURI(selectedImageUri);
                    }
                }
        );
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    public void back(View view) {
        finish();
    }

    public void logoutClick(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences preferences = getSharedPreferences("SelectedAddress", MODE_PRIVATE);
        SharedPreferences preferences2 = getSharedPreferences("typeAddress", MODE_PRIVATE);
        String addressStr = preferences.getString("SelectedAddress", "");
        String addressType = preferences2.getString("typeAddress", "");
        HashMap<String, Object> itemMap = new HashMap<>();
        itemMap.put("name", addressType);
        itemMap.put("fullAddress", addressStr);
        db.collection("users").document(sharedPreferences.getString("isLoggedIn", "")).collection("Address Selected")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        return;
                    }
                    db.collection("users").document(sharedPreferences.getString("isLoggedIn", "")).collection("Address Selected")
                            .add(itemMap).isSuccessful();
                });

        preferences2.edit().clear().apply();
        preferences.edit().clear().apply();
        sharedPreferences.edit().clear().apply();
        myAuth.signOut();
        Intent intent = new Intent(this, main_act.class); // Replace with actual target
        startActivity(intent);
        finish();
    }

    public void editProfileClick(View view) {
        Intent intent = new Intent(this, userAct.class); // Replace with actual target
        startActivity(intent);
        finish();
    }
}
