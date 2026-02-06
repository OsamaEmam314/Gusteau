package com.example.gusteau.presentation.register.presenter;

import android.content.Context;

import com.example.gusteau.data.Authentication.AuthRepository;
import com.example.gusteau.presentation.register.RegisterContract;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

import java.util.regex.Pattern;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RegisterPresenter implements RegisterContract.Presenter{
    private RegisterContract.View view;
    private final AuthRepository authRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );
    public RegisterPresenter(RegisterContract.View view, Context context) {
        this.view = view;
        this.authRepository = new AuthRepository(context);
    }


    @Override
    public void signUpWithEmail(String name, String email, String password, String confirmPassword) {
        if (view == null) return;

        view.clearErrors();

        if (!validateName(name)) return;
        if (!validateEmail(email)) return;
        if (!validatePassword(password)) return;
        if (!validateConfirmPassword(password, confirmPassword)) return;

        view.showLoading();
        Disposable localdisposable = authRepository.registerWithEmail(name, email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        user -> {
                            view.hideLoading();
                            navigateToHome();
                        },
                        error -> {
                            view.hideLoading();
                            handleRegistrationError(error);
                        }
                );

        disposables.add(localdisposable);
    }

    private boolean validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            view.showNameError("Name is required");
            return false;
        }

        String trimmedName = name.trim();
        if (trimmedName.length() < 2) {
            view.showNameError("Name must be at least 2 characters");
            return false;
        }

        if (trimmedName.length() > 50) {
            view.showNameError("Name cannot exceed 50 characters");
            return false;
        }

        if (!trimmedName.matches("^[a-zA-Z\\s'-]+$")) {
            view.showNameError("Name can only contain letters, spaces, hyphens and apostrophes");
            return false;
        }

        if (trimmedName.matches(".*['-]{2,}.*")) {
            view.showNameError("Cannot have consecutive special characters");
            return false;
        }

        return true;
    }

    private boolean validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            view.showEmailError("Email is required");
            return false;
        }

        String trimmedEmail = email.trim().toLowerCase();

        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            view.showEmailError("Please enter a valid email address");
            return false;
        }
        if (trimmedEmail.length() > 254) {
            view.showEmailError("Email is too long");
            return false;
        }

        if (trimmedEmail.startsWith(".") ||
                trimmedEmail.endsWith(".") ||
                trimmedEmail.contains("..")) {
            view.showEmailError("Invalid email format");
            return false;
        }

        return true;
    }

    private boolean validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            view.showPasswordError("Password is required");
            return false;
        }

        if (password.length() < 8) {
            view.showPasswordError("Password must be at least 8 characters");
            return false;
        }

        if (password.length() > 128) {
            view.showPasswordError("Password cannot exceed 128 characters");
            return false;
        }

        boolean hasUpperCase = !password.equals(password.toLowerCase());
        boolean hasLowerCase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

        int strengthScore = 0;
        if (hasUpperCase) strengthScore++;
        if (hasLowerCase) strengthScore++;
        if (hasDigit) strengthScore++;
        if (hasSpecialChar) strengthScore++;

        if (strengthScore < 4) {
            view.showPasswordError("Password should include uppercase, lowercase, numbers, and special characters");
            return false;
        }


        return true;
    }

    private boolean validateConfirmPassword(String password, String confirmPassword) {
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            view.showConfirmPasswordError("Please confirm your password");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            view.showConfirmPasswordError("Passwords do not match");
            return false;
        }

        return true;
    }
    @Override
    public void navigateToLogin() {
        if (view != null) {
            view.navigateToLogin();
        }
    }

    @Override
    public void navigateToHome() {
        if (view != null) {
            view.navigateToHome();
        }

    }


    private void handleRegistrationError(Throwable error) {
        if (view == null) return;
        if (error instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {
            view.showError("This email is already registered. Please log in instead.");
            view.navigateToLogin();
        } else if (error instanceof com.google.firebase.FirebaseNetworkException) {
            view.showError("Network error. Please check your connection.");
        } else if (error instanceof com.google.firebase.auth.FirebaseAuthWeakPasswordException) {
            view.showPasswordError("Password is too weak. Please use a stronger password.");
        } else if (error instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
            FirebaseAuthInvalidCredentialsException credsError = (FirebaseAuthInvalidCredentialsException) error;
            if (credsError.getErrorCode().equals("ERROR_INVALID_EMAIL")) {
                view.showEmailError("Invalid email format.");
            } else {
                view.showError("Invalid credentials. Please check your email and password.");
            }
        } else if (error instanceof com.google.firebase.auth.FirebaseAuthException) {
            FirebaseAuthException authError = (FirebaseAuthException) error;
            String errorCode = authError.getErrorCode();
            switch (errorCode) {
                case "ERROR_EMAIL_ALREADY_IN_USE":
                    view.showError("An account with this email already exists.");
                    break;
                case "ERROR_USER_DISABLED":
                    view.showError("This account has been disabled.");
                    break;
                case "ERROR_TOO_MANY_REQUESTS":
                    view.showError("Too many requests. Please try again later.");
                    break;
                default:
                    view.showError("Authentication failed: " + error.getMessage());
            }
        } else {
            view.showError("Registration failed. Please try again.");
        }
    }

    public void onDestroy() {
        disposables.clear();
    }



}
