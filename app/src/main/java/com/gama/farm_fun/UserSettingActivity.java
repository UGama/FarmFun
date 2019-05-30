package com.gama.farm_fun;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.facebook.drawee.backends.pipeline.Fresco;

public class UserSettingActivity extends AppCompatActivity implements View.OnClickListener {
    private String userId;

    private String netName;
    private String phone;
    private String sex;

    private ImageView headPic;

    private View netNameView;
    private TextView netNameTip;
    private TextView netNameText;
    private View phoneView;
    private TextView phoneTip;
    private TextView phoneText;
    private View sexView;
    private TextView sexTip;
    private TextView sexText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_user_setting);

        initUI();
    }

    public void initUI() {
        headPic = findViewById(R.id.headPic);
        headPic.setOnClickListener(this);

        netNameView = findViewById(R.id.user_name);
        netNameView.setOnClickListener(this);
        netNameTip = netNameView.findViewById(R.id.text);
        netNameTip.setText("昵称");
        netNameText = netNameView.findViewById(R.id.information);

        phoneView = findViewById(R.id.user_phone);
        phoneView.setOnClickListener(this);
        phoneTip = phoneView.findViewById(R.id.text);
        phoneTip.setText("手机");
        phoneText = phoneView.findViewById(R.id.information);

        sexView = findViewById(R.id.user_sex);
        sexView.setOnClickListener(this);
        sexTip = sexView.findViewById(R.id.text);
        sexTip.setText("性别");
        sexText = sexView.findViewById(R.id.information);

        getUserInformation();
    }

    public void getUserInformation() {
        AVQuery<AVObject> query = new AVQuery<>("Users");
        query.whereEqualTo("objectId", userId);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                netName = object.getString("netName");
                phone = object.getString("mobilePhone");
                sex = object.getString("sex");
                netNameText.setText(netName);
                phoneText.setText(phone);
                sexText.setText(sex);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headPic:
                break;
            case R.id.user_name:
                break;
            case R.id.user_phone:
                break;
            case R.id.user_sex:
                break;
        }
    }
}
