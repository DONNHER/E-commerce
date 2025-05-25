package com.example.Calayo.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Calayo.R;
import com.example.Calayo.acts.productsAct;
import com.example.Calayo.entities.adds;
import com.example.Calayo.acts.userLoginAct;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class AddsADaptor extends RecyclerView.Adapter<AddsADaptor.PageViewHolder> {
    private List<adds> items;
    FragmentActivity fragmentAct;
    private final FirebaseAuth myAuth= FirebaseAuth.getInstance();
    public AddsADaptor(List<adds> items, FragmentActivity fragmentActivity) {
        this.items = items;
        this.fragmentAct = fragmentActivity;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adds, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        adds item = items.get(position);
        holder.des.setText(item.getDescription());

        holder.button.setOnClickListener(view -> {
            if(myAuth.getCurrentUser() == null) {
                Intent order = new Intent(fragmentAct, userLoginAct.class);
                fragmentAct.startActivity(order);
            }else {
                Intent order = new Intent(fragmentAct, productsAct.class);
                fragmentAct.startActivity(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        Button button;
        TextView des;
        ImageView pic;
        PageViewHolder(View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.image_order);
            des = itemView.findViewById(R.id.Start5);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}
