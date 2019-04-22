package com.gama.farm_fun;

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

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.animation.ObjectAnimator.ofFloat;

public class HomeStayActivity extends AppCompatActivity {
    private String userId;

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
    private RecyclerView monthRecyclerView;
    private List<Date> dateList;
    private List<Month> monthList;

    private String startTime;
    private String endTime;

    private boolean firstTouch = true;

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
        if (userId != null) {
            Log.i("userId", userId);
        }
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
        homeStayPanel = findViewById(R.id.panel_homeStay);
        panelTopLayout = homeStayPanel.findViewById(R.id.topLayout);
        panelChosenStartDate = homeStayPanel.findViewById(R.id.chosenStartDate);
        panelChosenStartDate.setText("6月1日");
        startTime = "6月1日";
        panelChosenEndDate = homeStayPanel.findViewById(R.id.chosenEndDate);
        panelChosenEndDate.setText("6月2日");
        endTime = "6月2日";
        nights = homeStayPanel.findViewById(R.id.nights);
        nights.setText("共1晚>");
        roomRecyclerView = findViewById(R.id.roomRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        roomRecyclerView.setLayoutManager(linearLayoutManager);
        roomList = new ArrayList<>();

        timeChosePanel = findViewById(R.id.datePanel);
        monthRecyclerView = findViewById(R.id.timeChoseRecyclerView);
        dateList = new ArrayList<>();
        monthList = new ArrayList<>();

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

    private class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.ViewHolder> {
        private List<Week> weeksList;

        private WeekAdapter(List<Week> weeksList) {
            this.weeksList = weeksList;
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
                        v.setBackgroundColor(getResources().getColor(R.color.colorTheme));
                        if (firstTouch) {
                            holder.SunDetail.setText("入住");
                            firstTouch = false;
                        } else {
                            holder.SunDetail.setText("离店");
                        }
                    }
                });
            }
            if (week.weekArray[1] == 0) {
                holder.Mon.setText("");
            } else {
                holder.Mon.setText(String.valueOf(week.weekArray[1]));
            }
            if (week.weekArray[2] == 0) {
                holder.Tue.setText("");
            } else {
                holder.Tue.setText(String.valueOf(week.weekArray[2]));
            }
            if (week.weekArray[3] == 0) {
                holder.Wed.setText("");
            } else {
                holder.Wed.setText(String.valueOf(week.weekArray[3]));
            }
            if (week.weekArray[4] == 0) {
                holder.Thu.setText("");
            } else {
                holder.Thu.setText(String.valueOf(week.weekArray[4]));
            }
            if (week.weekArray[5] == 0) {
                holder.Fri.setText("");
            } else {
                holder.Fri.setText(String.valueOf(week.weekArray[5]));
            }
            if (week.weekArray[6] == 0) {
                holder.Sat.setText("");
            } else {
                holder.Sat.setText(String.valueOf(week.weekArray[6]));
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
            WeekAdapter weekAdapter = new WeekAdapter(weekList);
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
            Room room = roomsList.get(position);
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
                            String roomType = holder.roomType.getText().toString();
                            Log.i("RoomType", roomType);
                            AVObject avObject = new AVObject("Order");
                            avObject.put("userId", userId);
                            avObject.put("type", "HomeStay");
                            avObject.put("status", "待支付");

                        }
                    }
                });
            }
            AVQuery<AVObject> query = new AVQuery<>("_File");
            query.whereEqualTo("name", room.roomPicName);
            query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject object, AVException e) {
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
                }
        }

    }
}
