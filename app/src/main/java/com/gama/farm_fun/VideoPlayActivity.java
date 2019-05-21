package com.gama.farm_fun;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.IOException;

public class VideoPlayActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnPreparedListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    public String path;
    public SurfaceView surfaceView;
    public MediaPlayer mediaPlayer;
    public ImageView playOrPause;

    public SeekBar seekBar;

    public VideoHandler videoHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_play);
        path = "http://lc-kdUtqr57.cn-n1.lcfile.com/4c916e962537e97b02e5/test.mp4";

        initUI();
    }

    public void initUI() {
        surfaceView = findViewById(R.id.surfaceView);
        surfaceView.setZOrderOnTop(false);
        surfaceView.getHolder().addCallback(this);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnVideoSizeChangedListener(this);
        try {
            mediaPlayer.setDataSource(this, Uri.parse(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        playOrPause = findViewById(R.id.playOrPause);
        playOrPause.setOnClickListener(this);

        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);

        videoHandler = new VideoHandler(this);

    }





    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mediaPlayer.setDisplay(holder);
        mediaPlayer.prepareAsync();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        seekBar.setMax(mp.getDuration());
        seekBar.setProgress(mp.getCurrentPosition());
        Log.i("duration", String.valueOf(mediaPlayer.getDuration()));
    }
    private void play() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playOrPause.setVisibility(View.VISIBLE);
            playOrPause.setImageResource(android.R.drawable.ic_media_play);
            videoHandler.removeMessages(0);
        } else {
            mediaPlayer.start();
            playOrPause.setVisibility(View.VISIBLE);
            playOrPause.setImageResource(android.R.drawable.ic_media_pause);
            videoHandler.sendEmptyMessageDelayed(0, 500);
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playOrPause:
                play();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mediaPlayer != null) {
            if (fromUser) {
                mediaPlayer.seekTo(progress);
                Log.i("progress", String.valueOf(progress));
            }
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


}
