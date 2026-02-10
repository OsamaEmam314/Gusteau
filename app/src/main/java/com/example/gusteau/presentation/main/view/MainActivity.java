package com.example.gusteau.presentation.main.view;

import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.gusteau.R;
import com.example.gusteau.presentation.dialog.GuestDialog;
import com.example.gusteau.presentation.main.MainContract;
import com.example.gusteau.presentation.main.presenter.MainPresenter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private MainPresenter presenter;
    private boolean isGuestUser = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideSystemUI();
        setupNavigation();
        presenter = new MainPresenter(this, getApplicationContext());
        presenter.checkUserStatus();

    }

    @Override
    public void showGuestModeMessage() {
        GuestDialog guestDialog = new GuestDialog(this, findViewById(android.R.id.content));
        guestDialog.showGuestModeMessage();
    }

    @Override
    public void showError(String message) {
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void setGuestStatus(boolean isGuest) {
        this.isGuestUser = isGuest;
    }
    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_settings && isGuestUser) {
                showGuestModeMessage();
                return false;
            }

            return NavigationUI.onNavDestinationSelected(item, navController);
        });

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.mealDetailsFragment || destination.getId() == R.id.navigation_filtered_meals) {
                bottomNavigationView.setVisibility(View.GONE);
            } else {
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });
    }
    private void hideSystemUI() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            final WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }



    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}