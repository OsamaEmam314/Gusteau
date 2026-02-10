package com.example.gusteau.presentation.settings.presenter;

import android.content.Context;

import com.example.gusteau.data.authentication.AuthRepository;
import com.example.gusteau.data.meals.MealsRepository;
import com.example.gusteau.presentation.settings.SettingsContract;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SettingsPresenter implements SettingsContract.Presenter {
    private final SettingsContract.View view;
    private final AuthRepository authRepository;
    private final MealsRepository mealsRepository;
    CompositeDisposable disposables ;


    public SettingsPresenter(SettingsContract.View view, Context context) {
        this.view = view;
        this.authRepository = new AuthRepository(context);
        this.mealsRepository = new MealsRepository(context);
        this.disposables = new CompositeDisposable();
    }

    @Override
    public void logout() {
        view.showLoading();
        disposables.add(
                mealsRepository.clearAllUserData()
                        .andThen(authRepository.logout())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(

                                () -> {
                                    view.hideLoading();
                                    if (view != null) {
                                        view.navigateToLogin();
                                     }
                                    },
                                error -> {
                                    view.hideLoading();
                                    view.showError(error.getMessage());
                                }
                        )

        );

    }
    @Override
    public void backingUp() {
        view.showLoading();
        disposables.add(
                authRepository.getCurrentUser()
                        .flatMapCompletable(user -> {
                            return Completable.mergeArray(
                                    mealsRepository.uploadFavoritesToFirestore(user.getUid()),
                                    mealsRepository.uploadPlannedMealsToFirestore(user.getUid())
                            );
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    view.hideLoading();
                                    view.showError("Backup completed successfully!");
                                },
                                throwable -> {
                                    view.hideLoading();
                                    view.showError("Backup failed: " + throwable.getMessage());
                                }
                        )
        );
    }
    @Override
    public void about() {
        view.showAboutDialog();

    }
    @Override
    public void loadUserData() {
        disposables.add(
                authRepository.getCurrentUser()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                user -> {
                                    view.setUserData(user.getName(), user.getEmail());
                                },
                                error -> {
                                    view.showError(error.getMessage());
                                }
                        )
        );

    }
    @Override
    public void onDestroy() {
        disposables.clear();
    }

}
