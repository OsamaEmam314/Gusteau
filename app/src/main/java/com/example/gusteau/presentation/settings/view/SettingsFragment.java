package com.example.gusteau.presentation.settings.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gusteau.R;
import com.example.gusteau.WelcomeActivity;
import com.example.gusteau.presentation.settings.SettingsContract;
import com.example.gusteau.presentation.settings.presenter.SettingsPresenter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;


public class SettingsFragment extends Fragment implements SettingsContract.View {
    private MaterialButton logoutButton;
    private LinearLayout backingUp;
    private LinearLayout about;
    private TextView tvName;
    private TextView tvEmail;
    private ProgressBar loading;
    private SettingsPresenter presenter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupID();
        presenter = new SettingsPresenter(this, requireContext());
        presenter.loadUserData();
        logoutButton.setOnClickListener(v -> showLogoutConfirmationDialog());
        about.setOnClickListener(v -> presenter.about());
        backingUp.setOnClickListener(v -> presenter.backingUp());
}


    private void showLogoutConfirmationDialog() {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout? Remember To Back Up Your Data First")
                .setPositiveButton("Yes", (dialog, which) -> {
                    presenter.logout();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }
    @Override
    public void showAboutDialog() {
        String versionName = "1.0";
        try {
            versionName = requireContext().getPackageManager()
                    .getPackageInfo(requireContext().getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("About Gusteau")
                .setMessage("Gusteau App v" + versionName + "\n" +
                        "Your personal meal planner and recipe assistant.\n" +
                        "Developed by\nOsama Khaled")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
    public void setupID(){
        logoutButton = requireView().findViewById(R.id.btn_logout);
        backingUp = requireView().findViewById(R.id.ll_backup);
        about = requireView().findViewById(R.id.ll_about);
        tvName = requireView().findViewById(R.id.tv_user_name);
        tvEmail = requireView().findViewById(R.id.tv_user_email);
        loading = requireView().findViewById(R.id.progressBar2);
    }
    @Override
    public void navigateToLogin() {
        Intent intent = new Intent(getActivity(), WelcomeActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void showError(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
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
    public void setUserData(String name, String email) {
        tvName.setText(name);
        tvEmail.setText(email);
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        if (presenter != null) {
            presenter.onDestroy();
        }

    }
}