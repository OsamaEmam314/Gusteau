package com.example.gusteau.presentation.search.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gusteau.R;
import com.example.gusteau.data.model.Category;
import com.example.gusteau.data.model.Country;
import com.example.gusteau.data.model.Ingredients;
import com.example.gusteau.data.model.Meal;
import com.example.gusteau.data.network.NetworkState;
import com.example.gusteau.presentation.dialog.GuestDialog;
import com.example.gusteau.presentation.dialog.NoInternetDialog;
import com.example.gusteau.presentation.search.SearchContract;
import com.example.gusteau.presentation.search.presenter.SearchPresenter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class SearchFragment extends Fragment implements SearchContract.View {

    private SearchView searchView;
    private Chip chipCategories;
    private Chip chipCountry;
    private Chip chipIngredients;
    private TextView tvResultsHeader;
    private RecyclerView rvSearchResults;
    private ConstraintLayout llEmptyState;
    private ProgressBar progressBar;

    private SearchAdapter adapter;
    private SearchPresenter presenter;

    private BottomSheetDialog filterDialog;
    private NoInternetDialog noInternetDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupSearchView();
        setupChips();
        setupRecyclerView();

        presenter = new SearchPresenter(this, requireContext());
        presenter.loadInitialData();

    }
    @Override
    public void showNoInternetDialog() {
        if (noInternetDialog != null && noInternetDialog.isShowing()) {
            noInternetDialog.dismiss();
        }

        noInternetDialog = NoInternetDialog.show(
                requireContext(),
                () -> {
                    if (NetworkState.isNetworkAvailable(requireContext())) {
                        presenter.loadInitialData();
                    } else {
                        showNoInternetDialog();
                    }
                }
        );
    }

    private void initViews(View view) {
        searchView = view.findViewById(R.id.search_view);
        chipCategories = view.findViewById(R.id.chip_categories);
        chipCountry = view.findViewById(R.id.chip_country);
        chipIngredients = view.findViewById(R.id.chip_ingredients);
        tvResultsHeader = view.findViewById(R.id.tv_results_header);
        rvSearchResults = view.findViewById(R.id.rv_search_results);
        llEmptyState = view.findViewById(R.id.ll_empty_state);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.onSearchQuerySubmit(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                presenter.onSearchQueryChange(newText);
                return true;
            }
        });
    }
    private void setupChips() {
        chipCategories.setOnClickListener(v -> {
            if (chipCategories.isChecked()) {
                presenter.onCategoriesChipClick();
            } else{
                if(!areAnyChipsChecked()){
                    presenter.loadInitialData();
                }
            }
        });

        chipCountry.setOnClickListener(v -> {
            if (chipCountry.isChecked()) {
                presenter.onCountryChipClick();
            } else{
                if(!areAnyChipsChecked()){
                    presenter.loadInitialData();
                }
            }
        });

        chipIngredients.setOnClickListener(v -> {
            if (chipIngredients.isChecked()) {
                presenter.onIngredientsChipClick();
            } else{
                if(!areAnyChipsChecked()){
                    presenter.loadInitialData();
                }
            }
        });
    }
    @Override
    public void uncheckAllChips() {
        chipCategories.setChecked(false);
        chipCountry.setChecked(false);
        chipIngredients.setChecked(false);
    }

    @Override
    public boolean areAnyChipsChecked() {
        return chipCategories.isChecked() || chipCountry.isChecked() || chipIngredients.isChecked();
    }

    private void setupRecyclerView() {
        adapter = new SearchAdapter(
                meal -> presenter.onMealClick(meal),
                (meal, position) -> presenter.onFavoriteClick(meal, position)
        );

        rvSearchResults.setAdapter(adapter);
    }

    @Override
    public void showSearchResults(List<Meal> meals) {
        adapter.setMeals(meals);
        rvSearchResults.setVisibility(View.VISIBLE);
    }

    @Override
    public void showCategories(List<Category> categories) {
        showCategoriesBottomSheet(categories);
    }

    @Override
    public void showCountries(List<Country> countries) {
        showCountriesBottomSheet(countries);
    }

    @Override
    public void showIngredients(List<Ingredients> ingredients) {
        showIngredientsBottomSheet(ingredients);
    }

    private void showCategoriesBottomSheet(List<Category> categories) {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_filter, null);
        filterDialog = new BottomSheetDialog(requireContext());
        filterDialog.setContentView(bottomSheetView);

        TextView title = bottomSheetView.findViewById(R.id.tv_filter_title);
        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.rv_filter_items);

        title.setText("Select Category");

        FilterItemsAdapter filterAdapter = new FilterItemsAdapter(
                categories,
                category -> {
                    presenter.onCategorySelected(category);
                    filterDialog.dismiss();
                }
        );

        recyclerView.setAdapter(filterAdapter);
        filterDialog.show();
    }

    private void showCountriesBottomSheet(List<Country> countries) {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_filter, null);
        filterDialog = new BottomSheetDialog(requireContext());
        filterDialog.setContentView(bottomSheetView);

        TextView title = bottomSheetView.findViewById(R.id.tv_filter_title);
        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.rv_filter_items);

        title.setText("Select Country");

        FilterItemsAdapter filterAdapter = new FilterItemsAdapter(
                countries,
                country -> {
                    presenter.onCountrySelected(country);
                    filterDialog.dismiss();
                }
        );

        recyclerView.setAdapter(filterAdapter);
        filterDialog.show();
    }

    private void showIngredientsBottomSheet(List<Ingredients> ingredients) {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_filter, null);
        filterDialog = new BottomSheetDialog(requireContext());
        filterDialog.setContentView(bottomSheetView);

        TextView title = bottomSheetView.findViewById(R.id.tv_filter_title);
        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.rv_filter_items);

        title.setText("Select Ingredient");

        FilterItemsAdapter filterAdapter = new FilterItemsAdapter(
                ingredients,
                ingredient -> {
                    presenter.onIngredientSelected(ingredient);
                    filterDialog.dismiss();
                }
        );

        recyclerView.setAdapter(filterAdapter);
        filterDialog.show();
    }

    @Override
    public void showEmptyState() {
        if (llEmptyState != null) {
            llEmptyState.setVisibility(View.VISIBLE);
            rvSearchResults.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideEmptyState() {
        if (llEmptyState != null) {
            llEmptyState.setVisibility(View.GONE);
            rvSearchResults.setVisibility(View.VISIBLE);
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
    public void updateResultsHeader(String text) {
        if (tvResultsHeader != null) {
            tvResultsHeader.setText(text);
        }
    }

    @Override
    public void updateMealFavoriteStatus(int position, boolean isFavorite) {
        adapter.updateFavoriteStatus(position, isFavorite);
    }

    @Override
    public void navigateToMealDetails() {
        if (getView() != null) {
            Navigation.findNavController(getView())
                    .navigate(R.id.action_navigation_search_to_mealDetailsFragment);
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
    public void clearSearchQuery() {
        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.clearFocus();
        }
    }

    @Override
    public void hideFilters() {
        if (filterDialog != null && filterDialog.isShowing()) {
            filterDialog.dismiss();
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.onDestroy();
        }
        if (filterDialog != null && filterDialog.isShowing()) {
            filterDialog.dismiss();
        }
    }
}