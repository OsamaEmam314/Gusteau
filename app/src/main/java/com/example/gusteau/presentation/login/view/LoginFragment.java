package com.example.gusteau.presentation.login.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gusteau.MainActivity;
import com.example.gusteau.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginFragment extends Fragment {

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;

    private MaterialButton btnLogin;
    private MaterialButton btnGoogleSignIn;
    private MaterialButton btnGuestMode;

    private TextView tvSignup;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        tilEmail = view.findViewById(R.id.til_email);
        tilPassword = view.findViewById(R.id.til_password);

        btnLogin = view.findViewById(R.id.btn_login);
        btnGoogleSignIn = view.findViewById(R.id.btn_google_signin);
        btnGuestMode = view.findViewById(R.id.btn_guest_mode);

        tvSignup = view.findViewById(R.id.tv_signup);
        progressBar = view.findViewById(R.id.progress_bar);
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }

        });
    }
}