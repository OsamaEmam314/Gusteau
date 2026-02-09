package com.example.gusteau.presentation.home.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gusteau.R;
import com.example.gusteau.data.model.Category;
import com.example.gusteau.data.model.Country;
import com.example.gusteau.data.model.Ingredients;
import com.example.gusteau.data.model.Meal;
import com.example.gusteau.data.network.NetworkState;
import com.example.gusteau.presentation.dialog.GuestDialog;
import com.example.gusteau.presentation.dialog.NoInternetDialog;
import com.example.gusteau.presentation.home.HomeContract;
import com.example.gusteau.presentation.home.presenter.HomePresenter;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements HomeContract.View {

    private ShapeableImageView ivUserAvatar;
    private TextView tvGreeting;
    private TextView tvUserName;

    private MaterialCardView cardMealOfDay;
    private ShapeableImageView ivMealImage;
    private TextView tvMealName;
    private Chip chipCategory;
    private Chip chipCountry;
    private Chip chipIngredient;
    private FloatingActionButton fabFavorite;

    private RecyclerView rvCategories;
    private RecyclerView rvCuisines;
    private RecyclerView rvIngredients;

    private CategoryAdapter categoryAdapter, areaAdapter, ingredientAdapter;

    private ProgressBar progressBar;
    private HomePresenter presenter;
    private Meal currentMealOfDay;
    private NoInternetDialog noInternetDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerViews();
        setupClickListeners();

        presenter = new HomePresenter(this, requireContext());

        presenter.setGreeting();
        presenter.getUserName();

        if (!NetworkState.isNetworkAvailable(requireContext())) {
            showNoInternetDialog();
        } else {
            loadAllData();
        }
    }

    private void showNoInternetDialog() {
        if (noInternetDialog != null && noInternetDialog.isShowing()) {
            noInternetDialog.dismiss();
        }

        noInternetDialog = NoInternetDialog.show(
                requireContext(),
                () -> {
                    if (NetworkState.isNetworkAvailable(requireContext())) {
                        loadAllData();
                    } else {
                        showNoInternetDialog();
                    }
                }
        );
    }
    private void initViews(View view) {
        ivUserAvatar = view.findViewById(R.id.iv_user_avatar);
        tvGreeting = view.findViewById(R.id.tv_greeting);
        tvUserName = view.findViewById(R.id.tv_user_name);
        cardMealOfDay = view.findViewById(R.id.card_meal_of_day);
        ivMealImage = view.findViewById(R.id.iv_meal_image);
        tvMealName = view.findViewById(R.id.tv_meal_name);
        chipCategory = view.findViewById(R.id.chip_category);
        chipCountry = view.findViewById(R.id.chip_cusine);
        chipIngredient = view.findViewById(R.id.chip_ingredients);
        fabFavorite = view.findViewById(R.id.fab_favorite);
        rvCategories = view.findViewById(R.id.rv_categories);
        rvCuisines = view.findViewById(R.id.rv_cuisines);
        rvIngredients = view.findViewById(R.id.rv_ingredients);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupRecyclerViews() {
        categoryAdapter = new CategoryAdapter(category -> presenter.onCategoryClick(category));
        rvCategories.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);

        areaAdapter = new CategoryAdapter(country -> presenter.onCountryClick(country));
        rvCuisines.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        rvCuisines.setAdapter(areaAdapter);

        ingredientAdapter = new CategoryAdapter(ingredient -> presenter.onIngredientClick(ingredient));
        rvIngredients.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        rvIngredients.setAdapter(ingredientAdapter);
    }
    private Category mapCountryToCategory(Country country)
    {
        return new Category("",country.getName(), country.getFlag());
    }
    private Category mapIngredientTOCategory(Ingredients ingredient)
    {
        return new Category("",ingredient.getName(), ingredient.getImage());
    }

    private void setupClickListeners() {
        cardMealOfDay.setOnClickListener(v -> presenter.onMealOfDayClick());


        fabFavorite.setOnClickListener(v -> {
            if (currentMealOfDay != null) {
                presenter.onMealOfDayFavoriteClick(currentMealOfDay);
            }
        });
    }

    private void loadAllData() {
        presenter.loadMealOfDay();
        presenter.loadCategories();
        presenter.loadCountries();
        presenter.loadIngredients();
    }


    @Override
    public void setGreeting(String greeting) {
        tvGreeting.setText(greeting);
    }

    @Override
    public void setUserName(String userName) {
        tvUserName.setText(userName);
    }

    @Override
    public void showMealOfDay(Meal meal) {
        this.currentMealOfDay = meal;
        tvMealName.setText(meal.getName());
        chipCountry.setText(meal.getArea());
        chipCategory.setText(meal.getCategory());
        chipIngredient.setText(meal.getIngredient1());


        Glide.with(this)
                .load(meal.getImageUrl())
                .placeholder(R.drawable.ic_restaurant)
                .error(R.drawable.unloaded_image)
                .centerCrop()
                .into(ivMealImage);
        fabFavorite.setImageResource(meal.isFavorite() ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);

    }

    @Override
    public void updateMealOfDayFavoriteStatus(boolean isFavorite) {
        fabFavorite.setImageResource(isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
    }

    @Override
    public void showCategories(List<Category> categories) {
        categoryAdapter.setCategories(categories);
    }

    @Override
    public void showCountries(List<Country> countries) {
        List<Category> categories = new ArrayList<>();
        for (Country country : countries) {
            Category category = mapCountryToCategory(country);
            categories.add(category);
        }
        areaAdapter.setCategories(categories);
    }

    @Override
    public void showIngredients(List<Ingredients> ingredients) {
        List<Category> categories = new ArrayList<>();
        for (Ingredients ingredient : ingredients) {
            Category category = mapIngredientTOCategory(ingredient);
            categories.add(category);
        }
        ingredientAdapter.setCategories(categories);
    }

    @Override
    public void showLoading() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void navigateToMealDetails() {

        Navigation.findNavController(requireView()).navigate(R.id.action_navigation_home_to_mealDetailsFragment);
    }

    @Override
    public void navigateToFIlteredMeals() {
        Navigation.findNavController(requireView()).navigate(R.id.action_navigation_home_to_navigation_filtered_meals);
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
        presenter.onDestroy();
    }
}