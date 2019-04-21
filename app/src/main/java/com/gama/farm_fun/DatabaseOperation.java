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

        //upLoadRestaurantOrderInformation();
        //upLoadRoomInformation();
        //reUpLoadRoomInformation();
        //upLoadRoomTimeTable();
        //upTimeTable();

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
            for (int k = 0; k < 2; k++) {
                for (int j = 0; j < 4; j++) {
                    if (j == 0) {
                        AVObject avObject = new AVObject("Seat");
                        avObject.put("date", date);
                        avObject.put("seatNumber", 2);
                        avObject.put("describe", "小桌（2人）");
                        avObject.put("remain", 10);
                        if (k == 0) {
                            avObject.put("meal", "lunch");
                        } else {
                            avObject.put("meal", "dinner");
                        }
                        avObject.saveInBackground();
                    } else if (j == 1) {
                        AVObject avObject = new AVObject("Seat");
                        avObject.put("date", date);
                        avObject.put("seatNumber", 4);
                        avObject.put("describe", "中桌（4人）");
                        avObject.put("remain", 20);
                        if (k == 0) {
                            avObject.put("meal", "lunch");
                        } else {
                            avObject.put("meal", "dinner");
                        }
                        avObject.saveInBackground();
                    } else if (j == 2) {
                        AVObject avObject = new AVObject("Seat");
                        avObject.put("date", date);
                        avObject.put("seatNumber", 10);
                        avObject.put("describe", "大桌（10人）");
                        avObject.put("remain", 6);
                        if (k == 0) {
                            avObject.put("meal", "lunch");
                        } else {
                            avObject.put("meal", "dinner");
                        }
                        avObject.saveInBackground();
                    } else {
                        AVObject avObject = new AVObject("Seat");
                        avObject.put("date", date);
                        avObject.put("seatNumber", 12);
                        avObject.put("describe", "包厢（12人）");
                        avObject.put("remain", 4);
                        if (k == 0) {
                            avObject.put("meal", "lunch");
                        } else {
                            avObject.put("meal", "dinner");
                        }
                        avObject.saveInBackground();
                    }
                }
            }

        }

    }

    public void upLoadRoomInformation() {
        for (int i = 1; i < 31; i++) {
            String date;
            if (i <= 9) {
                date = "2019/06/0" + String.valueOf(i);
            } else {
                date = "2019/06/" + String.valueOf(i);
            }
            for (int j = 0; j < 4; j++) {
                if (j == 0) {
                    AVObject avObject = new AVObject("Room");
                    avObject.put("date", date);
                    avObject.put("roomPicName", "bigbedroom.jpg");
                    avObject.put("describe", "25m² 有窗 1张双人床1.8m");
                    avObject.put("roomType", "大床房");
                    avObject.put("price", 258);
                    avObject.put("remain", 20);
                    avObject.saveInBackground();
                } else if (j == 1) {
                    AVObject avObject = new AVObject("Room");
                    avObject.put("date", date);
                    avObject.put("roomPicName", "standardroom.jpg");
                    avObject.put("describe", "25m² 有窗 2张单人床1.2m");
                    avObject.put("roomType", "标准间");
                    avObject.put("price", 278);
                    avObject.put("remain", 15);
                    avObject.saveInBackground();
                } else if (j == 2) {
                    AVObject avObject = new AVObject("Room");
                    avObject.put("date", date);
                    avObject.put("roomPicName", "smallsuite.jpg");
                    avObject.put("describe", "25m² 有窗 1张双人床1.8m");
                    avObject.put("roomType", "小套房");
                    avObject.put("price", 328);
                    avObject.put("remain", 10);
                    avObject.saveInBackground();
                } else {
                    AVObject avObject = new AVObject("Room");
                    avObject.put("date", date);
                    avObject.put("roomPicName", "bigsuite.jpg");
                    avObject.put("describe", "25m² 有窗 2张双人床1.8m");
                    avObject.put("roomType", "大套房");
                    avObject.put("price", 479);
                    avObject.put("remain", 5);
                    avObject.saveInBackground();
                }
            }

        }

    }
    public void reUpLoadRoomInformation() {
        AVObject avObject3 = new AVObject("Room");
        avObject3.put("roomPicName", "bigbedroom.jpg");
        avObject3.put("describe", "25m² 有窗 1张双人床1.8m 可住2人）");
        avObject3.put("roomType", "大床房");
        avObject3.saveInBackground();
        AVObject avObject1 = new AVObject("Room");
        avObject1.put("roomPicName", "bigsuite.jpg");
        avObject1.put("describe", "25m² 有窗 2张双人床1.8m 可住4人）");
        avObject1.put("roomType", "大套房");
        avObject1.saveInBackground();
        AVObject avObject2 = new AVObject("Room");
        avObject2.put("roomPicName", "standardroom.jpg");
        avObject2.put("describe", "25m² 有窗 2张单人床1.2m 可住2人）");
        avObject2.put("roomType", "标准间");
        avObject2.saveInBackground();
        AVObject avObject4 = new AVObject("Room");
        avObject4.put("roomPicName", "smallsuite.jpg");
        avObject4.put("describe", "25m² 有窗 1张双人床1.8m 可住2人）");
        avObject4.put("roomType", "小套房");
        avObject4.saveInBackground();
    }

    public void upLoadRoomTimeTable() {
        for (int i = 1; i < 31; i++) {
            String date;
            if (i <= 9) {
                date = "2019/06/0" + String.valueOf(i);
            } else {
                date = "2019/06/" + String.valueOf(i);
            }

            for (int j = 0; j < 4; j++) {
                if (j == 0) {
                    AVObject avObject = new AVObject("RoomTimeTable");
                    avObject.put("date", date);
                    avObject.put("roomType", "大床房");
                    avObject.put("price", 258);
                    avObject.put("remain", 20);
                    avObject.saveInBackground();
                } else if (j == 1) {
                    AVObject avObject = new AVObject("RoomTimeTable");
                    avObject.put("date", date);
                    avObject.put("roomType", "标准间");
                    avObject.put("price", 278);
                    avObject.put("remain", 15);
                    avObject.saveInBackground();
                } else if (j == 2) {
                    AVObject avObject = new AVObject("RoomTimeTable");
                    avObject.put("date", date);
                    avObject.put("roomType", "小套房");
                    avObject.put("price", 328);
                    avObject.put("remain", 10);
                    avObject.saveInBackground();
                } else {
                    AVObject avObject = new AVObject("RoomTimeTable");
                    avObject.put("date", date);
                    avObject.put("roomType", "大套房");
                    avObject.put("price", 479);
                    avObject.put("remain", 5);
                    avObject.saveInBackground();
                }
            }

        }
    }

    public void upTimeTable() {
        for (int i = 1; i < 31; i++) {
            String date;
            if (i <= 9) {
                date = "2019/06/0" + String.valueOf(i);
            } else {
                date = "2019/06/" + String.valueOf(i);
            }
            int weekday = (i + 5) % 7;
            if (weekday == 0) {
                weekday = 7;
            }
            AVObject avObject = new AVObject("TimeTable");
            avObject.put("date", date);
            avObject.put("week", weekday);
            if (weekday == 6 || weekday == 7) {
                avObject.put("weekday", 0);
            } else {
                avObject.put("weekday", 1);
            }
            avObject.saveInBackground();
        }
        for (int i = 1; i < 32; i++) {
            String date;
            if (i <= 9) {
                date = "2019/07/0" + String.valueOf(i);
            } else {
                date = "2019/07/" + String.valueOf(i);
            }
            int weekday = i % 7;
            if (weekday == 0) {
                weekday = 7;
            }
            AVObject avObject = new AVObject("TimeTable");
            avObject.put("date", date);
            avObject.put("week", weekday);
            if (weekday == 6 || weekday == 7) {
                avObject.put("weekday", 0);
            } else {
                avObject.put("weekday", 1);
            }
            avObject.saveInBackground();
        }
        for (int i = 1; i < 32; i++) {
            String date;
            if (i <= 9) {
                date = "2019/08/0" + String.valueOf(i);
            } else {
                date = "2019/08/" + String.valueOf(i);
            }
            int weekday = (i + 3) % 7;
            if (weekday == 0) {
                weekday = 7;
            }
            AVObject avObject = new AVObject("TimeTable");
            avObject.put("date", date);
            avObject.put("week", weekday);
            if (weekday == 6 || weekday == 7) {
                avObject.put("weekday", 0);
            } else {
                avObject.put("weekday", 1);
            }
            avObject.saveInBackground();
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
