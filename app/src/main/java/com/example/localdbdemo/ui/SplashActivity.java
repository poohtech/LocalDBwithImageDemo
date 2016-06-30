package com.example.localdbdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.example.localdbdemo.R;
import com.example.localdbdemo.util.Util;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Util.SystemUpdate(SplashActivity.this);
        Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                handler.sendEmptyMessage(0);
            }
        };
        splashThread.start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent i = new Intent(SplashActivity.this, AddEmployeeActivity.class);
            startActivity(i);
            finish();
        }
    };
}
