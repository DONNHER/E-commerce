package com.example.Calayo.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Calayo.R;
import com.example.Calayo.acts.order_info;
import com.example.Calayo.entities.Order;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class rider_order_delivered  extends RecyclerView.Adapter<rider_order_delivered.PageViewHolder> {
    private List<Order> items;
    FragmentActivity fragmentAct;
    private tempStorage temp = tempStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public rider_order_delivered(List<Order> items, FragmentActivity fragmentActivity) {
        this.items = items;
        this.fragmentAct = fragmentActivity;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rider_order_delivered, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        Order item = items.get(position);
        holder.cancel.setOnClickListener(v -> {
            temp.setSelectedOrder(item);
            temp.getSelectedOrder().setStatus("Canceled");
        });
        holder.deliver.setOnClickListener(v -> {
            temp.setSelectedOrder(item);
            temp.getSelectedOrder().setStatus("Received");
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        Button deliver;
        Button cancel;
        PageViewHolder(View itemView) {
            super(itemView);
            deliver = itemView.findViewById(R.id.iconDelivery);
            cancel = itemView.findViewById(R.id.iconReturn);
        }
    }
}
