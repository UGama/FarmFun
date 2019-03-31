package com.gama.farm_fun;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private PosterPagerAdapter posterPagerAdapter;
    private List<Poster> posterList;
    private ViewPagerIndicator indicator;

    public TextView locationText;
    public String locationString;
    public ImageView locationPic;

    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();

    public Button onlineShop;
    public Button news;
    public Button mine;

    public RecyclerView recommendProjectsRecycler;
    List<Project> recommendProjectsList = null;

    public RecyclerView travelJournalRecycler;
    List<TravelJournal> travelJournalsList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
        setContentView(R.layout.activity_main);

        //StartLocateService();

        posterList = new ArrayList<>();
        posterList.add(new Poster(R.drawable.adtest));
        posterList.add(new Poster(R.drawable.adtest));
        posterList.add(new Poster(R.drawable.adtest));
        posterList.add(new Poster(R.drawable.adtest));
        posterList.add(new Poster(R.drawable.adtest));
        posterList.add(new Poster(R.drawable.adtest));
        viewPager = findViewById(R.id.viewPager);
        posterPagerAdapter = new PosterPagerAdapter(posterList);
        viewPager.setAdapter(posterPagerAdapter);
        viewPager.setPageTransformer(true,new com.gama.farm_fun.ScalePageTransformer());
        indicator = findViewById(R.id.indicator);
        indicator.setLength(posterList.size());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                indicator.setSelected(position);
                /*position = position % 4;
                switch (position) {
                    case 0:
                        Item.setImageResource(v1.SourceId1);
                        Bag_Pic.setImageResource(v1.SourceId2);
                        Item_name.setText(" ? ? ? ");
                        PagesCount = 1;
                        break;
                    case 1:
                        Item.setImageResource(v2.SourceId1);
                        Bag_Pic.setImageResource(v2.SourceId2);
                        Item_name.setText(" ? ? ? ");
                        PagesCount = 2;
                        break;
                    case 2:
                        Item.setImageResource(v3.SourceId1);
                        Bag_Pic.setImageResource(v3.SourceId2);
                        Item_name.setText(" ? ? ? ");
                        PagesCount = 3;
                        break;
                    case 3:
                        Item.setImageResource(v4.SourceId1);
                        Bag_Pic.setImageResource(v4.SourceId2);
                        Item_name.setText(" ? ? ? ");
                        PagesCount = 4;
                        break;
                }*/
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        AVObject testObject = new AVObject("TestObject");
        testObject.put("words","Hello World!");
        testObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(e == null){
                    Log.d("saved","success!");
                }
            }
        });

        locationPic = findViewById(R.id.locationPic);
        locationPic.setOnClickListener(this);

        onlineShop = findViewById(R.id.onlineStore);
        onlineShop.setOnClickListener(this);

        news = findViewById(R.id.news);
        news.setOnClickListener(this);

        mine = findViewById(R.id.mine);
        mine.setOnClickListener(this);

        Log.i("test1", "success");
        travelJournalRecycler = findViewById(R.id.travelJournalRecyclerView);
        travelJournalRecycler.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        travelJournalRecycler.setLayoutManager(linearLayoutManager2);
        Log.i("test2", "success");
        travelJournalsList = new ArrayList<>();
        TravelJournal travelJournal = new TravelJournal("test", "test", "test");
        travelJournalsList.add(travelJournal);
        travelJournalsList.add(travelJournal);travelJournalsList.add(travelJournal);
        travelJournalsList.add(travelJournal);
        travelJournalsList.add(travelJournal);
        travelJournalsList.add(travelJournal);
        travelJournalsList.add(travelJournal);
        travelJournalsList.add(travelJournal);
        travelJournalsList.add(travelJournal);
        Log.i("test3", "success");
        TravelJournalsAdapter travelJournalsAdapter = new TravelJournalsAdapter(travelJournalsList);
        Log.i("test4", "success");
        travelJournalRecycler.setAdapter(travelJournalsAdapter);
        Log.i("test5", "success");

        recommendProjectsRecycler = findViewById(R.id.recommendProjectsRecyclerView);
        recommendProjectsRecycler.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        recommendProjectsRecycler.setLayoutManager(linearLayoutManager1);
        recommendProjectsList = new ArrayList<>();
        Project project = new Project("test", "test", "test");
        recommendProjectsList.add(project);
        recommendProjectsList.add(project);
        recommendProjectsList.add(project);
        recommendProjectsList.add(project);
        recommendProjectsList.add(project);
        recommendProjectsList.add(project);
        recommendProjectsList.add(project);
        recommendProjectsList.add(project);
        recommendProjectsList.add(project);
        RecommendProjectsAdapter recommendProjectsAdapter = new RecommendProjectsAdapter(recommendProjectsList);
        recommendProjectsRecycler.setAdapter(recommendProjectsAdapter);


    }



    public void StartLocateService() {
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

//可选，设置定位模式，默认高精度
//LocationMode.Hight_Accuracy：高精度；
//LocationMode. Battery_Saving：低功耗；
//LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("bd09ll");
//可选，设置返回经纬度坐标类型，默认GCJ02
//GCJ02：国测局坐标；
//BD09ll：百度经纬度坐标；
//BD09：百度墨卡托坐标；
//海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标

        option.setScanSpan(0);
//可选，设置发起定位请求的间隔，int类型，单位ms
//如果设置为0，则代表单次定位，即仅定位一次，默认为0
//如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
//可选，设置是否使用gps，默认false
//使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
//可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(false);
//可选，定位SDK内部是一个service，并放到了独立进程。
//设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
//可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5*60*1000);
//可选，V7.2版本新增能力
//如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位

        option.setEnableSimulateGps(false);
//可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false


        option.setIsNeedAddress(true);
//可选，是否需要地址信息，默认为不需要，即参数为false
//如果开发者需要获得当前点的地址信息，此处必须为true
        option.setIsNeedLocationDescribe(true);
//可选，是否需要位置描述信息，默认为不需要，即参数为false
//如果开发者需要获得当前点的位置信息，此处必须为true
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }


    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            float radius = location.getRadius();    //获取定位精度，默认值为0.0f


            String coorType = location.getCoorType();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

            int errorCode = location.getLocType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息
            String locationDescribe = location.getLocationDescribe();
            Log.i("1", "onReceiveLocation: " + locationDescribe);
            locationString = locationDescribe;
            locationText = findViewById(R.id.locationText);
            if (locationString != null) {
                locationText.setText(locationString);
            }


        }
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.locationPic:
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
                break;
            case R.id.onlineStore:
                Intent intent1 = new Intent(MainActivity.this, OnlineShopActivity.class);
                startActivity(intent1);
                break;
        }
    }
    private class PosterPagerAdapter extends android.support.v4.view.PagerAdapter {

        List<Poster> posterList = new ArrayList<>();

        public PosterPagerAdapter(List<Poster> posterList) {
            this.posterList = posterList;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position = position % posterList.size();
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_poster, null);
            ImageView poster = view.findViewById(R.id.poster);
            poster.setBackgroundResource(posterList.get(position).getSourceId());
            //poster.setImageResource(posterList.get(position).getSourceId());
            /*switch (List.get(position).Number) {
                case 1:
                    List<OwnItem> ownItems1 = new ArrayList<>();
                    for (OwnItem ownItem : ownItems) {
                        if (ownItem.getType() == 1) {
                            ownItems1.add(ownItem);
                        }
                    }
                    LinearLayoutManager layoutManager1 = new LinearLayoutManager(Bag.this);
                    recyclerView.setLayoutManager(layoutManager1);
                    ItemAdapter adapter1 = new ItemAdapter(ownItems1);
                    recyclerView.setAdapter(adapter1);
                    break;
                case 2:
                    List<OwnItem> ownItems2 = new ArrayList<>();
                    for (OwnItem ownItem : ownItems) {
                        if (ownItem.getType() == 2) {
                            ownItems2.add(ownItem);
                        }
                    }
                    LinearLayoutManager layoutManager2 = new LinearLayoutManager(Bag.this);
                    recyclerView.setLayoutManager(layoutManager2);
                    ItemAdapter adapter2 = new ItemAdapter(ownItems2);
                    recyclerView.setAdapter(adapter2);
                    break;
                case 3:
                    List<OwnItem> ownItems3 = new ArrayList<>();
                    for (OwnItem ownItem : ownItems) {
                        if (ownItem.getType() == 3) {
                            ownItems3.add(ownItem);
                        }
                    }
                    LinearLayoutManager layoutManager3 = new LinearLayoutManager(Bag.this);
                    recyclerView.setLayoutManager(layoutManager3);
                    ItemAdapter adapter3 = new ItemAdapter(ownItems3);
                    recyclerView.setAdapter(adapter3);
                    break;
                case 4:
                    List<OwnItem> ownItems4 = new ArrayList<>();
                    for (OwnItem ownItem : ownItems) {
                        if (ownItem.getType() == 4) {
                            ownItems4.add(ownItem);
                        }
                    }
                    LinearLayoutManager layoutManager4 = new LinearLayoutManager(Bag.this);
                    recyclerView.setLayoutManager(layoutManager4);
                    ItemAdapter adapter4 = new ItemAdapter(ownItems4);
                    recyclerView.setAdapter(adapter4);
                    break;
            }*/
            container.addView(view);
            return view;
        }
    }

    private class RecommendProjectsAdapter extends RecyclerView.Adapter<RecommendProjectsAdapter.ViewHolder> {
        private List<Project> projectsList;

        private RecommendProjectsAdapter(List<Project> projectsList) {
            this.projectsList = projectsList;
        }

        @Override
        public RecommendProjectsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recommendproject, parent, false);
            final RecommendProjectsAdapter.ViewHolder holder = new RecommendProjectsAdapter.ViewHolder(view);
            /*holder.recommendProjectPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });*/
            return holder;
        }

        @Override
        public void onBindViewHolder(RecommendProjectsAdapter.ViewHolder holder, int position) {
            Project project = projectsList.get(position);
            Log.i(String.valueOf(position), project.title);
            holder.recommendProjectText.setText(project.title);
        }

        @Override
        public int getItemCount() {
            return projectsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView recommendProjectPic;
            private TextView recommendProjectText;

            private ViewHolder(View view) {
                super(view);
                recommendProjectPic = view.findViewById(R.id.projectPic);
                recommendProjectText = view.findViewById(R.id.projectText);
            }
        }


    }

    private class TravelJournalsAdapter extends RecyclerView.Adapter<TravelJournalsAdapter.ViewHolder> {
        private List<TravelJournal> travelJournalsList;

        private TravelJournalsAdapter(List<TravelJournal> travelJournalsList) {
            this.travelJournalsList = travelJournalsList;
        }

        @Override
        public TravelJournalsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_traveljournal, parent, false);
            final TravelJournalsAdapter.ViewHolder holder = new TravelJournalsAdapter.ViewHolder(view);
            holder.travelJournalView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(TravelJournalsAdapter.ViewHolder holder, int position) {
            TravelJournal travelJournal = travelJournalsList.get(position);
            Log.i(String.valueOf(position), "TravelJournal" + travelJournal.title);
            holder.travelJournalTitle.setText(travelJournal.title);
            holder.travelJournalSubtitle.setText(travelJournal.subTitle);
        }

        @Override
        public int getItemCount() {
            return travelJournalsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView travelJournalTitle;
            private TextView travelJournalSubtitle;
            private View travelJournalView;

            private ViewHolder(View view) {
                super(view);
                travelJournalTitle = view.findViewById(R.id.travelJournalTitle);
                travelJournalSubtitle = view.findViewById(R.id.travelJournalSubtitle);
                travelJournalView = view.findViewById(R.id.travelJournalBackground);
            }
        }
    }
}
