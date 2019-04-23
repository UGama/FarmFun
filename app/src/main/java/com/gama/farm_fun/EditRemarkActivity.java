package com.gama.farm_fun;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EditRemarkActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText remark;

    private View topBar;
    private TextView finish;
    private ImageView back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editremark);

        remark = findViewById(R.id.remark);

        topBar = findViewById(R.id.topBar);
        finish = topBar.findViewById(R.id.finish);
        finish.setText(getResources().getString(R.string.finish));
        finish.setVisibility(View.VISIBLE);
        finish.setOnClickListener(this);
        back = topBar.findViewById(R.id.back);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finish:
                Intent finishIntent = new Intent();
                finishIntent.putExtra("remark", remark.getText().toString());
                setResult(RESULT_OK, finishIntent);
                finish();
                break;
            case R.id.back:
                finish();
                break;
        }
    }
}
