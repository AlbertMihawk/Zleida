package com.zleidadr.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.service.LocationService;
import com.zleidadr.Zleida;

public class LocationBaseActivity extends Activity {
    protected LocationService mLocationService;
    protected static BDLocation mBdLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPhotoError();
    }

    private void initPhotoError() {
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationService = ((Zleida) getApplication()).mLocationService;
        mLocationService.registerListener(mListener);
        mLocationService.setLocationOption(mLocationService.getDefaultLocationClientOption());
        mLocationService.start();
    }

    @Override
    protected void onStop() {
        mLocationService.unregisterListener(mListener);
        mLocationService.stop();
        super.onStop();
    }

    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public synchronized void onReceiveLocation(BDLocation location) {
            if (null != location) {
                mBdLocation = location;
            }
        }

    };
}