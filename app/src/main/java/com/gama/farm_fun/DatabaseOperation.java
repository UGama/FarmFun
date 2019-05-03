package com.gama.farm_fun;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import java.util.Arrays;
import java.util.List;

public class DatabaseOperation extends AppCompatActivity {

    private TextView run;

    private int days = 1;

    private int restaurantTimeTableSupport = 1;
    private int s = 1;


    GeoCoder geoCoder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);
        setContentView(R.layout.activity_databaseoperation);
        run = findViewById(R.id.ActivityRun);
        //uploadAmusementGeopoint();
        //LatLng latLng = new LatLng(34.7568711, 113.663221);
        //getLocationDescribeByLatLng(latLng);

        //upLoadRestaurantOrderInformation();
        //upLoadSeatInformation();
        //upLoadRoomInformation();
        //reUpLoadRoomInformation();
        //upLoadRoomTimeTable();
        //upTimeTable();

        //SeatTimeTable();
        //updateTicketTimeTable3();

        //updateBarbecue();
        //updateChess();
        //updateKTV();

        //test();
    }

    public void test() {
        AVQuery<AVObject> query1 = new AVQuery<>("_File");
        query1.whereStartsWith("name", "poster");
        AVQuery<AVObject> query2 = new AVQuery<>("_File");
        query2.whereEqualTo("name", "restaurantmain.jpg");
        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(query1, query2));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {

                Log.i("avObjectsList", String.valueOf(avObjects.size()));

            }
        });


    }

    public void updateKTV() {
        if (s < 301) {
            String date;
            if (s % 30 <= 9 & s % 30 > 0) {
                date = "2019/06/0" + String.valueOf(s % 30);
            } else if (s % 30 == 0) {
                date = "2019/06/" + String.valueOf(30);
            } else {
                date = "2019/06/" + String.valueOf(s % 30);
            }
            if ((s - 1) / 30 == 0) {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "楠溪江音乐工厂");
                avObject.put("date", date);
                avObject.put("remain", 200);
                avObject.put("ticket","黄金场欢唱2小时（小包）");
                avObject.saveInBackground();

            } else if ((s - 1) / 30 == 1) {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "楠溪江音乐工厂");
                avObject.put("date", date);
                avObject.put("remain", 100);
                avObject.put("ticket","阳光场欢唱3小时（小包）");
                avObject.saveInBackground();
            } else if ((s - 1) / 30 == 2) {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "楠溪江音乐工厂");
                avObject.put("date", date);
                avObject.put("remain", 80);
                avObject.put("ticket","阳光场欢唱7小时（小包）");
                avObject.saveInBackground();
            } else if ((s - 1) / 30 == 3) {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "楠溪江音乐工厂");
                avObject.put("date", date);
                avObject.put("remain", 100);
                avObject.put("ticket","午夜场欢唱3小时（小包）");
                avObject.saveInBackground();
            }else if ((s - 1) / 30 == 4) {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "楠溪江音乐工厂");
                avObject.put("date", date);
                avObject.put("remain", 80);
                avObject.put("ticket","午夜场欢唱7小时（小包）");
                avObject.saveInBackground();
            }else if ((s - 1) / 30 == 5) {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "楠溪江音乐工厂");
                avObject.put("date", date);
                avObject.put("remain", 100);
                avObject.put("ticket","黄金场欢唱2小时（大包）");
                avObject.saveInBackground();
            }else if ((s - 1) / 30 == 6) {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "楠溪江音乐工厂");
                avObject.put("date", date);
                avObject.put("remain", 50);
                avObject.put("ticket","阳光场欢唱3小时（大包）");
                avObject.saveInBackground();
            }else if ((s - 1) / 30 == 7) {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "楠溪江音乐工厂");
                avObject.put("date", date);
                avObject.put("remain", 40);
                avObject.put("ticket","阳光场欢唱7小时（大包）");
                avObject.saveInBackground();
            }else if ((s - 1) / 30 == 8) {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "楠溪江音乐工厂");
                avObject.put("date", date);
                avObject.put("remain", 50);
                avObject.put("ticket","午夜场欢唱3小时（大包）");
                avObject.saveInBackground();
            }else {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "楠溪江音乐工厂");
                avObject.put("date", date);
                avObject.put("remain", 40);
                avObject.put("ticket","午夜场欢唱7小时（大包）");
                avObject.saveInBackground();
            }
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(run, "translationX", 0, 100, 0);
            objectAnimator.setDuration(1000);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    s++;
                    updateKTV();
                }
            });
            objectAnimator.start();
        }
    }
    public void updateChess() {
        if (s < 91) {
            String date;
            if (s % 30 <= 9 & s % 30 > 0) {
                date = "2019/06/0" + String.valueOf(s % 30);
            } else if (s % 30 == 0) {
                date = "2019/06/" + String.valueOf(30);
            } else {
                date = "2019/06/" + String.valueOf(s % 30);
            }
            if ((s - 1) / 30 == 0) {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "林溪小院客栈棋牌室");
                avObject.put("date", date);
                avObject.put("remain", 200);
                avObject.put("ticket","小厅（可容纳4人）");
                avObject.saveInBackground();

            } else if ((s - 1) / 30 == 1) {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "林溪小院客栈棋牌室");
                avObject.put("date", date);
                avObject.put("remain", 100);
                avObject.put("ticket","中厅（可容纳8人）");
                avObject.saveInBackground();
            } else if ((s - 1) / 30 == 2) {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "林溪小院客栈棋牌室");
                avObject.put("date", date);
                avObject.put("remain", 50);
                avObject.put("ticket","大厅（可容纳15人）");
                avObject.saveInBackground();
            }
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(run, "translationX", 0, 100, 0);
            objectAnimator.setDuration(1000);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    s++;
                    updateChess();
                }
            });
            objectAnimator.start();
        }
    }

    public void updateBarbecue() {
        if (s < 121) {
            String date;
            if (s % 30 <= 9 & s % 30 > 0) {
                date = "2019/06/0" + String.valueOf(s % 30);
            } else if (s % 30 == 0) {
                date = "2019/06/" + String.valueOf(30);
            } else {
                date = "2019/06/" + String.valueOf(s % 30);
            }
            if ((s - 1) / 30 == 0) {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "江边自助烧烤");
                avObject.put("date", date);
                avObject.put("remain", 200);
                avObject.put("ticket","户外单人烧烤套餐");
                avObject.saveInBackground();

            } else if ((s - 1) / 30 == 1) {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "江边自助烧烤");
                avObject.put("date", date);
                avObject.put("remain", 200);
                avObject.put("ticket","户外双人烧烤套餐");
                avObject.saveInBackground();
            } else if ((s - 1) / 30 == 2) {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "江边自助烧烤");
                avObject.put("date", date);
                avObject.put("remain", 80);
                avObject.put("ticket","户外四人烧烤套餐");
                avObject.saveInBackground();
            } else {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "江边自助烧烤");
                avObject.put("date", date);
                avObject.put("remain", 50);
                avObject.put("ticket","户外八人烧烤套餐");
                avObject.saveInBackground();
            }
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(run, "translationX", 0, 100, 0);
            objectAnimator.setDuration(1000);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    s++;
                    updateBarbecue();
                }
            });
            objectAnimator.start();
        }

    }

    public void uploadAmusementGeopoint() {
        AVObject avObject = new AVObject("Amusement");
        AVGeoPoint point = new AVGeoPoint(28.334628, 120.761494);
        avObject.put("locate", point);
        avObject.saveInBackground();
        Log.i("UploadAmusementGeopoint", "success!");
    }

    public void SeatTimeTable() {
        if (s < 241) {
            String date;
            if (s % 30 <= 9 & s % 30 > 0) {
                date = "2019/06/0" + String.valueOf(s % 30);
            } else if (s % 30 == 0) {
                date = "2019/06/" + String.valueOf(30);
            } else {
                date = "2019/06/" + String.valueOf(s % 30);
            }
            if ((s - 1) / 60 == 0) {
                AVObject avObject = new AVObject("SeatTimeTable");
                avObject.put("type", "小桌");
                avObject.put("date", date);
                avObject.put("remain", 10);
                if ((s - 1) / 30 == 0) {
                    avObject.put("meal", "中");
                } else {
                    avObject.put("meal", "晚");
                }
                avObject.saveInBackground();

            } else if ((s - 1) / 60 == 1) {
                AVObject avObject = new AVObject("SeatTimeTable");
                avObject.put("type", "中桌");
                avObject.put("date", date);
                avObject.put("remain", 20);
                if ((s - 1) / 30 == 0) {
                    avObject.put("meal", "中");
                } else {
                    avObject.put("meal", "晚");
                }
                avObject.saveInBackground();
            } else if ((s - 1) / 60 == 2) {
                AVObject avObject = new AVObject("SeatTimeTable");
                avObject.put("type", "大桌");
                avObject.put("date", date);
                avObject.put("remain", 6);
                if ((s - 1) / 30 == 0) {
                    avObject.put("meal", "中");
                } else {
                    avObject.put("meal", "晚");
                }
                avObject.saveInBackground();
            } else {
                AVObject avObject = new AVObject("SeatTimeTable");
                avObject.put("type", "包厢");
                avObject.put("date", date);
                avObject.put("remain", 4);
                if ((s - 1) / 30 == 0) {
                    avObject.put("meal", "中");
                } else {
                    avObject.put("meal", "晚");
                }
                avObject.saveInBackground();
            }
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(run, "translationX", 0, 100, 0);
            objectAnimator.setDuration(2000);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    s++;
                    SeatTimeTable();
                }
            });
            objectAnimator.start();
        }
    }

    public void upLoadRestaurantOrderInformation() {
        if (restaurantTimeTableSupport < 31) {
            int i = restaurantTimeTableSupport;
            String date;
            if (i <= 9) {
                date = "2019/06/0" + String.valueOf(i);
            } else {
                date = "2019/06/" + String.valueOf(i);
            }
            for (int k = 0; k < 2; k++) {
                for (int j = 0; j < 4; j++) {
                    if (j == 0) {
                        AVObject avObject = new AVObject("SeatTimeTable");
                        avObject.put("type", "小桌");
                        avObject.put("date", date);
                        avObject.put("remain", 10);
                        if (k == 0) {
                            avObject.put("meal", "中");
                        } else {
                            avObject.put("meal", "晚");
                        }
                        avObject.saveInBackground();
                    } else if (j == 1) {
                        AVObject avObject = new AVObject("SeatTimeTable");
                        avObject.put("type", "中桌");
                        avObject.put("date", date);
                        avObject.put("remain", 20);
                        if (k == 0) {
                            avObject.put("meal", "中");
                        } else {
                            avObject.put("meal", "晚");
                        }
                        avObject.saveInBackground();
                    } else if (j == 2) {
                        AVObject avObject = new AVObject("SeatTimeTable");
                        avObject.put("type", "大桌");
                        avObject.put("date", date);
                        avObject.put("remain", 6);
                        if (k == 0) {
                            avObject.put("meal", "中");
                        } else {
                            avObject.put("meal", "晚");
                        }
                        avObject.saveInBackground();
                    } else {
                        AVObject avObject = new AVObject("SeatTimeTable");
                        avObject.put("type", "包厢");
                        avObject.put("date", date);
                        avObject.put("remain", 4);
                        if (k == 0) {
                            avObject.put("meal", "中");
                        } else {
                            avObject.put("meal", "晚");
                        }
                        avObject.saveInBackground();
                    }
                }
            }
            restaurantTimeTableSupport++;
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(run, "translationX", 0, 100, 0);
            objectAnimator.setDuration(2000);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    upLoadRestaurantOrderInformation();
                }
            });
            objectAnimator.start();
        } else {
        }

    }

    public void upLoadSeatInformation() {
        for (int j = 0; j < 4; j++) {
            if (j == 0) {
                AVObject avObject = new AVObject("Seat");
                avObject.put("type","小桌");
                avObject.put("seatNumber", 2);
                avObject.put("describe", "最多可坐2人");
                avObject.saveInBackground();
            } else if (j == 1) {
                AVObject avObject = new AVObject("Seat");
                avObject.put("type","中桌");
                avObject.put("seatNumber", 4);
                avObject.put("describe", "最多可坐4人");
                avObject.saveInBackground();
            } else if (j == 2) {
                AVObject avObject = new AVObject("Seat");
                avObject.put("type", "大桌");
                avObject.put("seatNumber", 10);
                avObject.put("describe", "最多可坐10人");
                avObject.saveInBackground();
            } else {
                AVObject avObject = new AVObject("Seat");
                avObject.put("type","包厢");
                avObject.put("seatNumber", 12);
                avObject.put("describe", "最多可坐12人");
                avObject.put("remain", 4);
                avObject.saveInBackground();
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

    public void updateTimeTable() {
        String date;
        if (days <= 9) {
            date = "2019/06/0" + String.valueOf(days);
        } else {
            date = "2019/06/" + String.valueOf(days);
        }
        int weekday = (days + 5) % 7;
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
        days++;
        if (days < 30) {
            updateTimeTable();
        }

    }

    public void updateTicketTimeTable() {
        if (s < 125) {
            String date;
            if (s % 30 <= 9 & s % 30 > 0) {
                date = "2019/07/0" + String.valueOf(s % 30);
            } else if (s % 31 == 0) {
                date = "2019/07/" + String.valueOf(30);
            } else {
                date = "2019/07/" + String.valueOf(s % 30);
            }
            if ((s - 1) / 31 == 0) {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "楠溪江竹筏漂流");
                avObject.put("date", date);
                avObject.put("remain", 200);
                avObject.put("ticket","儿童票");
                avObject.saveInBackground();

            } else if ((s - 1) / 31 == 1) {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "楠溪江竹筏漂流");
                avObject.put("date", date);
                avObject.put("remain", 200);
                avObject.put("ticket","成人票");
                avObject.saveInBackground();
            } else if ((s - 1) / 31 == 2) {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "楠溪江竹筏漂流");
                avObject.put("date", date);
                avObject.put("remain", 80);
                avObject.put("ticket","亲子票【1大1小】");
                avObject.saveInBackground();
            } else {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "楠溪江竹筏漂流");
                avObject.put("date", date);
                avObject.put("remain", 50);
                avObject.put("ticket","亲子票【2大1小】");
                avObject.saveInBackground();
            }
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(run, "translationX", 0, 100, 0);
            objectAnimator.setDuration(2000);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    s++;
                    updateTicketTimeTable();
                }
            });
            objectAnimator.start();
        }
    }
    public void updateTicketTimeTable2() {
        if (s < 95) {
            String date;
            if (s % 31 <= 9 & s % 31 > 0) {
                date = "2019/07/0" + String.valueOf(s % 31);
            } else if (s % 31 == 0) {
                date = "2019/07/" + String.valueOf(31);
            } else {
                date = "2019/07/" + String.valueOf(s % 31);
            }
            if ((s - 1) / 31 == 0) {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "楠溪江巨俊农庄（枫叶蓝莓基地）");
                avObject.put("date", date);
                avObject.put("remain", 40);
                avObject.put("ticket","蓝莓采摘");
                avObject.saveInBackground();

            } else if ((s - 1) / 31 == 1) {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "楠溪江巨俊农庄（枫叶蓝莓基地）");
                avObject.put("date", date);
                avObject.put("remain", 40);
                avObject.put("ticket","蓝莓采摘+外带两斤蓝莓");
                avObject.saveInBackground();
            } else if ((s - 1) / 31 == 2) {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "楠溪江巨俊农庄（枫叶蓝莓基地）");
                avObject.put("date", date);
                avObject.put("remain", 40);
                avObject.put("ticket","蓝莓采摘+外带五斤蓝莓");
                avObject.saveInBackground();
            }
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(run, "translationX", 0, 100, 0);
            objectAnimator.setDuration(2000);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    s++;
                    updateTicketTimeTable2();
                }
            });
            objectAnimator.start();
        }
    }
    public void updateTicketTimeTable3() {
        if (s < 181) {
            String date;
            if (s % 30 <= 9 & s % 30 > 0) {
                date = "2019/06/0" + String.valueOf(s % 30);
            } else if (s % 31 == 0) {
                date = "2019/06/" + String.valueOf(30);
            } else {
                date = "2019/06/" + String.valueOf(s % 30);
            }
            if ((s - 1) / 31 == 0) {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "枫林鱼塘垂钓中心(合作社)");
                avObject.put("date", date);
                avObject.put("remain", 20);
                avObject.put("ticket","综合鱼塘（3小时）");
                avObject.saveInBackground();

            } else if ((s - 1) / 31 == 1) {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "枫林鱼塘垂钓中心(合作社)");
                avObject.put("date", date);
                avObject.put("remain", 20);
                avObject.put("ticket","综合鱼塘（6小时）");
                avObject.saveInBackground();
            } else if ((s - 1) / 31 == 2) {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "枫林鱼塘垂钓中心(合作社)");
                avObject.put("date", date);
                avObject.put("remain", 20);
                avObject.put("ticket","综合鱼塘（天）");
                avObject.saveInBackground();
            } else if ((s - 1) / 31 == 3) {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "枫林鱼塘垂钓中心(合作社)");
                avObject.put("date", date);
                avObject.put("remain", 20);
                avObject.put("ticket", "综合鱼塘（3小时）+半斤高级鱼饵");
                avObject.saveInBackground();
            } else if ((s - 1) / 31 == 4) {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "枫林鱼塘垂钓中心(合作社)");
                avObject.put("date", date);
                avObject.put("remain", 20);
                avObject.put("ticket", "综合鱼塘（6小时）+1斤高级鱼饵");
                avObject.saveInBackground();
            } else {
                AVObject avObject = new AVObject("TicketTimeTable");
                avObject.put("projectName", "枫林鱼塘垂钓中心(合作社)");
                avObject.put("date", date);
                avObject.put("remain", 20);
                avObject.put("ticket", "综合鱼塘（天）+2斤高级鱼饵");
                avObject.saveInBackground();
            }
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(run, "translationX", 0, 100, 0);
            objectAnimator.setDuration(2000);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    s++;
                    updateTicketTimeTable3();
                }
            });
            objectAnimator.start();
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
