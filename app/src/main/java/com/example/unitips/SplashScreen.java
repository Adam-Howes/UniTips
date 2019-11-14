package com.example.unitips;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.example.unitips.HomePage.HomePage;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    // Firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        setContentView(R.layout.activity_splash_screen);

        // Skips Splash if user is already logged in
        if (mAuth.getCurrentUser() != null) {

            // TODO: Check that th9s
            // Check if we're running on Android 5.0 or higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                // Apply activity transition#
                startActivity(new Intent(SplashScreen.this, HomePage.class));
                overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);

            } else {
                // Swap without transition
            }

        } else {
            // Countdown timer gives users time to look at the pretty splash screen
            new CountDownTimer(1500, 1000) {
                public void onFinish() {
                    startActivity(new Intent(SplashScreen.this, SignIn.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                }

                public void onTick(long millisUntilFinished) {
                }
            }.start();
        }
    }
}

