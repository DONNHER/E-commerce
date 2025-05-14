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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.Calayo.R;
import com.example.Calayo.entities.address;
import com.example.Calayo.entities.cartItem;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class addToCartAdapt  extends RecyclerView.Adapter<addToCartAdapt.PageViewHolder> {
    private List<cartItem> items;
    FragmentActivity fragmentAct;
    private tempStorage temp = tempStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        holder.minus.setOnClickListener(v -> {
            int unit = Integer.parseInt(holder.units.getText().toString().trim());
            if (unit > 1) {
                unit--;
                holder.units.setText("   " + unit + "   ");
            }
        });

        holder.add.setOnClickListener(v -> {
            int unit = Integer.parseInt(holder.units.getText().toString().trim());
            unit++;
            holder.units.setText("   " + unit + "   ");
        });

        // Proper checkbox state binding
        holder.checkBox.setOnCheckedChangeListener(null); // Prevent triggering listener during recycling
        holder.checkBox.setChecked(item.isSelected());

        // Update state when checkbox is toggled
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);
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
        PageViewHolder(View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            units = itemView.findViewById(R.id.units);
            checkBox = itemView.findViewById(R.id.checkbox);
            minus = itemView.findViewById(R.id.minus);
            add = itemView.findViewById(R.id.add);
        }
    }
}
