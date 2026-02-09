package com.example.gusteau.presentation.login.presenter;

import android.content.Context;

import com.example.gusteau.data.authentication.AuthRepository;
import com.example.gusteau.data.model.User;
import com.example.gusteau.presentation.login.LoginContract;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;


public class LoginPresenter implements LoginContract.Presenter {
    private LoginContract.View view;
    private final AuthRepository authRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public LoginPresenter(LoginContract.View view, Context context) {
        this.view = view;
        this.authRepository = new AuthRepository(context);
    }

    private boolean validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            view.showEmailError("Email is required");
            return false;
        }

        return true;
    }

    private boolean validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            view.showPasswordError("Password is required");
            return false;
        }
        return true;
    }

    @Override
    public void logInWithEmail(String email, String password) {
        if (view == null) return;
        view.clearErrors();
        if (!validateEmail(email)) return;
        if (!validatePassword(password)) return;
        view.showLoading();
        Disposable disposable = authRepository.signInWithEmail(email, password)
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
                        });
        disposables.add(disposable);

    }

    @Override
    public void logInWithGoogle(GoogleIdTokenCredential credential) {
        if (view == null) return;
        view.clearErrors();
        view.showLoading();

        disposables.add(
                authRepository.signInWithGoogle(credential)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                user -> {
                                    view.hideLoading();
                                    if (user.second) {
                                        view.navigateToOnBoarding();
                                    }
                                    else {
                                        navigateToHome();
                                    }
                                },
                                error -> {
                                    view.hideLoading();
                                    handleGoogleSignInError(error);
                                }
                        )
        );

    }
    private void handleGoogleSignInError(Throwable error) {
        if (view == null) return;

        if (error instanceof com.google.firebase.FirebaseNetworkException) {
            view.showError("Network error. Please check your connection.");
        } else if (error instanceof com.google.firebase.auth.FirebaseAuthException) {
            FirebaseAuthException authError = (FirebaseAuthException) error;
            String errorCode = authError.getErrorCode();

            switch (errorCode) {
                case "ERROR_INVALID_CREDENTIAL":
                case "ERROR_WRONG_PASSWORD":
                case "ERROR_USER_NOT_FOUND":
                    view.showError("Authentication failed. Please try again.");
                    break;
                case "ERROR_USER_DISABLED":
                    view.showError("This account has been disabled.");
                    break;
                case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                    view.showError("An account already exists with this email using a different sign-in method.");
                    break;
                case "ERROR_TOO_MANY_REQUESTS":
                    view.showError("Too many requests. Please try again later.");
                    break;
                default:
                    view.showError("Google Sign-In failed: " + error.getMessage());
            }
        } else {
            if (error.getMessage() != null && error.getMessage().contains("already exists using a different sign-in method")) {
                view.showError("An account already exists with this email using a different sign-in method.");
            } else {
                view.showError("Google Sign-In failed. Please try again.");
            }
        }
    }

    private void handleRegistrationError(Throwable error) {
        if (view == null) return;
        else if (error instanceof com.google.firebase.FirebaseNetworkException) {
            view.showError("Network error. Please check your connection.");
        }else if (error instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
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

    @Override
    public void navigateToRegister() {
        if (view != null) {
            view.navigateToRegister();
        }

    }

    @Override
    public void navigateToHome() {
        if (view != null) {
            view.navigateToHome();
        }
    }
    @Override
    public void onDestroy() {
        disposables.clear();
    }



    @Override
    public void guestLogin() {
        if (view == null) return;
        view.showLoading();

        disposables.add(
                authRepository.loginAsGuest()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    view.hideLoading();
                                    navigateToHome();
                                },
                                error -> {
                                    view.hideLoading();
                                    view.showError("Failed to login as guest: " + error.getMessage());
                                }
                        )
        );
    }
}
