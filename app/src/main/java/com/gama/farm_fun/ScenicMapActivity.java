package com.gama.farm_fun;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class ScenicMapActivity extends AppCompatActivity implements View.OnClickListener, BaiduMap.OnMarkerClickListener,BaiduMap.OnMapClickListener {
    public MapView mMapView;
    public BaiduMap baiduMap;
    public Double longitude;
    public Double latitude;
    public String projectName;
    public String addressString;
    public String url;

    public ImageView back;

    public View informationPanel;
    public SimpleDraweeView projectPic;
    public TextView project;
    public TextView address;
    public Button navigation;

    public List<MakerInfoUtil> markerInfoUtilList;

    public boolean firstTouch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
        Fresco.initialize(this);
        setContentView(R.layout.activity_scenic_map);
        mMapView = findViewById(R.id.mapView);

        initUI();
    }

    public void initUI() {
        mMapView = findViewById(R.id.mapView);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        informationPanel = findViewById(R.id.informationPanel);
        projectPic = informationPanel.findViewById(R.id.pic);
        project = informationPanel.findViewById(R.id.name);
        address = informationPanel.findViewById(R.id.address);
        navigation = informationPanel.findViewById(R.id.navigation);
        navigation.setOnClickListener(this);

        markerInfoUtilList = new ArrayList<>();

        firstTouch = true;

        initMap();
    }
    public void initMap() {
        mMapView.setLogoPosition(LogoPosition.logoPostionRightTop);
        mMapView.showScaleControl(false);
        mMapView.showZoomControls(false);
        baiduMap = mMapView.getMap();

        baiduMap.setCompassEnable(true);
        baiduMap.setCompassPosition(new android.graphics.Point(70, 380));
        getPosition1();
    }

    public void getPosition1() {
        AVQuery<AVObject> query = new AVQuery<>("Amusement");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                for (AVObject avObject : avObjects) {
                    AVGeoPoint avGeoPoint = avObject.getAVGeoPoint("locate");
                    LatLng latLng = new LatLng(avGeoPoint.getLatitude(), avGeoPoint.getLongitude());
                    markerInfoUtilList.add(new MakerInfoUtil(latLng.latitude,
                            latLng.longitude,
                            avObject.getString("name"),
                            avObject.getString("picName"),
                            avObject.getString("locateDescribe")));
                }
                getPosition2();
            }
        });
    }
    public void getPosition2() {
        AVQuery<AVObject> query = new AVQuery<>("HomeStay");
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                AVGeoPoint avGeoPoint = object.getAVGeoPoint("locate");
                LatLng latLng = new LatLng(avGeoPoint.getLatitude(), avGeoPoint.getLongitude());
                markerInfoUtilList.add(new MakerInfoUtil(latLng.latitude,
                        latLng.longitude,
                        object.getString("name"),
                        object.getString("mainPicName"),
                        object.getString("locateDescribe")));
                getPosition3();
            }
        });
    }
    public void getPosition3() {
        AVQuery<AVObject> query = new AVQuery<>("Restaurant");
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                AVGeoPoint avGeoPoint = object.getAVGeoPoint("locate");
                LatLng latLng = new LatLng(avGeoPoint.getLatitude(), avGeoPoint.getLongitude());
                markerInfoUtilList.add(new MakerInfoUtil(latLng.latitude,
                        latLng.longitude,
                        object.getString("name"),
                        object.getString("mainPicName"),
                        object.getString("locateDescribe")));
                initOverlay();
            }
        });
    }

    public void initOverlay() {
        baiduMap.clear();
        //创建marker的显示图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.locate_overlay);
        LatLng latLng = null;
        Marker marker;
        OverlayOptions options;
        for(MakerInfoUtil info:markerInfoUtilList){
            //获取经纬度
            latLng = new LatLng(info.getLatitude(),info.getLongitude());
            //设置marker
            options = new MarkerOptions()
                    .position(latLng)//设置位置
                    .icon(bitmap)//设置图标样式
                    .zIndex(9) // 设置marker所在层级
                    .draggable(true); // 设置手势拖拽;
            //添加marker
            marker = (Marker) baiduMap.addOverlay(options);
            //使用marker携带info信息，当点击事件的时候可以通过marker获得info信息
            Bundle bundle = new Bundle();
            //info必须实现序列化接口
            bundle.putSerializable("info", info);
            marker.setExtraInfo(bundle);
        }
        //将地图显示在最后一个marker的位置
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);

        baiduMap.setMapStatus(msu);
        LatLng newLatLng = new LatLng(28.334628,120.761494);

        MapStatus mapStatus = new MapStatus.Builder().target(newLatLng).zoom(16).build();

        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        baiduMap.setMapStatus(mapStatusUpdate);
        baiduMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.i("firstTouch", String.valueOf(firstTouch));
        if (!firstTouch) {
            informationPanel.setVisibility(View.INVISIBLE);
            firstTouch = true;
        }
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (firstTouch) {
            Bundle bundle = marker.getExtraInfo();
            MakerInfoUtil infoUtil = (MakerInfoUtil) bundle.getSerializable("info");
            project.setText(infoUtil.getName());
            projectName = infoUtil.getName();
            address.setText(infoUtil.getDescription());
            informationPanel.setVisibility(View.VISIBLE);
            latitude = infoUtil.getLatitude();
            longitude = infoUtil.getLongitude();
            AVQuery<AVObject> query = new AVQuery<>("_File");
            query.whereEqualTo("name", infoUtil.getPicName());
            query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject object, AVException e) {
                    Uri uri = Uri.parse(object.getString("url"));
                    projectPic.setImageURI(uri);
                    RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);
                    projectPic.getHierarchy().setRoundingParams(roundingParams);
                }
            });
            firstTouch = false;
        } else {
            Bundle bundle = marker.getExtraInfo();
            MakerInfoUtil infoUtil = (MakerInfoUtil) bundle.getSerializable("info");
            projectName = infoUtil.getName();
            project.setText(infoUtil.getName());
            address.setText(infoUtil.getDescription());
            informationPanel.setVisibility(View.VISIBLE);
            latitude = infoUtil.getLatitude();
            longitude = infoUtil.getLongitude();
            AVQuery<AVObject> query = new AVQuery<>("_File");
            query.whereEqualTo("name", infoUtil.getPicName());
            query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject object, AVException e) {
                    Uri uri = Uri.parse(object.getString("url"));
                    projectPic.setImageURI(uri);
                    RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);
                    projectPic.getHierarchy().setRoundingParams(roundingParams);
                }
            });
        }

        return true;
    }



    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.navigation:
                if (!isInstalled("com.baidu.BaiduMap")) {
                    return;
                }
                Intent intent = new Intent();
                intent.setData(Uri.parse("baidumap://map/direction?destination=latlng:"
                        + latitude + ","
                        + longitude + "|name:" + projectName + // 终点
                        "&mode=driving" + // 导航路线方式
                        "&src=" + getPackageName()));
                Log.i("Test3", "Succeed");
                startActivity(intent);
                break;
        }
    }

    private boolean isInstalled(String packageName) {
        PackageManager manager = getBaseContext().getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> installedPackages = manager.getInstalledPackages(0);
        if (installedPackages != null) {
            for (PackageInfo info : installedPackages) {
                if (info.packageName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
