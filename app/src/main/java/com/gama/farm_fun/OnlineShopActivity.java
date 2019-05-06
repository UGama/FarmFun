package com.gama.farm_fun;

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

public class OnlineShopActivity extends AppCompatActivity implements View.OnClickListener {
    private String userId;

    private View searchBar;
    private TextView title;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_onlineshop);

        Intent intent = getIntent();
        userId = intent.getStringExtra("UserId");
        Log.i("UserId", userId);

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
            holder.subtitle.setText(subtitle);
            holder.subView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chosenSubtitleString = holder.subtitle.getText().toString();
                    chosenSubtitle.setVisibility(View.INVISIBLE);
                    holder.choseBar.setVisibility(View.VISIBLE);
                    chosenSubtitle = holder.choseBar;
                    Log.i("Subtitle", chosenSubtitleString);
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
}
