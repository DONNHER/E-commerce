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

    public class manage_order extends DialogFragment {
        private String date, time, name, services, appointmentId,status;
        private double totalCost;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.manage_order, container, false);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            if (getArguments() != null) {
                appointmentId = getArguments().getString("appointmentId");
                date = getArguments().getString("date");
                time = getArguments().getString("time");
                totalCost = getArguments().getDouble("totalCost");
                name = getArguments().getString("name");
                services = getArguments().getString("services");
                status = getArguments().getString("status");
            }

            TextView Date = view.findViewById(R.id.Date);
            TextView Time = view.findViewById(R.id.Time);
            TextView tCost = view.findViewById(R.id.cost);
            TextView service = view.findViewById(R.id.service);

            Date.setText(date);
            Time.setText(time);
            tCost.setText(String.format("%.2f", totalCost));
            service.setText(services);

            view.findViewById(R.id.confirm).setOnClickListener(v -> updateStatus("Confirmed"));
            view.findViewById(R.id.cancel).setOnClickListener(v -> updateStatus("Cancelled"));
        }

        private void updateStatus(String status) {
            Toast.makeText(getContext(), "Appointment " + status, Toast.LENGTH_SHORT).show();

            db.collection("Appointments").document(appointmentId)
                    .update("status", status)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(getContext(), "Appointment " + status, Toast.LENGTH_SHORT).show();
                        dismiss();
                        getActivity().finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Failed to update: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        }
    }
