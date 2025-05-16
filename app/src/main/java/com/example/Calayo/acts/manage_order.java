package com.example.Calayo.acts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.Calayo.R;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This dialog allows an admin or user to view and manage a specific appointment/order.
 * The admin can confirm or cancel the appointment.
 */
public class manage_order extends DialogFragment {

    // Variables to hold appointment information
    private String date, time, name, services, appointmentId, status;
    private double totalCost;

    // Connect to Firebase Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Inflate the layout when the dialog is created
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.manage_order, container, false);
    }

    // This method runs after the layout is ready
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get data passed into this dialog (from another activity or fragment)
        if (getArguments() != null) {
            appointmentId = getArguments().getString("appointmentId");
            date = getArguments().getString("date");
            time = getArguments().getString("time");
            totalCost = getArguments().getDouble("totalCost");
            name = getArguments().getString("name");
            services = getArguments().getString("services");
            status = getArguments().getString("status");
        }

        // Find TextViews from the layout
        TextView Date = view.findViewById(R.id.Date);
        TextView Time = view.findViewById(R.id.Time);
        TextView tCost = view.findViewById(R.id.cost);
        TextView service = view.findViewById(R.id.service);

        // Show appointment details in the layout
        Date.setText(date);
        Time.setText(time);
        tCost.setText(String.format("%.2f", totalCost)); // Format the cost to 2 decimal places
        service.setText(services);

        // Set up buttons to change the appointment status
        view.findViewById(R.id.confirm).setOnClickListener(v -> updateStatus("Confirmed"));
        view.findViewById(R.id.cancel).setOnClickListener(v -> updateStatus("Cancelled"));
    }

    /**
     * Update the status of the appointment in Firebase Firestore.
     * Shows a toast message on success or failure.
     */
    private void updateStatus(String status) {
        // Show quick message
        Toast.makeText(getContext(), "Appointment " + status, Toast.LENGTH_SHORT).show();

        // Update the 'status' field in the database
        db.collection("Appointments").document(appointmentId)
                .update("status", status)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Appointment " + status, Toast.LENGTH_SHORT).show();
                    dismiss(); // Close the dialog
                    getActivity().finish(); // Close the current activity (optional)
                })
                .addOnFailureListener(e ->
                    Toast.makeText(getContext(), "Failed to update: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
