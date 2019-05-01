package com.gama.farm_fun;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

public class MineActivity extends AppCompatActivity implements View.OnClickListener {
    private String userId;
    private String netName;

    private ConstraintLayout userLayout;
    private SimpleDraweeView headPic;
    private TextView userName;

    private ImageView localSpecialtyOrder;
    private ImageView restaurantOrder;
    private ImageView homeStayOrder;
    private ImageView amusementOrder;

    private View comment;
    private ImageView commentIcon;
    private TextView commentText;
    private View setting;
    private ImageView settingIcon;
    private TextView settingText;
    private View allOrder;
    private ImageView allOrderIcon;
    private TextView allOrderText;

    private View bottomBar;
    private Button homePage;
    private Button news;
    private Button order;
    private Button mine;
    private TextView mineText;
    private Button post;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_mine);

        getUserInformation();
    }
    public void getUserInformation() {
        Intent intent = getIntent();
        userId = intent.getStringExtra("UserId");

        if (!userId.equals("tourist")) {
            AVQuery<AVObject> query = new AVQuery<>("Users");
            query.whereEqualTo("objectId", userId);
            query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject object, AVException e) {
                    netName = object.getString("netName");
                    userName = userLayout.findViewById(R.id.userName);
                    userName.setText(netName);
                    initUI();
                }
            });
        } else {
            initUI();
        }


    }
    public void initUI() {
        userLayout = findViewById(R.id.userLayout);
        userLayout.setOnClickListener(this);
        headPic = userLayout.findViewById(R.id.headPic);
        headPic.setImageResource(R.drawable.male);

        localSpecialtyOrder = findViewById(R.id.localSpecialtyOrder);
        localSpecialtyOrder.setOnClickListener(this);
        homeStayOrder = findViewById(R.id.homeStayOrder);
        homeStayOrder.setOnClickListener(this);
        restaurantOrder = findViewById(R.id.restaurantOrder);
        restaurantOrder.setOnClickListener(this);
        amusementOrder = findViewById(R.id.projectOrder);
        amusementOrder.setOnClickListener(this);

        comment = findViewById(R.id.comment);
        comment.setOnClickListener(this);
        commentIcon = comment.findViewById(R.id.common_icon);
        commentIcon.setImageResource(R.drawable.comment);
        commentText = comment.findViewById(R.id.common_name);
        commentText.setText("我的评价");
        setting = findViewById(R.id.setting);
        setting.setOnClickListener(this);
        settingIcon = setting.findViewById(R.id.common_icon);
        settingIcon.setImageResource(R.drawable.setting);
        settingText = setting.findViewById(R.id.common_name);
        settingText.setText("设置");
        allOrder = findViewById(R.id.all_order);
        allOrder.setOnClickListener(this);
        allOrderIcon = allOrder.findViewById(R.id.common_icon);
        allOrderIcon.setImageResource(R.drawable.allorder);
        allOrderText = allOrder.findViewById(R.id.common_name);
        allOrderText.setText("全部订单");
        bottomBar = findViewById(R.id.bottom_bar);
        homePage = bottomBar.findViewById(R.id.homePage);
        homePage.setOnClickListener(this);
        post = bottomBar.findViewById(R.id.post);
        post.setOnClickListener(this);
        news = bottomBar.findViewById(R.id.news);
        news.setOnClickListener(this);
        order = bottomBar.findViewById(R.id.order);
        order.setOnClickListener(this);
        mine = bottomBar.findViewById(R.id.mine);
        mine.setOnClickListener(this);
        mine.setBackground(getResources().getDrawable(R.drawable.mine1));
        mineText = bottomBar.findViewById(R.id.mineText);
        mineText.setTextColor(getResources().getColor(R.color.colorTheme));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.localSpecialtyOrder:
                break;
            case R.id.homeStayOrder:
                Intent homeStayOrderIntent = new Intent(MineActivity.this, MyOrder.class);
                homeStayOrderIntent.putExtra("Type", "HomeStay");
                homeStayOrderIntent.putExtra("UserId", userId);
                startActivity(homeStayOrderIntent);
                finish();
                break;
            case R.id.restaurantOrder:
                Intent restaurantOrderIntent = new Intent(MineActivity.this, MyOrder.class);
                restaurantOrderIntent.putExtra("Type", "Restaurant");
                restaurantOrderIntent.putExtra("UserId", userId);
                startActivity(restaurantOrderIntent);
                finish();
                break;
            case R.id.projectOrder:
                Intent amusementOrderIntent = new Intent(MineActivity.this, MyOrder.class);
                amusementOrderIntent.putExtra("Type", "Amusement");
                amusementOrderIntent.putExtra("UserId", userId);
                startActivity(amusementOrderIntent);
                break;
            case R.id.comment:
                break;
            case R.id.setting:
                break;
            case R.id.all_order:
                Intent allOrderIntent = new Intent(MineActivity.this, MyOrder.class);
                allOrderIntent.putExtra("Type", "all");
                startActivity(allOrderIntent);
                break;
            case R.id.userLayout:
                if (userId.equals("tourist")) {
                    Intent loginIntent = new Intent(MineActivity.this, LoginActivity.class);
                    startActivityForResult(loginIntent, 0);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    userId = data.getStringExtra("UserId");
                    Log.i("userId", userId);
                    AVQuery<AVObject> query = new AVQuery<>("Users");
                    query.whereEqualTo("objectId", userId);
                    query.getFirstInBackground(new GetCallback<AVObject>() {
                        @Override
                        public void done(AVObject object, AVException e) {
                            netName = object.getString("netName");

                        }
                    });
                }
                break;
        }
    }
}
