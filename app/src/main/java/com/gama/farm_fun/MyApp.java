package com.gama.farm_fun;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.avos.avoscloud.AVOSCloud;
import com.danikula.videocache.HttpProxyCacheServer;

public class MyApp extends Application {
    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        MyApp app = (MyApp) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheSize(1024 * 1024 * 1024)       // 1 Gb for cache
                .fileNameGenerator(new MyFileNameGenerator())
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        AVOSCloud.initialize(this,
                "kdUtqr57OFKrSuNO5VBChQn3-gzGzoHsz",
                "j5dBUyNfKLg36UM1AR3YXmJ7");
        AVOSCloud.setDebugLogEnabled(true);
    }
}
