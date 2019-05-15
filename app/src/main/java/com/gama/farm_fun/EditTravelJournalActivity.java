package com.gama.farm_fun;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

public class EditTravelJournalActivity extends AppCompatActivity implements View.OnClickListener {
    private String userId;

    public View topBar;
    public Button back;
    public TextView title;
    public TextView finish;

    private EditText journal;
    private TextView word;

    private SimpleDraweeView testPic;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_edit_traveljournal);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        initUI();
    }

    public void initUI() {
        topBar = findViewById(R.id.bar_top);
        back = topBar.findViewById(R.id.back);
        back.setOnClickListener(this);
        title = topBar.findViewById(R.id.title);
        title.setText("发表游记");
        finish = topBar.findViewById(R.id.finish);
        finish.setText("发布");
        finish.setVisibility(View.VISIBLE);

        word = findViewById(R.id.word);
        journal = findViewById(R.id.journal);
        journal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                if (length >= 140) {
                    word.setText("（字数超过限制）" + String.valueOf(length));
                    word.setTextColor(getResources().getColor(R.color.colorRemind));
                } else {
                    word.setText(String.valueOf(length));
                    word.setTextColor(getResources().getColor(R.color.colorBlack));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /*testPic = findViewById(R.id.testPic);
        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereEqualTo("name", "journal3:4.jpg");
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                Uri uri = Uri.parse(object.getString("url"));
                testPic.setImageURI(uri);
                RoundingParams roundingParams = RoundingParams.fromCornersRadius(30f);
                testPic.getHierarchy().setRoundingParams(roundingParams);
            }
        });*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }
}
