package com.example.gusteau.presentation.splash.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gusteau.MainActivity;
import com.example.gusteau.R;
import com.example.gusteau.WelcomeActivity;

public class SplashScreenActivity extends AppCompatActivity {
    ImageView appLogo;
    Intent intent;
    boolean isLoggedIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        appLogo = findViewById(R.id.appLogo);
        isLoggedIn = false;
        appLogo.animate()
                .rotationBy(25f)
                .setDuration(250)
                .withEndAction(() -> {
                    appLogo.animate()
                            .rotationBy(-50f)
                            .setDuration(750)
                            .withEndAction(() -> {
                                appLogo.animate()
                                        .rotationBy(35f)
                                        .setDuration(500).withEndAction(() -> {
                                            if(isLoggedIn)
                                            {
                                                intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                                            }
                                            else {
                                                intent = new Intent(SplashScreenActivity.this, WelcomeActivity.class);
                                            }
                                           startActivity(intent);
                                           finish();
                                        }).start();
                            }).start();
                }).start();

    }
}