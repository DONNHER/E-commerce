package com.example.Calayo.acts;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Calayo.R;
import com.example.Calayo.entities.Item;

import com.example.Calayo.entities.Seller;
import com.example.Calayo.helper.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class product_management extends AppCompatActivity {

    private ActivityResultLauncher<Intent> galleryLauncher;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Uri selectedImageUri;
    private ImageView imageView;
    private Seller s;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_management);

        EditText nameEditText = findViewById(R.id.TextName3);
        EditText desciption = findViewById(R.id.description);
        EditText priceEdit = findViewById(R.id.editTextPrice3);
        EditText slots = findViewById(R.id.slot_number);
        imageView = findViewById(R.id.imageViewSelected_pm);
        Button cancelButton = findViewById(R.id.cancel_pm);
        Button submitButton = findViewById(R.id.confirm_pm);

        // Register for result from gallery selection
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        imageView.setImageURI(selectedImageUri);  // Show the selected image in ImageView
                    }
                }
        );

        imageView.setOnClickListener(v -> openGallery());

        submitButton.setOnClickListener(v -> {
            String price = priceEdit.getText().toString().trim();
            String name = nameEditText.getText().toString().trim();
            String units = slots.getText().toString().trim();
            String des = desciption.getText().toString().trim();

            if (name.isEmpty() ||des.isEmpty() || price.isEmpty() || selectedImageUri == null) {
                Toast.makeText(this, "Please fill all fields and select an image.", Toast.LENGTH_SHORT).show();
            } else {
                if(des.length()>100){
                    Toast.makeText(this, "Description must be less than 100 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Generate a unique filename for the image (e.g., using the current timestamp)
                String fileName = "service_" + System.currentTimeMillis() + ".jpg";

                // Call the Firebase.upload method
                Firebase.upload(this, selectedImageUri, fileName, new Firebase.UploadCallback() {
                    @Override
                    public void onSuccess(String imageUrl) {
                        // Create a ServiceType object
                        String docId = UUID.randomUUID().toString();
                        Item item = new Item(Double.parseDouble(price), imageUrl, name,Integer.parseInt(units),des,s.getStoreName());
                        item.setId(docId);
                        // Prepare data to be stored in Firestore
                        Map<String, Object> data = new HashMap<>();
                        data.put("uid", item.getId());
                        data.put("name", item.getName());
                        data.put("price", item.getPrice());
                        data.put("Slots", item.getQuantity());
                        data.put("image", item.getImage());
                        data.put("description", item.getDescription());
                        data.put("Status", item.getStatus());

                        // Upload data to Firestore
                        db.collection("products").document(docId).set(data)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(product_management.this, "Food added: " + item.getName(), Toast.LENGTH_SHORT).show();
                                    recreate();
                                      // Close the dialog
                                })
                                .addOnFailureListener(e -> Toast.makeText(product_management.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(product_management.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        cancelButton.setOnClickListener(v -> finish());
    }


    // Method to open the gallery and select an image
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);  // Open gallery for image selection
    }
}
