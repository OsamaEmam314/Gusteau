package com.example.gusteau.presentation.search.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gusteau.R;
import com.example.gusteau.data.model.Category;
import com.example.gusteau.data.model.Country;
import com.example.gusteau.data.model.Ingredients;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class FilterItemsAdapter extends RecyclerView.Adapter<FilterItemsAdapter.FilterViewHolder> {

    private List<?> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String name);
    }

    public FilterItemsAdapter(List<?> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filter, parent, false);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {
        Object item = items.get(position);

        if (item instanceof Category) {
            Category category = (Category) item;
            holder.bind(category.getName(), category.getImage());
        } else if (item instanceof Country) {
            Country country = (Country) item;
            holder.bind(country.getName(), country.getFlag());
        } else if (item instanceof Ingredients) {
            Ingredients ingredient = (Ingredients) item;
            holder.bind(ingredient.getName(), ingredient.getImage());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class FilterViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView cardView;
        private final ShapeableImageView ivImage;
        private final TextView tvName;

        FilterViewHolder(View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            ivImage = itemView.findViewById(R.id.iv_filter_image);
            tvName = itemView.findViewById(R.id.tv_filter_name);
        }

        void bind(String name, String imageUrl) {
            tvName.setText(name);
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.unloaded_image)
                        .error(R.drawable.unloaded_image)
                        .centerCrop()
                        .into(ivImage);
            }
            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(name);
                }
            });
        }
    }
}