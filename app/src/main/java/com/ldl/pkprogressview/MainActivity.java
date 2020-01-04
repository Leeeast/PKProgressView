package com.ldl.pkprogressview;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private PKProgressView mPKProgressView;
    private float mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPKProgressView = findViewById(R.id.pkprogressview);
        mPKProgressView.setProgress(0.985f);
        findViewById(R.id.btn_set_progress).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pkprogressview:
                mProgress = mProgress + 0.1f;
                mPKProgressView.setProgress(mProgress);
                break;
        }
    }
}
