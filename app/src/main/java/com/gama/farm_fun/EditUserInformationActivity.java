package com.gama.farm_fun;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;

public class EditUserInformationActivity extends AppCompatActivity implements View.OnClickListener {
    private String userId;
    private String type;

    private View topBar;
    private TextView title;
    private Button back;

    private TextView tip;
    private EditText information;
    private Button confirm;

    private Toast toast;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_information);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        type = intent.getStringExtra("type");

        topBar = findViewById(R.id.top_bar);
        title = topBar.findViewById(R.id.title);
        back = topBar.findViewById(R.id.back);
        back.setOnClickListener(this);

        tip = findViewById(R.id.tip);
        information = findViewById(R.id.edit_information);
        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(this);

        if (type.equals("name")) {
            title.setText("修改昵称");
            tip.setText("请输入你想修改的昵称：");
        } else if (type.equals("phone")) {
            title.setText("修改手机号");
            tip.setText("请输入你想修改的手机号：");
            information.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.edit_information:
                if (type.equals("name")) {
                    checkNetName();
                } else if (type.equals("phone")) {
                    if (checkMobileNumber(information.getText().toString())) {
                        showToast("请输入正确的手机号！");
                    } else {
                        checkMobileRepeat();
                    }
                }
                break;
        }
    }

    private boolean checkMobileNumber(String mobileNumber) {
        if (mobileNumber.length() != 11 || mobileNumber.toCharArray()[0] != '1') {
            return true;
        } else {
            return false;
        }
    }
    private void checkMobileRepeat() {
        AVQuery<AVObject> query = new AVQuery<>("Users");
        query.whereEqualTo("mobilePhone", information.getText().toString());
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                if (object == null) {
                    changePhone();
                } else {
                    showToast("该手机号已被注册！");
                }
            }
        });
    }

    public void changePhone() {
        AVObject avObject = AVObject.createWithoutData("Users", userId);
        avObject.put("mobilePhone", information.getText().toString());
        avObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    showToast("修改成功！");
                    finish();
                }
            }
        });
    }

    public void checkNetName() {
        String netName = information.getText().toString();
        if (netName.equals("")) {
            showToast("请输入用户名");
        } else if (netName.length() <= 4) {
            showToast("用户名不得低于五个字符。");
        } else if (netName.length() >= 14) {
            showToast("用户名不得多于14个字符。");
        } else {
            changeNetName();
        }
    }

    public void changeNetName() {
        AVObject avObject = AVObject.createWithoutData("Users", userId);
        avObject.put("netName", information.getText().toString());
        avObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    showToast("修改成功！");
                    finish();
                }
            }
        });
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
