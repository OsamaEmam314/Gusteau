package com.example.gusteau.presentation.register.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gusteau.MainActivity;
import com.example.gusteau.R;
import com.example.gusteau.data.network.NetworkState;
import com.example.gusteau.presentation.onboarding.view.OnBoardingActivity;
import com.example.gusteau.presentation.register.RegisterContract;
import com.example.gusteau.presentation.register.presenter.RegisterPresenter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterFragment extends Fragment implements RegisterContract.View {
    private TextInputEditText etName;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private TextInputLayout tilName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmPassword;
    private MaterialButton btnSignup;
    private TextView tvLogin;
    private ProgressBar progressBar;
    private RegisterPresenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NestedScrollView nestedScrollView = view.findViewById(R.id.nestedScrollView);

        ViewCompat.setOnApplyWindowInsetsListener(nestedScrollView, (v, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = windowInsets.getInsets(WindowInsetsCompat.Type.ime());
            int bottomPadding = ime.bottom > 0 ? ime.bottom : systemBars.bottom;

            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    bottomPadding
            );

            return WindowInsetsCompat.CONSUMED;
        });

        idSetup(view);
        assert getActivity() != null;
        presenter = new RegisterPresenter(this, getActivity().getApplicationContext());
        tvLogin.setOnClickListener(v -> presenter.navigateToLogin());
        btnSignup.setOnClickListener(v -> {
            String name = etName.getText() != null ? etName.getText().toString().trim() : "";
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString() : "";
            String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString() : "";

            presenter.signUpWithEmail(name, email, password, confirmPassword);
        });
        setupTextChangeListeners();

    }


      public void idSetup(View view){
          etName = view.findViewById(R.id.et_name);
          etEmail = view.findViewById(R.id.et_email);
          etPassword = view.findViewById(R.id.et_password);
          etConfirmPassword = view.findViewById(R.id.et_confirm_password);

          tilName = view.findViewById(R.id.til_name);
          tilEmail = view.findViewById(R.id.til_email);
          tilPassword = view.findViewById(R.id.til_password);
          tilConfirmPassword = view.findViewById(R.id.til_confirm_password);
          btnSignup = view.findViewById(R.id.btn_signup);

          tvLogin = view.findViewById(R.id.tv_login);

          progressBar = view.findViewById(R.id.progress_bar);

      }

    private void setupTextChangeListeners() {
        addTextWatcher(etName, tilName);
        addTextWatcher(etEmail, tilEmail);
        addTextWatcher(etPassword, tilPassword);
        addTextWatcher(etConfirmPassword, tilConfirmPassword);
    }

    private void addTextWatcher(TextInputEditText editText, TextInputLayout inputLayout) {
        editText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int length, int before, int count) {
                if (inputLayout.getError() != null) {
                    inputLayout.setError(null);
                    inputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }
    @Override
    public void showError(String message) {
        if (getView() != null && getContext() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void showNameError(String error) {
        tilName.setError(error);
        etName.requestFocus();
    }

    @Override
    public void showEmailError(String error) {
        tilEmail.setError(error);
        etEmail.requestFocus();
    }

    @Override
    public void showPasswordError(String error) {
        tilPassword.setError(error);
        etPassword.requestFocus();
    }

    @Override
    public void showConfirmPasswordError(String error) {
        tilConfirmPassword.setError(error);
        etConfirmPassword.requestFocus();
    }
    @Override
    public void clearErrors() {
        tilName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        tilName.setErrorEnabled(false);
        tilEmail.setErrorEnabled(false);
        tilPassword.setErrorEnabled(false);
        tilConfirmPassword.setErrorEnabled(false);
    }
    @Override
    public void navigateToLogin() {
        if (getView() != null) {
            Navigation.findNavController(getView())
                    .navigate(R.id.action_registerFragment_to_loginFragment);
        }
    }

    @Override
    public void navigateToOnBoarding() {
        Intent intent = new Intent(getActivity(), OnBoardingActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        setFormEnabled(false);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
        setFormEnabled(true);
    }

    private void setFormEnabled(boolean enabled) {
        btnSignup.setEnabled(enabled);
        etName.setEnabled(enabled);
        etEmail.setEnabled(enabled);
        etPassword.setEnabled(enabled);
        etConfirmPassword.setEnabled(enabled);
        tvLogin.setEnabled(enabled);
    }


}
