package com.example.gusteau.presentation.plan.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gusteau.R;
import com.example.gusteau.data.model.PlannedMeal;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class PlannedMealAdapter extends RecyclerView.Adapter<PlannedMealAdapter.ViewHolder> {

    private List<PlannedMeal> meals;
    private OnMealClickListener clickListener;
    private OnDeleteClickListener deleteListener;

    public interface OnMealClickListener {
        void onMealClick(PlannedMeal meal);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(PlannedMeal meal);
    }

    public PlannedMealAdapter(OnMealClickListener clickListener, OnDeleteClickListener deleteListener) {
        this.meals = new ArrayList<>();
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_planned_meal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(meals.get(position));
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public void setMeals(List<PlannedMeal> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvMealName;
        private final TextView tvMealDetails;
        private final ShapeableImageView ivMeal;
        private final ImageView ivDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvMealName = itemView.findViewById(R.id.tv_meal_name);
            tvMealDetails = itemView.findViewById(R.id.tv_meal_details);
            ivMeal = itemView.findViewById(R.id.iv_meal);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }

        void bind(PlannedMeal meal) {
            tvMealName.setText(meal.getMealName());
            String details = "";
            if (meal.getMealCategory() != null && !meal.getMealCategory().isEmpty()) {
                details = meal.getMealCategory();
            }
            if (meal.getMealArea() != null && !meal.getMealArea().isEmpty()) {
                if (!details.isEmpty()) {
                    details += " • ";
                }
                details += meal.getMealArea();
            }
            tvMealDetails.setText(details);

            Glide.with(itemView.getContext())
                    .load(meal.getMealImage())
                    .placeholder(R.drawable.unloaded_image)
                    .error(R.drawable.unloaded_image)
                    .centerCrop()
                    .into(ivMeal);

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onMealClick(meal);
                }
            });

            ivDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteClick(meal);
                }
            });
        }
    }
}