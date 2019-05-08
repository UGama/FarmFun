package com.gama.farm_fun;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;

public class AddAddressActivity extends AppCompatActivity implements View.OnClickListener {
    private View topBar;
    private TextView title;
    private Button back;

    private String userId;
    private EditText name;
    private EditText phone;
    private EditText address;

    private String nameString;
    private String phoneString;
    private String addressString;

    private Button submit;

    private String edit;
    private String addressId;

    private Toast toast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        final Intent intent = getIntent();
        userId = intent.getStringExtra("UserId");
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);

        edit = "";
        edit = intent.getStringExtra("edit");
        if (edit.equals("edit")) {
            nameString = intent.getStringExtra("name");
            phoneString = intent.getStringExtra("phone");
            addressString = intent.getStringExtra("address");
            name.setText(nameString);
            phone.setText(phoneString);
            address.setText(addressString);
            addressId = intent.getStringExtra("id");
            submit = findViewById(R.id.submit);
            submit.setText("修 改 地 址");
        }


        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameString = name.getText().toString();
                phoneString = phone.getText().toString();
                addressString = address.getText().toString();
                if (edit.equals("edit")) {
                    AVObject avObject = AVObject.createWithoutData("Address", addressId);
                    avObject.put("address", addressString);
                    avObject.put("phone", phoneString);
                    avObject.put("name", nameString);
                    avObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                Intent intent1 = new Intent();
                                setResult(RESULT_OK, intent1);
                                showToast("修改成功！");
                                finish();
                            }
                        }
                    });
                } else {
                    AVObject avObject = new AVObject("Address");
                    avObject.put("userId", userId);
                    avObject.put("address", addressString);
                    avObject.put("name", nameString);
                    avObject.put("phone", phoneString);
                    avObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                Intent intent1 = new Intent();
                                setResult(RESULT_OK, intent1);
                                showToast("添加成功！");
                                finish();
                            }
                        }
                    });
                }

            }
        });

        topBar = findViewById(R.id.topBar);
        title = topBar.findViewById(R.id.title);
        title.setText("地址管理");
        back = topBar.findViewById(R.id.back);
        back.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
        }
    }
}
