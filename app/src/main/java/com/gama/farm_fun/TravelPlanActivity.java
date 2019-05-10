package com.gama.farm_fun;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class TravelPlanActivity extends AppCompatActivity implements View.OnClickListener {
    private String userId;
    private int children;
    private int adults;
    private String[] tabs;
    private String[] newTabs;
    private int startDay;
    private int endDay;
    private int dayCount;
    private int persons;

    private int screenWidth;
    private int screenHeight;

    private View topBar;
    private TextView title;
    private Button back;

    private TextView travelDate;

    private boolean haveRestaurant;
    private List<TravelPlan> travelPlanList;
    private List<SimpleTicket> simpleTicketList;
    private RecyclerView travelPlanRecyclerView;
    private int picSupport;
    private int travelPlanSupport;

    private List<TravelPlan> homeStayList;
    private RecyclerView homeStayRecyclerView;
    private List<SimpleTicket> roomList;

    private List<TravelPlan> restaurantList;
    private RecyclerView restaurantRecyclerView;
    private List<SimpleTicket> seatList;

    private List<SimpleTicket> allList;

    private Button order;
    private String[] names;
    private int[] counts;
    private String[] types;
    private int[] prices;

    private Toast toast;

    private View loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_travel_plan);

        loading = findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        userId = intent.getStringExtra("UserId");
        children = intent.getIntExtra("children", 0);
        adults = intent.getIntExtra("adults", 0);
        persons = children + adults;
        tabs = intent.getStringArrayExtra("tabs");
        showTabs(tabs, "initTabs");
        startDay = intent.getIntExtra("startTime",0);
        endDay = intent.getIntExtra("endTime",0);
        dayCount = intent.getIntExtra("days", 0);
        Log.i("dayCount", String.valueOf(dayCount));
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
        float width = screenWidth / density;  // 屏幕宽度(dp)
        float height = screenHeight / density;// 屏幕高度(dp)
        Log.i("width/height(px)", String.valueOf(screenWidth) + "/" + String.valueOf(screenHeight));
        Log.i("width/height(dp)", String.valueOf(width) + "/" + String.valueOf(height));

        initUI();
    }

    public void initUI() {
        topBar = findViewById(R.id.bar_top);
        title = topBar.findViewById(R.id.title);
        title.setText("生成旅行计划");
        back = topBar.findViewById(R.id.back);
        back.setOnClickListener(this);

        travelDate = findViewById(R.id.travel_date);
        travelDate.setText("（6月" + String.valueOf(startDay) + "日 至 6月" + String.valueOf(endDay) + "日）");

        allList = new ArrayList<>();

        order = findViewById(R.id.order);
        order.setOnClickListener(this);

        initData();
    }

    public void initData() {
        if (checkRestaurant()) {
            haveRestaurant = true;
            removeRestaurant();
            if (getTabsCount() > dayCount) {
                showTabs(tabs, "WhetherRemoveRestaurant");
                Log.i("getTabsCount", String.valueOf(getTabsCount()));
                newTabs = getTabsRandom(dayCount, getTabsCount());
                showTabs(newTabs, "newTabs");
            } else if (getTabsCount() < dayCount) {
                newTabs = addTabs(dayCount, getTabsCount());
                showTabs(newTabs, "newTabs");
            } else {
                newTabs = transferTabs(dayCount);
                showTabs(newTabs, "transferNewTabs");
            }
        } else {
            haveRestaurant = false;
            if (getTabsCount() > dayCount) {
                newTabs = getTabsRandom(dayCount, getTabsCount());
                showTabs(newTabs, "newTabs");
            } else if (getTabsCount() < dayCount) {
                newTabs = addTabs(dayCount, getTabsCount());
                showTabs(newTabs, "newTabs");
            } else {
                newTabs = transferTabs(dayCount);
                showTabs(newTabs, "transferNewTabs");
            }
        }
        getProjectInformation();
    }

    public void getProjectInformation() {
        travelPlanList = new ArrayList<>();
        travelPlanSupport = 0;
        for (int i = 0; i < newTabs.length; i++) {
            AVQuery<AVObject> query = new AVQuery<>("Amusement");
            query.whereEqualTo("type", newTabs[i]);
            query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject object, AVException e) {
                    TravelPlan travelPlan = new TravelPlan(travelPlanSupport + 1,
                            object.getString("name"),
                            object.getString("locateDescribe"),
                            object.getString("picName"));
                    travelPlanList.add(travelPlan);
                    travelPlan.setType(newTabs[travelPlanSupport]);
                    travelPlanSupport++;
                    if (travelPlanSupport == newTabs.length) {
                        picSupport = 0;
                        getTravelPlanPic();
                    }
                    Log.i("travelProject", travelPlan.name);
                }
            });
        }
    }

    public void getTravelPlanPic() {

        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereEqualTo("name", travelPlanList.get(picSupport).picName);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                travelPlanList.get(picSupport).setUrl(object.getString("url"));
                picSupport++;
                if (picSupport < travelPlanList.size()) {
                    getTravelPlanPic();
                } else {
                    initTravelPlan();
                }
            }
        });

    }

    public void initTravelPlan() {
        travelPlanRecyclerView = findViewById(R.id.travelPlanRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        travelPlanRecyclerView.setLayoutManager(linearLayoutManager);
        TravelPlanAdapter travelPlanAdapter = new TravelPlanAdapter(travelPlanList);
        travelPlanRecyclerView.setAdapter(travelPlanAdapter);

        getHomeStayInformation();
    }

    public void getHomeStayInformation() {
        homeStayList = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("HomeStay");
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                TravelPlan travelPlan = new TravelPlan(0,
                        object.getString("name"),
                        object.getString("locateDescribe"),
                        object.getString("mainPicName"));
                travelPlan.setType("HomeStay");
                homeStayList.add(travelPlan);
                getHomeStayPic();
            }
        });
    }

    public void getHomeStayPic() {
        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereEqualTo("name", homeStayList.get(0).picName);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                homeStayList.get(0).setUrl(object.getString("url"));
                initHomeStayRecyclerView();
            }
        });

    }

    public void initHomeStayRecyclerView() {
        homeStayRecyclerView = findViewById(R.id.homeStayRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        homeStayRecyclerView.setLayoutManager(linearLayoutManager);
        TravelPlanAdapter travelPlanAdapter = new TravelPlanAdapter(homeStayList);
        homeStayRecyclerView.setAdapter(travelPlanAdapter);
        getRestaurantInformation();
    }

    public void getRestaurantInformation() {
        restaurantList = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("Restaurant");
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                TravelPlan travelPlan = new TravelPlan(0,
                        object.getString("name"),
                        object.getString("locateDescribe"),
                        object.getString("mainPicName"));
                travelPlan.setType("Restaurant");
                restaurantList.add(travelPlan);
                getRestaurantPic();
            }
        });
    }

    public void getRestaurantPic() {
        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereEqualTo("name", restaurantList.get(0).picName);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                restaurantList.get(0).setUrl(object.getString("url"));
                initRestaurantRecyclerView();
            }
        });
    }

    public void initRestaurantRecyclerView() {
        restaurantRecyclerView = findViewById(R.id.restaurantRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        restaurantRecyclerView.setLayoutManager(linearLayoutManager);
        TravelPlanAdapter travelPlanAdapter = new TravelPlanAdapter(restaurantList);
        restaurantRecyclerView.setAdapter(travelPlanAdapter);

        loading.setVisibility(View.INVISIBLE);
    }

    private class TravelPlanAdapter extends RecyclerView.Adapter<TravelPlanAdapter.ViewHolder> {
        private List<TravelPlan> travelPlanList;

        private TravelPlanAdapter(List<TravelPlan> travelPlanList ) {
            this.travelPlanList = travelPlanList;
        }

        @Override
        public TravelPlanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_day_plan, parent, false);
            final TravelPlanAdapter.ViewHolder holder = new TravelPlanAdapter.ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final TravelPlanAdapter.ViewHolder holder, int position) {
            TravelPlan travelPlan = travelPlanList.get(position);
            Uri uri = Uri.parse(travelPlan.url);
            holder.projectPic.setImageURI(uri);
            RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);
            holder.projectPic.getHierarchy().setRoundingParams(roundingParams);
            holder.locate.setText(travelPlan.locateDescribe);
            holder.price.setText("¥200");
            if (travelPlan.day == 0) {
                holder.day.setVisibility(View.INVISIBLE);
            } else {
                holder.day.setText("Day" + String.valueOf(travelPlan.day));
            }
            holder.name.setText(travelPlan.name);

            holder.setNumber(travelPlan.day);
            Log.i("project", "succeed");

            simpleTicketList = new ArrayList<>();
            if (travelPlan.type.equals("drift")) {
                if (children == 0) {
                    SimpleTicket simpleTicket = new SimpleTicket("成人票", adults,
                            97 * adults);
                    simpleTicket.setType(travelPlan.type);
                    simpleTicketList.add(simpleTicket);
                } else {
                    SimpleTicket simpleTicket1 = new SimpleTicket("成人票", adults,
                            97 * adults);
                    simpleTicket1.setType(travelPlan.type);
                    simpleTicketList.add(simpleTicket1);
                    SimpleTicket simpleTicket2 = new SimpleTicket("儿童票", children,
                            67 * children);
                    simpleTicket2.setType(travelPlan.type);
                    simpleTicketList.add(simpleTicket2);
                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
                holder.projectRecyclerView.setLayoutManager(linearLayoutManager);
                ItemAdapter itemAdapter = new ItemAdapter(simpleTicketList);
                holder.projectRecyclerView.setAdapter(itemAdapter);
            } else if (travelPlan.type.equals("fishing")) {
                if (children == 0) {
                    SimpleTicket simpleTicket = new SimpleTicket("综合鱼塘（6小时）+1斤高级鱼饵",
                            adults, adults * 238);
                    simpleTicket.setType(travelPlan.type);
                    simpleTicketList.add(simpleTicket);
                } else {
                    SimpleTicket simpleTicket1 = new SimpleTicket("综合鱼塘（6小时）+1斤高级鱼饵",
                            adults, adults * 238);
                    simpleTicket1.setType(travelPlan.type);
                    simpleTicketList.add(simpleTicket1);
                    SimpleTicket simpleTicket2 = new SimpleTicket("综合鱼塘（6小时）",
                            children, children * 208);
                    simpleTicket2.setType(travelPlan.type);
                    simpleTicketList.add(simpleTicket2);
                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
                holder.projectRecyclerView.setLayoutManager(linearLayoutManager);
                ItemAdapter itemAdapter = new ItemAdapter(simpleTicketList);
                holder.projectRecyclerView.setAdapter(itemAdapter);

            } else if (travelPlan.type.equals("pick")) {
                if (children == 0) {
                    SimpleTicket simpleTicket = new SimpleTicket("蓝莓采摘+外带两斤蓝莓",
                            adults, adults * 128);
                    simpleTicket.setType(travelPlan.type);
                    simpleTicketList.add(simpleTicket);
                } else {
                    SimpleTicket simpleTicket1 = new SimpleTicket("蓝莓采摘+外带两斤蓝莓",
                            adults, adults * 128);
                    simpleTicket1.setType(travelPlan.type);
                    simpleTicketList.add(simpleTicket1);
                    SimpleTicket simpleTicket2 = new SimpleTicket("蓝莓采摘",
                            children, children * 88);
                    simpleTicket2.setType(travelPlan.type);
                    simpleTicketList.add(simpleTicket2);
                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
                holder.projectRecyclerView.setLayoutManager(linearLayoutManager);
                ItemAdapter itemAdapter = new ItemAdapter(simpleTicketList);
                holder.projectRecyclerView.setAdapter(itemAdapter);

            } else if (travelPlan.type.equals("ktv")) {
                if (persons > 6) {
                    SimpleTicket simpleTicket = new SimpleTicket("阳光场欢唱7小时（大包）",
                            1, 218);
                    simpleTicket.setType(travelPlan.type);
                    simpleTicketList.add(simpleTicket);
                } else {
                    SimpleTicket simpleTicket = new SimpleTicket("阳光场欢唱7小时（小包）",
                            1, 158);
                    simpleTicket.setType(travelPlan.type);
                    simpleTicketList.add(simpleTicket);
                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
                holder.projectRecyclerView.setLayoutManager(linearLayoutManager);
                ItemAdapter itemAdapter = new ItemAdapter(simpleTicketList);
                holder.projectRecyclerView.setAdapter(itemAdapter);
            } else if (travelPlan.type.equals("barbecue")) {
                if (persons == 1) {
                    SimpleTicket simpleTicket = new SimpleTicket("户外单人烧烤套餐",
                            1, 88);
                    simpleTicket.setType(travelPlan.type);
                    simpleTicketList.add(simpleTicket);
                } else if (persons == 2) {
                    SimpleTicket simpleTicket = new SimpleTicket("户外双人烧烤套餐",
                            1, 168);
                    simpleTicket.setType(travelPlan.type);
                    simpleTicketList.add(simpleTicket);
                } else if (persons <= 4) {
                    SimpleTicket simpleTicket = new SimpleTicket("户外四人烧烤套餐",
                            1, 328);
                    simpleTicket.setType(travelPlan.type);
                    simpleTicketList.add(simpleTicket);
                } else if (persons <= 8) {
                    SimpleTicket simpleTicket = new SimpleTicket("户外八人烧烤套餐",
                            1, 588);
                    simpleTicket.setType(travelPlan.type);
                    simpleTicketList.add(simpleTicket);
                } else if (persons <= 12) {
                    SimpleTicket simpleTicket1 = new SimpleTicket("户外四人烧烤套餐",
                            1, 328);
                    simpleTicket1.setType(travelPlan.type);
                    simpleTicketList.add(simpleTicket1);
                    SimpleTicket simpleTicket2 = new SimpleTicket("户外八人烧烤套餐",
                            1, 588);
                    simpleTicket2.setType(travelPlan.type);
                    simpleTicketList.add(simpleTicket2);
                } else {
                    SimpleTicket simpleTicket = new SimpleTicket("户外八人烧烤套餐",
                            2, 588 * 2);
                    simpleTicket.setType(travelPlan.type);
                    simpleTicketList.add(simpleTicket);
                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
                holder.projectRecyclerView.setLayoutManager(linearLayoutManager);
                ItemAdapter itemAdapter = new ItemAdapter(simpleTicketList);
                holder.projectRecyclerView.setAdapter(itemAdapter);
            } else if (travelPlan.type.equals("chess")) {
                if (adults <= 4) {
                    SimpleTicket simpleTicket = new SimpleTicket("小厅（可容纳4人）",
                            1, 97);
                    simpleTicket.setType(travelPlan.type);
                    simpleTicketList.add(simpleTicket);
                } else if (adults <= 8) {
                    SimpleTicket simpleTicket = new SimpleTicket("中厅（可容纳8人）",
                            1, 187);
                    simpleTicket.setType(travelPlan.type);
                    simpleTicketList.add(simpleTicket);
                } else {
                    SimpleTicket simpleTicket = new SimpleTicket("大厅（可容纳15人）",
                            1, 327);
                    simpleTicket.setType(travelPlan.type);
                    simpleTicketList.add(simpleTicket);
                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
                holder.projectRecyclerView.setLayoutManager(linearLayoutManager);
                ItemAdapter itemAdapter = new ItemAdapter(simpleTicketList);
                holder.projectRecyclerView.setAdapter(itemAdapter);
            } else if (travelPlan.type.equals("sightseeing")) {
                if (children == 0) {
                    SimpleTicket simpleTicket = new SimpleTicket("成人票",
                            adults, 120 * adults);
                    simpleTicket.setType(travelPlan.type);
                    simpleTicketList.add(simpleTicket);
                } else {
                    SimpleTicket simpleTicket1 = new SimpleTicket("成人票",
                            adults, 120 * adults);
                    simpleTicket1.setType(travelPlan.type);
                    simpleTicketList.add(simpleTicket1);
                    SimpleTicket simpleTicket2 = new SimpleTicket("儿童票",
                            children, 60 * children);
                    simpleTicket2.setType(travelPlan.type);
                    simpleTicketList.add(simpleTicket2);
                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
                holder.projectRecyclerView.setLayoutManager(linearLayoutManager);
                ItemAdapter itemAdapter = new ItemAdapter(simpleTicketList);
                holder.projectRecyclerView.setAdapter(itemAdapter);
            } else if (travelPlan.type.equals("HomeStay")) {
                SimpleTicket simpleTicket = new SimpleTicket("标准间" + travelDate.getText().toString(),
                        adults / 2 * dayCount, dayCount * adults / 2 * 278);
                simpleTicket.setType(travelPlan.type);
                simpleTicketList.add(simpleTicket);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
                holder.projectRecyclerView.setLayoutManager(linearLayoutManager);
                ItemAdapter itemAdapter = new ItemAdapter(simpleTicketList);
                holder.projectRecyclerView.setAdapter(itemAdapter);
            } else if (travelPlan.type.equals("Restaurant")) {
                if (haveRestaurant) {
                    if (persons <= 2) {
                        SimpleTicket simpleTicket1 = new SimpleTicket("小桌（6月" + String.valueOf(startDay + 1) + "日中午）",
                                1, 20);
                        simpleTicket1.setType(travelPlan.type);
                        simpleTicketList.add(simpleTicket1);
                        SimpleTicket simpleTicket2 = new SimpleTicket("小桌（6月" + String.valueOf(startDay + 1) + "日晚上）",
                                1, 20);
                        simpleTicket2.setType(travelPlan.type);
                        simpleTicketList.add(simpleTicket2);
                    } else if (persons <= 4) {
                        SimpleTicket simpleTicket1 = new SimpleTicket("中桌（6月" + String.valueOf(startDay + 1) + "日中午）",
                                1, 20);
                        simpleTicket1.setType(travelPlan.type);
                        simpleTicketList.add(simpleTicket1);
                        SimpleTicket simpleTicket2 = new SimpleTicket("中桌（6月" + String.valueOf(startDay + 1) + "日晚上）",
                                1, 20);
                        simpleTicket2.setType(travelPlan.type);
                        simpleTicketList.add(simpleTicket2);
                    } else if (persons <= 8) {
                        SimpleTicket simpleTicket1 = new SimpleTicket("大桌（6月" + String.valueOf(startDay + 1) + "日中午）",
                                1, 20);
                        simpleTicket1.setType(travelPlan.type);
                        simpleTicketList.add(simpleTicket1);
                        SimpleTicket simpleTicket2 = new SimpleTicket("大桌（6月" + String.valueOf(startDay + 1) + "日晚上）",
                                1, 20);
                        simpleTicket2.setType(travelPlan.type);
                        simpleTicketList.add(simpleTicket2);
                    } else {
                        SimpleTicket simpleTicket1 = new SimpleTicket("包厢（6月" + String.valueOf(startDay + 1) + "日中午）",
                                1, 20);
                        simpleTicket1.setType(travelPlan.type);
                        simpleTicketList.add(simpleTicket1);
                        SimpleTicket simpleTicket2 = new SimpleTicket("包厢（6月" + String.valueOf(startDay + 1) + "日晚上）",
                                1, 20);
                        simpleTicket2.setType(travelPlan.type);
                        simpleTicketList.add(simpleTicket2);
                    }

                } else {
                    if (persons <= 2) {
                        SimpleTicket simpleTicket = new SimpleTicket("小桌（6月" + String.valueOf(startDay + 1) + "日晚上）",
                                1, 20);
                        simpleTicket.setType(travelPlan.type);
                        simpleTicketList.add(simpleTicket);
                    } else if (persons <= 4) {
                        SimpleTicket simpleTicket = new SimpleTicket("中桌（6月" + String.valueOf(startDay + 1) + "日晚上）",
                                1, 20);
                        simpleTicket.setType(travelPlan.type);
                        simpleTicketList.add(simpleTicket);
                    } else if (persons <= 8) {
                        SimpleTicket simpleTicket = new SimpleTicket("大桌（6月" + String.valueOf(startDay + 1) + "日晚上）",
                                1, 20);
                        simpleTicket.setType(travelPlan.type);
                        simpleTicketList.add(simpleTicket);
                    } else {
                        SimpleTicket simpleTicket = new SimpleTicket("包厢（6月" + String.valueOf(startDay + 1) + "日晚上）",
                                1, 20);
                        simpleTicket.setType(travelPlan.type);
                        simpleTicketList.add(simpleTicket);
                    }
                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
                holder.projectRecyclerView.setLayoutManager(linearLayoutManager);
                ItemAdapter itemAdapter = new ItemAdapter(simpleTicketList);
                holder.projectRecyclerView.setAdapter(itemAdapter);
            }
            allList.addAll(simpleTicketList);
        }

        @Override
        public int getItemCount() {
            return travelPlanList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView day;
            private SimpleDraweeView projectPic;
            private TextView locate;
            private TextView price;
            private TextView name;
            private int number;
            private RecyclerView projectRecyclerView;

            private ViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.project_name);
                day = view.findViewById(R.id.day);
                projectPic = view.findViewById(R.id.project_pic);
                locate = view.findViewById(R.id.project_locate);
                price = view.findViewById(R.id.price);
                projectRecyclerView = view.findViewById(R.id.projectRecyclerView);
            }

            public void setNumber(int number) {
                this.number = number;
            }
        }

    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
        private List<SimpleTicket> simpleTicketList;

        private ItemAdapter(List<SimpleTicket> simpleTicketList) {
            this.simpleTicketList = simpleTicketList;
        }

        @Override
        public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_item2, parent, false);
            final ItemAdapter.ViewHolder holder = new ItemAdapter.ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ItemAdapter.ViewHolder holder, int position) {
            SimpleTicket simpleTicket = simpleTicketList.get(position);
            holder.projectName.setText(simpleTicket.name);
            if (checkHomeStay(holder.projectName.getText().toString())) {
                holder.count.setText("共" + String.valueOf(simpleTicket.count) + "晚");
            } else {
                holder.count.setText("x" + String.valueOf(simpleTicket.count));
            }
            holder.totalPrice.setText(String.valueOf(simpleTicket.totalPrice));

        }

        @Override
        public int getItemCount() {
            return simpleTicketList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView projectName;
            private TextView count;
            private TextView totalPrice;

            private ViewHolder(View view) {
                super(view);
                projectName = view.findViewById(R.id.project_name);
                count = view.findViewById(R.id.count);
                totalPrice = view.findViewById(R.id.total_price);
            }

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.order:
                if (userId.equals("tourist")) {
                    showToast("请先登录。");
                    Intent intent = new Intent(TravelPlanActivity.this, LoginActivity.class);
                    startActivityForResult(intent, 1);
                } else {
                    orderTravelPlan();
                }
                break;
        }
    }

    public boolean checkRestaurant() {
        boolean restaurant = false;
        for (int i = 0; i < tabs.length; i++) {
            if (tabs[i].equals("restaurant")) {
                restaurant = true;
                break;
            }
        }
        Log.i("checkRestaurant", String.valueOf(restaurant));
        return restaurant;
    }

    public String[] getTabsRandom(int need, int all) {
        String[] randomString = new String[need];
        int[] support = new int[need];
        for (int i = 0; i < support.length; i++) {
            int a = (int) (Math.random() * all);
            support[i] = a;
        }
        while (repeat(support)) {
            for (int i = 0; i < support.length; i++) {
                int a = (int) (Math.random() * all);
                support[i] = a;
            }
        }
        for (int i = 0; i < support.length; i++) {
            Log.i("support", String.valueOf(support[i]));
            randomString[i] = tabs[support[i]];
            Log.i("randomString", randomString[i]);
            Log.i("tabs", tabs[support[i]]);
        }

        return randomString;
    }

    public boolean repeat(int[] support) {
        boolean repeat = false;
        for (int i = 0; i < support.length; i++) {
            for (int j = i + 1; j < support.length; j++) {
                if (support[i] == support[j]) {
                    repeat = true;
                    break;
                }
            }
        }
        return repeat;
    }

    public void removeRestaurant() {
        for (int i = 0; i < tabs.length; i++) {
            if (tabs[i].equals("restaurant")) {
                tabs[i] = "";
            }
        }
    }

    public int getTabsCount() {
        int count = 0;
        for (int i = 0; i < tabs.length; i++) {
            if (!tabs[i].equals("")) {
                count++;
            }
        }
        return count;
    }

    public void showTabs(String[] tabs,String s) {
        String string = "";
        for (int i = 0; i < tabs.length; i++) {
            if (!tabs[i].equals("")) {
                string = string + (tabs[i] + ',');
            }
        }
        Log.i(s, string);
    }

    public String[] addTabs(int need, int all) {
        String[] newTabs = new String[need];
        for (int i = 0; i < need; i++) {
            newTabs[i] = "";
        }
        int k = all;
        int l = 0;
        for (int i = 0; i < all; i++) {
            for (int j = l; j < tabs.length; j++) {
                if (!tabs[j].equals("")) {
                    l = j + 1;
                    newTabs[i] = tabs[j];
                    break;
                }
            }
        }
        showTabs(newTabs, "tansNewTabs");
        String[] types = new String[]{"drift", "pick", "chess", "sightseeing", "ktv", "fishing", "barbecue"};
        for (int i = 0; i < types.length; i++) {
            boolean repeat = false;
            for (int j = 0; j < all; j++) {
                if (newTabs[j].equals(types[i])) {
                    repeat = true;
                    break;
                }
            }
            if (!repeat) {
                newTabs[k] = types[i];
                k++;
                Log.i("k", String.valueOf(k));
                Log.i("types", types[i]);
                if (k == need) {
                    break;
                }
            }
        }
        return newTabs;
    }

    public String[] transferTabs(int need) {
        String[] newTabs = new String[need];
        for (int i = 0; i < need; i++) {
            newTabs[i] = "";
        }
        int l = 0;
        for (int i = 0; i < need; i++) {
            for (int j = l; j < tabs.length; j++) {
                if (!tabs[j].equals("")) {
                    l = j + 1;
                    newTabs[i] = tabs[j];
                    break;
                }
            }
        }
        return newTabs;
    }

    public boolean checkHomeStay(String s) {
        char[] chars = s.toCharArray();
        String s1 = "";
        for (int i = 0; i < 3; i++) {
            s1 += String.valueOf(chars[i]);
        }
        if (s1.equals("标准间")) {
            return true;
        } else {
            return false;
        }

    }

    public void orderTravelPlan() {
        Intent intent = new Intent(TravelPlanActivity.this, CreateTravelPlanOrderActivity.class);
        names = new String[allList.size()];
        counts = new int[allList.size()];
        types = new String[allList.size()];
        prices = new int[allList.size()];
        int i = 0;
        for (SimpleTicket simpleTicket : allList) {
            names[i] = simpleTicket.name;
            counts[i] = simpleTicket.count;
            types[i] = simpleTicket.type;
            prices[i] = simpleTicket.totalPrice;
            i++;
        }

        intent.putExtra("startDay", startDay);
        intent.putExtra("dayCount", dayCount);
        intent.putExtra("names", names);
        intent.putExtra("counts", counts);
        intent.putExtra("types", types);
        intent.putExtra("prices", prices);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    userId = data.getStringExtra("UserId");
                }
                break;
        }
    }

    private void showToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
            toast.show();
        } else {
            toast.setText(msg);
            toast.show();
        }
    }
}
