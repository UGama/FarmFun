package com.gama.farm_fun;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

public class EditCommentActivity extends AppCompatActivity implements View.OnClickListener {
    private Toast toast;

    private View topBar;
    private TextView title;
    private TextView finish;
    private Button back;

    private String orderId;
    private String projectName;
    private String projectPicUrl;
    private String orderDetail;
    private String orderCount;
    private String orderItem;
    private String orderType;
    private String userId;

    private SimpleDraweeView projectPic;
    private TextView name;
    private TextView detail;
    private TextView count;
    private TextView item;

    private ImageView star1;
    private ImageView star2;
    private ImageView star3;
    private ImageView star4;
    private ImageView star5;

    private EditText comment;
    private TextView word;
    private boolean submit;

    private int rank;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_edit_comment);

        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        Log.i("Id", orderId);
        projectName = intent.getStringExtra("project");
        projectPicUrl = intent.getStringExtra("url");
        orderDetail = intent.getStringExtra("detail");
        orderCount = intent.getStringExtra("count");
        orderItem = intent.getStringExtra("item");
        orderType = intent.getStringExtra("type");
        userId = intent.getStringExtra("userId");

        initUI();
    }

    public void initUI() {
        topBar = findViewById(R.id.bar_top);
        title = topBar.findViewById(R.id.title);
        finish = topBar.findViewById(R.id.finish);
        title.setText("评论");
        finish.setVisibility(View.VISIBLE);
        finish.setText("完成");
        finish.setOnClickListener(this);
        back = topBar.findViewById(R.id.back);
        back.setOnClickListener(this);

        projectPic = findViewById(R.id.project_pic);
        Uri imageUri = Uri.parse(projectPicUrl);
        projectPic.setImageURI(imageUri);
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);
        projectPic.getHierarchy().setRoundingParams(roundingParams);
        name = findViewById(R.id.project_name);
        name.setText(projectName);
        detail = findViewById(R.id.detail);
        detail.setText(orderDetail);
        count = findViewById(R.id.count);
        count.setText(orderCount);
        item = findViewById(R.id.item);
        item.setText(orderItem);

        star1 = findViewById(R.id.star1);
        star1.setOnClickListener(this);
        star2 = findViewById(R.id.star2);
        star2.setOnClickListener(this);
        star3 = findViewById(R.id.star3);
        star3.setOnClickListener(this);
        star4 = findViewById(R.id.star4);
        star4.setOnClickListener(this);
        star5 = findViewById(R.id.star5);
        star5.setOnClickListener(this);

        word = findViewById(R.id.word);
        comment = findViewById(R.id.comment);
        comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                if (length >= 140) {
                    word.setText("（字数超过限制）" + String.valueOf(length));
                    word.setTextColor(getResources().getColor(R.color.colorRemind));
                    submit = false;
                } else {
                    word.setText(String.valueOf(length));
                    word.setTextColor(getResources().getColor(R.color.colorBlack));
                    submit = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        rank = 0;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finish:
                if (submit) {
                    if (rank == 0) {
                        showToast("请先评分！");
                    } else {
                        AVObject avObject = new AVObject("Comment");
                        avObject.put("orderId", orderId);
                        avObject.put("rank", rank);
                        avObject.put("comment", comment.getText().toString());
                        avObject.put("type", orderType);
                        avObject.put("item", orderItem);
                        avObject.put("userId", userId);
                        avObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    AVObject avObject = AVObject.createWithoutData("Order", orderId);
                                    avObject.put("comment", false);
                                    avObject.put("status", "已评价");
                                    avObject.saveInBackground();
                                    showToast("感谢评价!");
                                    Intent intent = new Intent();
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            }
                        });

                    }

                } else {
                    showToast("请输入正确的字数！");
                }
                break;
            case R.id.star1:
                star1.setImageResource(R.drawable.star2);
                star2.setImageResource(R.drawable.star);
                star3.setImageResource(R.drawable.star);
                star4.setImageResource(R.drawable.star);
                star5.setImageResource(R.drawable.star);
                rank = 1;
                break;
            case R.id.star2:
                star1.setImageResource(R.drawable.star2);
                star2.setImageResource(R.drawable.star2);
                star3.setImageResource(R.drawable.star);
                star4.setImageResource(R.drawable.star);
                star5.setImageResource(R.drawable.star);
                rank = 2;
                break;
            case R.id.star3:
                star1.setImageResource(R.drawable.star2);
                star2.setImageResource(R.drawable.star2);
                star3.setImageResource(R.drawable.star2);
                star4.setImageResource(R.drawable.star);
                star5.setImageResource(R.drawable.star);
                rank = 3;
                break;
            case R.id.star4:
                star1.setImageResource(R.drawable.star2);
                star2.setImageResource(R.drawable.star2);
                star3.setImageResource(R.drawable.star2);
                star4.setImageResource(R.drawable.star2);
                star5.setImageResource(R.drawable.star);
                rank = 4;
                break;
            case R.id.star5:
                star1.setImageResource(R.drawable.star2);
                star2.setImageResource(R.drawable.star2);
                star3.setImageResource(R.drawable.star2);
                star4.setImageResource(R.drawable.star2);
                star5.setImageResource(R.drawable.star2);
                rank = 5;
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
}
