package com.gama.farm_fun;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.SaveCallback;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String userId;

    private ImageView map;

    private ViewPager viewPager;
    private PosterPagerAdapter posterPagerAdapter;
    private List<Poster> posterList;
    private ViewPagerIndicator indicator;

    private TextView locationText;
    private String locationString;
    private ImageView locationPic;
    private LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();

    private ImageView pick;
    private ImageView drift;
    private ImageView homeStay;
    private ImageView restaurant;
    private ImageView fishing;
    private ImageView barbecue;
    private ImageView ktv;
    private ImageView chess;
    private ImageView sightseeing;
    private ImageView all;

    public View bottomBar;
    public Button homePage;
    public TextView homePageText;
    public Button post;
    public Button news;
    public Button mine;
    public Button order;

    public RecyclerView recommendProjectsRecycler;
    List<RecommendProject> recommendProjectsList;

    public RecyclerView travelJournalRecycler;
    List<TravelJournal> travelJournalsList = null;

    private View postPanel;
    private Button postBack;
    private ImageView postComment;
    private ImageView postCustomized;
    private ImageView postJournal;
    private TextView postCustomizedText;
    private TextView postCommentText;
    private TextView postJournalText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

        StartLocateService();

        AVObject testObject = new AVObject("TestObject");
        testObject.put("words", "Hello World!");
        testObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Log.d("saved", "success!");
                }
            }
        });

        locationPic = findViewById(R.id.locationPic);
        locationPic.setOnClickListener(this);

        Log.i("test1", "success");
        travelJournalRecycler = findViewById(R.id.travelJournalRecyclerView);
        travelJournalRecycler.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        travelJournalRecycler.setLayoutManager(linearLayoutManager2);
        Log.i("test2", "success");
        travelJournalsList = new ArrayList<>();

        recommendProjectsRecycler = findViewById(R.id.recommendProjectsRecyclerView);
        recommendProjectsRecycler.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        recommendProjectsRecycler.setLayoutManager(linearLayoutManager1);
        recommendProjectsList = new ArrayList<>();

        getPoster();
    }

    public void getPoster() {
        posterList = new ArrayList<>();

        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereStartsWith("name", "poster");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                for (AVObject avObject : avObjects) {
                    Poster poster = new Poster(avObject.getString("url"), "");
                    posterList.add(poster);
                }
                Log.i("avObjectsList", String.valueOf(avObjects.size()));
                viewPager = findViewById(R.id.viewPager);
                posterPagerAdapter = new PosterPagerAdapter(posterList);
                viewPager.setAdapter(posterPagerAdapter);
                viewPager.setPageTransformer(true, new com.gama.farm_fun.ScalePageTransformer());
                indicator = findViewById(R.id.indicator);
                indicator.setLength(posterList.size());
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        indicator.setSelected(position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

                getUserInformation();
            }
        });


    }

    public void getUserInformation() {
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        userId = sharedPreferences.getString("id", "tourist");
        Log.i("userId", userId);
        getRecommendProject();
    }

    public void getRecommendProject() {
        AVQuery<AVObject> query = new AVQuery<>("RecommendProject");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                for (AVObject avObject : avObjects) {
                    RecommendProject recommendProject = new RecommendProject(avObject.getString("name"),
                            avObject.getString("type"),
                            avObject.getString("picName"));
                    recommendProjectsList.add(recommendProject);
                }
                RecommendProjectsAdapter recommendProjectsAdapter = new RecommendProjectsAdapter(recommendProjectsList);
                recommendProjectsRecycler.setAdapter(recommendProjectsAdapter);

                getTravelJournal();
            }
        });
    }

    public void getTravelJournal() {
        AVQuery<AVObject> query = new AVQuery<>("TravelJournal");
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                for (AVObject avObject : avObjects) {
                    TravelJournal travelJournal = new TravelJournal(avObject.getInt("number"),
                            avObject.getString("title"),
                            avObject.getString("subTitle"),
                            "journal" + String.valueOf(avObject.getInt("number")) + ":1.jpg");
                    Log.i("journal", travelJournal.firstPic);
                    travelJournalsList.add(travelJournal);
                    if (travelJournalsList.size() > 5) {
                        break;
                    }
                }
                TravelJournalsAdapter travelJournalsAdapter = new TravelJournalsAdapter(travelJournalsList);
                travelJournalRecycler.setAdapter(travelJournalsAdapter);

                initUI();
            }
        });
    }

    public void initUI() {
        map = findViewById(R.id.map);
        map.setOnClickListener(this);

        pick = findViewById(R.id.pick);
        pick.setOnClickListener(this);
        drift = findViewById(R.id.drift);
        drift.setOnClickListener(this);
        homeStay = findViewById(R.id.homeStay);
        homeStay.setOnClickListener(this);
        restaurant = findViewById(R.id.restaurant);
        restaurant.setOnClickListener(this);
        fishing = findViewById(R.id.fishing);
        fishing.setOnClickListener(this);
        barbecue = findViewById(R.id.barbecue);
        barbecue.setOnClickListener(this);
        ktv = findViewById(R.id.ktv);
        ktv.setOnClickListener(this);
        chess = findViewById(R.id.chess);
        chess.setOnClickListener(this);
        sightseeing = findViewById(R.id.sightseeing);
        sightseeing.setOnClickListener(this);
        all = findViewById(R.id.all);
        all.setOnClickListener(this);

        bottomBar = findViewById(R.id.bottom_bar);
        homePage = bottomBar.findViewById(R.id.homePage);
        homePage.setOnClickListener(this);
        homePage.setBackground(getResources().getDrawable(R.drawable.homepage1));
        homePageText = bottomBar.findViewById(R.id.homePageText);
        homePageText.setTextColor(getResources().getColor(R.color.colorTheme));
        post = bottomBar.findViewById(R.id.post);
        post.setOnClickListener(this);
        news = bottomBar.findViewById(R.id.news);
        news.setOnClickListener(this);
        order = bottomBar.findViewById(R.id.onlineShop);
        order.setOnClickListener(this);
        mine = bottomBar.findViewById(R.id.mine);
        mine.setOnClickListener(this);

        postPanel = findViewById(R.id.panel_post);
        postPanel.setOnClickListener(this);
        postBack = postPanel.findViewById(R.id.post_back);
        postBack.setOnClickListener(this);
        postComment = postPanel.findViewById(R.id.post_comment);
        postComment.setOnClickListener(this);
        postCustomized = postPanel.findViewById(R.id.post_customized);
        postCustomized.setOnClickListener(this);
        postJournal = postPanel.findViewById(R.id.post_journal);
        postJournal.setOnClickListener(this);
        postCommentText = postPanel.findViewById(R.id.comment_text);
        postCustomizedText = postPanel.findViewById(R.id.customized_text);
        postJournalText = postPanel.findViewById(R.id.journal_text);
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

        option.setWifiCacheTimeOut(5 * 60 * 1000);

        option.setEnableSimulateGps(false);

        option.setIsNeedAddress(true);

        option.setIsNeedLocationDescribe(true);

        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {

            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            float radius = location.getRadius();    //获取定位精度，默认值为0.0f

            String coorType = location.getCoorType();

            int errorCode = location.getLocType();

            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息
            String locationDescribe = location.getLocationDescribe();
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
                Intent intent = new Intent(MainActivity.this, OnlineShopActivity.class);
                intent.putExtra("UserId", userId);
                startActivity(intent);
                break;
            case R.id.pick:
                Intent pickIntent = new Intent(MainActivity.this, AmusementActivity.class);
                pickIntent.putExtra("Type", "pick");
                pickIntent.putExtra("UserId", userId);
                startActivity(pickIntent);
                break;
            case R.id.drift:
                Intent driftIntent = new Intent(MainActivity.this, AmusementActivity.class);
                driftIntent.putExtra("Type", "drift");
                driftIntent.putExtra("UserId", userId);
                startActivity(driftIntent);
                break;
            case R.id.homeStay:
                Intent homeStayIntent = new Intent(MainActivity.this, HomeStayActivity.class);
                homeStayIntent.putExtra("UserId", userId);
                startActivity(homeStayIntent);
                break;
            case R.id.restaurant:
                Intent restaurantIntent = new Intent(MainActivity.this, RestaurantActivity.class);
                restaurantIntent.putExtra("UserId", userId);
                startActivity(restaurantIntent);
                break;
            case R.id.fishing:
                Intent fishingIntent = new Intent(MainActivity.this, AmusementActivity.class);
                fishingIntent.putExtra("Type", "fishing");
                fishingIntent.putExtra("UserId", userId);
                startActivity(fishingIntent);
                break;
            case R.id.barbecue:
                Intent barbecueIntent = new Intent(MainActivity.this, AmusementActivity.class);
                barbecueIntent.putExtra("Type","barbecue");
                barbecueIntent.putExtra("UserId", userId);
                startActivity(barbecueIntent);
                break;
            case R.id.mine:
                Intent mineIntent = new Intent(MainActivity.this, MineActivity.class);
                mineIntent.putExtra("UserId", userId);
                startActivity(mineIntent);
                break;
            case R.id.onlineShop:
                Intent orderIntent = new Intent(MainActivity.this, OnlineShopActivity.class);
                orderIntent.putExtra("UserId", userId);
                startActivity(orderIntent);
                break;
            case R.id.ktv:
                Intent ktvIntent = new Intent(MainActivity.this, AmusementActivity.class);
                ktvIntent.putExtra("UserId", userId);
                ktvIntent.putExtra("Type", "ktv");
                startActivity(ktvIntent);
                break;
            case R.id.chess:
                Intent chessIntent = new Intent(MainActivity.this, AmusementActivity.class);
                chessIntent.putExtra("UserId", userId);
                chessIntent.putExtra("Type", "chess");
                startActivity(chessIntent);
                break;
            case R.id.sightseeing:
                Intent sightSeeingIntent = new Intent(MainActivity.this, AmusementActivity.class);
                sightSeeingIntent.putExtra("UserId", userId);
                sightSeeingIntent.putExtra("Type", "sightseeing");
                startActivity(sightSeeingIntent);
                break;
            case R.id.all:
            case R.id.news:
                Intent newsIntent = new Intent(MainActivity.this, NewsActivity.class);
                newsIntent.putExtra("UserId", userId);
                startActivity(newsIntent);
                break;
            case R.id.map:
                Intent mapIntent = new Intent(MainActivity.this, ScenicMapActivity.class);
                startActivity(mapIntent);
                break;
            case R.id.post:
                postCommentText.setVisibility(View.INVISIBLE);
                postCustomizedText.setVisibility(View.INVISIBLE);
                postJournalText.setVisibility(View.INVISIBLE);
                postPanel.setVisibility(View.VISIBLE);
                postPanel.getBackground().setAlpha(240);
                ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(postComment,
                        "translationY", 700, 0);
                objectAnimator1.setDuration(1000);
                objectAnimator1.setInterpolator(new OvershootInterpolator());
                objectAnimator1.start();

                ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(postCustomized,
                        "translationY", 700, 0);
                objectAnimator2.setDuration(1000);
                objectAnimator2.setInterpolator(new OvershootInterpolator());
                objectAnimator2.start();

                ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(postJournal,
                        "translationY", 700, 0);
                objectAnimator3.setDuration(1000);
                objectAnimator3.setInterpolator(new OvershootInterpolator());
                objectAnimator3.start();
                objectAnimator3.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        postCommentText.setVisibility(View.VISIBLE);
                        postCustomizedText.setVisibility(View.VISIBLE);
                        postJournalText.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                ObjectAnimator objectAnimator4 = ObjectAnimator.ofFloat(postBack,
                        "rotation", 0, 45);
                objectAnimator4.setDuration(500);
                objectAnimator4.setInterpolator(new OvershootInterpolator());
                objectAnimator4.start();

                ObjectAnimator objectAnimator5 = ObjectAnimator.ofFloat(postCommentText,
                        "alpha", 0, 1);
                objectAnimator5.setDuration(500);
                objectAnimator5.setStartDelay(1000);
                objectAnimator5.setInterpolator(new OvershootInterpolator());
                objectAnimator5.start();

                ObjectAnimator objectAnimator6 = ObjectAnimator.ofFloat(postCustomizedText,
                        "alpha", 0, 1);
                objectAnimator6.setDuration(500);
                objectAnimator6.setStartDelay(1000);
                objectAnimator6.setInterpolator(new OvershootInterpolator());
                objectAnimator6.start();

                ObjectAnimator objectAnimator7 = ObjectAnimator.ofFloat(postJournalText,
                        "alpha", 0, 1);
                objectAnimator7.setDuration(500);
                objectAnimator7.setStartDelay(1000);
                objectAnimator7.setInterpolator(new OvershootInterpolator());
                objectAnimator7.start();
                break;
            case R.id.post_back:
                postPanel.setVisibility(View.INVISIBLE);
                break;
            case R.id.post_customized:
                Intent intent1 = new Intent(MainActivity.this, CustomizedActivity.class);
                intent1.putExtra("UserId", userId);
                startActivity(intent1);
                break;
            case R.id.post_comment:
                Intent intent2 = new Intent(MainActivity.this, MyOrder.class);
                intent2.putExtra("UserId", userId);
                intent2.putExtra("Type", "comment");
                startActivity(intent2);
                break;
        }
    }

    private class PosterPagerAdapter extends android.support.v4.view.PagerAdapter {

        List<Poster> posterList;

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
            SimpleDraweeView poster = view.findViewById(R.id.poster);
            Uri uri = Uri.parse(posterList.get(position).url);
            poster.setImageURI(uri);
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
        private List<RecommendProject> projectsList;

        private RecommendProjectsAdapter(List<RecommendProject> projectsList) {
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
        public void onBindViewHolder(final RecommendProjectsAdapter.ViewHolder holder, int position) {
            RecommendProject recommendProject = recommendProjectsList.get(position);
            Log.i(String.valueOf(position), recommendProject.name);
            holder.recommendProjectText.setText(recommendProject.name);
            AVQuery<AVObject> query = new AVQuery<>("_File");
            query.whereEqualTo("name", recommendProject.picName);
            query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject object, AVException e) {
                    AVFile avFile = new AVFile("test.jpg", object.getString("url"), new HashMap<String, Object>());
                    avFile.getThumbnailUrl(true, 100, 120);
                    avFile.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, AVException e) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            holder.recommendProjectPic.setImageBitmap(bitmap);
                        }
                    });
                }
            });
            holder.setType(recommendProject.type);
            holder.recommendProjectPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.type.equals("HomeStay")) {
                        Intent intent = new Intent(MainActivity.this, HomeStayActivity.class);
                        intent.putExtra("UserId", userId);
                        startActivity(intent);
                    } else if (holder.type.equals("Restaurant")) {
                        Intent intent = new Intent(MainActivity.this, RestaurantActivity.class);
                        intent.putExtra("UserId", userId);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, AmusementActivity.class);
                        intent.putExtra("UserId", userId);
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return projectsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView recommendProjectPic;
            private TextView recommendProjectText;
            private String type;

            private ViewHolder(View view) {
                super(view);
                recommendProjectPic = view.findViewById(R.id.projectPic);
                recommendProjectText = view.findViewById(R.id.projectText);
            }

            private void setType(String type) {
                this.type = type;
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
        public void onBindViewHolder(final TravelJournalsAdapter.ViewHolder holder, int position) {
            TravelJournal travelJournal = travelJournalsList.get(position);
            Log.i(String.valueOf(position), "TravelJournal" + travelJournal.title);
            holder.travelJournalTitle.setText(travelJournal.title);
            holder.travelJournalSubtitle.setText(travelJournal.subTitle);
            holder.setNumber(travelJournal.number);
            AVQuery<AVObject> query = new AVQuery<>("_File");
            query.whereEqualTo("name", travelJournal.firstPic);
            query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject object, AVException e) {
                    Uri uri = Uri.parse(object.getString("url"));
                    holder.travelJournalView.setImageURI(uri);
                    RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);
                    holder.travelJournalView.getHierarchy().setRoundingParams(roundingParams);
                }
            });

            holder.travelJournalView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, TravelJournalActivity.class);
                    intent.putExtra("number", holder.number);
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return travelJournalsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView travelJournalTitle;
            private TextView travelJournalSubtitle;
            private SimpleDraweeView travelJournalView;
            private int number;

            private ViewHolder(View view) {
                super(view);
                travelJournalTitle = view.findViewById(R.id.travelJournalTitle);
                travelJournalSubtitle = view.findViewById(R.id.travelJournalSubtitle);
                travelJournalView = view.findViewById(R.id.travelJournalBackground);
            }

            public void setNumber(int number) {
                this.number = number;
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        userId = sharedPreferences.getString("id", "tourist");
        Log.i("userId", userId);
    }
}
