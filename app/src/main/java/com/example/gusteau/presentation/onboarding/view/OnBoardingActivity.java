package com.example.gusteau.presentation.onboarding.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.gusteau.R;
import com.example.gusteau.presentation.main.view.MainActivity;
import com.example.gusteau.presentation.onboarding.OnBoardingContract;
import com.example.gusteau.presentation.onboarding.presenter.OnBoardingPresenter;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class OnBoardingActivity extends AppCompatActivity implements OnBoardingContract.View {

    private ViewPager2 viewPager;
    private LinearLayout layoutDots;
    private MaterialButton btnSkip;
    private MaterialButton btnGetStarted;

    private OnBoardingAdapter adapter;
    private OnBoardingPresenter presenter;

    private ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);
        hideSystemUI();

        initViews();
        setupViewPager();
        setupButtons();
        presenter = new OnBoardingPresenter(this, this);
        presenter.loadOnboardingPages();
    }
    private void hideSystemUI() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            final WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }
    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        layoutDots = findViewById(R.id.layoutDots);
        btnSkip = findViewById(R.id.btnSkip);
        btnGetStarted = findViewById(R.id.btnGetStarted);
    }

    private void setupViewPager() {
        adapter = new OnBoardingAdapter();
        viewPager.setAdapter(adapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                presenter.onPageChanged(position);
            }
        });
    }

    private void setupButtons() {
        btnSkip.setOnClickListener(v -> presenter.onSkipClicked());
        btnGetStarted.setOnClickListener(v -> presenter.onGetStartedClicked());
    }

    @Override
    public void showOnboardingPages(List<OnBoardingContract.OnboardingPage> pages) {
        adapter.setPages(pages);
        setupDotsIndicator(pages.size());
    }

    private void setupDotsIndicator(int count) {
        dots = new ImageView[count];
        layoutDots.removeAllViews();

        for (int i = 0; i < count; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(
                    this, R.drawable.indecator_inactive
            ));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);

            layoutDots.addView(dots[i], params);
        }
    }

    @Override
    public void updatePageIndicator(int position) {
        for (int i = 0; i < dots.length; i++) {
            int drawableId = (i == position)
                    ? R.drawable.indecator_activ
                    : R.drawable.indecator_inactive;
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, drawableId));

            if (i == position) {
                dots[i].animate()
                        .scaleX(1.1f)
                        .scaleY(1.1f)
                        .setDuration(200)
                        .start();
            } else {
                dots[i].animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(200)
                        .start();
            }
        }
    }

    @Override
    public void showSkipButton() {
        btnSkip.setVisibility(View.VISIBLE);
        btnSkip.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
    }

    @Override
    public void hideSkipButton() {
        btnSkip.setVisibility(View.GONE);
        btnSkip.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
    }

    @Override
    public void showGetStartedButton() {
        btnGetStarted.setVisibility(View.VISIBLE);
        btnGetStarted.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up));
    }

    @Override
    public void hideGetStartedButton() {
        btnGetStarted.setVisibility(View.GONE);
    }


    @Override
    public void navigateToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }
}