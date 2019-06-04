package com.gama.farm_fun;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private String userId;

    private View topBar;
    private TextView title;
    private Button back;

    private Button logout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        topBar = findViewById(R.id.top_bar);
        title = topBar.findViewById(R.id.title);
        title.setText("设置");
        back = topBar.findViewById(R.id.back);
        back.setOnClickListener(this);

        logout = findViewById(R.id.logout);
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.logout:
                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putString("id", "tourist");
                editor.apply();
                setResult(RESULT_OK);
                finish();
                break;
        }
    }
}
