package com.example.Calayo.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.Calayo.R;
import com.example.Calayo.entities.Item;
import com.example.Calayo.helper.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class product_management extends DialogFragment {

    private ActivityResultLauncher<Intent> galleryLauncher;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Uri selectedImageUri;
    private ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_management, container, false);

        EditText nameEditText = view.findViewById(R.id.TextName3);
        EditText priceEdit = view.findViewById(R.id.editTextPrice3);
        EditText slots = view.findViewById(R.id.slot_number);
        imageView = view.findViewById(R.id.imageViewSelected_pm);
        Button cancelButton = view.findViewById(R.id.cancel_pm);
        Button submitButton = view.findViewById(R.id.confirm_pm);

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

            if (name.isEmpty() || price.isEmpty() || selectedImageUri == null) {
                Toast.makeText(getContext(), "Please fill all fields and select an image.", Toast.LENGTH_SHORT).show();
            } else {
                // Generate a unique filename for the image (e.g., using the current timestamp)
                String fileName = "service_" + System.currentTimeMillis() + ".jpg";

                // Call the Firebase.upload method
                Firebase.upload(getContext(), selectedImageUri, fileName, new Firebase.UploadCallback() {
                    @Override
                    public void onSuccess(String imageUrl) {
                        // Create a ServiceType object
                        String docId = UUID.randomUUID().toString();
                        Item item = new Item(Double.parseDouble(price), imageUrl, name,Integer.parseInt(units));
                        item.setId(docId);
                        // Prepare data to be stored in Firestore
                        Map<String, Object> data = new HashMap<>();
                        data.put("uid", item.getId());
                        data.put("name", item.getName());
                        data.put("price", item.getPrice());
                        data.put("Slots", item.getQuantity());
                        data.put("image", item.getImage());

                        // Upload data to Firestore
                        db.collection("products").document(docId).set(data)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Service added: " + item.getName(), Toast.LENGTH_SHORT).show();
                                    dismiss();
                                    requireActivity().recreate();
                                      // Close the dialog
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        cancelButton.setOnClickListener(v -> dismiss());
        return view;
    }


    // Method to open the gallery and select an image
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);  // Open gallery for image selection
    }
}
