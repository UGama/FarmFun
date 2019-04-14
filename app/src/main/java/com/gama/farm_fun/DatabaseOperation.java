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

        upLoadRestaurantOrderInformation();
    }

    public void uploadAmusementGeopoint() {
        AVObject avObject = new AVObject("Amusement");
        AVGeoPoint point = new AVGeoPoint(28.334628, 120.761494);
        avObject.put("locate", point);
        avObject.saveInBackground();
        Log.i("UploadAmusementGeopoint", "success!");
    }

    public void upLoadRestaurantOrderInformation() {
        for (int i = 1; i < 31; i++) {
            String date;
            if (i <= 9) {
                date = "2019/06/0" + String.valueOf(i);
            } else {
                date = "2019/06/" + String.valueOf(i);
            }
            for (int j = 0; j < 4; j++) {
                if (j == 0) {
                    AVObject avObject = new AVObject("RestaurantOrder");
                    avObject.put("date", date);
                    avObject.put("seatNumber", 2);
                    avObject.put("type", "小桌（2人）");
                    avObject.put("remain", 10);
                    avObject.saveInBackground();
                } else if (j == 1) {
                    AVObject avObject = new AVObject("RestaurantOrder");
                    avObject.put("date", date);
                    avObject.put("seatNumber", 4);
                    avObject.put("type", "中桌（4人）");
                    avObject.put("remain", 20);
                    avObject.saveInBackground();
                } else if (j == 2) {
                    AVObject avObject = new AVObject("RestaurantOrder");
                    avObject.put("date", date);
                    avObject.put("seatNumber", 10);
                    avObject.put("type", "大桌（10人）");
                    avObject.put("remain", 6);
                    avObject.saveInBackground();
                } else {
                    AVObject avObject = new AVObject("RestaurantOrder");
                    avObject.put("date", date);
                    avObject.put("seatNumber", 12);
                    avObject.put("type", "包厢（12人）");
                    avObject.put("remain", 4);
                    avObject.saveInBackground();
                }
            }
        }

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
