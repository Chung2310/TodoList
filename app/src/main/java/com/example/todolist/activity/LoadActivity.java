package com.example.todolist.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.todolist.R;
import com.example.todolist.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.todolist.utils.Utils.*;

public class LoadActivity extends AppCompatActivity {

    private LottieAnimationView animationView;
    private TextView splashText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_load);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Ánh xạ view
        animationView = findViewById(R.id.animation_view);
        splashText = findViewById(R.id.splashtext);

        // Thiết lập animation
        setupAnimation();

        // Delay 3 giây (đủ thời gian xem animation) trước khi kiểm tra đăng nhập
        new Handler().postDelayed(this::checkLoginStatus, 3000);
    }
    private void setupAnimation() {
        // Có thể custom animation tại đây nếu cần
        animationView.setAnimation(R.raw.animationload);
        animationView.playAnimation();
        animationView.loop(true);

        // Hiệu ứng cho text (tuỳ chọn)
        splashText.setText("TO DO APP");
        splashText.setAlpha(0f);
        splashText.animate()
                .alpha(1f)
                .setDuration(1500)
                .setStartDelay(500)
                .start();
    }
    private void checkLoginStatus() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            Utils.U_ID = currentUser.getUid();
            navigateToMain();
        } else {
            // Kiểm tra token lưu trữ
            checkSavedToken();
        }
    }

    private void checkSavedToken() {
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String savedToken = pref.getString("idToken", null);

        if (savedToken != null) {
            FirebaseAuth.getInstance().signInWithCustomToken(savedToken)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            navigateToMain();
                        } else {
                            navigateToLogin();
                        }
                    });
        } else {
            navigateToLogin();
        }
    }

    private void navigateToMain() {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, DangNhapActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        // Dọn dẹp animation để tránh memory leak
        if (animationView != null) {
            animationView.cancelAnimation();
        }
        super.onDestroy();
    }
}