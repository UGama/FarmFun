package com.gama.farm_fun;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.facebook.drawee.view.SimpleDraweeView;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private SimpleDraweeView themePic;

    private Toast toast;

    private ConstraintLayout userNameLayout;
    private EditText userName;
    private Button clearUserName;
    private String userNameString;

    private ConstraintLayout passwordLayout;
    private EditText password;
    private Button clearPassword;
    private String passwordString;

    private ConstraintLayout mobileLayout;
    private EditText mobile;
    private Button clearMobile;
    private String mobileString;

    private Button register;

    private View loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loading = findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);

        initUI();
    }

    public void initUI() {
        themePic = findViewById(R.id.themePic);
        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereEqualTo("name", "begin2.jpg");
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                Uri uri = Uri.parse(object.getString("url"));
                themePic.setImageURI(uri);
            }
        });
        userNameLayout = findViewById(R.id.userNamePanel);
        userName = findViewById(R.id.userName);
        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                clearUserName.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    clearUserName.setVisibility(View.INVISIBLE);
                } else {
                    clearUserName.setVisibility(View.VISIBLE);
                }
                Log.i("words", String.valueOf(s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        clearUserName = findViewById(R.id.clearUserName);
        clearUserName.setOnClickListener(this);

        passwordLayout = findViewById(R.id.userPasswordPanel);
        password = findViewById(R.id.password);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                clearPassword.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    clearPassword.setVisibility(View.INVISIBLE);
                } else {
                    clearPassword.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        clearPassword = findViewById(R.id.clearPassword);
        clearPassword.setOnClickListener(this);

        mobileLayout = findViewById(R.id.mobileNumberPanel);
        mobile = findViewById(R.id.mobileNumber);
        mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                clearMobile.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    clearMobile.setVisibility(View.INVISIBLE);
                } else {
                    clearMobile.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        clearMobile = findViewById(R.id.clearMobile);
        clearMobile.setOnClickListener(this);

        register = findViewById(R.id.button_register);
        register.setOnClickListener(this);

        loading.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clearUserName:
                userName.setText("");
                clearUserName.setVisibility(View.INVISIBLE);
                break;
            case R.id.clearPassword:
                password.setText("");
                clearPassword.setVisibility(View.INVISIBLE);
                break;
            case R.id.clearMobile:
                mobile.setText("");
                clearMobile.setVisibility(View.INVISIBLE);
                break;
            case R.id.button_register:
                userNameString = userName.getText().toString();
                passwordString = password.getText().toString();
                mobileString = mobile.getText().toString();
                if (userNameString.isEmpty()) {
                    showToast("用户名不能为空！");
                    userNameLayout.startAnimation(shakeAnimation());
                } else if (passwordString.isEmpty()) {
                    showToast("密码不能为空！");
                    passwordLayout.startAnimation(shakeAnimation());
                } else if (mobileString.isEmpty()) {
                    showToast("请填写手机号!");
                    mobileLayout.startAnimation(shakeAnimation());
                } else if (checkUserName(userNameString)) {
                    userNameLayout.startAnimation(shakeAnimation());
                } else if (checkPassword(passwordString)) {
                    passwordLayout.startAnimation(shakeAnimation());
                } else if (checkMobileNumber(mobileString)) {
                    mobileLayout.startAnimation(shakeAnimation());
                } else {
                    checkUserNameRepeat();
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

    private Animation shakeAnimation() {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(5));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }

    private boolean checkMobileNumber(String mobileNumber) {
        if (mobileNumber.length() != 11 || mobileNumber.toCharArray()[0] != '1') {
            showToast("请输入正确的手机号！");
            return true;
        } else {
            return false;
        }
    }

    private boolean checkUserName(String userName) {
        if (userName.length() >= 14) {
            showToast("用户名不能超过13个字符！");
            return true;
        } else if (userName.length() < 6) {
            showToast("用户名不能低于6个字符！");
            return true;
        } else {
            char[] userNameChar = userName.toCharArray();
            boolean userCharBoolean = false;
            for (int i = 0; i < userName.length(); i++) {
                if (userNameChar[i] >= 'a' & userNameChar[i] <= 'z' || (userNameChar[i] >= 'A' & userNameChar[i] <= 'Z')
                        || (userNameChar[i] >= '0' & userNameChar[i] <= '9')) {
                } else {
                    userCharBoolean = true;
                    showToast("用户名仅限大小写字母以及数字！");
                    break;
                }
            }
            return userCharBoolean;
        }
    }

    private boolean checkPassword(String password) {
        if (password.length() >= 14) {
            showToast("密码不能超过13个字符！");
            return true;
        } else if (password.length() < 6) {
            showToast("密码不能低于6个字符！");
            return true;
        } else {
            char[] passwordChar = password.toCharArray();
            boolean userCharBoolean = false;
            for (int i = 0; i < password.length(); i++) {
                if (passwordChar[i] >= 'a' & passwordChar[i] <= 'z' || (passwordChar[i] >= 'A' & passwordChar[i] <= 'Z')
                        || (passwordChar[i] >= '0' & passwordChar[i] <= '9')) {
                } else {
                    userCharBoolean = true;
                    showToast("密码仅限大小写字母以及数字！");
                    break;
                }
            }
            return userCharBoolean;
        }
    }

    private void checkUserNameRepeat() {
        AVQuery<AVObject> query = new AVQuery<>("Users");
        query.whereEqualTo("name", userNameString);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                if (object == null) {
                    checkMobileRepeat();
                } else {
                    showToast("该用户名已被注册！");
                    userNameLayout.startAnimation(shakeAnimation());
                }
            }
        });
    }

    private void checkMobileRepeat() {
        AVQuery<AVObject> query = new AVQuery<>("Users");
        query.whereEqualTo("mobilePhone", mobileString);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                if (object == null) {
                    finishRegister();
                } else {
                    showToast("该手机号已被注册！");
                    mobileLayout.startAnimation(shakeAnimation());
                }
            }
        });
    }

    private void finishRegister() {
        final AVObject avObject = new AVObject("Users");
        avObject.put("name", userName.getText().toString());
        avObject.put("password", password.getText().toString());
        avObject.put("mobilePhone", mobile.getText().toString());
        avObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    char[] netNameArray = avObject.getObjectId().toCharArray();
                    char[] netNameArray2 = new char[7];
                    for (int i = 0; i < 7; i++) {
                        netNameArray2[i] = netNameArray[i];
                    }
                    String netName = "小农" + String.valueOf(netNameArray2);
                    Log.i("netName", netName);
                    AVObject avObject1 = AVObject.createWithoutData("Users", avObject.getObjectId());
                    avObject1.put("netName", netName);
                    avObject1.saveInBackground();
                    showToast("注册成功！");
                    finish();
                }
            }
        });
    }
}
