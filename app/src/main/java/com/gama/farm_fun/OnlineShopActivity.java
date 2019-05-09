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
import android.widget.ScrollView;
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

public class OnlineShopActivity extends AppCompatActivity implements View.OnClickListener {
    private String userId;

    private View searchBar;
    private TextView title;
    private Button cart;

    private RecyclerView subTitleRecyclerView;
    private List<String> stringList;
    private View chosenSubtitle;
    private String chosenSubtitleString;

    private ViewPager viewPager;
    private PosterPagerAdapter posterPagerAdapter;
    private List<Poster> posterList;
    private ViewPagerIndicator indicator;
    private int pageCount;

    private View galleryPanel;
    private List<String> urls;
    private SimpleDraweeView bigPic;
    private SimpleDraweeView smallPic1;
    private SimpleDraweeView smallPic2;

    private List<Commodity> commodityList;
    private int commodityPicSupport;

    private RecyclerView leftRecyclerView;
    private List<Commodity> leftCommodityList;

    private RecyclerView rightRecyclerView;
    private List<Commodity> rightCommodityList;

    private RecyclerView commentRecyclerView;
    private List<Comment> commentList;
    private int commentNumber;

    public View bottomBar;
    public Button homePage;
    public Button post;
    public Button news;
    public Button mine;
    public Button order;
    public TextView orderText;

    private View postPanel;
    private Button postBack;
    private ImageView postComment;
    private ImageView postCustomized;
    private ImageView postJournal;
    private TextView postCustomizedText;
    private TextView postCommentText;
    private TextView postJournalText;

    private ObservableScrollView observableScrollView;
    private ImageView tip1;
    private ImageView tip2;
    private ImageView tip3;
    private ImageView tip4;

    private View firstBar;
    private View secondBar;
    private View thirdBar;
    private View fourthBar;

    private View loading;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_onlineshop);

        Intent intent = getIntent();
        userId = intent.getStringExtra("UserId");
        Log.i("UserId", userId);

        loading = findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);

        initUI();
    }

    public void initUI() {
        galleryPanel = findViewById(R.id.panel_gallery);
        bigPic = galleryPanel.findViewById(R.id.big_pic);
        bigPic.setOnClickListener(this);
        smallPic1 = galleryPanel.findViewById(R.id.small_pic1);
        smallPic1.setOnClickListener(this);
        smallPic2 = galleryPanel.findViewById(R.id.small_pic2);
        smallPic2.setOnClickListener(this);
        urls = new ArrayList<>();

        searchBar = findViewById(R.id.bar_search);
        title = searchBar.findViewById(R.id.title);
        title.setText("线上商城");
        cart = searchBar.findViewById(R.id.cart_icon);
        cart.setOnClickListener(this);

        bottomBar = findViewById(R.id.bar_bottom);
        homePage = bottomBar.findViewById(R.id.homePage);
        homePage.setOnClickListener(this);
        post = bottomBar.findViewById(R.id.post);
        post.setOnClickListener(this);
        news = bottomBar.findViewById(R.id.news);
        news.setOnClickListener(this);
        order = bottomBar.findViewById(R.id.onlineShop);
        orderText = bottomBar.findViewById(R.id.onlineShopText);
        order.setBackground(getResources().getDrawable(R.drawable.onlinestore1));
        order.setOnClickListener(this);
        orderText.setTextColor(getResources().getColor(R.color.colorTheme));
        mine = bottomBar.findViewById(R.id.mine);
        mine.setOnClickListener(this);

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

        commentRecyclerView = findViewById(R.id.commentRecyclerView);
        commentList = new ArrayList<>();

        observableScrollView = findViewById(R.id.observableScrollView);
        tip1 = findViewById(R.id.tip);
        tip2 = findViewById(R.id.tip2);
        tip3 = findViewById(R.id.tip3);
        tip4 = findViewById(R.id.tip4);

        initSubTitle();
    }

    public void initSubTitle() {
        subTitleRecyclerView = findViewById(R.id.subTitleRecyclerView);
        stringList = new ArrayList<>();
        String s1 = "推荐";
        String s2 = "全部宝贝";
        String s3 = "评论";
        String s4 = "店铺信息";
        stringList.add(s1);
        stringList.add(s2);
        stringList.add(s3);
        stringList.add(s4);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        subTitleRecyclerView.setLayoutManager(linearLayoutManager);
        SubTitleAdapter subTitleAdapter = new SubTitleAdapter(stringList);
        subTitleRecyclerView.setAdapter(subTitleAdapter);
        chosenSubtitleString = "推荐";

        getPoster();
    }

    public void getPoster() {
        posterList = new ArrayList<>();

        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereStartsWith("name", "cposter");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                for (AVObject avObject : avObjects) {
                    String code = avObject.getString("name");
                    code = getPosterCode(code);
                    Poster poster = new Poster(avObject.getString("url"), "", code);
                    posterList.add(poster);
                }
                Log.i("avObjectsList", String.valueOf(avObjects.size()));
                viewPager = findViewById(R.id.viewPager);
                posterPagerAdapter = new PosterPagerAdapter(posterList);
                viewPager.setAdapter(posterPagerAdapter);
                viewPager.setPageTransformer(true, new com.gama.farm_fun.ScalePageTransformer());
                indicator = findViewById(R.id.indicator);
                indicator.setLength(posterList.size());
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        indicator.setSelected(position);
                        Log.i("page", String.valueOf(position));
                        pageCount = position;
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });


                getGallery();
            }
        });

    }

    public void getGallery() {
        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereStartsWith("name", "gallery");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                for (AVObject avObject : avObjects) {
                    urls.add(avObject.getString("url"));
                }
                Uri uri1 = Uri.parse(urls.get(0));
                bigPic.setImageURI(uri1);
                RoundingParams roundingParams1 = RoundingParams.fromCornersRadius(10f);
                bigPic.getHierarchy().setRoundingParams(roundingParams1);

                Uri uri2 = Uri.parse(urls.get(1));
                smallPic1.setImageURI(uri2);
                RoundingParams roundingParams2 = RoundingParams.fromCornersRadius(10f);
                smallPic1.getHierarchy().setRoundingParams(roundingParams2);

                Uri uri3 = Uri.parse(urls.get(2));
                smallPic2.setImageURI(uri3);
                RoundingParams roundingParams3 = RoundingParams.fromCornersRadius(10f);
                smallPic2.getHierarchy().setRoundingParams(roundingParams3);

                getCommodityInformation();
            }
        });
    }

    public void getCommodityInformation() {
        commodityList = new ArrayList<>();
        leftCommodityList = new ArrayList<>();
        rightCommodityList = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("Commodity");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                for (AVObject avObject : avObjects) {
                    Log.i("name", avObject.getString("name"));
                    Commodity commodity = new Commodity(avObject.getString("name"),
                            avObject.getString("describe"),
                            avObject.getString("code"),
                            avObject.getInt("lowestPrice"));
                    commodityList.add(commodity);
                }
                halveCommodityList();
            }
        });
    }

    public void halveCommodityList() {
        for (int i = 0; i < commodityList.size(); i++) {
            if (i % 2 == 0) {
                leftCommodityList.add(commodityList.get(i));
            } else {
                rightCommodityList.add(commodityList.get(i));
            }
        }
        commodityPicSupport = 0;
        getCommodityUrl();
    }

    public void getCommodityUrl() {
        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereStartsWith("name", commodityList.get(commodityPicSupport).code);
        Log.i("code", commodityList.get(commodityPicSupport).code);
        query.orderByAscending("name");
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                Log.i("picName", object.getString("name"));
                commodityList.get(commodityPicSupport).setUrl(object.getString("url"));
                commodityPicSupport++;
                if (commodityPicSupport == commodityList.size()) {
                    initLeftRightRecyclerView();
                } else {
                    getCommodityUrl();
                }
            }
        });
    }

    public void initLeftRightRecyclerView() {
        leftRecyclerView = findViewById(R.id.leftRecyclerView);
        rightRecyclerView = findViewById(R.id.rightRecyclerView);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        leftRecyclerView.setLayoutManager(linearLayoutManager1);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        rightRecyclerView.setLayoutManager(linearLayoutManager2);

        CommodityAdapter commodityAdapter1 = new CommodityAdapter(leftCommodityList);
        leftRecyclerView.setAdapter(commodityAdapter1);

        CommodityAdapter commodityAdapter2 = new CommodityAdapter(rightCommodityList);
        rightRecyclerView.setAdapter(commodityAdapter2);

        getComment();
    }

    public void getComment() {
        AVQuery<AVObject> query = new AVQuery<>("Comment");
        query.whereEqualTo("type", "manyC");
        query.orderByDescending("createdAt");
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
                        setCommentRecyclerView();
                    }
                }
            });
        }

    }

    public void setCommentRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        commentRecyclerView.setLayoutManager(linearLayoutManager);
        CommentAdapter commentAdapter = new CommentAdapter(commentList);
        commentRecyclerView.setAdapter(commentAdapter);

        setObservableScrollView();
    }

    public void setObservableScrollView() {
        observableScrollView.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy) {
                if (y < tip2.getTop()) {
                    subTitleRecyclerView.smoothScrollToPosition(0);
                    firstBar.setVisibility(View.VISIBLE);
                    secondBar.setVisibility(View.INVISIBLE);
                    thirdBar.setVisibility(View.INVISIBLE);
                    fourthBar.setVisibility(View.INVISIBLE);
                } else if (y < tip3.getTop()) {
                    firstBar.setVisibility(View.INVISIBLE);
                    secondBar.setVisibility(View.VISIBLE);
                    thirdBar.setVisibility(View.INVISIBLE);
                    fourthBar.setVisibility(View.INVISIBLE);
                    subTitleRecyclerView.smoothScrollToPosition(1);
                } else if (y < tip4.getTop()) {
                    firstBar.setVisibility(View.INVISIBLE);
                    secondBar.setVisibility(View.INVISIBLE);
                    thirdBar.setVisibility(View.VISIBLE);
                    fourthBar.setVisibility(View.INVISIBLE);
                    subTitleRecyclerView.smoothScrollToPosition(2);
                } else {
                    firstBar.setVisibility(View.INVISIBLE);
                    secondBar.setVisibility(View.INVISIBLE);
                    thirdBar.setVisibility(View.INVISIBLE);
                    fourthBar.setVisibility(View.VISIBLE);
                    subTitleRecyclerView.smoothScrollToPosition(3);

                }

            }
        });

        loading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.big_pic:
                Intent bigPicIntent = new Intent(OnlineShopActivity.this, CommodityActivity.class);
                bigPicIntent.putExtra("UserId", userId);
                bigPicIntent.putExtra("code", "WC");
                startActivity(bigPicIntent);
                finish();
                break;
            case R.id.small_pic1:
                Intent smallPic1Intent = new Intent(OnlineShopActivity.this, CommodityActivity.class);
                smallPic1Intent.putExtra("UserId", userId);
                smallPic1Intent.putExtra("code", "DL");
                startActivity(smallPic1Intent);
                break;
            case R.id.small_pic2:
                Intent smallPic2Intent = new Intent(OnlineShopActivity.this, CommodityActivity.class);
                smallPic2Intent.putExtra("UserId", userId);
                smallPic2Intent.putExtra("code", "DP");
                startActivity(smallPic2Intent);
                break;
            case R.id.cart_icon:
                if (userId.equals("tourist")) {
                    Intent intent = new Intent(OnlineShopActivity.this, LoginActivity.class);
                    startActivityForResult(intent, 0);
                } else {
                    Intent cartIntent = new Intent(OnlineShopActivity.this, CartActivity.class);
                    cartIntent.putExtra("UserId", userId);
                    startActivity(cartIntent);
                }
                break;
            case R.id.mine:
                Intent mineIntent = new Intent(OnlineShopActivity.this, MineActivity.class);
                mineIntent.putExtra("UserId", userId);
                startActivity(mineIntent);
                finish();
                break;
            case R.id.news:
                Intent newsIntent = new Intent(OnlineShopActivity.this, NewsActivity.class);
                newsIntent.putExtra("UserId", userId);
                startActivity(newsIntent);
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
                Intent intent1 = new Intent(OnlineShopActivity.this, CustomizedActivity.class);
                intent1.putExtra("UserId", userId);
                startActivity(intent1);
                break;
            case R.id.post_comment:
                Intent intent2 = new Intent(OnlineShopActivity.this, MyOrderActivity.class);
                intent2.putExtra("UserId", userId);
                intent2.putExtra("Type", "comment");
                startActivity(intent2);
                break;
        }
    }

    private class SubTitleAdapter extends RecyclerView.Adapter<SubTitleAdapter.ViewHolder> {
        private List<String> stringList;

        private SubTitleAdapter(List<String> stringList) {
            this.stringList = stringList;
        }

        @Override
        public SubTitleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_subtitle, parent, false);
            final SubTitleAdapter.ViewHolder holder = new SubTitleAdapter.ViewHolder(view);
            /*holder.recommendProjectPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });*/
            return holder;
        }

        @Override
        public void onBindViewHolder(final SubTitleAdapter.ViewHolder holder, int position) {
            String subtitle = stringList.get(position);
            if (subtitle.equals("推荐")) {
                firstBar = holder.choseBar;
            } else if (subtitle.equals("全部宝贝")) {
                secondBar = holder.choseBar;
            } else if (subtitle.equals("评论")) {
                thirdBar = holder.choseBar;
            } else {
                fourthBar = holder.choseBar;
            }
            holder.subtitle.setText(subtitle);
            holder.subView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chosenSubtitleString = holder.subtitle.getText().toString();
                    chosenSubtitle.setVisibility(View.INVISIBLE);
                    holder.choseBar.setVisibility(View.VISIBLE);
                    chosenSubtitle = holder.choseBar;
                    Log.i("Subtitle", chosenSubtitleString);

                    if (chosenSubtitleString.equals("推荐")) {
                        observableScrollView.smoothScrollTo(0, tip1.getTop());
                    } else if (chosenSubtitleString.equals("全部宝贝")) {
                        observableScrollView.smoothScrollTo(0, tip2.getTop());
                    } else if (chosenSubtitleString.equals("评论")) {
                        observableScrollView.smoothScrollTo(0, tip3.getTop());
                    } else {
                        observableScrollView.smoothScrollTo(0, tip4.getTop());
                    }

                }
            });
            if (subtitle.equals(chosenSubtitleString)) {
                holder.choseBar.setVisibility(View.VISIBLE);
                chosenSubtitle = holder.choseBar;
            }
        }

        @Override
        public int getItemCount() {
            return stringList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView subtitle;
            private ImageView choseBar;
            private View subView;

            private ViewHolder(View view) {
                super(view);
                subtitle = view.findViewById(R.id.subtitle);
                choseBar = view.findViewById(R.id.bar_chose);
                subView = view;
            }

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
            View view = LayoutInflater.from(OnlineShopActivity.this).inflate(R.layout.item_poster, null);
            SimpleDraweeView poster = view.findViewById(R.id.poster);
            final Uri uri = Uri.parse(posterList.get(position).url);
            poster.setImageURI(uri);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int k = pageCount % posterList.size();
                    Log.i("k", String.valueOf(k));
                    Poster poster = posterList.get(k);
                    String code = poster.code;
                    Log.i("Chosen", code);
                    Intent intent = new Intent(OnlineShopActivity.this, CommodityActivity.class);
                    intent.putExtra("UserId", userId);
                    intent.putExtra("code", code);
                    startActivity(intent);
                }
            });

            container.addView(view);
            return view;
        }
    }

    private class CommodityAdapter extends RecyclerView.Adapter<CommodityAdapter.ViewHolder> {
        private List<Commodity> commodityList;

        private CommodityAdapter(List<Commodity> commodityList) {
            this.commodityList = commodityList;
        }

        @Override
        public CommodityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_commodity, parent, false);
            final CommodityAdapter.ViewHolder holder = new CommodityAdapter.ViewHolder(view);
            /*holder.recommendProjectPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });*/
            return holder;
        }

        @Override
        public void onBindViewHolder(final CommodityAdapter.ViewHolder holder, int position) {
            final Commodity commodity = commodityList.get(position);
            final Uri uri = Uri.parse(commodity.url);
            holder.pic.setImageURI(uri);
            RoundingParams roundingParams = RoundingParams.fromCornersRadius(20f);
            holder.pic.getHierarchy().setRoundingParams(roundingParams);

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OnlineShopActivity.this, CommodityActivity.class);
                    intent.putExtra("UserId", userId);
                    intent.putExtra("code", commodity.code);
                    startActivity(intent);
                }
            });
            holder.name.setText(commodity.name);
            holder.price.setText(String.valueOf(commodity.lowestPrice));
        }

        @Override
        public int getItemCount() {
            return commodityList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private SimpleDraweeView pic;
            private TextView name;
            private TextView price;
            private View view;

            private ViewHolder(View view) {
                super(view);
                pic = view.findViewById(R.id.pic);
                name = view.findViewById(R.id.name);
                price = view.findViewById(R.id.price);
                this.view = view;
            }

        }


    }

    public String getPosterCode(String code) {
        String newCode;
        char[] codeChar = code.toCharArray();
        int k = 0;
        for (int i = 0; i < codeChar.length; i++) {
            if (codeChar[i] == '.') {
                k = i;
                break;
            }
        }
        newCode = String.valueOf(codeChar[k - 2]) + String.valueOf(codeChar[k - 1]);
        return newCode;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
            holder.item.setText(getFirstItem(comment.item) + " 等共" + String.valueOf(getItemsCount(comment.item)) + "件商品");
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

    private String getFirstItem(String nameString) {
        char[] nameChar = nameString.toCharArray();
        String s = "";
        int l = 0;
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] == ';') {
                l = i;
                break;
            }
        }
        for (int i = 0; i < l; i++) {
            s += String.valueOf(nameChar[i]);
        }
        return s;

    }

    private int getItemsCount(String nameString) {
        char[] nameChar = nameString.toCharArray();
        int k = 0;
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] == ';') {
                k++;
            }
        }
        return k;
    }
}
