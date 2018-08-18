package com.miaomaio.miaocameralibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

/**
 * Author : zhongwenpeng
 * Email : 1340751953@qq.com
 * Time :  2018/8/17
 * Description :
 */
public class ConfirmImageActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageView mIvImage;
    private ImageView mIvClose;
    private ImageView mIvConfirm;
    private ImageView mIvReset;
    private String mImagePath;
    public static final int RESULT_CLOSE = 0x10;
    public static final int RESULT_CONFIRM = 0x11;
    public static final int RESULT_RESET = 0x12;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_image);


        initView();


        initData();

    }

    private void initView() {
        mIvImage = findViewById(R.id.mIvImage);
        mIvClose = findViewById(R.id.mIvClose);
        mIvConfirm = findViewById(R.id.mIvConfirm);
        mIvReset = findViewById(R.id.mIvReset);

        mIvClose.setOnClickListener(this);
        mIvConfirm.setOnClickListener(this);
        mIvReset.setOnClickListener(this);
    }

    private void initData() {
        mImagePath = getIntent().getStringExtra("imagePath");
        if (TextUtils.isEmpty(mImagePath)) {
            Toast.makeText(this, "图片地址异常", Toast.LENGTH_SHORT).show();
            return;
        }

        Glide.with(this).load(mImagePath).into(mIvImage);


    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.mIvClose) {
            callBack(RESULT_CLOSE);
        } else if (id == R.id.mIvConfirm) {
            callBack(RESULT_CONFIRM);
        } else if (id == R.id.mIvReset) {
            callBack(RESULT_RESET);
        }
    }

    private void callBack(int type) {
        final Intent intent = new Intent();
        intent.putExtra("imagePath", mImagePath);
        setResult(type, intent);
        finish();
    }
}
