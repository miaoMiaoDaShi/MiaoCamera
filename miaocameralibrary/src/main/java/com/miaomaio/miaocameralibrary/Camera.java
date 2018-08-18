package com.miaomaio.miaocameralibrary;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;

/**
 * Author : zhongwenpeng
 * Email : 1340751953@qq.com
 * Time :  2018/7/6
 * Description :
 */
public class Camera {
    private final WeakReference<Activity> mContext;
    private final WeakReference<Fragment> mFragment;

    private Camera(Activity activity) {
        this(activity, null);
    }

    private Camera(Fragment fragment) {
        this(fragment.getActivity(), fragment);
    }

    private Camera(Activity activity, Fragment fragment) {
        mContext = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }

    public static Camera from(Activity activity) {
        return new Camera(activity);
    }

    public static Camera from(Fragment fragment) {
        return new Camera(fragment);
    }

    public CameraRequest request(int requestCode) {
        return new CameraRequest(this, requestCode);
    }

    @Nullable
    Activity getActivity() {
        return mContext.get();
    }

    @Nullable
    Fragment getFragment() {
        return mFragment != null ? mFragment.get() : null;
    }

    public static String getImagePath(Intent intent) {
        return intent.getStringExtra("imagePath");
    }

}
