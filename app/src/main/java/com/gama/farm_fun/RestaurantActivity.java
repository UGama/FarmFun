package com.gama.farm_fun;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;

import java.util.HashMap;

public class RestaurantActivity extends AppCompatActivity {

    private ImageView restaurantPic;
    private TextView restaurantName;
    private TextView locationDescribe;
    private TextView distance;
    private View midPanel;
    private View topPanel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        initUI();

    }
    public void initUI() {
        restaurantPic = findViewById(R.id.restaurantPic);
        restaurantName = findViewById(R.id.restaurantName);
        locationDescribe = findViewById(R.id.locationDescribe);
        topPanel = findViewById(R.id.topPanel);
        midPanel = findViewById(R.id.midPanel);
        getRestaurantInformation();
    }

    public void getRestaurantInformation() {
        AVQuery<AVObject> query = new AVQuery<>("Restaurant");
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                restaurantName.setText(object.getString("name"));
                locationDescribe.setText(object.getString("locateDescribe"));
                loadMainPic();
            }
        });
    }
    public void loadMainPic(){
        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereEqualTo("name", "restaurantmain.jpg");
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                AVFile avFile = new AVFile("Type.png", object.getString("url"), new HashMap<String, Object>());
                avFile.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, AVException e) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Log.i("bitmap(width/height)", String.valueOf(bitmap.getWidth()) + "/" + String.valueOf(bitmap.getHeight()));
                        restaurantPic.setImageBitmap(bitmap);

                    }
                }, new ProgressCallback() {
                    @Override
                    public void done(Integer percentDone) {
                        if (percentDone == 100) {

                        }
                    }
                });
            }
        });
    }
}
