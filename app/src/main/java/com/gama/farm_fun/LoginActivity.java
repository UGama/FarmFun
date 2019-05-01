package com.gama.farm_fun;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.facebook.drawee.view.SimpleDraweeView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
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

    private Button login;
    private TextView register;

    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUI();
    }

    public void initUI() {
        themePic = findViewById(R.id.themePic);
        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereEqualTo("name", "begin1.jpg");
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

        login = findViewById(R.id.button_login);
        login.setOnClickListener(this);

        register = findViewById(R.id.button_register);
        register.setOnClickListener(this);


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
            case R.id.button_login:
                userNameString = userName.getText().toString();
                passwordString = password.getText().toString();
                if (userNameString.isEmpty()) {
                    showToast("用户名不能为空！");
                    userNameLayout.startAnimation(shakeAnimation());
                } else if (passwordString.isEmpty()) {
                    showToast("密码不能为空！");
                    passwordLayout.startAnimation(shakeAnimation());
                } else if (checkUserName(userNameString)) {
                    userNameLayout.startAnimation(shakeAnimation());
                } else if (checkPassword(passwordString)) {
                    passwordLayout.startAnimation(shakeAnimation());
                } else {
                    checkUserNameMatch();
                }
                break;
            case R.id.button_register:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
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

    private boolean checkUserName(String userName) {
        if (userName.length() >= 14) {
            showToast("该用户名不存在！");
            return true;
        } else if (userName.length() < 6) {
            showToast("该用户名不存在！");
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
            showToast("密码错误！");
            return true;
        } else if (password.length() < 6) {
            showToast("密码错误！");
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

    public void checkUserNameMatch() {
        AVQuery<AVObject> query = new AVQuery<>("Users");
        query.whereEqualTo("name", userNameString);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                if (object == null) {
                    checkMobileMatch();
                } else {
                    if (passwordString.equals(object.getString("password"))) {
                        userId = object.getObjectId();
                        finishLogin();
                    } else {
                        showToast("密码错误！");
                        passwordLayout.startAnimation(shakeAnimation());
                    }
                }
            }
        });
    }

    public void checkMobileMatch() {
        AVQuery<AVObject> query = new AVQuery<>("Users");
        query.whereEqualTo("mobilePhone", userNameString);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                if (object == null) {
                    showToast("该用户名不存在！");
                    userNameLayout.startAnimation(shakeAnimation());
                } else {
                    if (passwordString.equals(object.getString("password"))) {
                        userId = object.getObjectId();
                        finishLogin();
                    } else {
                        showToast("密码错误！");
                        passwordLayout.startAnimation(shakeAnimation());
                    }
                }
            }
        });
    }

    public void finishLogin() {
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putString("id", userId);
        editor.apply();
        Log.i("Id", userId);
        Intent intent = new Intent();
        intent.putExtra("UserId", userId);
        setResult(RESULT_OK, intent);
        finish();
    }

}
