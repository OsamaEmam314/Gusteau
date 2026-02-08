package com.example.gusteau.presentation.favorites.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gusteau.R;
import com.example.gusteau.data.model.Meal;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {

    private List<Meal> favorites;
    private OnMealClickListener clickListener;
    private OnFavoriteClickListener favoriteListener;

    public interface OnMealClickListener {
        void onMealClick(Meal meal);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Meal meal, int position);
    }

    public FavoritesAdapter(OnMealClickListener clickListener, OnFavoriteClickListener favoriteListener) {
        this.favorites = new ArrayList<>();
        this.clickListener = clickListener;
        this.favoriteListener = favoriteListener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal_grid, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        holder.bind(favorites.get(position), position);
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    public void setFavorites(List<Meal> favorites) {
        this.favorites = favorites;
        notifyDataSetChanged();
    }

    public void removeFavorite(int position) {
        if (position >= 0 && position < favorites.size()) {
            favorites.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, favorites.size());
        }
    }

    public void updateFavoriteStatus(int position, boolean isFavorite) {
        if (position >= 0 && position < favorites.size()) {
            favorites.get(position).setFavorite(isFavorite);
            notifyItemChanged(position);
        }
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder {

        private final ShapeableImageView ivMeal;
        private final ImageButton btnFavorite;
        private final TextView tvMealName;

        FavoriteViewHolder(View itemView) {
            super(itemView);
            ivMeal = itemView.findViewById(R.id.iv_meal);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
            tvMealName = itemView.findViewById(R.id.tv_meal_name);
        }

        void bind(Meal meal, int position) {
            tvMealName.setText(meal.getName());
            Glide.with(itemView.getContext())
                    .load(meal.getImageUrl())
                    .placeholder(R.drawable.unloaded_image)
                    .error(R.drawable.unloaded_image)
                    .centerCrop()
                    .into(ivMeal);
            btnFavorite.setImageResource(R.drawable.ic_favorite);
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onMealClick(meal);
                }
            });

            btnFavorite.setOnClickListener(v -> {
                if (favoriteListener != null) {
                    favoriteListener.onFavoriteClick(meal, position);
                }
            });
        }
    }
}