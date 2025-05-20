package com.example.Calayo.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.Calayo.R;
import com.example.Calayo.entities.Item;
import com.example.Calayo.entities.address;
import com.example.Calayo.entities.cartItem;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class addToCartAdapt  extends RecyclerView.Adapter<addToCartAdapt.PageViewHolder> {
    private List<cartItem> items;
    FragmentActivity fragmentAct;
    private tempStorage temp = tempStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int selectedPosition = -1;
    public addToCartAdapt(List<cartItem> items, FragmentActivity fragmentActivity) {
        this.items = items;
        this.fragmentAct = fragmentActivity;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        cartItem item = items.get(position);

        Glide.with(holder.pic.getContext())
                .load(item.getImage())
                .into(holder.pic);

        holder.units.setText(item.getQuantity());
        holder.name.setText(item.getName());
        holder.price.setText(item.getPrice());

        holder.addOns.setLayoutManager(new LinearLayoutManager(fragmentAct, LinearLayoutManager.VERTICAL, false));
        holder.aDaptor = new addOns(item.getAddOns(), fragmentAct);
        holder.addOns.setAdapter(holder.aDaptor);

//        holder.minus.setOnClickListener(v -> {
//            int unit = Integer.parseInt(holder.units.getText().toString().trim());
//            if (unit > 1) {
//                unit--;
//                holder.units.setText("   " + unit + "   ");
//            }
//        });
//
//        holder.add.setOnClickListener(v -> {
//            int unit = Integer.parseInt(holder.units.getText().toString().trim());
//            unit++;
//            holder.units.setText("   " + unit + "   ");
//        });

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(position == selectedPosition);

        // Update state when checkbox is toggled
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                int oldPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();

                for (int i = 0; i < items.size(); i++) {
                    items.get(i).setSelected(i == selectedPosition);
                }

                notifyItemChanged(oldPosition);
                notifyItemChanged(selectedPosition);

                temp.setSelectedCartItem(items.get(selectedPosition));
            } else {
                if (position == selectedPosition) {
                    items.get(position).setSelected(false);
                    selectedPosition = -1;
                    notifyItemChanged(position);
                    temp.setSelectedCartItem(null);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView units;
        TextView name;
        CheckBox checkBox;
        ImageView minus;
        ImageView add;
        TextView price;
        RecyclerView addOns;
        addOns aDaptor;
        PageViewHolder(View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            units = itemView.findViewById(R.id.units);
            checkBox = itemView.findViewById(R.id.checkbox);
//            minus = itemView.findViewById(R.id.minus);
//            add = itemView.findViewById(R.id.add);
            price = itemView.findViewById(R.id.priceOrder);
            addOns = itemView.findViewById(R.id.OrderDetails);
        }
    }
}
