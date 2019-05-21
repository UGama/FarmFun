package com.gama.farm_fun;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

public class VideoPageActivity extends AppCompatActivity {
    private RecyclerView videoRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private List<String> urlList;
    private SnapHelper snapHelper;
    private ListVideoAdapter listVideoAdapter;

    private View loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_page);
        ButterKnife.bind(this);

        loading = findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
        initData();
    }

    public void initData() {
        urlList = new ArrayList<>();

        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereStartsWith("name", "video");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                for (AVObject avObject : avObjects) {
                    urlList.add(avObject.getString("url"));
                }
                initUI();
            }
        });
    }

    public void initUI() {
        videoRecyclerView = findViewById(R.id.videoRecyclerView);
        listVideoAdapter = new ListVideoAdapter(urlList);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        videoRecyclerView.setLayoutManager(linearLayoutManager);
        videoRecyclerView.setAdapter(listVideoAdapter);

        snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(videoRecyclerView);

        loading.setVisibility(View.INVISIBLE);

        videoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE://停止滚动
                        View view = snapHelper.findSnapView(linearLayoutManager);
                        JZVideoPlayer.releaseAllVideos();
                        RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
                        if (viewHolder != null && viewHolder instanceof VideoViewHolder) {
                            ((VideoViewHolder) viewHolder).mp_video.startVideo();
                        }

                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING://拖动
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING://惯性滑动
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private class ListVideoAdapter extends BaseRecAdapter<String, VideoViewHolder> {


        public ListVideoAdapter(List<String> list) {
            super(list);
        }

        @Override
        public void onHolder(VideoViewHolder holder, String bean, int position) {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            holder.title.setText("楠溪江太好玩了！");
            holder.mp_video.setUp(bean, JZVideoPlayerStandard.CURRENT_STATE_NORMAL);
            if (position == 0) {
                holder.mp_video.startVideo();
            }
            Glide.with(context).load(bean).into(holder.mp_video.thumbImageView);
        }

        @Override
        public VideoViewHolder onCreateHolder() {
            return new VideoViewHolder(getViewByRes(R.layout.item_video_page));
        }


    }

    public class VideoViewHolder extends BaseRecViewHolder {
        public View rootView;
        public MyVideoPlayer mp_video;
        public TextView title;

        public VideoViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.mp_video = rootView.findViewById(R.id.mp_video);
            this.title = rootView.findViewById(R.id.title);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }
}


