package com.example.gusteau.presentation.register.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gusteau.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterFragment extends Fragment {

    private ImageButton btnBack;
    private TextInputEditText etName;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;

    private TextInputLayout tilName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmPassword;

    private MaterialButton btnSignup;
    private MaterialButton btnGoogleSignup;

    private TextView tvLogin;

    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBack = view.findViewById(R.id.btn_back);

        etName = view.findViewById(R.id.et_name);
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);

        tilName = view.findViewById(R.id.til_name);
        tilEmail = view.findViewById(R.id.til_email);
        tilPassword = view.findViewById(R.id.til_password);
        tilConfirmPassword = view.findViewById(R.id.til_confirm_password);


        btnSignup = view.findViewById(R.id.btn_signup);
        btnGoogleSignup = view.findViewById(R.id.btn_google_signup);

        tvLogin = view.findViewById(R.id.tv_login);

        progressBar = view.findViewById(R.id.progress_bar);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_loginFragment);
            }
        });
    }
    }
