package com.gama.farm_fun;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

public class DatabaseOperation extends AppCompatActivity {


    GeoCoder geoCoder;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);
        setContentView(R.layout.activity_databaseoperation);

        //uploadAmusementGeopoint();
        //LatLng latLng = new LatLng(34.7568711, 113.663221);
        //getLocationDescribeByLatLng(latLng);
        Test();
    }

    public void uploadAmusementGeopoint() {
        AVObject avObject = new AVObject("Amusement");
        AVGeoPoint point = new AVGeoPoint(28.334628, 120.761494);
        avObject.put("locate", point);
        avObject.saveInBackground();
        Log.i("UploadAmusementGeopoint", "success!");
    }

    public void Test() {
        //新建编码查询对象
        GeoCoder geocode = GeoCoder.newInstance();
        //新建查询对象要查询的条件
        GeoCodeOption GeoOption = new GeoCodeOption().city("郑州").address("郑州东站");
        //发起地理编码请求
        geocode.geocode(GeoOption);
        //设置查询结果监听者
        geocode.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                Log.i("test", String.valueOf(geoCodeResult.getLocation()));
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }
        });
    }

    public void getLocationDescribeByLatLng(LatLng latLng) {
        geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (geoCodeResult.getLocation() == null) {
                    Log.i("LatLng", "null");
                }
                LatLng latLng1 = geoCodeResult.getLocation();
                Log.i("LatLng", String.valueOf(latLng1.latitude) + "," + String.valueOf(latLng1.longitude));
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if (reverseGeoCodeResult.getAddress() == null) {
                    Log.i("GeoCoder", "没有检测到结果！");
                } else {
                    Log.i("Address", reverseGeoCodeResult.getAddress());
                }

            }
        });
        Log.i("LatLng", String.valueOf(latLng.latitude) + "+" + String.valueOf(latLng.longitude));

        boolean tf = geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
        Log.i("reverse", String.valueOf(tf));

    }
}
