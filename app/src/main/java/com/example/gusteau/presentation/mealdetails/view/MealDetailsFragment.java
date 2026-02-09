package com.example.gusteau.presentation.mealdetails.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gusteau.R;
import com.example.gusteau.data.model.Meal;
import com.example.gusteau.data.network.NetworkState;
import com.example.gusteau.presentation.dialog.GuestDialog;
import com.example.gusteau.presentation.dialog.NoInternetDialog;
import com.example.gusteau.presentation.mealdetails.MealDetailsContract;
import com.example.gusteau.presentation.mealdetails.presenter.MealDetailsPresenter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MealDetailsFragment extends Fragment implements MealDetailsContract.View {

    private MaterialToolbar toolbar;
    private ScrollView svContent;
    private ProgressBar progressBar;
    private ShapeableImageView ivMealImage;
    private TextView tvTitle;
    private Chip chipCountry;
    private Chip chipCategory;
    private RecyclerView rvIngredients;
    private YouTubePlayerView youtubePlayerView;
    private TextView tvVideoHeader;
    private RecyclerView rvSteps;
    private FloatingActionButton fabFavorite;
    private FloatingActionButton fabCalendar;

    private IngredientsAdapter ingredientsAdapter;
    private StepsAdapter stepsAdapter;

    private MealDetailsPresenter presenter;

    private String mealId;
    private List<MealDetailsContract.DayInfo> weekDays;
    private NoInternetDialog noInternetDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meal_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupToolbar();
        setupRecyclerViews();
        setupFABs();
        presenter = new MealDetailsPresenter(this, requireContext());
        mealId = presenter.getMealId();
        if (NetworkState.isNetworkAvailable(requireContext())) {
            presenter.loadMealDetails(mealId);
        } else {
            showNoInternetDialog();
        }
    }
    public void showNoInternetDialog() {
        if (noInternetDialog != null && noInternetDialog.isShowing()) {
            noInternetDialog.dismiss();
        }

        noInternetDialog = NoInternetDialog.show(
                requireContext(),
                () -> {
                    if (NetworkState.isNetworkAvailable(requireContext())) {
                        presenter.loadMealDetails(mealId);
                    } else {
                        showNoInternetDialog();
                    }
                }
        );
    }
    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        svContent = view.findViewById(R.id.svContent);
        progressBar = view.findViewById(R.id.progressBar);
        ivMealImage = view.findViewById(R.id.ivMealImage);
        tvTitle = view.findViewById(R.id.tvTitle);
        chipCountry = view.findViewById(R.id.chipCountry);
        chipCategory = view.findViewById(R.id.chipCategory);
        rvIngredients = view.findViewById(R.id.rvIngredients);
        youtubePlayerView = view.findViewById(R.id.youtube_player_view);
        tvVideoHeader = view.findViewById(R.id.tvVideoHeader);
        rvSteps = view.findViewById(R.id.rvSteps);
        fabFavorite = view.findViewById(R.id.fabFavorite);
        fabCalendar = view.findViewById(R.id.fabCalendar);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setupRecyclerViews() {
        ingredientsAdapter = new IngredientsAdapter();
        rvIngredients.setAdapter(ingredientsAdapter);

        stepsAdapter = new StepsAdapter();
        rvSteps.setAdapter(stepsAdapter);
        rvSteps.setNestedScrollingEnabled(false);
    }

    private void setupFABs() {
        fabFavorite.setOnClickListener(v -> presenter.onFavoriteClick());
        fabCalendar.setOnClickListener(v -> presenter.onCalendarClick());
    }

    @Override
    public void showMealDetails(Meal meal) {
        Glide.with(this)
                .load(meal.getImageUrl())
                .placeholder(R.drawable.unloaded_image)
                .error(R.drawable.unloaded_image)
                .into(ivMealImage);

        tvTitle.setText(meal.getName());
        if (meal.getArea() != null && !meal.getArea().isEmpty()) {
            chipCountry.setText(meal.getArea());
            chipCountry.setVisibility(View.VISIBLE);
        } else {
            chipCountry.setVisibility(View.GONE);
        }

        if (meal.getCategory() != null && !meal.getCategory().isEmpty()) {
            chipCategory.setText(meal.getCategory());
            chipCategory.setVisibility(View.VISIBLE);
        } else {
            chipCategory.setVisibility(View.GONE);
        }
        loadIngredients(meal);

        setupYouTubePlayer(meal.getYoutubeUrl());

        loadPreparationSteps(meal);
    }

    private void loadIngredients(Meal meal) {
        List<IngredientsAdapter.Ingredient> ingredientsList = new ArrayList<>();

        String[] ingredients = meal.getIngredientsArray();
        String[] measures = meal.getMeasuresArray();

        if (ingredients != null) {
            for (int i = 0; i < ingredients.length; i++) {
                if (ingredients[i] != null && !ingredients[i].trim().isEmpty()) {
                    String measure = (i < measures.length) ? measures[i] : "";
                    ingredientsList.add(new IngredientsAdapter.Ingredient(ingredients[i], measure));
                }
            }
        }

        ingredientsAdapter.setIngredients(ingredientsList);
    }

    private void setupYouTubePlayer(String videoUrl) {
        if (videoUrl == null || videoUrl.isEmpty()) {
            youtubePlayerView.setVisibility(View.GONE);
            tvVideoHeader.setVisibility(View.GONE);
            return;
        }

        String videoId = extractYouTubeId(videoUrl);
        if (videoId == null) {
            youtubePlayerView.setVisibility(View.GONE);
            tvVideoHeader.setVisibility(View.GONE);
            return;
        }

        youtubePlayerView.setVisibility(View.VISIBLE);
        tvVideoHeader.setVisibility(View.VISIBLE);
        getLifecycle().addObserver(youtubePlayerView);

        youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.cueVideo(videoId, 0);
            }
        });
    }

    private String extractYouTubeId(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

        Pattern compiledPattern = java.util.regex.Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }

    private void loadPreparationSteps(Meal meal) {
        List<String> stepsList = new ArrayList<>();
        String instructions = meal.getInstructions();
        if (instructions != null && !instructions.isEmpty()) {
            String[] steps = instructions.split("\\.(?=[A-Z])|\\r\\n|\\n");
            for (String step : steps) {
                step = step.trim();
                if (!step.isEmpty() && step.length() > 10) {
                    stepsList.add(step);
                }
            }
        }

        stepsAdapter.setSteps(stepsList);
    }

    @Override
    public void showLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
            svContent.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
            svContent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showError(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void updateFavoriteButton(boolean isFavorite) {

        int iconRes = isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border;
        fabFavorite.setImageResource(iconRes);

        if (isFavorite) {
            fabFavorite.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.primary));
        } else {
            fabFavorite.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.gray_200));
        }
    }

    @Override
    public void showWeekPlannerDialog(List<MealDetailsContract.DayInfo> days) {
        this.weekDays = days;

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_week_planner, null);

        ChipGroup chipGroupDays = dialogView.findViewById(R.id.chipGroupDays);
        ChipGroup chipGroupMealTypes = dialogView.findViewById(R.id.chipGroupMealTypes);

        for (MealDetailsContract.DayInfo dayInfo : days) {
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.item_day_chip, chipGroupDays, false);
            chip.setText(dayInfo.getDisplayName());
            chip.setTag(dayInfo.getDate());
            chipGroupDays.addView(chip);
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setCancelable(true);

        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btnAdd).setOnClickListener(v -> {
            int selectedDayId = chipGroupDays.getCheckedChipId();
            int selectedMealTypeId = chipGroupMealTypes.getCheckedChipId();

            if (selectedDayId == -1) {
                showError("Please select a day");
                return;
            }

            if (selectedMealTypeId == -1) {
                showError("Please select a meal type");
                return;
            }

            Chip selectedDayChip = dialogView.findViewById(selectedDayId);
            Chip selectedMealTypeChip = dialogView.findViewById(selectedMealTypeId);

            String dayDate = (String) selectedDayChip.getTag();
            String mealType = selectedMealTypeChip.getText().toString();

            presenter.onMealTypeSelected(dayDate, mealType);
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    public void showGuestModeMessage() {
        if (getView() != null) {
            GuestDialog guestDialog = new GuestDialog(requireContext(),getView());
            guestDialog.showGuestModeMessage();
        }
    }

    @Override
    public void showMealAddedToPlan(String day, String mealType) {
        if (getView() != null) {
            Snackbar.make(getView(),
                            "Added to " + day + " - " + mealType,
                            Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showMealAddedToFavorites() {
        if (getView() != null) {
            Snackbar.make(getView(),
                            "Added to Favorites",
                            Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void showMealRemovedFromFavorites() {
        if (getView() != null) {
            Snackbar.make(getView(),
                            "Removed from Favorites",
                            Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void showVideoNotAvailable() {
        if (youtubePlayerView != null) {
            youtubePlayerView.setVisibility(View.GONE);
        }
        if (tvVideoHeader != null) {
            tvVideoHeader.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (youtubePlayerView != null) {
            youtubePlayerView.release();
        }
        if (presenter != null) {
            presenter.onDestroy();
        }
    }
}