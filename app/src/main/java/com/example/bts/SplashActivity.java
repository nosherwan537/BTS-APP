package com.example.bts;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bts.Authentication.MainActivity;
import com.example.bts.Authentication.Role;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, Role.class);
                startActivity(intent);
                finish();
            }
        }, 3000); // 3000 milliseconds delay
    }
}
