package com.gama.farm_fun;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.List;

import static android.animation.ObjectAnimator.ofFloat;

public class CustomizedActivity extends AppCompatActivity implements View.OnClickListener {
    private String userId;

    private View topBar;
    private TextView title;
    private Button back;

    private int screenWidth;
    private int screenHeight;

    private TextView drift;
    private TextView pick;
    private TextView ktv;
    private TextView fishing;
    private TextView sightseeing;
    private TextView chess;
    private TextView barbecue;
    private TextView restaurant;
    private String[] tabs;

    private View timeChosePanel;
    private ImageView shelter;
    private Button cancelDateChose;
    private RecyclerView monthRecyclerView;
    private List<Date> dateList;
    private List<Month> monthList;
    private AnimatorSet timeChosePanelQuit;
    private TextView timeChosePanelTitle;

    private String startTime;
    private String endTime;
    private int startMonth;
    private int startDay;
    private int endMonth;
    private int endDay;
    private int dayCount;

    private View timeShowPanel;
    private TextView chosenStartDate;
    private TextView chosenEndDate;
    private TextView days;

    private View peopleShowPanel;
    private TextView adultsText;
    private TextView childrenText;
    private AnimatorSet peopleChosePanelQuit;

    private int adults;
    private int children;
    private int persons;

    private View peopleChosePanel;
    private NumberPicker adultsNumberPick;
    private NumberPicker childrenNumberPick;

    private ImageView customized;
    private TextView customizedText;

    private boolean firstTouch = true;

    private Toast toast;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_customized);

        Intent intent = getIntent();
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
        Log.i("width/height(px)", String.valueOf(screenWidth) + "/" + String.valueOf(screenHeight));
        Log.i("width/height(dp)", String.valueOf(width) + "/" + String.valueOf(height));

        initUI();
    }

    public void initUI() {
        topBar = findViewById(R.id.top_bar);
        title = topBar.findViewById(R.id.title);
        title.setText("项目定制");
        back = topBar.findViewById(R.id.back);
        back.setOnClickListener(this);

        drift = findViewById(R.id.drift);
        drift.setOnClickListener(this);
        pick = findViewById(R.id.pick);
        pick.setOnClickListener(this);
        ktv = findViewById(R.id.ktv);
        ktv.setOnClickListener(this);
        sightseeing = findViewById(R.id.sightseeing);
        sightseeing.setOnClickListener(this);
        chess = findViewById(R.id.chess);
        chess.setOnClickListener(this);
        fishing = findViewById(R.id.fishing);
        fishing.setOnClickListener(this);
        barbecue = findViewById(R.id.barbecue);
        barbecue.setOnClickListener(this);
        restaurant = findViewById(R.id.restaurant);
        restaurant.setOnClickListener(this);

        tabs = new String[]{"","","","","","","",""};

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
        timeChosePanelQuit.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                timeChosePanel.setVisibility(View.INVISIBLE);
            }
        });
        timeChosePanelTitle = timeChosePanel.findViewById(R.id.choseDate);
        timeChosePanelTitle.setText("请选择出行与返程时间：");

        timeShowPanel = findViewById(R.id.panel_time_show);
        timeShowPanel.setOnClickListener(this);
        chosenStartDate = timeShowPanel.findViewById(R.id.chosenStartDate);
        chosenStartDate.setText("6月1日");
        startTime = "6月1日";
        chosenEndDate = timeShowPanel.findViewById(R.id.chosenEndDate);
        chosenEndDate.setText("6月2日");
        endTime = "6月2日";
        days = timeShowPanel.findViewById(R.id.days);
        days.setText("共2天>");
        dayCount = 2;
        startMonth = 6;
        startDay = 1;
        endMonth = 6;
        endDay = 2;

        peopleShowPanel = findViewById(R.id.panel_people_show);
        peopleShowPanel.setOnClickListener(this);
        adultsText = peopleShowPanel.findViewById(R.id.adults);
        childrenText = peopleShowPanel.findViewById(R.id.children);
        adultsText.setText("1名成人");
        adults = 1;
        childrenText.setText("0名儿童");
        children = 0;
        persons = 1;

        peopleChosePanel = findViewById(R.id.panel_people_chose);
        adultsNumberPick = peopleChosePanel.findViewById(R.id.adults_pick);
        childrenNumberPick = peopleChosePanel.findViewById(R.id.children_pick);
        ObjectAnimator delay1 = ofFloat(peopleChosePanel, "rotation", 0, 0);
        delay.setDuration(500);
        ObjectAnimator quit1 = ofFloat(peopleChosePanel, "translationY", 0, screenHeight);
        quit.setDuration(500);
        peopleChosePanelQuit = new AnimatorSet();
        peopleChosePanelQuit.play(delay1).before(quit1);
        peopleChosePanelQuit.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                peopleChosePanel.setVisibility(View.INVISIBLE);
            }
        });

        customized = findViewById(R.id.customized);
        customized.setOnClickListener(this);
        customizedText = findViewById(R.id.customized_text);

        initNumberPick();
    }

    public void initNumberPick() {
        adultsNumberPick.setMaxValue(15);
        adultsNumberPick.setMinValue(0);
        adultsNumberPick.setValue(1);
        adultsNumberPick.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        adultsNumberPick.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.i("adults", String.valueOf(newVal));
                adults = newVal;
                persons = adults + children;
                adultsText.setText(String.valueOf(adults) + "名成人");
            }
        });

        childrenNumberPick.setMaxValue(8);
        childrenNumberPick.setMinValue(0);
        childrenNumberPick.setValue(0);
        childrenNumberPick.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        childrenNumberPick.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.i("children", String.valueOf(newVal));
                children = newVal;
                persons = adults + children;
                childrenText.setText(String.valueOf(children) + "名儿童");
            }
        });

        getDateInformation();

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
            case R.id.drift:
                boolean chosen = false;
                for (int i = 0; i < tabs.length; i++) {
                    if (tabs[i].equals("drift")) {
                        chosen = true;
                        tabs[i] = "";
                        break;
                    }
                }
                if (chosen) {
                    drift.setBackground(getResources().getDrawable(R.drawable.shape_catalog1));
                    drift.setTextColor(getResources().getColor(R.color.colorTextGray));
                } else {
                    for (int i = 0; i < tabs.length; i++) {
                        if (tabs[i].equals("")) {
                            tabs[i] = "drift";
                            drift.setBackground(getResources().getDrawable(R.drawable.shape_catalog2));
                            drift.setTextColor(getResources().getColor(R.color.colorWhite));
                            break;
                        }
                    }
                }
                showTabs(tabs);
                break;
            case R.id.chess:
                boolean chosen2 = false;
                for (int i = 0; i < tabs.length; i++) {
                    if (tabs[i].equals("chess")) {
                        chosen2 = true;
                        tabs[i] = "";
                        break;
                    }
                }
                if (chosen2) {
                    chess.setBackground(getResources().getDrawable(R.drawable.shape_catalog1));
                    chess.setTextColor(getResources().getColor(R.color.colorTextGray));
                } else {
                    for (int i = 0; i < tabs.length; i++) {
                        if (tabs[i].equals("")) {
                            tabs[i] = "chess";
                            chess.setBackground(getResources().getDrawable(R.drawable.shape_catalog2));
                            chess.setTextColor(getResources().getColor(R.color.colorWhite));
                            break;
                        }
                    }
                }
                showTabs(tabs);
                break;
            case R.id.ktv:
                boolean chosen3 = false;
                for (int i = 0; i < tabs.length; i++) {
                    if (tabs[i].equals("ktv")) {
                        chosen3 = true;
                        tabs[i] = "";
                        break;
                    }
                }
                if (chosen3) {
                    ktv.setBackground(getResources().getDrawable(R.drawable.shape_catalog1));
                    ktv.setTextColor(getResources().getColor(R.color.colorTextGray));
                } else {
                    for (int i = 0; i < tabs.length; i++) {
                        if (tabs[i].equals("")) {
                            tabs[i] = "ktv";
                            ktv.setBackground(getResources().getDrawable(R.drawable.shape_catalog2));
                            ktv.setTextColor(getResources().getColor(R.color.colorWhite));
                            break;
                        }
                    }
                }
                showTabs(tabs);
                break;
            case R.id.barbecue:
                boolean chosen4 = false;
                for (int i = 0; i < tabs.length; i++) {
                    if (tabs[i].equals("barbecue")) {
                        chosen4 = true;
                        tabs[i] = "";
                        break;
                    }
                }
                if (chosen4) {
                    barbecue.setBackground(getResources().getDrawable(R.drawable.shape_catalog1));
                    barbecue.setTextColor(getResources().getColor(R.color.colorTextGray));
                } else {
                    for (int i = 0; i < tabs.length; i++) {
                        if (tabs[i].equals("")) {
                            tabs[i] = "barbecue";
                            barbecue.setBackground(getResources().getDrawable(R.drawable.shape_catalog2));
                            barbecue.setTextColor(getResources().getColor(R.color.colorWhite));
                            break;
                        }
                    }
                }
                showTabs(tabs);
                break;
            case R.id.fishing:
                boolean chosen5 = false;
                for (int i = 0; i < tabs.length; i++) {
                    if (tabs[i].equals("fishing")) {
                        chosen5 = true;
                        tabs[i] = "";
                        break;
                    }
                }
                if (chosen5) {
                    fishing.setBackground(getResources().getDrawable(R.drawable.shape_catalog1));
                    fishing.setTextColor(getResources().getColor(R.color.colorTextGray));
                } else {
                    for (int i = 0; i < tabs.length; i++) {
                        if (tabs[i].equals("")) {
                            tabs[i] = "fishing";
                            fishing.setBackground(getResources().getDrawable(R.drawable.shape_catalog2));
                            fishing.setTextColor(getResources().getColor(R.color.colorWhite));
                            break;
                        }
                    }
                }
                showTabs(tabs);
                break;
            case R.id.pick:
                boolean chosen6 = false;
                for (int i = 0; i < tabs.length; i++) {
                    if (tabs[i].equals("pick")) {
                        chosen6 = true;
                        tabs[i] = "";
                        break;
                    }
                }
                if (chosen6) {
                    pick.setBackground(getResources().getDrawable(R.drawable.shape_catalog1));
                    pick.setTextColor(getResources().getColor(R.color.colorTextGray));
                } else {
                    for (int i = 0; i < tabs.length; i++) {
                        if (tabs[i].equals("")) {
                            tabs[i] = "pick";
                            pick.setBackground(getResources().getDrawable(R.drawable.shape_catalog2));
                            pick.setTextColor(getResources().getColor(R.color.colorWhite));
                            break;
                        }
                    }
                }
                showTabs(tabs);
                break;
            case R.id.sightseeing:
                boolean chosen7 = false;
                for (int i = 0; i < tabs.length; i++) {
                    if (tabs[i].equals("sightseeing")) {
                        chosen7 = true;
                        tabs[i] = "";
                        break;
                    }
                }
                if (chosen7) {
                    sightseeing.setBackground(getResources().getDrawable(R.drawable.shape_catalog1));
                    sightseeing.setTextColor(getResources().getColor(R.color.colorTextGray));
                } else {
                    for (int i = 0; i < tabs.length; i++) {
                        if (tabs[i].equals("")) {
                            tabs[i] = "sightseeing";
                            sightseeing.setBackground(getResources().getDrawable(R.drawable.shape_catalog2));
                            sightseeing.setTextColor(getResources().getColor(R.color.colorWhite));
                            break;
                        }
                    }
                }
                showTabs(tabs);
                break;
            case R.id.restaurant:
                boolean chosen8 = false;
                for (int i = 0; i < tabs.length; i++) {
                    if (tabs[i].equals("restaurant")) {
                        chosen8 = true;
                        tabs[i] = "";
                        break;
                    }
                }
                if (chosen8) {
                    restaurant.setBackground(getResources().getDrawable(R.drawable.shape_catalog1));
                    restaurant.setTextColor(getResources().getColor(R.color.colorTextGray));
                } else {
                    for (int i = 0; i < tabs.length; i++) {
                        if (tabs[i].equals("")) {
                            tabs[i] = "restaurant";
                            restaurant.setBackground(getResources().getDrawable(R.drawable.shape_catalog2));
                            restaurant.setTextColor(getResources().getColor(R.color.colorWhite));
                            break;
                        }
                    }
                }
                showTabs(tabs);
                break;
            case R.id.panel_people_show:
                peopleChosePanel.setVisibility(View.VISIBLE);
                ObjectAnimator objectAnimator1 = ofFloat(peopleChosePanel,
                        "translationY",
                        screenHeight, 0);
                objectAnimator1.setDuration(1000);
                objectAnimator1.start();
                shelter.setVisibility(View.VISIBLE);
                break;
            case R.id.panel_time_show:
                initTimePanel();
                timeChosePanel.setVisibility(View.VISIBLE);
                ObjectAnimator objectAnimator2 = ofFloat(timeChosePanel,
                        "translationY",
                        screenHeight, 0);
                objectAnimator2.setDuration(1000);
                objectAnimator2.start();
                shelter.setVisibility(View.VISIBLE);
                break;
            case R.id.shelter:
            case R.id.cancel:
                shelter.setVisibility(View.INVISIBLE);
                if (!firstTouch) {
                    startTime = chosenStartDate.getText().toString();
                    firstTouch = true;
                }
                timeChosePanelQuit.start();
                peopleChosePanelQuit.start();
                break;
            case R.id.customized:
                if (dayCount > 7) {
                    showToast("暂时只提供7日内农家乐项目定制。");
                    customized.startAnimation(shakeAnimation());
                    customizedText.startAnimation(shakeAnimation());
                } else if (endMonth != 6 || startMonth != 6) {
                    customized.startAnimation(shakeAnimation());
                    customizedText.startAnimation(shakeAnimation());
                    showToast("暂时只提供6月份的农家乐旅游项目定制。");
                } else {
                    boolean tab = false;
                    for (int i = 0; i < tabs.length; i++) {
                        if (!tabs[i].equals("")) {
                            tab = true;
                            break;
                        }
                    }
                    if (tab) {
                        Intent intent = new Intent(CustomizedActivity.this, TravelPlanActivity.class);
                        intent.putExtra("UserId", userId);
                        intent.putExtra("tabs", tabs);
                        intent.putExtra("children", children);
                        intent.putExtra("adults", adults);
                        intent.putExtra("startTime", startDay);
                        intent.putExtra("endTime", endDay);
                        intent.putExtra("days", dayCount);
                        startActivityForResult(intent, 0);
                    } else {
                        customized.startAnimation(shakeAnimation());
                        customizedText.startAnimation(shakeAnimation());
                        showToast("请至少选择一个你喜欢的标签。");
                    }
                }
                break;
            case R.id.back:
                finish();
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

        int[] weekArray = new int[7];
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
                            holder.SunDetail.setText("出发");
                            holder.SunDetail.setVisibility(View.VISIBLE);
                            startTime = String.valueOf(month) + "月" + holder.Sun.getText().toString() + "日";
                            startMonth = month;
                            startDay = Integer.parseInt(holder.Sun.getText().toString());
                            Log.i("StartTime", startTime);
                            firstTouch = false;
                        } else {
                            endMonth = month;
                            endDay = Integer.parseInt(holder.Sun.getText().toString());
                            dayCount = getDays(startMonth, startDay, endMonth, endDay);
                            if (dayCount > 0) {
                                v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                                holder.SunDetail.setText("返程");
                                holder.SunDetail.setVisibility(View.VISIBLE);
                                endTime = String.valueOf(month) + "月" + holder.Sun.getText().toString() + "日";
                                days.setText("共" + String.valueOf(dayCount) + "天>");
                                Log.i("endTime", endTime);
                                chosenStartDate.setText(startTime);
                                chosenEndDate.setText(endTime);
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
                            holder.MonDetail.setText("出发");
                            holder.MonDetail.setVisibility(View.VISIBLE);
                            startTime = String.valueOf(month) + "月" + holder.Mon.getText().toString() + "日";
                            startMonth = month;
                            startDay = Integer.parseInt(holder.Mon.getText().toString());
                            firstTouch = false;
                        } else {
                            endMonth = month;
                            endDay = Integer.parseInt(holder.Mon.getText().toString());
                            dayCount = getDays(startMonth, startDay, endMonth, endDay);
                            if (dayCount > 0) {
                                v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                                holder.MonDetail.setText("返程");
                                holder.MonDetail.setVisibility(View.VISIBLE);
                                endTime = String.valueOf(month) + "月" + holder.Mon.getText().toString() + "日";
                                days.setText("共" + String.valueOf(dayCount) + "天>");
                                shelter.setVisibility(View.INVISIBLE);
                                chosenStartDate.setText(startTime);
                                chosenEndDate.setText(endTime);
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
                            holder.TueDetail.setText("出发");
                            holder.TueDetail.setVisibility(View.VISIBLE);
                            startTime = String.valueOf(month) + "月" + holder.Tue.getText().toString() + "日";
                            startMonth = month;
                            startDay = Integer.parseInt(holder.Tue.getText().toString());
                            firstTouch = false;
                        } else {
                            endMonth = month;
                            endDay = Integer.parseInt(holder.Tue.getText().toString());
                            dayCount = getDays(startMonth, startDay, endMonth, endDay);
                            if (dayCount > 0) {
                                v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                                holder.TueDetail.setText("返程");
                                holder.TueDetail.setVisibility(View.VISIBLE);
                                endTime = String.valueOf(month) + "月" + holder.Tue.getText().toString() + "日";
                                days.setText("共" + String.valueOf(dayCount) + "天>");
                                shelter.setVisibility(View.INVISIBLE);
                                chosenStartDate.setText(startTime);
                                chosenEndDate.setText(endTime);
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
                            holder.WedDetail.setText("出发");
                            holder.WedDetail.setVisibility(View.VISIBLE);
                            startTime = String.valueOf(month) + "月" + holder.Wed.getText().toString() + "日";
                            startMonth = month;
                            startDay = Integer.parseInt(holder.Wed.getText().toString());
                            firstTouch = false;
                        } else {
                            endMonth = month;
                            endDay = Integer.parseInt(holder.Wed.getText().toString());
                            dayCount = getDays(startMonth, startDay, endMonth, endDay);
                            if (dayCount > 0) {
                                v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                                holder.WedDetail.setText("返程");
                                holder.WedDetail.setVisibility(View.VISIBLE);
                                endTime = String.valueOf(month) + "月" + holder.Wed.getText().toString() + "日";
                                days.setText("共" + String.valueOf(dayCount) + "天>");
                                shelter.setVisibility(View.INVISIBLE);
                                chosenStartDate.setText(startTime);
                                chosenEndDate.setText(endTime);
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
                            holder.ThuDetail.setText("出发");
                            holder.ThuDetail.setVisibility(View.VISIBLE);
                            startTime = String.valueOf(month) + "月" + holder.Thu.getText().toString() + "日";
                            startMonth = month;
                            startDay = Integer.parseInt(holder.Thu.getText().toString());
                            firstTouch = false;
                        } else {
                            endMonth = month;
                            endDay = Integer.parseInt(holder.Thu.getText().toString());
                            dayCount = getDays(startMonth, startDay, endMonth, endDay);
                            if (dayCount > 0) {
                                v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                                holder.ThuDetail.setText("返程");
                                holder.ThuDetail.setVisibility(View.VISIBLE);
                                endTime = String.valueOf(month) + "月" + holder.Thu.getText().toString() + "日";
                                days.setText("共" + String.valueOf(dayCount) + "天>");
                                shelter.setVisibility(View.INVISIBLE);
                                chosenStartDate.setText(startTime);
                                chosenEndDate.setText(endTime);
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
                            holder.FriDetail.setText("出发");
                            holder.FriDetail.setVisibility(View.VISIBLE);
                            startTime = String.valueOf(month) + "月" + holder.Fri.getText().toString() + "日";
                            startMonth = month;
                            startDay = Integer.parseInt(holder.Fri.getText().toString());
                            firstTouch = false;
                        } else {
                            endMonth = month;
                            endDay = Integer.parseInt(holder.Fri.getText().toString());
                            dayCount = getDays(startMonth, startDay, endMonth, endDay);
                            if (dayCount > 0) {
                                v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                                holder.FriDetail.setText("返程");
                                holder.FriDetail.setVisibility(View.VISIBLE);
                                endTime = String.valueOf(month) + "月" + holder.Fri.getText().toString() + "日";
                                days.setText("共" + String.valueOf(dayCount) + "天>");
                                shelter.setVisibility(View.INVISIBLE);
                                chosenStartDate.setText(startTime);
                                chosenEndDate.setText(endTime);
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
                            holder.SatDetail.setText("出发");
                            holder.SatDetail.setVisibility(View.VISIBLE);
                            startTime = String.valueOf(month) + "月" + holder.Sat.getText().toString() + "日";
                            startMonth = month;
                            startDay = Integer.parseInt(holder.Sat.getText().toString());
                            firstTouch = false;
                        } else {
                            endMonth = month;
                            endDay = Integer.parseInt(holder.Sat.getText().toString());
                            dayCount = getDays(startMonth, startDay, endMonth, endDay);
                            if (dayCount > 0) {
                                v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                                holder.SatDetail.setText("返程");
                                holder.SatDetail.setVisibility(View.VISIBLE);
                                endTime = String.valueOf(month) + "月" + holder.Sat.getText().toString() + "日";
                                days.setText("共" + String.valueOf(dayCount) + "天>");
                                shelter.setVisibility(View.INVISIBLE);
                                chosenStartDate.setText(startTime);
                                chosenEndDate.setText(endTime);
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

    public int getDays(int startMonth, int startDay, int endMonth, int endDay) {
        int days = 0;
        if (startMonth == endMonth) {
            days = endDay - startDay + 1;
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
            days = endDay - startDay + 1;
        }
        return days;
    }

    public void showTabs(String[] tabs) {
        String string = "";
        for (int i = 0; i < tabs.length; i++) {
            if (!tabs[i].equals("")) {
                string = string + (tabs[i] + ',');
            }
        }
        Log.i("tabs", string);
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
        }
    }

    private Animation shakeAnimation() {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(5));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }

}
