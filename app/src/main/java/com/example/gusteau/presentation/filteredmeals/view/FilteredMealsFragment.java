package com.example.gusteau.presentation.filteredmeals.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gusteau.R;
import com.example.gusteau.data.model.Meal;
import com.example.gusteau.presentation.GuestDialog;
import com.example.gusteau.presentation.filteredmeals.FilteredMealsContract;
import com.example.gusteau.presentation.filteredmeals.presenter.FilteredMealsPresenter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class FilteredMealsFragment extends Fragment implements FilteredMealsContract.View {

    private MaterialToolbar toolbar;
    private TextView tvResultsCount;
    private RecyclerView rvMeals;
    private LinearLayout llEmptyState;
    private ProgressBar progressBar;

    private MealGridAdapter adapter;
    private FilteredMealsPresenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filtered_meals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupToolbar();
        setupRecyclerView();
        presenter = new FilteredMealsPresenter(this, requireContext());
        presenter.loadFilteredMeals();
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        tvResultsCount = view.findViewById(R.id.tv_results_count);
        rvMeals = view.findViewById(R.id.rv_meals);
        llEmptyState = view.findViewById(R.id.ll_empty_state);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new MealGridAdapter(
                meal -> presenter.onMealClick(meal),
                (meal, position) -> presenter.onFavoriteClick(meal, position)
        );

        rvMeals.setAdapter(adapter);
    }

    @Override
    public void showMeals(List<Meal> meals) {
        adapter.setMeals(meals);
        rvMeals.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmptyState() {
        if (llEmptyState != null) {
            llEmptyState.setVisibility(View.VISIBLE);
            rvMeals.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideEmptyState() {
        if (llEmptyState != null) {
            llEmptyState.setVisibility(View.GONE);
            rvMeals.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void showError(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    public void updateMealFavoriteStatus(int position, boolean isFavorite) {
        adapter.updateFavoriteStatus(position, isFavorite);
    }

    @Override
    public void setToolbarTitle(String title) {
        if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }

    @Override
    public void setResultsCount(int count) {
        if (tvResultsCount != null) {
            String text = count + (count == 1 ? " meal found" : " meals found");
            tvResultsCount.setText(text);
        }
    }

    @Override
    public void navigateToMealDetails() {

        if (getView() != null) {
            Navigation.findNavController(getView())
                    .navigate(R.id.action_navigation_filtered_meals_to_mealDetailsFragment);
        }
    }

    @Override
    public void showGuestModeMessage() {
        if (getView() != null) {
            GuestDialog guestDialog = new GuestDialog(requireContext(),getView());
            guestDialog.showGuestModeMessage();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }
}