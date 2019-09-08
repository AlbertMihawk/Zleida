package com.zleidadr.ui;

import android.app.Activity;
import android.os.Bundle;

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