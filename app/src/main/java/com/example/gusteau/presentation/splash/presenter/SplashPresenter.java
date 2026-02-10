package com.example.gusteau.presentation.splash.presenter;

import android.content.Context;

import com.example.gusteau.data.authentication.AuthRepository;
import com.example.gusteau.presentation.splash.SplashContract;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SplashPresenter implements SplashContract.Presenter {
    private final SplashContract.View view;
    private final AuthRepository authRepository;
    private final CompositeDisposable disposables;

    public SplashPresenter(SplashContract.View view, Context context) {
        this.view = view;
        this.authRepository = new AuthRepository(context);
        this.disposables = new CompositeDisposable();
    }

    @Override
    public void checkLoggedIn() {
        disposables.add(
                authRepository.isUserLoggedIn()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                isLoggedIn -> {
                                    view.setIsLoggedIn(isLoggedIn);
                                },
                                error -> {
                                    view.setIsLoggedIn(false);
                                }
                        )
        );
    }

    @Override
    public void onDestroy() {
        disposables.clear();
    }
}
