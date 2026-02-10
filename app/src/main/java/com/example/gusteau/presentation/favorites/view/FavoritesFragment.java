package com.example.gusteau.presentation.favorites.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gusteau.R;
import com.example.gusteau.data.model.Meal;
import com.example.gusteau.presentation.dialog.GuestDialog;
import com.example.gusteau.presentation.favorites.FavoritesContract;
import com.example.gusteau.presentation.favorites.presenter.FavoritesPresenter;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class FavoritesFragment extends Fragment implements FavoritesContract.View {

    private RecyclerView rvFavs;
    private ConstraintLayout favEmptyState;

    private FavoritesAdapter adapter;
    private FavoritesPresenter presenter;
    private ProgressBar loading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        presenter = new FavoritesPresenter(this, requireContext());
        presenter.loadFavorites();
    }

    private void initViews(View view) {
        rvFavs = view.findViewById(R.id.rv_favs);
        favEmptyState = view.findViewById(R.id.fav_empty_state);
        loading = view.findViewById(R.id.favProgressBar);
    }

    private void setupRecyclerView() {
        adapter = new FavoritesAdapter(
                meal -> presenter.onMealClick(meal),
                (meal, position) -> presenter.onFavoriteClick(meal, position)
        );
        rvFavs.setAdapter(adapter);
    }

    @Override
    public void showFavorites(List<Meal> favorites) {
        adapter.setFavorites(favorites);
        rvFavs.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmptyState() {
        if (favEmptyState != null) {
            favEmptyState.setVisibility(View.VISIBLE);
            rvFavs.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideEmptyState() {
        if (favEmptyState != null) {
            favEmptyState.setVisibility(View.GONE);
            rvFavs.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        loading.setVisibility(View.GONE);
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
    public void removeMealFromList(int position) {
        adapter.removeFavorite(position);

        if (adapter.getItemCount() == 0) {
            showEmptyState();
        }
    }

    @Override
    public void navigateToMealDetails() {
        if (getView() != null) {
            Navigation.findNavController(getView())
                    .navigate(R.id.action_navigation_favorites_to_mealDetailsFragment);
        }
    }

    @Override
    public void showGuestModeMessage() {
        if (getView() != null) {
            GuestDialog guestDialog = new GuestDialog(requireContext(), getView());
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