package com.gama.farm_fun;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.animation.ObjectAnimator.ofFloat;

public class RestaurantTicketActivity extends AppCompatActivity implements View.OnClickListener {

    private String userId;

    public int screenWidth;
    public int screenHeight;

    private ObservableScrollView observableScrollView;

    private ImageView restaurantPic;
    private TextView restaurantName;
    private TextView locationDescribe;

    private View topPanel;
    private ConstraintLayout topPanelTopLayout;
    private TextView topPanelChosenDate;
    private View midPanel;
    private ConstraintLayout midPanelTopLayout;
    private TextView midPanelChosenDate;
    private ImageView barBottomLine;
    private ImageView panelBottomLine;

    private RecyclerView seatRecyclerView;
    private List<Seat> seatsList;

    private View timeChosePanel;
    private ImageView shelter;
    private Button cancelDateChose;
    private RecyclerView monthRecyclerView;
    private List<Date> dateList;
    private List<Month> monthList;
    private AnimatorSet timeChosePanelQuit;

    private String time;

    private AVObject orderAVObject;
    private String orderSeatType;
    private int orderPrice;
    private String orderDetail;

    private Toast toast;

    private View loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_ticket);

        Intent intent = getIntent();
        userId = intent.getStringExtra("UserId");

        loading = findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);

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
        initUI();
    }

    public void initUI() {
        observableScrollView = findViewById(R.id.observableScrollView);

        restaurantPic = findViewById(R.id.restaurantPic);
        restaurantName = findViewById(R.id.restaurantName);
        locationDescribe = findViewById(R.id.locationDescribe);
        topPanel = findViewById(R.id.topPanel);
        topPanelTopLayout = topPanel.findViewById(R.id.topLayout);
        topPanelTopLayout.setOnClickListener(this);
        topPanelChosenDate = topPanel.findViewById(R.id.chosenDate);
        topPanelChosenDate.setText("2019年06月01日");
        midPanel = findViewById(R.id.midPanel);
        midPanelTopLayout = midPanel.findViewById(R.id.topLayout);
        midPanelTopLayout.setOnClickListener(this);
        midPanelChosenDate = midPanel.findViewById(R.id.chosenDate);
        midPanelChosenDate.setText("2019年06月01日");
        barBottomLine = findViewById(R.id.barBottomLine);
        panelBottomLine = findViewById(R.id.panelBottomLine);

        seatRecyclerView = findViewById(R.id.seatRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        seatRecyclerView.setLayoutManager(linearLayoutManager);
        seatsList = new ArrayList<>();

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

        time = topPanelChosenDate.getText().toString();

        setObservableScrollView();
    }

    public void setObservableScrollView() {
        observableScrollView.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy) {
                int[] panelLocation = new int[2];
                midPanel.getLocationOnScreen(panelLocation);
                int midPanelY = panelLocation[1];
                int statusBarHeight = getStatusBarHeight();
                if (midPanelY <= statusBarHeight + barBottomLine.getBottom()) {
                    topPanel.setVisibility(View.VISIBLE);
                    panelBottomLine.setVisibility(View.VISIBLE);
                } else {
                    topPanel.setVisibility(View.GONE);
                    panelBottomLine.setVisibility(View.GONE);
                }
            }
        });

        observableScrollView.smoothScrollTo(0, 20);
        getRestaurantInformation();
    }

    public void getRestaurantInformation() {
        AVQuery<AVObject> query = new AVQuery<>("Restaurant");
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                restaurantName.setText(object.getString("name"));
                locationDescribe.setText(object.getString("locateDescribe"));
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
                avFile.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, AVException e) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Log.i("bitmap(width/height)", String.valueOf(bitmap.getWidth()) + "/" + String.valueOf(bitmap.getHeight()));
                        restaurantPic.setImageBitmap(bitmap);

                    }
                }, new ProgressCallback() {
                    @Override
                    public void done(Integer percentDone) {
                        if (percentDone == 100) {
                            getSeatInformation();
                        }
                    }
                });
            }
        });
    }

    public void getSeatInformation() {
        AVQuery<AVObject> query = new AVQuery<>("Seat");
        query.orderByAscending("seatNumber");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                for (AVObject avObject : avObjects) {
                    Seat lunchSeat = new Seat(avObject.getString("picName"),
                            avObject.getString("type"),
                            avObject.getString("describe"),
                            "中");
                    seatsList.add(lunchSeat);
                    Log.i("test1", lunchSeat.seatType + " " + lunchSeat.picName + " " + lunchSeat.describe + " " + lunchSeat.meal);
                    Seat dinnerSeat = new Seat(avObject.getString("picName"),
                            avObject.getString("type"),
                            avObject.getString("describe"),
                            "晚");
                    seatsList.add(dinnerSeat);
                }
                getSeatRemain();
            }
        });
    }

    public void getSeatRemain() {
        AVQuery<AVObject> query = new AVQuery<>("SeatTimeTable");
        query.whereEqualTo("date", changeToSymbolDate(time));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                Log.i("test4", String.valueOf(avObjects.size()));
                int i = 0;
                for (AVObject avObject : avObjects) {
                    for (Seat seat : seatsList) {
                        if (seat.seatType.equals(avObject.getString("type")) & seat.meal.equals(avObject.getString("meal"))) {
                            seatsList.get(i).setRemain(avObject.getInt("remain"));
                            Log.i("test2", seatsList.get(i).seatType + " " + seatsList.get(i).remain);
                        }
                    }
                    i++;
                }
                SeatAdapter seatAdapter = new SeatAdapter(seatsList);
                seatRecyclerView.setAdapter(seatAdapter);
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

        loading.setVisibility(View.INVISIBLE);
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
                        v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                        holder.SunDetail.setText("就餐");
                        holder.SunDetail.setVisibility(View.VISIBLE);
                        String timeMonth = String.valueOf(month);
                        if (month < 10) {
                            timeMonth = '0' + timeMonth;
                        }
                        String timeDay = holder.Sun.getText().toString();
                        if (Integer.parseInt(timeDay) < 10) {
                            timeDay = '0' + timeDay;
                        }
                        time = "2019年" + timeMonth + "月" + timeDay + "日";
                        topPanelChosenDate.setText(time);
                        midPanelChosenDate.setText(time);
                        Log.i("Time", time);
                        shelter.setVisibility(View.INVISIBLE);
                        timeChosePanelQuit.start();
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
                        v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                        holder.MonDetail.setText("就餐");
                        holder.MonDetail.setVisibility(View.VISIBLE);
                        String timeMonth = String.valueOf(month);
                        if (month < 10) {
                            timeMonth = '0' + timeMonth;
                        }
                        String timeDay = holder.Mon.getText().toString();
                        if (Integer.parseInt(timeDay) < 10) {
                            timeDay = '0' + timeDay;
                        }
                        time = "2019年" + timeMonth + "月" + timeDay + "日";
                        topPanelChosenDate.setText(time);
                        midPanelChosenDate.setText(time);
                        Log.i("Time", time);
                        shelter.setVisibility(View.INVISIBLE);
                        timeChosePanelQuit.start();
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
                        v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                        holder.TueDetail.setText("就餐");
                        holder.TueDetail.setVisibility(View.VISIBLE);
                        String timeMonth = String.valueOf(month);
                        if (month < 10) {
                            timeMonth = '0' + timeMonth;
                        }
                        String timeDay = holder.Tue.getText().toString();
                        if (Integer.parseInt(timeDay) < 10) {
                            timeDay = '0' + timeDay;
                        }
                        time = "2019年" + timeMonth + "月" + timeDay + "日";
                        topPanelChosenDate.setText(time);
                        midPanelChosenDate.setText(time);
                        Log.i("Time", time);
                        shelter.setVisibility(View.INVISIBLE);
                        timeChosePanelQuit.start();
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
                        v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                        holder.WedDetail.setText("就餐");
                        holder.WedDetail.setVisibility(View.VISIBLE);
                        String timeMonth = String.valueOf(month);
                        if (month < 10) {
                            timeMonth = '0' + timeMonth;
                        }
                        String timeDay = holder.Wed.getText().toString();
                        if (Integer.parseInt(timeDay) < 10) {
                            timeDay = '0' + timeDay;
                        }
                        time = "2019年" + timeMonth + "月" + timeDay + "日";
                        topPanelChosenDate.setText(time);
                        midPanelChosenDate.setText(time);
                        Log.i("Time", time);
                        shelter.setVisibility(View.INVISIBLE);
                        timeChosePanelQuit.start();
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
                        v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                        holder.ThuDetail.setText("就餐");
                        holder.ThuDetail.setVisibility(View.VISIBLE);
                        String timeMonth = String.valueOf(month);
                        if (month < 10) {
                            timeMonth = '0' + timeMonth;
                        }
                        String timeDay = holder.Thu.getText().toString();
                        if (Integer.parseInt(timeDay) < 10) {
                            timeDay = '0' + timeDay;
                        }
                        time = "2019年" + timeMonth + "月" + timeDay + "日";
                        topPanelChosenDate.setText(time);
                        midPanelChosenDate.setText(time);
                        Log.i("Time", time);
                        shelter.setVisibility(View.INVISIBLE);
                        timeChosePanelQuit.start();
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
                        v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                        holder.FriDetail.setText("就餐");
                        holder.FriDetail.setVisibility(View.VISIBLE);
                        String timeMonth = String.valueOf(month);
                        if (month < 10) {
                            timeMonth = '0' + timeMonth;
                        }
                        String timeDay = holder.Fri.getText().toString();
                        if (Integer.parseInt(timeDay) < 10) {
                            timeDay = '0' + timeDay;
                        }
                        time = "2019年" + timeMonth + "月" + timeDay + "日";
                        topPanelChosenDate.setText(time);
                        midPanelChosenDate.setText(time);
                        Log.i("Time", time);
                        shelter.setVisibility(View.INVISIBLE);
                        timeChosePanelQuit.start();
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
                        v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                        holder.SatDetail.setText("就餐");
                        holder.SatDetail.setVisibility(View.VISIBLE);
                        String timeMonth = String.valueOf(month);
                        if (month < 10) {
                            timeMonth = '0' + timeMonth;
                        }
                        String timeDay = holder.Sat.getText().toString();
                        if (Integer.parseInt(timeDay) < 10) {
                            timeDay = '0' + timeDay;
                        }
                        time = "2019年" + timeMonth + "月" + timeDay + "日";
                        topPanelChosenDate.setText(time);
                        midPanelChosenDate.setText(time);
                        Log.i("Time", time);
                        shelter.setVisibility(View.INVISIBLE);
                        timeChosePanelQuit.start();
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

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public String NumberToDate(int year, int month, int day) {
        String date;
        date = String.valueOf(year) + "/" + String.valueOf(month) + "/" + String.valueOf(day);
        return date;
    }

    public int[] DateToNumber(String date) {
        int[] dateNumber = new int[3];
        char[] dateChar = date.toCharArray();
        int j = 0;
        for (int i = 0; i < dateChar.length; i++) {
            if (dateChar[i] != '/') {
                dateNumber[j] = dateNumber[j] * 10 + Integer.parseInt(String.valueOf(dateChar[i]));
            } else {
                j++;
            }
        }
        return dateNumber;
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
                timeChosePanelQuit.start();
                break;
        }
    }

    private class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.ViewHolder> {
        private List<Seat> seatsList;

        private SeatAdapter(List<Seat> seatsList) {
            this.seatsList = seatsList;
        }

        @Override
        public SeatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_order_seat, parent, false);
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
            holder.remain.setText(String.valueOf(seat.remain));
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
            holder.payOnline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userId.equals("tourist")) {
                        Intent loginIntent = new Intent(RestaurantTicketActivity.this, LoginActivity.class);
                        startActivityForResult(loginIntent, 1);
                    } else {
                        orderSeatType = holder.seatType.getText().toString();
                        orderPrice = holder.price;
                        orderDetail = midPanelChosenDate.getText().toString();
                        orderAVObject = new AVObject("Order");
                        orderAVObject.put("userId", userId);
                        orderAVObject.put("type", "Restaurant");
                        orderAVObject.put("project", restaurantName.getText().toString());
                        orderAVObject.put("status", "待支付");
                        orderAVObject.put("item", orderSeatType);
                        orderAVObject.put("detail", orderDetail);
                        orderAVObject.put("price", orderPrice);
                        orderAVObject.put("count", 1);
                        orderAVObject.put("comment",false);
                        orderAVObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    Intent orderIntent = new Intent(RestaurantTicketActivity.this, CreateOrderActivity.class);
                                    orderIntent.putExtra("UserId", userId);
                                    orderIntent.putExtra("Type", "restaurant");
                                    orderIntent.putExtra("Project", restaurantName.getText().toString());
                                    orderIntent.putExtra("Item", orderSeatType);
                                    orderIntent.putExtra("Url", holder.url);
                                    orderIntent.putExtra("Detail", orderDetail);
                                    orderIntent.putExtra("Count", 1);
                                    orderIntent.putExtra("Price", orderPrice);
                                    orderIntent.putExtra("OrderId", orderAVObject.getObjectId());
                                    startActivityForResult(orderIntent, 0);
                                }
                            }
                        });


                    }
                }
            });
            holder.order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userId.equals("tourist")) {
                        Intent loginIntent = new Intent(RestaurantTicketActivity.this, LoginActivity.class);
                        startActivityForResult(loginIntent, 1);
                    } else {
                        orderSeatType = holder.seatType.getText().toString();
                        orderPrice = holder.price;
                        orderDetail = midPanelChosenDate.getText().toString();
                        orderAVObject = new AVObject("Order");
                        orderAVObject.put("userId", userId);
                        orderAVObject.put("type", "Restaurant");
                        orderAVObject.put("project", restaurantName.getText().toString());
                        orderAVObject.put("status", "待支付");
                        orderAVObject.put("item", orderSeatType);
                        orderAVObject.put("detail", orderDetail);
                        orderAVObject.put("price", orderPrice);
                        orderAVObject.put("count", 1);
                        orderAVObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    Intent orderIntent = new Intent(RestaurantTicketActivity.this, CreateOrderActivity.class);
                                    orderIntent.putExtra("UserId", userId);
                                    orderIntent.putExtra("Type", "restaurant");
                                    orderIntent.putExtra("Project", restaurantName.getText().toString());
                                    orderIntent.putExtra("Item", orderSeatType);
                                    orderIntent.putExtra("Url", holder.url);
                                    orderIntent.putExtra("Detail", orderDetail);
                                    orderIntent.putExtra("Count", 1);
                                    orderIntent.putExtra("Price", orderPrice);
                                    orderIntent.putExtra("OrderId", orderAVObject.getObjectId());
                                    orderIntent.putExtra("comment", false);
                                    startActivityForResult(orderIntent, 0);
                                }
                            }
                        });


                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return seatsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView seatType;
            private TextView remain;
            private TextView meal;
            private ImageView seatPic;
            private TextView describe;
            private Button payOnline;
            private Button order;
            private int price;
            private String url;

            private ViewHolder(View view) {
                super(view);
                seatType = view.findViewById(R.id.seatType);
                remain = view.findViewById(R.id.remainSeats);
                meal = view.findViewById(R.id.meal);
                seatPic = view.findViewById(R.id.seatPic);
                describe = view.findViewById(R.id.describe);
                payOnline = view.findViewById(R.id.payOnline);
                order = view.findViewById(R.id.onlineShop);
                price = 20;
            }
            private void setUrl(String url) {
                this.url = url;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    finish();
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    userId = data.getStringExtra("UserId");
                    showToast("登录成功！");
                }
                break;
        }
    }

    public String changeToSymbolDate(String date) {
        char[] dateChar = date.toCharArray();
        char[] dateChar2 = new char[dateChar.length - 1];
        for (int i = 0; i < dateChar.length; i++) {
            if (dateChar[i] < '0' || dateChar[i] > '9') {
                dateChar[i] = '/';
            }
        }
        for (int j = 0; j < dateChar2.length; j++) {
            dateChar2[j] = dateChar[j];
        }

        return String.valueOf(dateChar2);
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
