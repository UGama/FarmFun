package com.gama.farm_fun;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.animation.ObjectAnimator.ofFloat;

public class RestaurantActivity extends AppCompatActivity implements View.OnClickListener, BaiduMap.OnMapClickListener {
    private String userId;

    public int screenWidth;
    public int screenHeight;

    private String type;
    private String projectNameString;
    private String url;
    private String addressString;

    private View topBar;
    private float alphaStorage;
    private TextView title;
    private ImageView titleIcon;
    private TextView firstSubTitle;
    private TextView secondSubTitle;
    private TextView thirdSubTitle;
    private TextView fourthSubTitle;
    private Button back;

    private ObservableScrollView observableScrollView;
    private ImageView mainPic;
    private TextView projectName;
    private TextView describe;
    private TextView address;
    private TextView Seat;
    private TextView allSeatType;
    private RecyclerView seatRecyclerView;
    private List<Seat> seatList;
    private int seatNumber;

    private RecyclerView commentRecyclerView;
    private List<Comment> commentList;
    private int commentNumber;

    private MapView mapView;
    private Double longitude;
    private Double latitude;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        Intent intent = getIntent();

        type = "Restaurant";
        userId = intent.getStringExtra("UserId");

        getWindowInformation();
    }

    public void getWindowInformation() {
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;         // 屏幕宽度（像素）
        screenHeight = displayMetrics.heightPixels;       // 屏幕高度（像素）
        float density = displayMetrics.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = displayMetrics.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        float width = screenWidth / density;  // 屏幕宽度(dp)
        float height = screenHeight / density;// 屏幕高度(dp)
        getProject();
    }

    public void getProject() {
        Log.i("initData", "Start");
        AVQuery<AVObject> query = new AVQuery<>("Restaurant");
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                projectNameString = object.getString("name");
                AVGeoPoint avGeoPoint = object.getAVGeoPoint("locate");
                latitude = avGeoPoint.getLatitude();
                longitude = avGeoPoint.getLongitude();

                Log.i("longitude", String.valueOf(longitude));

                Log.i("ProjectName", projectNameString);
                initUI();
            }
        });

    }

    public void initUI() {
        topBar = findViewById(R.id.topBar);
        topBar.setVisibility(View.VISIBLE);
        topBar.setAlpha(0);
        alphaStorage = 0f;
        title = topBar.findViewById(R.id.title);
        title.setText("餐饮");
        titleIcon = topBar.findViewById(R.id.titleIcon);
        titleIcon.setImageResource(R.drawable.amusementtitle);
        firstSubTitle = topBar.findViewById(R.id.firstSubTitle);
        secondSubTitle = topBar.findViewById(R.id.secondSubTitle);
        thirdSubTitle = topBar.findViewById(R.id.thirdSubTitle);
        fourthSubTitle = topBar.findViewById(R.id.fourthSubTitle);
        firstSubTitle.setText("餐厅概况");
        secondSubTitle.setText("桌位预订");
        thirdSubTitle.setText("用户评论");
        fourthSubTitle.setText("地图导览");
        back = topBar.findViewById(R.id.back);
        back.setOnClickListener(this);

        observableScrollView = findViewById(R.id.observableScrollView);
        mainPic = findViewById(R.id.mainPic);
        projectName = findViewById(R.id.projectName);
        describe = findViewById(R.id.describe);
        address = findViewById(R.id.address);
        Seat = findViewById(R.id.seat);
        Seat.setText("桌位预订");
        allSeatType = findViewById(R.id.allSeatType);
        allSeatType.setOnClickListener(this);
        seatList = new ArrayList<>();
        seatRecyclerView = findViewById(R.id.seatRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        seatRecyclerView.setLayoutManager(linearLayoutManager);
        setObservableScrollView();

        commentRecyclerView = findViewById(R.id.commentRecyclerView);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        commentRecyclerView.setLayoutManager(linearLayoutManager1);
        commentList = new ArrayList<>();

        getProjectInformation();
    }

    public void setObservableScrollView() {
        observableScrollView.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy) {
                int[] mainPicLocationOnScreen = new int[2];
                mainPic.getLocationOnScreen(mainPicLocationOnScreen);
                int mainPicY = mainPicLocationOnScreen[1] - getStatusBarHeight();
                if (-mainPicY >= mainPic.getBottom() - topBar.getBottom()) {
                    topBar.setVisibility(View.VISIBLE);
                } else if (-mainPicY < mainPic.getBottom() - topBar.getBottom()) {
                    float alpha = (float) (-mainPicY) / (mainPic.getBottom() - topBar.getBottom());
                    ObjectAnimator objectAnimator = ofFloat(topBar, "alpha", alphaStorage, alpha);
                    alphaStorage = alpha;
                    objectAnimator.setDuration(100);
                    objectAnimator.start();
                }
            }
        });
    }

    public void getProjectInformation() {
        AVQuery<AVObject> projectQuery = new AVQuery<>("Restaurant");
        projectQuery.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                projectName.setText(object.getString("name"));
                describe.setText(object.getString("describe"));
                address.setText(object.getString("locateDescribe"));
                addressString = object.getString("locateDescribe");
                loadMainPic();
            }
        });
    }

    public void loadMainPic() {
        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereEqualTo("name", "restaurantmain.jpg");
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                AVFile avFile = new AVFile("Type.png", object.getString("url"), new HashMap<String, Object>());
                url = object.getString("url");
                Log.i("Url", object.getString("url"));
                avFile.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, AVException e) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Log.i("bitmap(width/height)", String.valueOf(bitmap.getWidth()) + "/" + String.valueOf(bitmap.getHeight()));
                        mainPic.setImageBitmap(bitmap);

                    }
                }, new ProgressCallback() {
                    @Override
                    public void done(Integer percentDone) {
                        if (percentDone == 100) {
                            getSeat();
                        }
                    }
                });
            }
        });
    }

    public void getSeat() {
        seatNumber = 0;
        AVQuery<AVObject> seatQuery = new AVQuery<>("Seat");
        seatQuery.orderByAscending("seatNumber");
        seatQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                allSeatType.setText("全部8种座位");
                for (AVObject avObject : avObjects) {
                    if (seatNumber < 2) {
                        Seat lunchSeat = new Seat(avObject.getString("picName"),
                                avObject.getString("type"),
                                avObject.getString("describe"),
                                "中");
                        seatList.add(lunchSeat);
                        Log.i("test1", lunchSeat.seatType + " " + lunchSeat.picName + " " + lunchSeat.describe + " " + lunchSeat.meal);
                        Seat dinnerSeat = new Seat(avObject.getString("picName"),
                                avObject.getString("type"),
                                avObject.getString("describe"),
                                "晚");
                        seatList.add(dinnerSeat);
                        seatNumber++;
                    } else {
                        break;
                    }

                }
                Log.i("SeatList.size", String.valueOf(seatList.size()));
                seatRecyclerView = findViewById(R.id.seatRecyclerView);
                LinearLayoutManager linearLayout = new LinearLayoutManager(RestaurantActivity.this);
                seatRecyclerView.setLayoutManager(linearLayout);
                SeatAdapter seatAdapter = new SeatAdapter(seatList);
                seatRecyclerView.setAdapter(seatAdapter);

                getComment();

            }
        });

    }

    @Override
    public void onMapClick(LatLng latLng) {

        Intent intent = new Intent(RestaurantActivity.this, MapActivity.class);
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        intent.putExtra("project", projectNameString);
        intent.putExtra("address", addressString);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    private class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.ViewHolder> {
        private List<Seat> seatsList;

        private SeatAdapter(List<Seat> seatsList) {
            this.seatsList = seatsList;
        }

        @Override
        public SeatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_seat, parent, false);
            final SeatAdapter.ViewHolder holder = new SeatAdapter.ViewHolder(view);
            /*holder.recommendProjectPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });*/
            return holder;
        }

        @Override
        public void onBindViewHolder(final SeatAdapter.ViewHolder holder, int position) {
            Seat seat = seatsList.get(position);
            Log.i(String.valueOf(position), seat.seatType);
            holder.seatType.setText(seat.seatType);
            holder.meal.setText("(" + seat.meal + ")");
            holder.describe.setText(seat.describe);
            final ImageView seatPic = holder.seatPic;
            AVQuery<AVObject> query = new AVQuery<>("_File");
            query.whereEqualTo("name", seat.picName);
            query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject object, AVException e) {
                    AVFile avFile = new AVFile("seat.png", object.getString("url"), new HashMap<String, Object>());
                    holder.setUrl(object.getString("url"));
                    avFile.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, AVException e) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            seatPic.setImageBitmap(bitmap);
                        }
                    }, new ProgressCallback() {
                        @Override
                        public void done(Integer percentDone) {
                            if (percentDone == 100) {

                            }
                        }
                    });
                }
            });
            holder.price.setText("20");

        }

        @Override
        public int getItemCount() {
            return seatsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView seatType;
            private TextView meal;
            private ImageView seatPic;
            private TextView describe;
            private TextView price;
            private String url;

            private ViewHolder(View view) {
                super(view);
                seatType = view.findViewById(R.id.seatType);
                meal = view.findViewById(R.id.meal);
                seatPic = view.findViewById(R.id.seatPic);
                describe = view.findViewById(R.id.describe);
                price = view.findViewById(R.id.price);

            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }

    public void getComment() {
        AVQuery<AVObject> query = new AVQuery<>("Comment");
        query.whereEqualTo("type", "Restaurant");
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                for (AVObject avObject : avObjects) {
                    Comment comment = new Comment(avObject.getString("userId"),
                            avObject.getString("comment"),
                            avObject.getInt("rank"),
                            avObject.getString("item"),
                            String.valueOf(avObject.getDate("createdAt")));
                    Log.i("userId", comment.userId);
                    Log.i("comment", avObject.getString("comment"));
                    Log.i("createdAt", comment.createdAt);
                    commentList.add(comment);
                    if (commentList.size() > 4) {
                        break;
                    }
                }
                getUserName();
            }
        });
    }
    public void getUserName() {
        commentNumber = 0;
        Log.i("commentList.size()", String.valueOf(commentList.size()));
        for (int i = 0; i < commentList.size(); i++) {
            AVQuery<AVObject> query = new AVQuery<>("Users");
            query.whereEqualTo("objectId", commentList.get(i).userId);
            query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject object, AVException e) {
                    commentList.get(commentNumber).setUserName(object.getString("netName"));
                    Log.i("netName", commentList.get(commentNumber).userName);
                    commentNumber++;
                    if (commentNumber == commentList.size()) {
                        setCommentRecyclerView();
                    }
                }
            });
        }

    }

    public void setCommentRecyclerView() {
        RestaurantActivity.CommentAdapter commentAdapter = new RestaurantActivity.CommentAdapter(commentList);
        commentRecyclerView.setAdapter(commentAdapter);
        loadMapView();
    }
    public void loadMapView() {
        mapView = findViewById(R.id.mapView);
        BaiduMap baiduMap = mapView.getMap();
        LatLng latLng = new LatLng(latitude, longitude);
        Log.i("longitude2", String.valueOf(longitude));
        MapStatus mapStatus = new MapStatus.Builder().target(latLng).zoom(18).build();

        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        baiduMap.setMapStatus(mapStatusUpdate);

        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
                .fromResource(R.drawable.locate_overlay);
        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(latLng)
                .icon(bitmapDescriptor).zIndex(8).draggable(true);
        baiduMap.addOverlay(option);

        baiduMap.setOnMapClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.allSeatType:
                Intent allSeatIntent = new Intent(RestaurantActivity.this, RestaurantTicketActivity.class);
                allSeatIntent.putExtra("UserId", userId);
                startActivity(allSeatIntent);
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private class CommentAdapter extends RecyclerView.Adapter<RestaurantActivity.CommentAdapter.ViewHolder> {
        private List<Comment> commentList;

        private CommentAdapter(List<Comment> commentList) {
            this.commentList = commentList;
        }

        @Override
        public RestaurantActivity.CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_comment, parent, false);
            final RestaurantActivity.CommentAdapter.ViewHolder holder = new RestaurantActivity.CommentAdapter.ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            /*holder.recommendProjectPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });*/
            return holder;
        }

        @Override
        public void onBindViewHolder(RestaurantActivity.CommentAdapter.ViewHolder holder, int position) {
            Comment comment = commentList.get(position);
            holder.userName.setText(comment.userName);
            holder.comment.setText(comment.comment);
            holder.item.setText(comment.item);
            holder.time.setText(comment.createdAt);
            holder.setRank(comment.rank);
            if (holder.rank == 1) {
                holder.star1.setBackground(getResources().getDrawable(R.drawable.star2));
            } else if (holder.rank == 2) {
                holder.star1.setBackground(getResources().getDrawable(R.drawable.star2));
                holder.star2.setBackground(getResources().getDrawable(R.drawable.star2));
            } else if (holder.rank == 3) {
                holder.star1.setBackground(getResources().getDrawable(R.drawable.star2));
                holder.star2.setBackground(getResources().getDrawable(R.drawable.star2));
                holder.star3.setBackground(getResources().getDrawable(R.drawable.star2));
            } else if (holder.rank == 4) {
                holder.star1.setBackground(getResources().getDrawable(R.drawable.star2));
                holder.star2.setBackground(getResources().getDrawable(R.drawable.star2));
                holder.star3.setBackground(getResources().getDrawable(R.drawable.star2));
                holder.star4.setBackground(getResources().getDrawable(R.drawable.star2));
            } else {
                holder.star1.setBackground(getResources().getDrawable(R.drawable.star2));
                holder.star2.setBackground(getResources().getDrawable(R.drawable.star2));
                holder.star3.setBackground(getResources().getDrawable(R.drawable.star2));
                holder.star4.setBackground(getResources().getDrawable(R.drawable.star2));
                holder.star5.setBackground(getResources().getDrawable(R.drawable.star2));
            }
        }

        @Override
        public int getItemCount() {
            return commentList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView userName;
            private TextView comment;
            private int rank;
            private TextView item;
            private TextView time;
            private ImageView star1;
            private ImageView star2;
            private ImageView star3;
            private ImageView star4;
            private ImageView star5;

            private ViewHolder(View view) {
                super(view);
                userName = view.findViewById(R.id.userName);
                comment = view.findViewById(R.id.commentText);
                item = view.findViewById(R.id.item);
                time = view.findViewById(R.id.time);

                star1 = view.findViewById(R.id.star1);
                star2 = view.findViewById(R.id.star2);
                star3 = view.findViewById(R.id.star3);
                star4 = view.findViewById(R.id.star4);
                star5 = view.findViewById(R.id.star5);
            }

            public void setRank(int rank) {
                this.rank = rank;
            }
        }


    }

}
