package com.example.Calayo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Calayo.R;
import com.example.Calayo.entities.Item;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.Calayo.helper.tempStorage;

import java.util.List;

public class addOns extends RecyclerView.Adapter<addOns.PageViewHolder> {
    private List<Item.addOn> items;
    FragmentActivity fragmentAct;
    private tempStorage temp = tempStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public addOns(List<Item.addOn> items, FragmentActivity fragmentActivity) {
        this.items = items;
        this.fragmentAct = fragmentActivity;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.addons, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        Item.addOn item = items.get(position);
        holder.name.setText(item.getAddOnName());
        holder.price.setText(String.valueOf(item.getAddOnPrice()));

        holder.checkBox.setOnCheckedChangeListener(null); // Prevent recycling issues

        holder.checkBox.setChecked(item.isChecked()); // Set checkbox to current state
        if(fragmentAct.getClass().getSimpleName().equals("checkout")){
            holder.checkBox.setVisibility(View.GONE);
        }else if(fragmentAct.getClass().getSimpleName().equals("AddToCart")){
            holder.checkBox.setVisibility(View.GONE);
        }
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setChecked(isChecked); // update state
            if (isChecked) {
                temp.getAddOnArrayList().add(item);
             } else {
                temp.getAddOnArrayList().remove(item);
            }
        });

    }

    @Override
    public int getItemCount() {
        return (items != null) ? items.size() : 0;
    }


    static class PageViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView name;
        TextView price;
        PageViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            price = itemView.findViewById(R.id.price);
            name = itemView.findViewById(R.id.name);
        }
    }
}
