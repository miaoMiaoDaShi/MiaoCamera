package com.miaomaio.miaocamera;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.miaomaio.miaocameralibrary.Camera;
import com.miaomaio.miaocameralibrary.TakePhotoActivity;

public class MainActivity extends AppCompatActivity {
    private TextView mTvTakePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvTakePhoto = findViewById(R.id.mTvTakePhoto);

        mTvTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Camera.from(MainActivity.this).request(0x10);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x10 && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, Camera.getImagePath(data), Toast.LENGTH_SHORT).show();
        }
    }
}
