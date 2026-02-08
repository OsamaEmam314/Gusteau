package com.example.gusteau.presentation.login.view;
import static com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL;

import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.gusteau.MainActivity;
import com.example.gusteau.R;
import com.example.gusteau.presentation.login.LoginContract;
import com.example.gusteau.presentation.login.presenter.LoginPresenter;;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.Credential;
import androidx.credentials.CustomCredential;

public class LoginFragment extends Fragment implements LoginContract.View {

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;

    private MaterialButton btnLogin;
    private MaterialButton btnGoogleSignIn;
    private MaterialButton btnGuestMode;

    private TextView tvSignup;
    private ProgressBar progressBar;
    LoginPresenter presenter;
/*    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        ScrollView nestedScrollView = view.findViewById(R.id.loginScroll);

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
        super.onViewCreated(view, savedInstanceState);
        idSetup(view);
        setupTextChangeListeners();
        presenter = new LoginPresenter(this, getActivity().getApplicationContext());
        lisenersSetup();



    }
    private void launchGoogleSignIn() {
        CredentialManager credentialManager = CredentialManager.create(requireContext());

        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.default_web_client_id))
                .setAutoSelectEnabled(true)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(
                requireContext(),
                request,
                null,
                ContextCompat.getMainExecutor(requireContext()),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        handleSignIn(result.getCredential());
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        Log.e("LoginFragment", "Credential Manager Error: " + e.getMessage());
                        showError("Google Sign-In failed.");
                    }
                }
        );
    }
    private void handleSignIn(Credential credential) {

        if (credential instanceof CustomCredential) {
            CustomCredential customCredential = (CustomCredential) credential;

            if (credential.getType().equals(TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
                Bundle credentialData = customCredential.getData();
                GoogleIdTokenCredential googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credentialData);

                presenter.logInWithGoogle(googleIdTokenCredential);
            }
        } else {
            Log.w("TAG", "Credential is not of type Google ID!");
        }
    }


    public void lisenersSetup(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
                String password = etPassword.getText() != null ? etPassword.getText().toString() : "";
                presenter.logInWithEmail(email, password);;
            }
        });
        btnGuestMode.setOnClickListener(v -> presenter.guestLogin());
        tvSignup.setOnClickListener(v -> presenter.navigateToRegister());
        btnGoogleSignIn.setOnClickListener(v ->{launchGoogleSignIn();} /*startGoogleSignIn()*/);
    }
    public void idSetup(View view){
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        tilEmail = view.findViewById(R.id.til_email);
        tilPassword = view.findViewById(R.id.til_password);

        btnLogin = view.findViewById(R.id.btn_login);
        btnGoogleSignIn = view.findViewById(R.id.btn_google_signin);
        btnGuestMode = view.findViewById(R.id.btn_guest_mode);

        tvSignup = view.findViewById(R.id.tv_signup);
        progressBar = view.findViewById(R.id.progress_bar);
    }
    private void setupTextChangeListeners() {
        addTextWatcher(etEmail, tilEmail);
        addTextWatcher(etPassword, tilPassword);
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
        btnLogin.setEnabled(enabled);
        btnGoogleSignIn.setEnabled(enabled);
        btnGuestMode.setEnabled(enabled);
        tvSignup.setEnabled(enabled);
        etEmail.setEnabled(enabled);
        etPassword.setEnabled(enabled);
    }

    @Override
    public void showError(String message) {
        if (getView() != null && getContext() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        }
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
    public void navigateToRegister() {
        if (getView() != null) {
            Navigation.findNavController(getView())
                    .navigate(R.id.action_loginFragment_to_registerFragment);
        }


    }

    @Override
    public void navigateToHome() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();


    }

    @Override
    public void clearErrors() {
        tilEmail.setError(null);
        tilPassword.setError(null);

        tilEmail.setErrorEnabled(false);
        tilPassword.setErrorEnabled(false);
    }
}