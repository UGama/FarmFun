package com.gama.farm_fun;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

public class LeanCloudApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this,
                "kdUtqr57OFKrSuNO5VBChQn3-gzGzoHsz",
                "j5dBUyNfKLg36UM1AR3YXmJ7");
        AVOSCloud.setDebugLogEnabled(true);
    }
}
