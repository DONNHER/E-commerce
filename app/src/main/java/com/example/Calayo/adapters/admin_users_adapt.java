package com.example.Calayo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.Calayo.R;
import com.example.Calayo.entities.Order;
import com.example.Calayo.entities.user;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class admin_users_adapt  extends RecyclerView.Adapter<admin_users_adapt.PageViewHolder> {
    private List<user> items;
    FragmentActivity fragmentAct;
    private tempStorage temp = tempStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public admin_users_adapt(List<user> items, FragmentActivity fragmentActivity) {
        this.items = items;
        this.fragmentAct = fragmentActivity;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rider_order, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        user item = items.get(position);
        Glide.with(holder.pic.getContext())
                .load(item.getImage())
                .into(holder.pic);
        holder.role.setText(item.getRole());
        holder.name.setText(item.getName());
        holder.email.setText(item.getEmail());
        holder.address.setText(item.getAddress());
        holder.delete.setOnClickListener(v -> {
//            temp.getSelectedOrder().setStatus("Out of Delivery");
        });
        holder.edit.setOnClickListener(v -> {
//            temp.getSelectedOrder().setStatus("Out of Delivery");
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        Button delete;
        Button edit;
        ImageView pic;
        TextView role;
        TextView email;
        TextView name;
        TextView address;

        PageViewHolder(View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.image);
            role = itemView.findViewById(R.id.role);
            name = itemView.findViewById(R.id.restaurant2);
            email = itemView.findViewById(R.id.email);
            address = itemView.findViewById(R.id.address);
            delete = itemView.findViewById(R.id.iconDelete);
            delete = itemView.findViewById(R.id.edit);

        }
    }
}
