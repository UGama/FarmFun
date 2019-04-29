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

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class MapActivity extends AppCompatActivity implements View.OnClickListener {

    public MapView mMapView;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
        Fresco.initialize(this);
        setContentView(R.layout.activity_map);
        Log.i("MapActivity", "success!");
        Intent intent = getIntent();
        longitude = intent.getDoubleExtra("longitude", 0);
        latitude = intent.getDoubleExtra("latitude", 0);
        Log.i("Longitude", String.valueOf(longitude));
        Log.i("Latitude", String.valueOf(latitude));
        projectName = intent.getStringExtra("project");
        addressString = intent.getStringExtra("address");
        url = intent.getStringExtra("url");

        mMapView = findViewById(R.id.mapView);
        initUI();
    }

    public void initUI() {
        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        informationPanel = findViewById(R.id.informationPanel);
        projectPic = informationPanel.findViewById(R.id.pic);
        project = informationPanel.findViewById(R.id.name);
        address = informationPanel.findViewById(R.id.address);
        navigation = informationPanel.findViewById(R.id.navigation);
        Uri uri = Uri.parse(url);
        projectPic.setImageURI(uri);
        project.setText(projectName);
        address.setText(addressString);
        navigation.setOnClickListener(this);

        initMap();
    }
    public void initMap() {
        mMapView.setLogoPosition(LogoPosition.logoPostionRightTop);
        mMapView.showScaleControl(false);
        mMapView.showZoomControls(false);
        BaiduMap baiduMap = mMapView.getMap();
        LatLng latLng = new LatLng(latitude, longitude);

        MapStatus mapStatus = new MapStatus.Builder().target(latLng).zoom(18).build();

        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        baiduMap.setMapStatus(mapStatusUpdate);

        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
                .fromResource(R.drawable.locate_overlay);
        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(latLng)
                .icon(bitmapDescriptor).zIndex(8).draggable(true);
        baiduMap.addOverlay(option);

        baiduMap.setCompassEnable(true);
        baiduMap.setCompassPosition(new android.graphics.Point(70, 380));

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
                goToBaiduMap();
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
                    Log.i("Test3", "true");
                    return true;
                }
            }
        }
        Log.i("Test4", "false");
        return false;
    }
    private void goToBaiduMap() {
        Log.i("Test1", "Succeed");
        if (!isInstalled("com.baidu.BaiduMap")) {
            Log.i("test5", "failed");
            return;
        }
        Intent intent = new Intent();
        intent.setData(Uri.parse("baidumap://map/direction?destination=latlng:"
                + latitude + ","
                + longitude + "|name:" + projectName + // 终点
                "&mode=driving" + // 导航路线方式
                "&src=" + getPackageName()));
        Log.i("Test3", "Succeed");
        startActivity(intent); // 启动调用
    }

}
