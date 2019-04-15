package com.gama.farm_fun;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;

public class HomeStayActivity extends AppCompatActivity {

    private ObservableScrollView observableScrollView;

    private ImageView homeStayPic;
    private TextView homeStayName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homestay);

        initUI();
    }

    public void initUI() {
        observableScrollView = findViewById(R.id.observableScrollView);

        homeStayPic = findViewById(R.id.mainPic);
        homeStayName = findViewById(R.id.homeStayName);

        getHomeStayInformation();
    }

    public void getHomeStayInformation() {
        AVQuery<AVObject> query = new AVQuery<>("HomeStay");
    }
}
