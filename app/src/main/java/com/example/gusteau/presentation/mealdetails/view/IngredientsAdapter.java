package com.example.gusteau.presentation.mealdetails.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gusteau.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

    private List<Ingredient> ingredients;

    public static class Ingredient {
        private String name;
        private String measure;

        public Ingredient(String name, String measure) {
            this.name = name;
            this.measure = measure;
        }

        public String getName() {
            return name;
        }

        public String getMeasure() {
            return measure;
        }
    }

    public IngredientsAdapter() {
        this.ingredients = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(ingredients.get(position));
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ShapeableImageView ivIngredient;
        private final TextView tvIngredient;
        private final TextView tvMeasure;

        ViewHolder(View itemView) {
            super(itemView);
            ivIngredient = itemView.findViewById(R.id.iv_ingredient);
            tvIngredient = itemView.findViewById(R.id.tv_ingredient);
            tvMeasure = itemView.findViewById(R.id.tv_measure);
        }

        void bind(Ingredient ingredient) {
            tvIngredient.setText(ingredient.getName());

            if (ingredient.getMeasure() != null && !ingredient.getMeasure().trim().isEmpty()) {
                tvMeasure.setText(ingredient.getMeasure());
                tvMeasure.setVisibility(View.VISIBLE);
            } else {
                tvMeasure.setVisibility(View.GONE);
            }

            String imageUrl = "https://www.themealdb.com/images/ingredients/"
                    + ingredient.getName() + "-Small.png";

            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.unloaded_image)
                    .error(R.drawable.unloaded_image)
                    .centerInside()
                    .into(ivIngredient);
        }
    }
}