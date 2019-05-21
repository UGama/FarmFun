package com.gama.farm_fun;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;

public class VideoHandler extends Handler {
    private WeakReference<VideoPlayActivity> videoPlayActivityWeakReference;

    VideoHandler(VideoPlayActivity videoPlayActivity) {
        videoPlayActivityWeakReference = new WeakReference<>(videoPlayActivity);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        VideoPlayActivity videoPlayActivity = videoPlayActivityWeakReference.get();
        switch (msg.what) {
            case 0:
                videoPlayActivity.seekBar.setProgress(videoPlayActivity.mediaPlayer.getCurrentPosition());
                Log.i("currentPosition", String.valueOf(videoPlayActivity.mediaPlayer.getCurrentPosition()));
                sendEmptyMessageDelayed(0, 500);
                break;
        }
    }
}
