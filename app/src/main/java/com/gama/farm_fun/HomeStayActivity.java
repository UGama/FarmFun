package com.gama.farm_fun;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.animation.ObjectAnimator.ofFloat;

public class HomeStayActivity extends AppCompatActivity implements View.OnClickListener {
    private String userId;
    private String projectName;

    public int screenWidth;
    public int screenHeight;

    private View topBar;
    private float alphaStorage;
    private TextView title;
    private ImageView titleIcon;
    private TextView firstSubTitle;
    private TextView secondSubTitle;
    private TextView thirdSubTitle;
    private TextView fourthSubTitle;

    private ObservableScrollView observableScrollView;

    private ImageView homeStayPic;
    private String mainPicName;
    private TextView homeStayName;
    private TextView describe;
    private TextView address;

    private TextView reserve;
    private View homeStayPanel;
    private ConstraintLayout panelTopLayout;
    private TextView panelChosenStartDate;
    private TextView panelChosenEndDate;
    private TextView nights;
    private RecyclerView roomRecyclerView;
    private List<Room> roomList;
    private int[][] roomSupport;

    private View timeChosePanel;
    private ImageView shelter;
    private Button cancelDateChose;
    private RecyclerView monthRecyclerView;
    private List<Date> dateList;
    private List<Month> monthList;
    private AnimatorSet timeChosePanelQuit;

    private String startTime;
    private String endTime;
    private int startMonth;
    private int startDay;
    private int endMonth;
    private int endDay;
    private int nightCount;

    private boolean firstTouch = true;

    private AVObject orderAVObject;
    private String orderRoomType;
    private int orderPrice;

    private Toast toast;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_homestay);

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
        Log.i("width/height(px)", String.valueOf(screenWidth) + "/" + String.valueOf(screenHeight));
        Log.i("width/height(dp)", String.valueOf(width) + "/" + String.valueOf(height));
        getUserInformation();
    }

    public void getUserInformation() {
        Intent intent = getIntent();
        userId = intent.getStringExtra("UserId");
        initUI();
    }

    public void initUI() {
        topBar = findViewById(R.id.topBar);
        topBar.setVisibility(View.VISIBLE);
        topBar.setAlpha(0);
        alphaStorage = 0f;
        title = topBar.findViewById(R.id.title);
        title.setText("民宿");
        titleIcon = topBar.findViewById(R.id.titleIcon);
        titleIcon.setImageResource(R.drawable.homestaytitle);
        firstSubTitle = topBar.findViewById(R.id.firstSubTitle);
        secondSubTitle = topBar.findViewById(R.id.secondSubTitle);
        thirdSubTitle = topBar.findViewById(R.id.thirdSubTitle);
        fourthSubTitle = topBar.findViewById(R.id.fourthSubTitle);
        firstSubTitle.setText("民宿概况");
        secondSubTitle.setText("房间预订");
        thirdSubTitle.setText("住户评论");
        fourthSubTitle.setText("地图导览");

        observableScrollView = findViewById(R.id.observableScrollView);
        setObservableScrollView();

        homeStayPic = findViewById(R.id.mainPic);
        homeStayName = findViewById(R.id.homeStayName);
        describe = findViewById(R.id.describe);
        address = findViewById(R.id.address);
        reserve = findViewById(R.id.reserve);
        reserve.setText("房间预订");
        homeStayPanel = findViewById(R.id.panel_homeStay_middle);
        panelTopLayout = homeStayPanel.findViewById(R.id.topLayout);
        panelTopLayout.setOnClickListener(this);
        panelChosenStartDate = homeStayPanel.findViewById(R.id.chosenStartDate);
        panelChosenStartDate.setText("6月1日");
        startTime = "6月1日";
        panelChosenEndDate = homeStayPanel.findViewById(R.id.chosenEndDate);
        panelChosenEndDate.setText("6月2日");
        endTime = "6月2日";
        nights = homeStayPanel.findViewById(R.id.nights);
        nights.setText("共1晚>");
        nightCount = 1;

        roomRecyclerView = findViewById(R.id.roomRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        roomRecyclerView.setLayoutManager(linearLayoutManager);
        roomList = new ArrayList<>();

        timeChosePanel = findViewById(R.id.datePanel);
        shelter = findViewById(R.id.shelter);
        shelter.setOnClickListener(this);
        cancelDateChose = timeChosePanel.findViewById(R.id.cancel);
        cancelDateChose.setOnClickListener(this);
        monthRecyclerView = findViewById(R.id.timeChoseRecyclerView);
        dateList = new ArrayList<>();
        monthList = new ArrayList<>();
        ObjectAnimator delay = ofFloat(timeChosePanel, "rotation", 0, 0);
        delay.setDuration(500);
        ObjectAnimator quit = ofFloat(timeChosePanel, "translationY", 0, screenHeight);
        quit.setDuration(500);
        timeChosePanelQuit = new AnimatorSet();
        timeChosePanelQuit.play(delay).before(quit);

        getHomeStayInformation();
    }

    public void setObservableScrollView() {
        observableScrollView.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy) {
                int[] mainPicLocationOnScreen = new int[2];
                homeStayPic.getLocationOnScreen(mainPicLocationOnScreen);
                int mainPicY = mainPicLocationOnScreen[1] - getStatusBarHeight();
//                Log.i("mainPicY", String.valueOf(-mainPicY));
//                Log.i("mainPic.getBottom", String.valueOf(homeStayPic.getBottom()));
//                Log.i("topBar.getBottom", String.valueOf(topBar.getBottom()));
                if (-mainPicY >= homeStayPic.getBottom() - topBar.getBottom()) {
                    topBar.setVisibility(View.VISIBLE);
                } else if (-mainPicY < homeStayPic.getBottom() - topBar.getBottom()) {
                    float alpha = (float) (-mainPicY) / (homeStayPic.getBottom() - topBar.getBottom());
//                    Log.i("alpha", String.valueOf(alpha));
                    ObjectAnimator objectAnimator = ofFloat(topBar, "alpha", alphaStorage, alpha);
                    alphaStorage = alpha;
                    objectAnimator.setDuration(100);
                    objectAnimator.start();
                }
            }
        });
    }

    public void getHomeStayInformation() {
        AVQuery<AVObject> query = new AVQuery<>("HomeStay");
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                homeStayName.setText(object.getString("name"));
                projectName = object.getString("name");
                describe.setText(object.getString("describe"));
                address.setText(object.getString("locateDescribe"));
                mainPicName = object.getString("mainPicName");

                loadMainPic();
            }
        });
    }

    public void loadMainPic() {
        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereEqualTo("name", mainPicName);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                AVFile avFile = new AVFile("HomeStay.png", object.getString("url"), new HashMap<String, Object>());
                avFile.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, AVException e) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Log.i("bitmap(width/height)", String.valueOf(bitmap.getWidth()) + "/" + String.valueOf(bitmap.getHeight()));
                        homeStayPic.setImageBitmap(bitmap);

                    }
                }, new ProgressCallback() {
                    @Override
                    public void done(Integer percentDone) {
                        if (percentDone == 100) {
                            getRoomInformation();
                        }
                    }
                });
            }
        });
    }

    public void getRoomInformation() {
        AVQuery<AVObject> query = new AVQuery<>("Room");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {

                for (AVObject avObject : avObjects) {
                    Room room = new Room(avObject.getString("roomType"),
                            avObject.getString("describe"), avObject.getString("bed"),
                            avObject.getString("roomPicName"));
                    roomList.add(room);
                }
                getRoomTimeTable();

            }
        });
    }

    public void getRoomTimeTable() {
        AVQuery<AVObject> query = new AVQuery<>("RoomTimeTable");
        query.whereEqualTo("date", "2019/06/01");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                roomSupport = new int[avObjects.size()][2];
                for (AVObject avObject : avObjects) {
                    for (int j = 0; j < roomList.size(); j++) {
                        if (avObject.getString("roomType").equals(roomList.get(j).roomType)) {
                            roomList.get(j).setPrice(avObject.getInt("price"));
                            roomList.get(j).setRemain(avObject.getInt("remain"));
                            roomSupport[j][0] = j;
                            roomSupport[j][1] = avObject.getInt("price");
                            Log.i("辅助数组", String.valueOf(roomSupport[j][0])
                                    + "/" + String.valueOf(roomSupport[j][1]));
                            break;
                        }
                    }
                }
                roomList = sortRoom(roomList, roomSupport);

                RoomAdapter roomAdapter = new RoomAdapter(roomList);
                roomRecyclerView.setAdapter(roomAdapter);
                getDateInformation();
            }
        });
    }

    public void getDateInformation() {
        AVQuery<AVObject> query = new AVQuery<>("TimeTable");
        query.orderByAscending("date");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                for (AVObject avObject : avObjects) {
                    Date date = new Date(avObject.getString("date"), avObject.getInt("week"));
                    dateList.add(date);
                }
                initTimePanel();
            }
        });
    }

    public void initTimePanel() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        monthRecyclerView.setLayoutManager(linearLayoutManager);
        monthList = transToMonth(dateList);
        Log.i("monthList", String.valueOf(monthList.size()));
        MonthAdapter monthAdapter = new MonthAdapter(monthList);
        monthRecyclerView.setAdapter(monthAdapter);
        Log.i("Test", "setAdapterSuccess!");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.topLayout:
                initTimePanel();
                timeChosePanel.setVisibility(View.VISIBLE);
                ObjectAnimator objectAnimator = ofFloat(timeChosePanel, "translationY", screenHeight, 0);
                objectAnimator.setDuration(1000);
                objectAnimator.start();
                shelter.setVisibility(View.VISIBLE);
                break;
            case R.id.shelter:
            case R.id.cancel:
                timeChosePanel.setVisibility(View.INVISIBLE);
                shelter.setVisibility(View.INVISIBLE);
                if (!firstTouch) {
                    startTime = panelChosenStartDate.getText().toString();
                    firstTouch = true;
                }
                timeChosePanelQuit.start();
                break;
        }
    }

    private class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.ViewHolder> {
        private List<Week> weeksList;
        private int month;

        private WeekAdapter(List<Week> weeksList, int month) {
            this.weeksList = weeksList;
            this.month = month;
        }

        @NonNull
        @Override
        public WeekAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_week, parent, false);
            WeekAdapter.ViewHolder holder = new WeekAdapter.ViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final WeekAdapter.ViewHolder holder, int position) {
            Week week = weeksList.get(position);
            if (week.weekArray[0] == 0) {
                holder.Sun.setText("");
            } else {
                holder.Sun.setText(String.valueOf(week.weekArray[0]));
                holder.Sun.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (firstTouch) {
                            v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                            holder.SunDetail.setText("入住");
                            holder.SunDetail.setVisibility(View.VISIBLE);
                            startTime = String.valueOf(month) + "月" + holder.Sun.getText().toString() + "日";
                            startMonth = month;
                            startDay = Integer.parseInt(holder.Sun.getText().toString());
                            Log.i("StartTime", startTime);
                            firstTouch = false;
                        } else {
                            endMonth = month;
                            endDay = Integer.parseInt(holder.Sun.getText().toString());
                            nightCount = getNights(startMonth, startDay, endMonth, endDay);
                            if (nightCount > 0) {
                                v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                                holder.SunDetail.setText("离店");
                                holder.SunDetail.setVisibility(View.VISIBLE);
                                endTime = String.valueOf(month) + "月" + holder.Sun.getText().toString() + "日";
                                nights.setText("共" + String.valueOf(nightCount) + "晚>");
                                Log.i("endTime", endTime);
                                panelChosenStartDate.setText(startTime);
                                panelChosenEndDate.setText(endTime);
                                shelter.setVisibility(View.INVISIBLE);
                                timeChosePanelQuit.start();
                                firstTouch = true;
                            }
                        }
                    }
                });
            }
            if (week.weekArray[1] == 0) {
                holder.Mon.setText("");
            } else {
                holder.Mon.setText(String.valueOf(week.weekArray[1]));
                holder.Mon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (firstTouch) {
                            v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                            holder.MonDetail.setText("入住");
                            holder.MonDetail.setVisibility(View.VISIBLE);
                            startTime = String.valueOf(month) + "月" + holder.Mon.getText().toString() + "日";
                            startMonth = month;
                            startDay = Integer.parseInt(holder.Mon.getText().toString());
                            firstTouch = false;
                        } else {
                            endMonth = month;
                            endDay = Integer.parseInt(holder.Mon.getText().toString());
                            nightCount = getNights(startMonth, startDay, endMonth, endDay);
                            if (nightCount > 0) {
                                v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                                holder.MonDetail.setText("离店");
                                holder.MonDetail.setVisibility(View.VISIBLE);
                                endTime = String.valueOf(month) + "月" + holder.Mon.getText().toString() + "日";
                                nights.setText("共" + String.valueOf(nightCount) + "晚>");
                                shelter.setVisibility(View.INVISIBLE);
                                panelChosenStartDate.setText(startTime);
                                panelChosenEndDate.setText(endTime);
                                timeChosePanelQuit.start();
                                firstTouch = true;
                            }
                        }
                    }
                });
            }
            if (week.weekArray[2] == 0) {
                holder.Tue.setText("");
            } else {
                holder.Tue.setText(String.valueOf(week.weekArray[2]));
                holder.Tue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (firstTouch) {
                            v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                            holder.TueDetail.setText("入住");
                            holder.TueDetail.setVisibility(View.VISIBLE);
                            startTime = String.valueOf(month) + "月" + holder.Tue.getText().toString() + "日";
                            startMonth = month;
                            startDay = Integer.parseInt(holder.Tue.getText().toString());
                            firstTouch = false;
                        } else {
                            endMonth = month;
                            endDay = Integer.parseInt(holder.Tue.getText().toString());
                            nightCount = getNights(startMonth, startDay, endMonth, endDay);
                            if (nightCount > 0) {
                                v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                                holder.TueDetail.setText("离店");
                                holder.TueDetail.setVisibility(View.VISIBLE);
                                endTime = String.valueOf(month) + "月" + holder.Tue.getText().toString() + "日";
                                nights.setText("共" + String.valueOf(nightCount) + "晚>");
                                shelter.setVisibility(View.INVISIBLE);
                                panelChosenStartDate.setText(startTime);
                                panelChosenEndDate.setText(endTime);
                                timeChosePanelQuit.start();
                                firstTouch = true;
                            }
                        }
                    }
                });
            }
            if (week.weekArray[3] == 0) {
                holder.Wed.setText("");
            } else {
                holder.Wed.setText(String.valueOf(week.weekArray[3]));
                holder.Wed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (firstTouch) {
                            v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                            holder.WedDetail.setText("入住");
                            holder.WedDetail.setVisibility(View.VISIBLE);
                            startTime = String.valueOf(month) + "月" + holder.Wed.getText().toString() + "日";
                            startMonth = month;
                            startDay = Integer.parseInt(holder.Wed.getText().toString());
                            firstTouch = false;
                        } else {
                            endMonth = month;
                            endDay = Integer.parseInt(holder.Wed.getText().toString());
                            nightCount = getNights(startMonth, startDay, endMonth, endDay);
                            if (nightCount > 0) {
                                v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                                holder.WedDetail.setText("离店");
                                holder.WedDetail.setVisibility(View.VISIBLE);
                                endTime = String.valueOf(month) + "月" + holder.Wed.getText().toString() + "日";
                                nights.setText("共" + String.valueOf(nightCount) + "晚>");
                                shelter.setVisibility(View.INVISIBLE);
                                panelChosenStartDate.setText(startTime);
                                panelChosenEndDate.setText(endTime);
                                timeChosePanelQuit.start();
                                firstTouch = true;
                            }
                        }
                    }
                });
            }
            if (week.weekArray[4] == 0) {
                holder.Thu.setText("");
            } else {
                holder.Thu.setText(String.valueOf(week.weekArray[4]));
                holder.Thu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (firstTouch) {
                            v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                            holder.ThuDetail.setText("入住");
                            holder.ThuDetail.setVisibility(View.VISIBLE);
                            startTime = String.valueOf(month) + "月" + holder.Thu.getText().toString() + "日";
                            startMonth = month;
                            startDay = Integer.parseInt(holder.Thu.getText().toString());
                            firstTouch = false;
                        } else {
                            endMonth = month;
                            endDay = Integer.parseInt(holder.Thu.getText().toString());
                            nightCount = getNights(startMonth, startDay, endMonth, endDay);
                            if (nightCount > 0) {
                                v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                                holder.ThuDetail.setText("离店");
                                holder.ThuDetail.setVisibility(View.VISIBLE);
                                endTime = String.valueOf(month) + "月" + holder.Thu.getText().toString() + "日";
                                nights.setText("共" + String.valueOf(nightCount) + "晚>");
                                shelter.setVisibility(View.INVISIBLE);
                                panelChosenStartDate.setText(startTime);
                                panelChosenEndDate.setText(endTime);
                                timeChosePanelQuit.start();
                                firstTouch = true;
                            }
                        }
                    }
                });
            }
            if (week.weekArray[5] == 0) {
                holder.Fri.setText("");
            } else {
                holder.Fri.setText(String.valueOf(week.weekArray[5]));
                holder.Fri.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (firstTouch) {
                            v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                            holder.FriDetail.setText("入住");
                            holder.FriDetail.setVisibility(View.VISIBLE);
                            startTime = String.valueOf(month) + "月" + holder.Fri.getText().toString() + "日";
                            startMonth = month;
                            startDay = Integer.parseInt(holder.Fri.getText().toString());
                            firstTouch = false;
                        } else {
                            endMonth = month;
                            endDay = Integer.parseInt(holder.Fri.getText().toString());
                            nightCount = getNights(startMonth, startDay, endMonth, endDay);
                            if (nightCount > 0) {
                                v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                                holder.FriDetail.setText("离店");
                                holder.FriDetail.setVisibility(View.VISIBLE);
                                endTime = String.valueOf(month) + "月" + holder.Fri.getText().toString() + "日";
                                nights.setText("共" + String.valueOf(nightCount) + "晚>");
                                shelter.setVisibility(View.INVISIBLE);
                                panelChosenStartDate.setText(startTime);
                                panelChosenEndDate.setText(endTime);
                                timeChosePanelQuit.start();
                                firstTouch = true;
                            }
                        }
                    }
                });
            }
            if (week.weekArray[6] == 0) {
                holder.Sat.setText("");
            } else {
                holder.Sat.setText(String.valueOf(week.weekArray[6]));
                holder.Sat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (firstTouch) {
                            v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                            holder.SatDetail.setText("入住");
                            holder.SatDetail.setVisibility(View.VISIBLE);
                            startTime = String.valueOf(month) + "月" + holder.Sat.getText().toString() + "日";
                            startMonth = month;
                            startDay = Integer.parseInt(holder.Sat.getText().toString());
                            firstTouch = false;
                        } else {
                            endMonth = month;
                            endDay = Integer.parseInt(holder.Sat.getText().toString());
                            nightCount = getNights(startMonth, startDay, endMonth, endDay);
                            if (nightCount > 0) {
                                v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                                holder.SatDetail.setText("离店");
                                holder.SatDetail.setVisibility(View.VISIBLE);
                                endTime = String.valueOf(month) + "月" + holder.Sat.getText().toString() + "日";
                                nights.setText("共" + String.valueOf(nightCount) + "晚>");
                                shelter.setVisibility(View.INVISIBLE);
                                panelChosenStartDate.setText(startTime);
                                panelChosenEndDate.setText(endTime);
                                timeChosePanelQuit.start();
                                firstTouch = true;
                            }
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return weeksList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView Sun;
            private TextView Mon;
            private TextView Tue;
            private TextView Wed;
            private TextView Thu;
            private TextView Fri;
            private TextView Sat;
            private TextView SunDetail;
            private TextView MonDetail;
            private TextView TueDetail;
            private TextView WedDetail;
            private TextView ThuDetail;
            private TextView FriDetail;
            private TextView SatDetail;

            private ViewHolder(View view) {
                super(view);
                Sun = view.findViewById(R.id.Sun);
                Mon = view.findViewById(R.id.Mon);
                Tue = view.findViewById(R.id.Tue);
                Wed = view.findViewById(R.id.Wed);
                Thu = view.findViewById(R.id.Thu);
                Fri = view.findViewById(R.id.Fri);
                Sat = view.findViewById(R.id.Sat);
                SunDetail = view.findViewById(R.id.Sun_detail);
                MonDetail = view.findViewById(R.id.Mon_detail);
                TueDetail = view.findViewById(R.id.Tue_detail);
                WedDetail = view.findViewById(R.id.Wed_detail);
                ThuDetail = view.findViewById(R.id.Thu_detail);
                FriDetail = view.findViewById(R.id.Fri_detail);
                SatDetail = view.findViewById(R.id.Sat_detail);
            }
        }
    }

    private class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.ViewHolder> {
        private List<Month> monthList;

        private MonthAdapter(List<Month> monthList) {
            this.monthList = monthList;
        }


        @NonNull
        @Override
        public MonthAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_months, parent, false);
            MonthAdapter.ViewHolder holder = new MonthAdapter.ViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final MonthAdapter.ViewHolder holder, int position) {
            Month month = monthList.get(position);
            holder.month.setText(month.month);
            List<Week> weekList = transToWeek(month);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
            holder.weekRecyclerView.setLayoutManager(linearLayoutManager);
            WeekAdapter weekAdapter = new WeekAdapter(weekList, Integer.parseInt(month.month));
            holder.weekRecyclerView.setAdapter(weekAdapter);
        }

        @Override
        public int getItemCount() {
            return monthList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView month;
            private RecyclerView weekRecyclerView;

            private ViewHolder(View view) {
                super(view);
                month = view.findViewById(R.id.month);
                weekRecyclerView = view.findViewById(R.id.week);
            }
        }
    }

    private class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
        private List<Room> roomsList;

        private RoomAdapter(List<Room> roomsList) {
            this.roomsList = roomsList;
        }


        @NonNull
        @Override
        public RoomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_room, parent, false);
            RoomAdapter.ViewHolder holder = new RoomAdapter.ViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final RoomAdapter.ViewHolder holder, int position) {
            final Room room = roomsList.get(position);
            Log.i(String.valueOf(position), room.roomType);
            if (room.remain == 0) {
                holder.roomType.setText(room.roomType);
                holder.roomDescribe.setText(room.roomDescribe);
                holder.bed.setText(room.bed);
                holder.roomRemain.setText("已售完");
                holder.price.setText(String.valueOf(room.price));
                holder.price.setTextColor(getResources().getColor(R.color.colorTextGray));
                holder.storePrice.setText(String.valueOf(room.price + 20));
                holder.storePrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.roomType.setText(room.roomType);
                holder.roomDescribe.setText(room.roomDescribe);
                holder.bed.setText(room.bed);
                holder.roomRemain.setText("还剩" + String.valueOf(room.remain) + "间");
                holder.price.setText(String.valueOf(room.price));
                holder.storePrice.setText(String.valueOf(room.price + 20));
                holder.storePrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (userId.equals("tourist")) {
                            Log.i("User", "click");
                            Intent loginIntent = new Intent(HomeStayActivity.this, LoginActivity.class);
                            startActivityForResult(loginIntent, 1);
                        } else {
                            orderRoomType = holder.roomType.getText().toString();
                            orderPrice = Integer.parseInt(holder.price.getText().toString());
                            Log.i("RoomType", orderRoomType);
                            orderAVObject = new AVObject("Order");
                            orderAVObject.put("userId", userId);
                            orderAVObject.put("type", "HomeStay");
                            orderAVObject.put("project", projectName);
                            orderAVObject.put("status", "待支付");
                            orderAVObject.put("item", orderRoomType);
                            orderAVObject.put("detail", startTime + " 至 " + endTime);
                            orderAVObject.put("count", nightCount);
                            orderAVObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null) {
                                        Intent orderIntent = new Intent(HomeStayActivity.this, OrderActivity.class);
                                        orderIntent.putExtra("UserId", userId);
                                        orderIntent.putExtra("Type", "homeStay");
                                        orderIntent.putExtra("Project", projectName);
                                        orderIntent.putExtra("Item", orderRoomType);
                                        orderIntent.putExtra("Url", holder.url);
                                        orderIntent.putExtra("Detail", startTime + " 至 " + endTime);
                                        orderIntent.putExtra("Count", nightCount);
                                        orderIntent.putExtra("Price",orderPrice);
                                        orderIntent.putExtra("OrderId", orderAVObject.getObjectId());
                                        startActivityForResult(orderIntent, 0);
                                    }
                                }
                            });


                        }
                    }
                });
            }
            AVQuery<AVObject> query = new AVQuery<>("_File");
            query.whereEqualTo("name", room.roomPicName);
            query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject object, AVException e) {
                    holder.setUrl(object.getString("url"));
                    Log.i("url", holder.url);
                    Uri imageUri = Uri.parse(object.getString("url"));
                    holder.roomPic.setImageURI(imageUri);
                    RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);
                    holder.roomPic.getHierarchy().setRoundingParams(roundingParams);
                }
            });


        }

        @Override
        public int getItemCount() {
            return roomsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView roomType;
            private TextView roomDescribe;
            private TextView bed;
            private TextView roomRemain;
            private TextView price;
            private TextView storePrice;
            private SimpleDraweeView roomPic;
            private Button order;
            private String url;

            private ViewHolder(View view) {
                super(view);
                roomType = view.findViewById(R.id.roomType);
                roomDescribe = view.findViewById(R.id.describe);
                roomRemain = view.findViewById(R.id.roomRemain);
                bed = view.findViewById(R.id.bed);
                price = view.findViewById(R.id.price);
                storePrice = view.findViewById(R.id.physicalStorePrice);
                roomPic = view.findViewById(R.id.roomPic);
                order = view.findViewById(R.id.order);
            }

            private void setUrl(String url) {
                this.url = url;
            }
        }
    }

    public List<Room> sortRoom(List<Room> roomList, int[][] roomSupport) {
        List<Room> rooms = new ArrayList<>();
        int supportPrice[] = new int[2];
        int minPrice = 9999;
        int count = 0;
        for (int i = 0; i < roomList.size(); i++) {
            for (int j = i; j < roomList.size(); j++) {
                if (roomSupport[j][1] < minPrice) {
                    minPrice = roomSupport[j][1];
                    count = j;
                }
            }
            supportPrice[0] = roomSupport[count][0];
            supportPrice[1] = roomSupport[count][1];
            roomSupport[count][0] = roomSupport[i][0];
            roomSupport[count][1] = roomSupport[i][1];
            roomSupport[i][0] = supportPrice[0];
            roomSupport[i][1] = supportPrice[1];
            minPrice = 9999;
            count = 0;
        }
        for (int i = 0; i < roomList.size(); i++) {
            rooms.add(roomList.get(roomSupport[i][0]));
            Log.i("排序后辅助数组", String.valueOf(roomSupport[i][0])
                    + "/" + String.valueOf(roomSupport[i][1]));
        }
        return rooms;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public List<Month> transToMonth(List<Date> dateList) {
        List<Month> months = new ArrayList<>();
        int days[] = new int[6];
        int j = -1;
        for (int i = 0; i < dateList.size(); i++) {
            char[] dateChar = dateList.get(i).day.toCharArray();
            Date date = dateList.get(i);
            if (i == 0) {
                Log.i("dateExample", date.day);
            }
            //example:"2019/06/01"
            if (dateChar[8] == '0' & dateChar[9] == '1') {
//                Log.i("dateInformation", String.valueOf(dateChar[5]));
//                Log.i("dateInformation", String.valueOf(dateChar[6]));
//                Log.i("dateInformation", String.valueOf(dateChar[5] + String.valueOf(dateChar[6])));
                Month month = new Month(date.week, String.valueOf(dateChar[5] + String.valueOf(dateChar[6])), 0);
                Log.i("monthInformation", month.month + " " + String.valueOf(month.firstDayWeek));
                months.add(month);
                j++;
                days[j] = 1;
            } else {
                days[j]++;
            }
        }
        for (int k = 0; k < months.size(); k++) {
            months.get(k).setDays(days[k]);
        }
        return months;
    }

    public List<Week> transToWeek(Month month) {
        List<Week> weeks = new ArrayList<>();
        if (month.firstDayWeek == 7) {
            month.firstDayWeek = 0;
        }
        int[] firstWeekArray = new int[7];
        int k = 1;
        for (int j = 0; j < 7; j++) {
            if (j < month.firstDayWeek) {
                firstWeekArray[j] = 0;
            } else {
                firstWeekArray[j] = k;
                k++;
            }
        }
        Week firstWeek = new Week(firstWeekArray);
        weeks.add(firstWeek);

        int[] weekArray=new int[7];
        int l = 0;
        for (int i = 8 - month.firstDayWeek; i <= month.days; i++) {
            if ((i + month.firstDayWeek) % 7 == 1) {
                weekArray[0] = i;
//                Log.i("weekFirst", String.valueOf(weekArray[0]));
                l = 1;
            } else {
                weekArray[l] = i;
                if (l == 6) {
                    Week week = new Week(weekArray);
//                    Log.i("weekFinal", String.valueOf(weekArray[l]));
//                    Log.i("week.weekArray", String.valueOf(week.weekArray[l]));
                    weeks.add(week);
                    weekArray = new int[7];
                }
                l++;
            }
            if (i == month.days) {
                Week week = new Week(weekArray);
                weeks.add(week);
            }
        }
//        for (int i = 0; i < weeks.size(); i++) {
//            Log.i("weeks", String.valueOf(weeks.get(i).weekArray[0]));
//        }
        return weeks;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    userId = data.getStringExtra("UserId");
                    showToast("登录成功！");
                }
        }

    }

    public int getNights(int startMonth, int startDay, int endMonth, int endDay) {
        int nights = 0;
        if (startMonth == endMonth) {
            nights = endDay - startDay;
        } else {
            for (int i = 0; i < endMonth - startMonth; i++) {
                switch (startMonth + i) {
                    case 6:
                        endDay += 30;
                        break;
                    case 7:
                        endDay += 31;
                        break;
                    case 8:
                        endDay += 31;
                        break;
                }
            }
            nights = endDay - startDay;
        }
        return nights;
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
