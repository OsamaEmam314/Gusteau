package com.example.gusteau.presentation.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.example.gusteau.R;
import com.example.gusteau.WelcomeActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class GuestDialog {
    private final Context context;

    public GuestDialog(Context context, View view) {
        this.context = context;
    }

    public void showGuestModeMessage() {
        View guestDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_guest_prompt, null);

        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setView(guestDialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Button btnLogin = guestDialogView.findViewById(R.id.btn_login);
        Button btnCancel = guestDialogView.findViewById(R.id.btn_cancel);

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(context, WelcomeActivity.class);
            context.startActivity(intent);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}