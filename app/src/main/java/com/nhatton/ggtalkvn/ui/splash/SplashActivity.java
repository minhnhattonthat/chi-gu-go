package com.nhatton.ggtalkvn.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.ads.MobileAds;
import com.nhatton.ggtalkvn.BuildConfig;
import com.nhatton.ggtalkvn.ui.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MobileAds.initialize(this, BuildConfig.ADMOB_APP_ID);


        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
