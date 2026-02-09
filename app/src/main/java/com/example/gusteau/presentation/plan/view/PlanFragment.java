package com.example.gusteau.presentation.plan.view;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gusteau.R;
import com.example.gusteau.data.model.PlannedMeal;
import com.example.gusteau.presentation.dialog.GuestDialog;
import com.example.gusteau.presentation.plan.PlanContract;
import com.example.gusteau.presentation.plan.presenter.PlanPresenter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class PlanFragment extends Fragment implements PlanContract.View {

    private LinearLayout llCalendarDays;
    private RecyclerView rvBreakfast;
    private RecyclerView rvLunch;
    private RecyclerView rvDinner;
    private RecyclerView rvSnack;
    private MaterialButton btnAddBreakfast;
    private MaterialButton btnAddLunch;
    private MaterialButton btnAddDinner;
    private MaterialButton btnAddSnack;
    private ProgressBar progressBar;

    private PlannedMealAdapter breakfastAdapter;
    private PlannedMealAdapter lunchAdapter;
    private PlannedMealAdapter dinnerAdapter;
    private  PlannedMealAdapter snackAdapter;


    private PlanPresenter presenter;

    private CardView[] dayCards;
    private TextView[] dayTextViews;
    private int selectedCardIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerViews();
        setupButtons();

        presenter = new PlanPresenter(this, requireContext());
        presenter.loadWeekDays();
        presenter.loadMealsForSelectedDay();
    }

    private void initViews(View view) {
        llCalendarDays = view.findViewById(R.id.ll_calendar_days);
        rvBreakfast = view.findViewById(R.id.rv_breakfast);
        rvLunch = view.findViewById(R.id.rv_lunch);
        rvDinner = view.findViewById(R.id.rv_dinner);
        rvSnack = view.findViewById(R.id.rv_snacks);
        btnAddBreakfast = view.findViewById(R.id.btn_add_breakfast);
        btnAddLunch = view.findViewById(R.id.btn_add_lunch);
        btnAddDinner = view.findViewById(R.id.btn_add_dinner);
        btnAddSnack = view.findViewById(R.id.btn_add_snacks);
        progressBar = view.findViewById(R.id.progress_bar);

        dayCards = new CardView[7];
        dayTextViews = new TextView[7];

        dayCards[0] = (CardView) llCalendarDays.getChildAt(0);
        dayCards[1] = (CardView) llCalendarDays.getChildAt(1);
        dayCards[2] = (CardView) llCalendarDays.getChildAt(2);
        dayCards[3] = (CardView) llCalendarDays.getChildAt(3);
        dayCards[4] = (CardView) llCalendarDays.getChildAt(4);
        dayCards[5] = (CardView) llCalendarDays.getChildAt(5);
        dayCards[6] = (CardView) llCalendarDays.getChildAt(6);

        for (int i = 0; i < 7; i++) {
            LinearLayout dayLayout = (LinearLayout) dayCards[i].getChildAt(0);
            dayTextViews[i] = (TextView) dayLayout.getChildAt(1);
        }
    }

    private void setupRecyclerViews() {
        breakfastAdapter = new PlannedMealAdapter(
                meal -> presenter.onMealClick(meal),
                meal -> presenter.onDeleteMealClick(meal)
        );
        rvBreakfast.setAdapter(breakfastAdapter);
        rvBreakfast.setNestedScrollingEnabled(false);

        lunchAdapter = new PlannedMealAdapter(
                meal -> presenter.onMealClick(meal),
                meal -> presenter.onDeleteMealClick(meal)
        );
        rvLunch.setAdapter(lunchAdapter);
        rvLunch.setNestedScrollingEnabled(false);

        dinnerAdapter = new PlannedMealAdapter(
                meal -> presenter.onMealClick(meal),
                meal -> presenter.onDeleteMealClick(meal)
        );
        rvDinner.setAdapter(dinnerAdapter);
        rvDinner.setNestedScrollingEnabled(false);

        snackAdapter = new PlannedMealAdapter(
                meal -> presenter.onMealClick(meal),
                meal -> presenter.onDeleteMealClick(meal)
        );
        rvSnack.setAdapter(snackAdapter);
        rvSnack.setNestedScrollingEnabled(false);
    }

    private void setupButtons() {
        btnAddBreakfast.setOnClickListener(v -> presenter.onAddMeal());
        btnAddLunch.setOnClickListener(v -> presenter.onAddMeal());
        btnAddDinner.setOnClickListener(v -> presenter.onAddMeal());
        btnAddSnack.setOnClickListener(v -> presenter.onAddMeal());
    }

    @Override
    public void updateWeekDays(String[] dayNumbers, int selectedDayIndex) {
        for (int i = 0; i < Math.min(dayNumbers.length, dayTextViews.length); i++) {
            dayTextViews[i].setText(dayNumbers[i]);
        }

        for (int i = 0; i < dayCards.length; i++) {
            final int dayIndex = i;
            dayCards[i].setOnClickListener(v -> {
                updateSelectedDay(dayIndex);
                presenter.onDaySelected(dayIndex);
            });
        }
        updateSelectedDay(selectedDayIndex);
    }

    private void updateSelectedDay(int dayIndex) {
        for (int i = 0; i < dayCards.length; i++) {
            if (i == dayIndex) {
                dayCards[i].setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary));
                dayTextViews[i].setTextColor(Color.WHITE);
                LinearLayout layout = (LinearLayout) dayCards[i].getChildAt(0);
                TextView dayName = (TextView) layout.getChildAt(0);
                dayName.setTextColor(Color.WHITE);
            } else {
                dayCards[i].setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.surface_light));
                dayTextViews[i].setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary_light));

                LinearLayout layout = (LinearLayout) dayCards[i].getChildAt(0);
                TextView dayName = (TextView) layout.getChildAt(0);
                dayName.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary_light));
            }
        }

        selectedCardIndex = dayIndex;
    }

    @Override
    public void showBreakfastMeals(List<PlannedMeal> meals) {
        breakfastAdapter.setMeals(meals);

        if (meals.isEmpty()) {
            rvBreakfast.setVisibility(View.GONE);
        } else {
            rvBreakfast.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showLunchMeals(List<PlannedMeal> meals) {
        lunchAdapter.setMeals(meals);

        if (meals.isEmpty()) {
            rvLunch.setVisibility(View.GONE);
        } else {
            rvLunch.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showDinnerMeals(List<PlannedMeal> meals) {
        dinnerAdapter.setMeals(meals);

        if (meals.isEmpty()) {
            rvDinner.setVisibility(View.GONE);
        } else {
            rvDinner.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void showSnackMeals(List<PlannedMeal> meals) {
        snackAdapter.setMeals(meals);

        if (meals.isEmpty()) {
            rvSnack.setVisibility(View.GONE);
        } else {
            rvSnack.setVisibility(View.VISIBLE);
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
    public void showMealDeleted(String mealType) {
        if (getView() != null) {
            Snackbar.make(getView(),
                            mealType + " removed from plan",
                            Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void navigateToMealDetails() {

        if (getView() != null) {
            Navigation.findNavController(getView())
                    .navigate(R.id.action_navigation_plan_to_mealDetailsFragment);
        }
    }

    @Override
    public void navigateToSearchForMeal() {
        if (getView() != null) {
            NavOptions navOptions = new NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setPopUpTo(R.id.navigation_home, false)
                    .build();

            Navigation.findNavController(getView())
                    .navigate(R.id.navigation_search, null, navOptions);
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