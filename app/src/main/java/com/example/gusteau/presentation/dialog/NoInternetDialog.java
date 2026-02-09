package com.example.gusteau.presentation.dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.gusteau.R;
import com.google.android.material.button.MaterialButton;

public class NoInternetDialog {

    private Dialog dialog;
    private OnRetryListener retryListener;

    public interface OnRetryListener {
        void onRetry();
    }

    public NoInternetDialog(Context context) {
        createDialog(context);
    }

    private void createDialog(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = LayoutInflater.from(context).inflate(R.layout.no_internet_dialog, null);
        dialog.setContentView(view);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        setupViews(view, context);
    }

    private void setupViews(View view, Context context) {
        ImageView ivNoInternet = view.findViewById(R.id.iv_no_internet);
        MaterialButton btnRetry = view.findViewById(R.id.btn_retry);
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);

        Animation pulse = AnimationUtils.loadAnimation(context, R.anim.pulse);
        ivNoInternet.startAnimation(pulse);

        btnRetry.setOnClickListener(v -> {
            if (retryListener != null) {
                retryListener.onRetry();
            }
            dismiss();
        });

        btnCancel.setOnClickListener(v -> dismiss());
    }

    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }

    public void setOnRetryListener(OnRetryListener listener) {
        this.retryListener = listener;
    }

    public static NoInternetDialog show(Context context, OnRetryListener listener) {
        NoInternetDialog dialog = new NoInternetDialog(context);
        dialog.setOnRetryListener(listener);
        dialog.show();
        return dialog;
    }

}