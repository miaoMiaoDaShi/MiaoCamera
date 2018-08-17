package com.miaomaio.miaocameralibrary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.miaomaio.miaocameralibrary.utils.DisplayView;

/**
 * Author : zhongwenpeng
 * Email : 1340751953@qq.com
 * Time :  2018/7/24
 * Description :
 */
public class TakePhotoActivity extends AppCompatActivity implements View.OnClickListener {
    private DisplayView mDisplayView;
    private ImageView mIvClose;
    private ImageView mIvConfirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        mDisplayView = findViewById(R.id.mDisPlayView);


        mIvClose = findViewById(R.id.mIvClose);
        mIvConfirm = findViewById(R.id.mIvConfirm);

        mIvClose.setOnClickListener(this);
        mIvConfirm.setOnClickListener(this);
    }

    private boolean mIsTaked = false;
    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.mIvClose) {
            finish();
        } else if (id == R.id.mIvConfirm) {
            mIvConfirm.setEnabled(false);
            mDisplayView.takePhoto();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDisplayView.startCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDisplayView.stopCamera();
    }


}
