package com.gama.farm_fun;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements View.OnClickListener {
    public String userId;

    public View bottomBar;
    public Button homePage;
    public Button post;
    public Button news;
    public TextView newsText;
    public Button mine;
    public Button order;

    public SimpleDraweeView projectPic;
    public String projectPicNameString;
    public TextView projectName;
    public TextView projectLocate;
    public TextView detail;

    public ViewPager viewPager;
    public PosterPagerAdapter posterPagerAdapter;
    public List<Poster> posterList;
    public ViewPagerIndicator indicator;

    public RecyclerView typeRecyclerView;
    public List<Type> typeList;
    public String typeShowing;
    public View choseTypeView;
    public boolean init;
    public String showType;

    public RecyclerView commentRecyclerView;
    public List<Comment> commentList;
    public int commentNumber;

    public RecyclerView travelJournalRecyclerView;
    public List<TravelJournal> travelJournalList;
    public int travelJournalSupportNumber;

    private View postPanel;
    private Button postBack;
    private ImageView postComment;
    private ImageView postCustomized;
    private ImageView postJournal;
    private TextView postCustomizedText;
    private TextView postCommentText;
    private TextView postJournalText;

    private View loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_news);

        Intent intent = getIntent();
        userId = intent.getStringExtra("UserId");

        loading = findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
        loading.setOnClickListener(this);

        initUI();
    }

    public void initUI() {
        bottomBar = findViewById(R.id.bottom_bar);
        homePage = bottomBar.findViewById(R.id.homePage);
        homePage.setOnClickListener(this);
        post = bottomBar.findViewById(R.id.post);
        post.setOnClickListener(this);
        news = bottomBar.findViewById(R.id.news);
        news.setOnClickListener(this);
        news.setBackground(getResources().getDrawable(R.drawable.news1));
        newsText = bottomBar.findViewById(R.id.newsText);
        newsText.setTextColor(getResources().getColor(R.color.colorTheme));
        order = bottomBar.findViewById(R.id.onlineShop);
        order.setOnClickListener(this);
        mine = bottomBar.findViewById(R.id.mine);
        mine.setOnClickListener(this);

        projectPic = findViewById(R.id.project_pic);
        projectName = findViewById(R.id.project_name);
        projectLocate = findViewById(R.id.project_locate);
        detail = findViewById(R.id.detail);
        detail.setOnClickListener(this);

        init = true;
        showType = "pick";

        postPanel = findViewById(R.id.panel_post);
        postPanel.setOnClickListener(this);
        postBack = postPanel.findViewById(R.id.post_back);
        postBack.setOnClickListener(this);
        postComment = postPanel.findViewById(R.id.post_comment);
        postComment.setOnClickListener(this);
        postCustomized = postPanel.findViewById(R.id.post_customized);
        postCustomized.setOnClickListener(this);
        postJournal = postPanel.findViewById(R.id.post_journal);
        postJournal.setOnClickListener(this);
        postCommentText = postPanel.findViewById(R.id.comment_text);
        postCustomizedText = postPanel.findViewById(R.id.customized_text);
        postJournalText = postPanel.findViewById(R.id.journal_text);

        initTypeRecyclerView();
    }

    public void initTypeRecyclerView() {
        typeRecyclerView = findViewById(R.id.typeRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        typeRecyclerView.setLayoutManager(linearLayoutManager);
        typeList = new ArrayList<>();

        Type type1 = new Type(R.drawable.pick, "蓝莓采摘", "pick");
        Type type2 = new Type(R.drawable.homestay, "民宿", "HomeStay");
        Type type3 = new Type(R.drawable.restaurant, "餐厅", "Restaurant");
        Type type4 = new Type(R.drawable.waterplay, "竹筏漂流", "drift");
        Type type5 = new Type(R.drawable.ktv, "KTV", "ktv");
        Type type6 = new Type(R.drawable.fishing, "垂钓", "fishing");
        Type type7 = new Type(R.drawable.chess, "棋牌室", "chess");
        Type type8 = new Type(R.drawable.barbecue, "烧烤", "barbecue");
        Type type9 = new Type(R.drawable.sightseeing, "景点", "sightseeing");

        typeList.add(type1);
        typeList.add(type2);
        typeList.add(type3);
        typeList.add(type4);
        typeList.add(type5);
        typeList.add(type6);
        typeList.add(type7);
        typeList.add(type8);
        typeList.add(type9);

        TypeAdapter typeAdapter = new TypeAdapter(typeList);
        typeRecyclerView.setAdapter(typeAdapter);
        typeShowing = "pick";

        getProjectInformation();
    }

    public void getProjectInformation() {
        if (typeShowing.equals("HomeStay")) {
            AVQuery<AVObject> query = new AVQuery<>("HomeStay");
            query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject object, AVException e) {
                    Log.i("project", object.getString("name"));
                    projectPicNameString = object.getString("mainPicName");
                    projectName.setText(object.getString("name"));
                    projectLocate.setText(object.getString("locateDescribe"));
                    initViewPager();
                }
            });
        } else if (typeShowing.equals("Restaurant")) {
            AVQuery<AVObject> query = new AVQuery<>("Restaurant");
            query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject object, AVException e) {
                    Log.i("project", object.getString("name"));
                    projectPicNameString = object.getString("mainPicName");
                    projectName.setText(object.getString("name"));
                    projectLocate.setText(object.getString("locateDescribe"));

                    initViewPager();
                }
            });

        } else {
            AVQuery<AVObject> query = new AVQuery<>("Amusement");
            query.whereEqualTo("type", typeShowing);
            query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject object, AVException e) {
                    Log.i("project", object.getString("name"));
                    projectPicNameString = object.getString("picName");
                    projectName.setText(object.getString("name"));
                    projectLocate.setText(object.getString("locateDescribe"));

                    initViewPager();
                }
            });
        }
    }

    public void initViewPager() {
        viewPager = findViewById(R.id.viewPager);
        indicator = findViewById(R.id.indicator);
        posterList = new ArrayList<>();
        String picType;
        if (typeShowing.equals("HomeStay")) {
            picType = "homestay";
        } else if (typeShowing.equals("Restaurant")) {
            picType = "restaurant";
        } else {
            picType = typeShowing;
        }
        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereStartsWith("name", picType);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                for (AVObject avObject : avObjects) {
                    Poster poster = new Poster(avObject.getString("url"), "");
                    posterList.add(poster);
                }
                posterPagerAdapter = new PosterPagerAdapter(posterList);
                viewPager.setAdapter(posterPagerAdapter);
                viewPager.setPageTransformer(true, new com.gama.farm_fun.ScalePageTransformer());
                indicator.setLength(posterList.size());
                indicator.setSelected(0);
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        indicator.setSelected(position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                initCommentRecyclerView();
            }
        });
    }

    public void initCommentRecyclerView() {
        commentRecyclerView = findViewById(R.id.commentRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        commentRecyclerView.setLayoutManager(linearLayoutManager);
        commentList = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("Comment");
        query.whereEqualTo("type", typeShowing);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                for (AVObject avObject : avObjects) {
                    Comment comment = new Comment(avObject.getString("userId"),
                            avObject.getString("comment"),
                            avObject.getInt("rank"),
                            avObject.getString("item"),
                            String.valueOf(avObject.getDate("createdAt")));
                    Log.i("userId", comment.userId);
                    Log.i("comment", avObject.getString("comment"));
                    Log.i("createdAt", comment.createdAt);
                    commentList.add(comment);
                    if (commentList.size() > 4) {
                        break;
                    }
                }
                getUserName();
            }
        });
    }

    public void getUserName() {
        commentNumber = 0;
        Log.i("commentList.size()", String.valueOf(commentList.size()));
        for (int i = 0; i < commentList.size(); i++) {
            AVQuery<AVObject> query = new AVQuery<>("Users");
            query.whereEqualTo("objectId", commentList.get(i).userId);
            query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject object, AVException e) {
                    commentList.get(commentNumber).setUserName(object.getString("netName"));
                    Log.i("netName", commentList.get(commentNumber).userName);
                    commentNumber++;
                    if (commentNumber == commentList.size()) {
                        getProjectPic();
                    }
                }
            });
        }

    }

    public void getProjectPic() {
        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereEqualTo("name", projectPicNameString);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                Uri uri = Uri.parse(object.getString("url"));
                projectPic.setImageURI(uri);
                RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);
                projectPic.getHierarchy().setRoundingParams(roundingParams);
                setCommentRecyclerView();
            }
        });
    }

    public void setCommentRecyclerView() {
        CommentAdapter commentAdapter = new CommentAdapter(commentList);
        commentRecyclerView.setAdapter(commentAdapter);

        initTravelJournalRecyclerView();
    }

    public void initTravelJournalRecyclerView() {
        travelJournalRecyclerView = findViewById(R.id.travelJournalRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        travelJournalRecyclerView.setLayoutManager(linearLayoutManager);
        travelJournalList = new ArrayList<>();

        AVQuery<AVObject> query = new AVQuery<>("TravelJournal");
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                for (AVObject avObject : avObjects) {
                    TravelJournal travelJournal = new TravelJournal(avObject.getInt("number"),
                            avObject.getString("title"),
                            avObject.getString("subTitle"),
                            "journal" + String.valueOf(avObject.getInt("number")) + ":1.jpg");
                    Log.i("journal", travelJournal.firstPic);
                    travelJournalList.add(travelJournal);
                    if (travelJournalList.size() > 5) {
                        break;
                    }
                }
                travelJournalSupportNumber = 0;
                getTravelJournalUrl();
            }
        });
    }
    public void getTravelJournalUrl() {
        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereEqualTo("name", travelJournalList.get(travelJournalSupportNumber).firstPic);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                travelJournalList.get(travelJournalSupportNumber).setUrl(object.getString("url"));
                travelJournalSupportNumber++;
                if (travelJournalSupportNumber < travelJournalList.size()) {
                    getTravelJournalUrl();
                } else {
                    TravelJournalsAdapter travelJournalsAdapter = new TravelJournalsAdapter(travelJournalList);
                    travelJournalRecyclerView.setAdapter(travelJournalsAdapter);
                    loading.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loading:
                break;
            case R.id.detail:
                if (typeShowing.equals("HomeStay")) {
                    Intent intent = new Intent(NewsActivity.this, HomeStayActivity.class);
                    intent.putExtra("UserId", userId);
                    startActivity(intent);
                } else if (typeShowing.equals("Restaurant")) {
                    Intent intent = new Intent(NewsActivity.this, RestaurantActivity.class);
                    intent.putExtra("UserId", userId);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(NewsActivity.this, AmusementActivity.class);
                    intent.putExtra("UserId", userId);
                    intent.putExtra("Type", typeShowing);
                    startActivity(intent);

                }
                break;
            case R.id.mine:
                Intent intent = new Intent(NewsActivity.this, MineActivity.class);
                intent.putExtra("UserId", userId);
                startActivity(intent);
                finish();
                break;
            case R.id.onlineShop:
                Intent intent1 = new Intent(NewsActivity.this, OnlineShopActivity.class);
                intent1.putExtra("UserId", userId);
                startActivity(intent1);
                finish();
                break;
            case R.id.homePage:
                finish();
                break;
            case R.id.post:
                postCommentText.setVisibility(View.INVISIBLE);
                postCustomizedText.setVisibility(View.INVISIBLE);
                postJournalText.setVisibility(View.INVISIBLE);
                postPanel.setVisibility(View.VISIBLE);
                postPanel.getBackground().setAlpha(240);
                ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(postComment,
                        "translationY", 700, 0);
                objectAnimator1.setDuration(1000);
                objectAnimator1.setInterpolator(new OvershootInterpolator());
                objectAnimator1.start();

                ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(postCustomized,
                        "translationY", 700, 0);
                objectAnimator2.setDuration(1000);
                objectAnimator2.setInterpolator(new OvershootInterpolator());
                objectAnimator2.start();

                ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(postJournal,
                        "translationY", 700, 0);
                objectAnimator3.setDuration(1000);
                objectAnimator3.setInterpolator(new OvershootInterpolator());
                objectAnimator3.start();
                objectAnimator3.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        postCommentText.setVisibility(View.VISIBLE);
                        postCustomizedText.setVisibility(View.VISIBLE);
                        postJournalText.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                ObjectAnimator objectAnimator4 = ObjectAnimator.ofFloat(postBack,
                        "rotation", 0, 45);
                objectAnimator4.setDuration(500);
                objectAnimator4.setInterpolator(new OvershootInterpolator());
                objectAnimator4.start();

                ObjectAnimator objectAnimator5 = ObjectAnimator.ofFloat(postCommentText,
                        "alpha", 0, 1);
                objectAnimator5.setDuration(500);
                objectAnimator5.setStartDelay(1000);
                objectAnimator5.setInterpolator(new OvershootInterpolator());
                objectAnimator5.start();

                ObjectAnimator objectAnimator6 = ObjectAnimator.ofFloat(postCustomizedText,
                        "alpha", 0, 1);
                objectAnimator6.setDuration(500);
                objectAnimator6.setStartDelay(1000);
                objectAnimator6.setInterpolator(new OvershootInterpolator());
                objectAnimator6.start();

                ObjectAnimator objectAnimator7 = ObjectAnimator.ofFloat(postJournalText,
                        "alpha", 0, 1);
                objectAnimator7.setDuration(500);
                objectAnimator7.setStartDelay(1000);
                objectAnimator7.setInterpolator(new OvershootInterpolator());
                objectAnimator7.start();
                break;
            case R.id.post_back:
                postPanel.setVisibility(View.INVISIBLE);
                break;
            case R.id.post_customized:
                Intent intent2 = new Intent(NewsActivity.this, CustomizedActivity.class);
                intent2.putExtra("UserId", userId);
                startActivity(intent2);
                break;
            case R.id.post_comment:
                Intent intent3 = new Intent(NewsActivity.this, MyOrderActivity.class);
                intent3.putExtra("UserId", userId);
                intent3.putExtra("Type", "comment");
                startActivity(intent3);
                break;
        }
    }

    private class PosterPagerAdapter extends android.support.v4.view.PagerAdapter {

        List<Poster> posterList;

        public PosterPagerAdapter(List<Poster> posterList) {
            this.posterList = posterList;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position = position % posterList.size();
            View view = LayoutInflater.from(NewsActivity.this).inflate(R.layout.item_poster, null);
            SimpleDraweeView poster = view.findViewById(R.id.poster);
            Uri uri = Uri.parse(posterList.get(position).url);
            poster.setImageURI(uri);
            container.addView(view);
            return view;
        }
    }

    private class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
        private List<Comment> commentList;

        private CommentAdapter(List<Comment> commentList) {
            this.commentList = commentList;
        }

        @Override
        public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_comment, parent, false);
            final CommentAdapter.ViewHolder holder = new CommentAdapter.ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            /*holder.recommendProjectPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });*/
            return holder;
        }

        @Override
        public void onBindViewHolder(CommentAdapter.ViewHolder holder, int position) {
            Comment comment = commentList.get(position);
            holder.userName.setText(comment.userName);
            holder.comment.setText(comment.comment);
            holder.item.setText(comment.item);
            holder.time.setText(comment.createdAt);
            holder.setRank(comment.rank);
            if (holder.rank == 1) {
                holder.star1.setBackground(getResources().getDrawable(R.drawable.star2));
            } else if (holder.rank == 2) {
                holder.star1.setBackground(getResources().getDrawable(R.drawable.star2));
                holder.star2.setBackground(getResources().getDrawable(R.drawable.star2));
            } else if (holder.rank == 3) {
                holder.star1.setBackground(getResources().getDrawable(R.drawable.star2));
                holder.star2.setBackground(getResources().getDrawable(R.drawable.star2));
                holder.star3.setBackground(getResources().getDrawable(R.drawable.star2));
            } else if (holder.rank == 4) {
                holder.star1.setBackground(getResources().getDrawable(R.drawable.star2));
                holder.star2.setBackground(getResources().getDrawable(R.drawable.star2));
                holder.star3.setBackground(getResources().getDrawable(R.drawable.star2));
                holder.star4.setBackground(getResources().getDrawable(R.drawable.star2));
            } else {
                holder.star1.setBackground(getResources().getDrawable(R.drawable.star2));
                holder.star2.setBackground(getResources().getDrawable(R.drawable.star2));
                holder.star3.setBackground(getResources().getDrawable(R.drawable.star2));
                holder.star4.setBackground(getResources().getDrawable(R.drawable.star2));
                holder.star5.setBackground(getResources().getDrawable(R.drawable.star2));
            }
        }

        @Override
        public int getItemCount() {
            return commentList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView userName;
            private TextView comment;
            private int rank;
            private TextView item;
            private TextView time;
            private ImageView star1;
            private ImageView star2;
            private ImageView star3;
            private ImageView star4;
            private ImageView star5;

            private ViewHolder(View view) {
                super(view);
                userName = view.findViewById(R.id.userName);
                comment = view.findViewById(R.id.commentText);
                item = view.findViewById(R.id.item);
                time = view.findViewById(R.id.time);

                star1 = view.findViewById(R.id.star1);
                star2 = view.findViewById(R.id.star2);
                star3 = view.findViewById(R.id.star3);
                star4 = view.findViewById(R.id.star4);
                star5 = view.findViewById(R.id.star5);
            }

            public void setRank(int rank) {
                this.rank = rank;
            }
        }


    }

    private class TravelJournalsAdapter extends RecyclerView.Adapter<TravelJournalsAdapter.ViewHolder> {
        private List<TravelJournal> travelJournalsList;

        private TravelJournalsAdapter(List<TravelJournal> travelJournalsList) {
            this.travelJournalsList = travelJournalsList;
        }

        @Override
        public TravelJournalsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_traveljournal, parent, false);
            final TravelJournalsAdapter.ViewHolder holder = new TravelJournalsAdapter.ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final TravelJournalsAdapter.ViewHolder holder, int position) {
            TravelJournal travelJournal = travelJournalsList.get(position);
            Log.i(String.valueOf(position), "TravelJournal" + travelJournal.title);
            holder.travelJournalTitle.setText(travelJournal.title);
            holder.travelJournalSubtitle.setText(travelJournal.subTitle);
            holder.setNumber(travelJournal.number);
            Uri uri = Uri.parse(travelJournal.url);
            holder.travelJournalView.setImageURI(uri);
            RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);
            holder.travelJournalView.getHierarchy().setRoundingParams(roundingParams);

            holder.travelJournalView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(NewsActivity.this, TravelJournalActivity.class);
                    intent.putExtra("number", holder.number);
                    startActivity(intent);
                }
            });
        }
        
        @Override
        public int getItemCount() {
            return travelJournalsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView travelJournalTitle;
            private TextView travelJournalSubtitle;
            private SimpleDraweeView travelJournalView;
            private int number;

            private ViewHolder(View view) {
                super(view);
                travelJournalTitle = view.findViewById(R.id.travelJournalTitle);
                travelJournalSubtitle = view.findViewById(R.id.travelJournalSubtitle);
                travelJournalView = view.findViewById(R.id.travelJournalBackground);
            }

            public void setNumber(int number) {
                this.number = number;
            }
        }
    }

    private class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.ViewHolder> {
        private List<Type> typeList;

        private TypeAdapter(List<Type> typeList) {
            this.typeList = typeList;
        }

        @Override
        public TypeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_type, parent, false);
            final TypeAdapter.ViewHolder holder = new TypeAdapter.ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            /*holder.recommendProjectPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });*/
            return holder;
        }

        @Override
        public void onBindViewHolder(final TypeAdapter.ViewHolder holder, int position) {
            Type type = typeList.get(position);
            holder.image.setImageResource(type.picId);
            holder.type.setText(type.type);
            holder.setCode(type.typeCode);
            holder.background.setVisibility(View.INVISIBLE);
            holder.typeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("Type", holder.code);
                    loading.setVisibility(View.VISIBLE);
                    typeShowing = holder.code;
                    choseTypeView.setVisibility(View.INVISIBLE);
                    holder.background.setVisibility(View.VISIBLE);
                    choseTypeView = holder.background;
                    showType = holder.code;
                    getProjectInformation();
                }
            });
            if (holder.code.equals(showType)) {
                holder.background.setVisibility(View.VISIBLE);
                choseTypeView = holder.background;
            }

        }

        @Override
        public int getItemCount() {
            return typeList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView image;
            private TextView type;
            private String code;
            private View typeView;
            private ImageView background;

            private ViewHolder(View view) {
                super(view);
                image = view.findViewById(R.id.image);
                type = view.findViewById(R.id.type);
                typeView = view;
                background = view.findViewById(R.id.background);
            }

            public void setCode(String code) {
                this.code = code;
            }
        }


    }

}
