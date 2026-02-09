package com.example.gusteau.presentation.onboarding.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gusteau.R;
import com.example.gusteau.presentation.onboarding.OnBoardingContract;

import java.util.ArrayList;
import java.util.List;

public class OnBoardingAdapter extends RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder> {

    private List<OnBoardingContract.OnboardingPage> pages;

    public OnBoardingAdapter() {
        this.pages = new ArrayList<>();
    }

    @NonNull
    @Override
    public OnBoardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_onboarding_page, parent, false);
        return new OnBoardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnBoardingViewHolder holder, int position) {
        holder.bind(pages.get(position));
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    public void setPages(List<OnBoardingContract.OnboardingPage> pages) {
        this.pages = pages;
        notifyDataSetChanged();
    }

    static class OnBoardingViewHolder extends RecyclerView.ViewHolder {

        private final View rootView;
        private final ImageView ivOnboarding;
        private final TextView tvTitle;
        private final TextView tvDescription;

        OnBoardingViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            ivOnboarding = itemView.findViewById(R.id.iv_onboarding);
            tvTitle = itemView.findViewById(R.id.tv_onboarding_title);
            tvDescription = itemView.findViewById(R.id.tv_onboarding_description);
        }

        void bind(OnBoardingContract.OnboardingPage page) {
            rootView.setBackgroundColor(
                    ContextCompat.getColor(itemView.getContext(), page.getBackgroundColor())
            );

            ivOnboarding.setImageResource(page.getImageRes());

            tvTitle.setText(page.getTitle());

            tvDescription.setText(page.getDescription());
        }
    }
}