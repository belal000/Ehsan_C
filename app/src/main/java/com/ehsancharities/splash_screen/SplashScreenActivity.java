package com.ehsancharities.splash_screen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.ehsancharities.AdminActivity;
import com.ehsancharities.R;
import com.ehsancharities.home.MainActivity;
import com.ehsancharities.login.LoginActivity;
import com.ehsancharities.utils.Const;
import com.ehsancharities.utils.Session;
import com.ehsancharities.utils.Tools;

public class SplashScreenActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Tools.setSystemBarColor(this, R.color.blue_grey_900);
        startSplashScreen();
    }

    private void startSplashScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (Session.getInstance().getTYPE_ACCOUNT(getApplicationContext()).equals("Admin")) {
                    Intent mainIntent = new Intent(SplashScreenActivity.this, AdminActivity.class);
                    SplashScreenActivity.this.startActivity(mainIntent);
                    SplashScreenActivity.this.finish();
                } else {

                    Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    SplashScreenActivity.this.startActivity(mainIntent);
                    SplashScreenActivity.this.finish();
                }
            }
        }, Const.SPLASH_DISPLAY_LENGTH);
    }
}

