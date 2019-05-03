package com.gama.farm_fun;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_travel_plan);

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
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
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


        initData();
    }

    public void initData() {
        if (checkRestaurant()) {
            removeRestaurant();
            if (getTabsCount() > dayCount) {
                showTabs(tabs, "WhetherRemoveRestaurant");
                Log.i("getTabsCount", String.valueOf(getTabsCount()));
                newTabs = getTabsRandom(dayCount, getTabsCount());
                showTabs(newTabs, "newTabs");
            } else if (getTabsCount() < dayCount) {

            } else {

            }
        } else {
            if (getTabsCount() > dayCount) {
                newTabs = getTabsRandom(dayCount, getTabsCount());
                showTabs(newTabs, "newTabs");
            } else if (getTabsCount() < dayCount) {

            } else {

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
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
            randomString[i] = tabs[support[i]];
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
        String[] types = new String[]{"drift", "pick", "chess", "sightseeing", "ktv", "fishing", "barbecue"};
        return newTabs;
    }
}
