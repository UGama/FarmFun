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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

public class EditCommentActivity extends AppCompatActivity implements View.OnClickListener {

    private String orderId;
    private String projectName;
    private String projectPicUrl;
    private String orderDetail;
    private String orderCount;
    private String orderItem;

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

        initUI();
    }

    public void initUI() {
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

        comment = findViewById(R.id.comment);
        comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public void onClick(View v) {

    }
}
