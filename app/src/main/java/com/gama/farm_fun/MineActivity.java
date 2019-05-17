package com.gama.farm_fun;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private View address;
    private ImageView addressIcon;
    private TextView addressText;
    private View onlineOrder;
    private ImageView onlineOrderIcon;
    private TextView onlineOrderText;
    private View message;
    private ImageView messageIcon;
    private TextView messageText;
    private View customerService;
    private ImageView customerServiceIcon;
    private TextView customerServiceText;

    private View bottomBar;
    private Button homePage;
    private Button news;
    private Button order;
    private Button mine;
    private TextView mineText;
    private Button post;

    private Toast toast;

    private View postPanel;
    private Button postBack;
    private ImageView postComment;
    private ImageView postCustomized;
    private ImageView postJournal;
    private TextView postCustomizedText;
    private TextView postCommentText;
    private TextView postJournalText;

    private View loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_mine);

        loading = findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);

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
                    userLayout = findViewById(R.id.userLayout);
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
        userName = userLayout.findViewById(R.id.userName);

        localSpecialtyOrder = findViewById(R.id.toBeCommentedOrder);
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
        address = findViewById(R.id.address);
        address.setOnClickListener(this);
        addressIcon = address.findViewById(R.id.common_icon);
        addressIcon.setImageResource(R.drawable.address);
        addressText = address.findViewById(R.id.common_name);
        addressText.setText("我的地址");
        onlineOrder = findViewById(R.id.online_order);
        onlineOrder.setOnClickListener(this);
        onlineOrderIcon = onlineOrder.findViewById(R.id.common_icon);
        onlineOrderIcon.setImageResource(R.drawable.online_order);
        onlineOrderText = onlineOrder.findViewById(R.id.common_name);
        onlineOrderText.setText("商城订单");
        message = findViewById(R.id.message);
        message.setOnClickListener(this);
        messageIcon = message.findViewById(R.id.common_icon);
        messageIcon.setImageResource(R.drawable.icon_message2);
        messageText = message.findViewById(R.id.common_name);
        messageText.setText("我的消息");
        customerService = findViewById(R.id.customer_service);
        customerService.setOnClickListener(this);
        customerServiceIcon = customerService.findViewById(R.id.common_icon);
        customerServiceIcon.setImageResource(R.drawable.icon_customer_service);
        customerServiceText = customerService.findViewById(R.id.common_name);
        customerServiceText.setText("专属客服");

        bottomBar = findViewById(R.id.bottom_bar);
        homePage = bottomBar.findViewById(R.id.homePage);
        homePage.setOnClickListener(this);
        post = bottomBar.findViewById(R.id.post);
        post.setOnClickListener(this);
        news = bottomBar.findViewById(R.id.news);
        news.setOnClickListener(this);
        order = bottomBar.findViewById(R.id.onlineShop);
        order.setOnClickListener(this);
        mine = bottomBar.findViewById(R.id.mine);
        mine.setOnClickListener(this);
        mine.setBackground(getResources().getDrawable(R.drawable.mine1));
        mineText = bottomBar.findViewById(R.id.mineText);
        mineText.setTextColor(getResources().getColor(R.color.colorTheme));

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

        loading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toBeCommentedOrder:
                if (userId.equals("tourist")) {
                    showToast("请先登录。");
                } else {
                    Intent toBeCommentOrder = new Intent(MineActivity.this, MyOrderActivity.class);
                    toBeCommentOrder.putExtra("UserId", userId);
                    toBeCommentOrder.putExtra("Type", "comment");
                    startActivity(toBeCommentOrder);
                }
                break;
            case R.id.homeStayOrder:
                if (userId.equals("tourist")) {
                    showToast("请先登录。");
                } else {
                    Intent homeStayOrderIntent = new Intent(MineActivity.this, MyOrderActivity.class);
                    homeStayOrderIntent.putExtra("Type", "HomeStay");
                    homeStayOrderIntent.putExtra("UserId", userId);
                    startActivity(homeStayOrderIntent);
                }
                break;
            case R.id.restaurantOrder:
                if (userId.equals("tourist")) {
                    showToast("请先登录。");
                } else {
                    Intent restaurantOrderIntent = new Intent(MineActivity.this, MyOrderActivity.class);
                    restaurantOrderIntent.putExtra("Type", "Restaurant");
                    restaurantOrderIntent.putExtra("UserId", userId);
                    startActivity(restaurantOrderIntent);
                }
                break;
            case R.id.projectOrder:
                if (userId.equals("tourist")) {
                    showToast("请先登录。");
                } else {
                    Intent amusementOrderIntent = new Intent(MineActivity.this, MyOrderActivity.class);
                    amusementOrderIntent.putExtra("Type", "Amusement");
                    amusementOrderIntent.putExtra("UserId", userId);
                    startActivity(amusementOrderIntent);
                }
                break;
            case R.id.comment:
                if (userId.equals("tourist")) {
                    showToast("请先登录。");
                } else {
                    Intent commentIntent = new Intent(MineActivity.this, MyCommentActivity.class);
                    commentIntent.putExtra("UserId", userId);
                    startActivity(commentIntent);
                }
                break;
            case R.id.setting:
                break;
            case R.id.all_order:
                if (userId.equals("tourist")) {
                    showToast("请先登录。");
                } else {
                    Intent allOrderIntent = new Intent(MineActivity.this, MyOrderActivity.class);
                    allOrderIntent.putExtra("UserId", userId);
                    allOrderIntent.putExtra("Type", "all");
                    startActivity(allOrderIntent);
                }
                break;
            case R.id.message:
                if (userId.equals("tourist")) {
                    showToast("请先登录");
                } else {
                    Intent messageIntent = new Intent(MineActivity.this, ConversationListActivity.class);
                    messageIntent.putExtra("userId", userId);
                    startActivity(messageIntent);
                }
                break;
            case R.id.customer_service:
                if (userId.equals("tourist")) {
                    showToast("请先登录");
                } else {
                    Intent messageIntent = new Intent(MineActivity.this, ConversationListActivity.class);
                    messageIntent.putExtra("userId", userId);
                    messageIntent.putExtra("customer_service", true);
                    startActivity(messageIntent);
                }
                break;
            case R.id.userLayout:
                if (userId.equals("tourist")) {
                    Intent loginIntent = new Intent(MineActivity.this, LoginActivity.class);
                    startActivityForResult(loginIntent, 0);
                }
                break;
            case R.id.homePage:
                finish();
                break;
            case R.id.news:
                Intent intent = new Intent(MineActivity.this, NewsActivity.class);
                intent.putExtra("UserId", userId);
                startActivity(intent);
                finish();
                break;
            case R.id.onlineShop:
                Intent intent0 = new Intent(MineActivity.this, OnlineShopActivity.class);
                intent0.putExtra("UserId", userId);
                startActivity(intent0);
                finish();
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
                Intent intent1 = new Intent(MineActivity.this, CustomizedActivity.class);
                intent1.putExtra("UserId", userId);
                startActivity(intent1);
                break;
            case R.id.post_comment:
                Intent intent2 = new Intent(MineActivity.this, MyOrderActivity.class);
                intent2.putExtra("UserId", userId);
                intent2.putExtra("Type", "comment");
                startActivity(intent2);
                break;
            case R.id.address:
                if (userId.equals("tourist")) {
                    showToast("请先登录。");
                } else {
                    Intent intent3 = new Intent(MineActivity.this, AddressActivity.class);
                    intent3.putExtra("UserId", userId);
                    intent3.putExtra("Type", "manage");
                    startActivity(intent3);
                }
                break;
            case R.id.online_order:
                Intent intent4 = new Intent(MineActivity.this, MyCOrderActivity.class);
                intent4.putExtra("UserId", userId);
                startActivity(intent4);
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
                            userName.setText(netName);
                        }
                    });
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
