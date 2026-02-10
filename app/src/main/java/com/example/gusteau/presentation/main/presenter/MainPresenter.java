package com.example.gusteau.presentation.main.presenter;

import android.content.Context;

import com.example.gusteau.data.authentication.AuthRepository;
import com.example.gusteau.presentation.main.MainContract;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainPresenter implements MainContract.presenter{
    private final AuthRepository authRepository;
    private final MainContract.View view;
    CompositeDisposable disposables ;

    public MainPresenter(MainContract.View view, Context context) {
        this.view = view;
        this.authRepository = new AuthRepository(context);
        this.disposables = new CompositeDisposable();

    }

    @Override
    public void checkUserStatus() {
        disposables.add(
                authRepository.isGuestMode()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                isGuest -> {
                                    view.setGuestStatus(isGuest);
                                },
                                error -> {
                                    view.showError("Failed to check user status");
                                }
                        )
        );
    }
    @Override
    public void onDestroy() {
        disposables.clear();
    }




}
