package com.ldl.pkprogressview;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private PKProgressView mPKProgressView;
    private float mProgress = 0.0f;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            mPKProgressView.setProgress(mProgress);
            mProgress += 0.005f;
            sendEmptyMessageDelayed(0, 50);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPKProgressView = findViewById(R.id.pkprogressview);
        mPKProgressView.setProgress(0.5f);
        findViewById(R.id.btn_set_progress).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_set_progress:
                mProgress = mProgress + 0.001f;
                mHandler.sendEmptyMessage(0);
                break;
        }
    }
}
