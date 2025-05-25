package com.example.Calayo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Calayo.R;
import com.example.Calayo.entities.Order;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class seller_otw_order_adapt extends RecyclerView.Adapter<seller_otw_order_adapt.PageViewHolder> {
    private List<Order> items;
    FragmentActivity fragmentAct;
    private tempStorage temp = tempStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public seller_otw_order_adapt(List<Order> items, FragmentActivity fragmentActivity) {
        this.items = items;
        this.fragmentAct = fragmentActivity;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_p_orders, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        Order item = items.get(position);

        holder.approve.setOnClickListener(v -> {
            temp.setSelectedOrder(item);
            temp.getSelectedOrder().setStatus("Received");
        });
        holder.cancel.setOnClickListener(v -> {
            temp.setSelectedOrder(item);
            temp.getSelectedOrder().setStatus("Cancelled");
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        Button approve;
        Button cancel;
        PageViewHolder(View itemView) {
            super(itemView);
            approve = itemView.findViewById(R.id.iconApproved);
            cancel = itemView.findViewById(R.id.iconReturn);
        }
    }
}
