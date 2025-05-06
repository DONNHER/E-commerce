package com.example.Calayo.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.Calayo.R;
import com.example.Calayo.entities.Item;
import com.example.Calayo.fragments.product_management;

import java.util.ArrayList;

public class product_adapt  extends   RecyclerView.Adapter<product_adapt.ViewHolder> {
    private ArrayList<Item> items;
    private FragmentActivity fragmentActivity;
    public product_adapt(ArrayList<Item> items, FragmentActivity fragmentActivity){
        this.items = items;
        this.fragmentActivity = fragmentActivity;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate((R.layout.item),parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.price.setText(""+ item.getPrice());
        holder.name.setText(item.getName());
        Glide.with(holder.pic.getContext())
                .load(item.getImage())
                .into(holder.pic);
        holder.itemView.setOnClickListener(view -> {
            product_management dialogFragment = new product_management();
            dialogFragment.show(fragmentActivity.getSupportFragmentManager(), "AppointmentDialog");
        });
        holder.favorite.setOnClickListener(v -> Toast.makeText(fragmentActivity,"Added Successfuly",Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView price;
        public ImageView pic;
        public TextView name;
        public ImageView favorite;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            price = itemView.findViewById(R.id.price);
            name = itemView.findViewById(R.id.name);
            favorite = itemView.findViewById(R.id.favorite);
        }
    }
}

