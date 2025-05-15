package com.example.Calayo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.Calayo.R;
import com.example.Calayo.entities.Item;
import com.example.Calayo.entities.Order;
import com.example.Calayo.entities.cartItem;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class check_list_adapter extends RecyclerView.Adapter<check_list_adapter.PageViewHolder> {
    private List<cartItem> items;
    FragmentActivity fragmentAct;
    private tempStorage temp = tempStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public check_list_adapter(List<cartItem> items, FragmentActivity fragmentActivity) {
        this.items = items;
        this.fragmentAct = fragmentActivity;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_checklist, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        cartItem item = items.get(position);
        holder.price.setText(String.valueOf(item.getPrice()));
        holder.units.setText(item.getQuantity()+"x");
        holder.name.setText(item.getName());
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(fragmentAct));
        addOns addOn = new addOns(temp.searchItem(item.getName()).getAddOns(),fragmentAct);
        holder.recyclerView.setAdapter(addOn);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        TextView price;
        TextView units;
        TextView name;
        RecyclerView recyclerView;
        PageViewHolder(View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.Price3);
            name = itemView.findViewById(R.id.name2);
            units = itemView.findViewById(R.id.name3);
            recyclerView = itemView.findViewById(R.id.OrderAddons);
        }
    }
}
