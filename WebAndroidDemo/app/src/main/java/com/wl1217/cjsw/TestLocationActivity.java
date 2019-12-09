package com.wl1217.cjsw;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class TestLocationActivity extends AppCompatActivity {

    public LocationClient mLocationClient = null;
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_location);

        mLocationClient = new LocationClient(getApplicationContext());
        LocationClientOption mOption = new LocationClientOption();
        mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mOption.setOpenGps(true); // 打开gps
        mOption.setCoorType("bd09ll");
        mOption.setScanSpan(3000);
        mOption.setIsNeedAddress(true);
        mOption.setIsNeedLocationDescribe(true);
        mLocationClient.setLocOption(mOption);//设置定位参数
        mLocationClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                Log.d("wg", "onReceiveLocation: " + bdLocation.getLatitude());
                Log.d("wg", "onReceiveLocation: " + bdLocation.getLongitude());
            }
        });
        mLocationClient.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        super.onDestroy();
    }
}
