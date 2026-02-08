package com.example.gusteau.presentation.search.view;

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

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private List<Meal> meals;
    private OnMealClickListener clickListener;
    private OnFavoriteClickListener favoriteListener;

    public interface OnMealClickListener {
        void onMealClick(Meal meal);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Meal meal, int position);
    }

    public SearchAdapter(OnMealClickListener clickListener, OnFavoriteClickListener favoriteListener) {
        this.meals = new ArrayList<>();
        this.clickListener = clickListener;
        this.favoriteListener = favoriteListener;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal_grid, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        holder.bind(meals.get(position), position);
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    public void updateFavoriteStatus(int position, boolean isFavorite) {
        if (position >= 0 && position < meals.size()) {
            meals.get(position).setFavorite(isFavorite);
            notifyItemChanged(position);
        }
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {

        private final ShapeableImageView ivMeal;
        private final ImageButton btnFavorite;
        private final TextView tvMealName;

        SearchViewHolder(View itemView) {
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

            updateFavoriteIcon(meal.isFavorite());

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

        private void updateFavoriteIcon(boolean isFavorite) {
            int iconRes = isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border;
            btnFavorite.setImageResource(iconRes);
        }
    }
}