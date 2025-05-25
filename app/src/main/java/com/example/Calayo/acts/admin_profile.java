package com.example.Calayo.acts;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Calayo.R;
import com.example.Calayo.helper.Firebase;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class admin_profile  extends AppCompatActivity {

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

        TextView changePass = findViewById(R.id.changePass);
        TextView changeEmail = findViewById(R.id.changeEmail);

        changePass.setOnClickListener(v -> {
            Intent intent = new Intent(this, change_pass.class);
            startActivity(intent);
        });
        changeEmail.setOnClickListener(v -> {
            Intent intent = new Intent(this, change_email.class);
            startActivity(intent);
        });

        ImageView home = findViewById(R.id.home);

        home.setOnClickListener(view -> {
            FirebaseUser user = myAuth.getCurrentUser();
            if (user != null) {
                String uid = user.getUid();
                db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Intent intent = new Intent(this, Rider_Dashboard.class); // Replace with actual target
                        startActivity(intent);
                        finish();
                    }
                });
            } else {
                Intent homepage = new Intent(this, main_act.class);
                startActivity(homepage);
            }
        });


        ImageView profile = findViewById(R.id.profile);
        profile.setOnClickListener(view -> recreate());
        imageEdit.setOnClickListener(v -> {
            openGallery();
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
                            Toast.makeText(admin_profile.this, "Error", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(admin_profile.this, errorMessage, Toast.LENGTH_SHORT).show();
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

        });
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
        db.collection("users").document(temp.getLoggedin()).collection("Address Selected")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        return;
                    }
                    db.collection("users").document(temp.getLoggedin()).collection("Address Selected")
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
