package com.zleidadr.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.zleidadr.R;

public class SplashActivity extends Activity {

    private Handler mMainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setClass(getApplication(), MainV2Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mMainHandler.sendEmptyMessageDelayed(0, 3000);

    }

    @Override
    public void onBackPressed() {
    }
}