package com.gama.farm_fun;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

import static android.animation.ObjectAnimator.ofFloat;

public class CommodityActivity extends AppCompatActivity implements View.OnClickListener {
    private String userId;
    private String code;

    public int screenWidth;
    public int screenHeight;

    private Commodity commodity;

    private TextView nameText;
    private TextView describeText;
    private TextView priceText;

    private ObservableScrollView observableScrollView;

    public ViewPager viewPager;
    public PosterPagerAdapter posterPagerAdapter;
    public List<Poster> posterList;
    public ViewPagerIndicator indicator;

    private View chose;
    private TextView chosenKindText;
    private TextView allKindText;
    private RecyclerView kindRecyclerView;
    private List<CItem> cItemList;

    private View commodityChosePanel;
    private AnimatorSet commodityChosePanelQuit;
    private SimpleDraweeView item_pic;
    private RecyclerView kindChoseRecyclerView;
    private List<CItemArray> cItemArrayList;
    private CItemArray cItemArray;
    private View countChosePanel;
    private TextView countText;
    private ImageView plus;
    private ImageView minus;
    private int count;
    private ImageView shelter;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_commodity);

        Intent intent = getIntent();
        userId = intent.getStringExtra("UserId");
        code = intent.getStringExtra("code");

        initUI();
    }

    public void initUI() {
        nameText = findViewById(R.id.name);
        describeText = findViewById(R.id.describe);
        priceText = findViewById(R.id.price);

        chose = findViewById(R.id.kind_show);
        chose.setOnClickListener(this);
        chosenKindText = chose.findViewById(R.id.kind);
        chosenKindText.setOnClickListener(this);
        allKindText = chose.findViewById(R.id.all_kind);
        allKindText.setOnClickListener(this);
        kindRecyclerView = chose.findViewById(R.id.kindRecyclerView);
        kindRecyclerView.setOnClickListener(this);

        commodityChosePanel = findViewById(R.id.panel_commodity_chose);
        item_pic = commodityChosePanel.findViewById(R.id.item_pic);
        kindChoseRecyclerView = commodityChosePanel.findViewById(R.id.kindChoseRecyclerView);
        countChosePanel = commodityChosePanel.findViewById(R.id.panel_count_chose);
        countText = countChosePanel.findViewById(R.id.count);
        plus = commodityChosePanel.findViewById(R.id.plus);
        plus.setOnClickListener(this);
        minus = commodityChosePanel.findViewById(R.id.minus);
        minus.setOnClickListener(this);
        count = 1;
        ObjectAnimator delay = ofFloat(commodityChosePanel, "rotation", 0, 0);
        delay.setDuration(500);
        ObjectAnimator quit = ofFloat(commodityChosePanel, "translationY", 0, screenHeight);
        quit.setDuration(500);
        commodityChosePanelQuit = new AnimatorSet();
        commodityChosePanelQuit.play(delay).before(quit);
        commodityChosePanelQuit.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                commodityChosePanel.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        shelter = findViewById(R.id.shelter);

        getWindowInformation();
    }

    public void getWindowInformation() {
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;         // 屏幕宽度（像素）
        screenHeight = displayMetrics.heightPixels;       // 屏幕高度（像素）
        float density = displayMetrics.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = displayMetrics.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        float width = screenWidth / density;  // 屏幕宽度(dp)
        float height = screenHeight / density;// 屏幕高度(dp)
        Log.i("width/height(px)", String.valueOf(screenWidth) + "/" + String.valueOf(screenHeight));
        Log.i("width/height(dp)", String.valueOf(width) + "/" + String.valueOf(height));

        setObservableScrollView();
    }

    public void setObservableScrollView() {

        getCommodityInformation();
    }

    public void getCommodityInformation() {
        AVQuery<AVObject> query = new AVQuery<>("Commodity");
        query.whereEqualTo("code", code);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                commodity = new Commodity(object.getString("name"),
                        object.getString("describe"),
                        object.getString("code"),
                        object.getInt("lowestPrice"));

                nameText.setText(commodity.name);
                describeText.setText(commodity.describe);
                priceText.setText(String.valueOf(commodity.lowestPrice));

                initViewPager();
            }
        });
    }

    public void initViewPager() {
        viewPager = findViewById(R.id.viewPager);
        indicator = findViewById(R.id.indicator);
        posterList = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereStartsWith("name", commodity.code);
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

                getItems();

            }
        });
    }

    public void getItems() {
        cItemList = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("CommodityKind");
        query.whereEqualTo("code", commodity.code);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                for (AVObject avObject : avObjects) {
                    CItem cItem = new CItem(avObject.getString("name"),
                            commodity.code,
                            avObject.getInt("price"),
                            avObject.getString("item"),
                            avObject.getString("picName"));
                    cItemList.add(cItem);
                }
                allKindText.setText("共" + String.valueOf(cItemList.size()) + "种商品>");

                initKindRecyclerView();
            }
        });
    }

    public void initKindRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        kindRecyclerView.setLayoutManager(linearLayoutManager);

        KindAdapter kindAdapter = new KindAdapter(cItemList);
        kindRecyclerView.setAdapter(kindAdapter);

        initCommodityChose();
    }

    public void initCommodityChose() {
        cItemArrayList = transToCItemArray(cItemList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        kindChoseRecyclerView.setLayoutManager(linearLayoutManager);
        CItemArrayArrayAdapter cItemArrayArrayAdapter = new CItemArrayArrayAdapter(cItemArrayList);
        kindChoseRecyclerView.setAdapter(cItemArrayArrayAdapter);

        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereStartsWith("name", code + "Kind");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                for (AVObject avObject : avObjects) {
                    Uri uri = Uri.parse(avObject.getString("url"));
                    item_pic.setImageURI(uri);
                    RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);
                    item_pic.getHierarchy().setRoundingParams(roundingParams);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.plus:
                count++;
                countText.setText(String.valueOf(count));
                if (count == 2) {
                    minus.setBackground(getResources().getDrawable(R.drawable.shape_circle));
                }
                break;
            case R.id.minus:
                if (count > 1) {
                    count--;
                    countText.setText(String.valueOf(count));
                    if (count == 1) {
                        minus.setBackground(getResources().getDrawable(R.drawable.shape_circle2));
                    }
                }
                break;
            case R.id.kind_show:
            case R.id.kind:
            case R.id.all_kind:
            case R.id.kindRecyclerView:
                initCommodityChose();
                commodityChosePanel.setVisibility(View.VISIBLE);
                ObjectAnimator objectAnimator = ofFloat(commodityChosePanel, "translationY", screenHeight, 0);
                objectAnimator.setDuration(1000);
                objectAnimator.start();
                shelter.setVisibility(View.VISIBLE);
                break;
            case R.id.shelter:
                shelter.setVisibility(View.INVISIBLE);
                commodityChosePanelQuit.start();
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
            View view = LayoutInflater.from(CommodityActivity.this).inflate(R.layout.item_poster, null);
            SimpleDraweeView poster = view.findViewById(R.id.poster);
            Uri uri = Uri.parse(posterList.get(position).url);
            poster.setImageURI(uri);
            container.addView(view);
            return view;
        }
    }

    private class KindAdapter extends RecyclerView.Adapter<KindAdapter.ViewHolder> {
        private List<CItem> cItemList;

        private KindAdapter(List<CItem> cItemList) {
            this.cItemList = cItemList;
        }

        @Override
        public KindAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_c_item, parent, false);
            final KindAdapter.ViewHolder holder = new KindAdapter.ViewHolder(view);
            /*holder.recommendProjectPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });*/
            return holder;
        }

        @Override
        public void onBindViewHolder(final KindAdapter.ViewHolder holder, int position) {
            CItem cItem = cItemList.get(position);

            holder.itemName.setText(cItem.name);
        }

        @Override
        public int getItemCount() {
            return cItemList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView itemName;

            private ViewHolder(View view) {
                super(view);
                itemName = view.findViewById(R.id.item_name);
            }

        }


    }

    public List<CItemArray> transToCItemArray(List<CItem> cItemList) {
        List<CItemArray> cItemArrayList = new ArrayList<>();
        for (int i = 0; i < cItemList.size(); i++) {
            if (i % 4 == 0) {
                cItemArray = new CItemArray(cItemList.get(i).name);
                if (i == cItemList.size() - 1) {
                    cItemArray.setS2("");
                    cItemArray.setS3("");
                    cItemArray.setS4("");
                    cItemArrayList.add(cItemArray);
                }
            } else if (i % 4 == 1) {
                cItemArray.setS2(cItemList.get(i).name);
                if (i == cItemList.size() - 1) {
                    cItemArray.setS3("");
                    cItemArray.setS4("");
                    cItemArrayList.add(cItemArray);
                }
            } else if (i % 4 == 2) {
                cItemArray.setS3(cItemList.get(i).name);
                if (i == cItemList.size() - 1) {
                    cItemArray.setS4("");
                    cItemArrayList.add(cItemArray);
                }
            } else {
                cItemArray.setS4(cItemList.get(i).name);
                cItemArrayList.add(cItemArray);
            }
        }

        return cItemArrayList;
    }

    private class CItemArrayArrayAdapter extends RecyclerView.Adapter<CItemArrayArrayAdapter.ViewHolder> {
        private List<CItemArray> CItemArrayList;

        private CItemArrayArrayAdapter(List<CItemArray> CItemArrayList) {
            this.CItemArrayList = CItemArrayList;
        }

        @Override
        public CItemArrayArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_citem_array, parent, false);
            final CItemArrayArrayAdapter.ViewHolder holder = new CItemArrayArrayAdapter.ViewHolder(view);
            /*holder.recommendProjectPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });*/
            return holder;
        }

        @Override
        public void onBindViewHolder(final CItemArrayArrayAdapter.ViewHolder holder, int position) {
            CItemArray cItemArray = CItemArrayList.get(position);
            holder.itemName1.setText(cItemArray.s1);
            holder.itemName2.setText(cItemArray.s2);
            holder.itemName3.setText(cItemArray.s3);
            holder.itemName4.setText(cItemArray.s4);

            if (cItemArray.s2.equals("")) {
                holder.tab2.setVisibility(View.INVISIBLE);
            }
            if (cItemArray.s3.equals("")) {
                holder.tab3.setVisibility(View.INVISIBLE);
            }
            if (cItemArray.s4.equals("")) {
                holder.tab4.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return CItemArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView itemName1;
            private TextView itemName2;
            private TextView itemName3;
            private TextView itemName4;
            private ImageView tab1;
            private ImageView tab2;
            private ImageView tab3;
            private ImageView tab4;

            private ViewHolder(View view) {
                super(view);
                itemName1 = view.findViewById(R.id.item_name1);
                itemName2 = view.findViewById(R.id.item_name2);
                itemName3 = view.findViewById(R.id.item_name3);
                itemName4 = view.findViewById(R.id.item_name4);

                tab1 = view.findViewById(R.id.tab1);
                tab2 = view.findViewById(R.id.tab2);
                tab3 = view.findViewById(R.id.tab3);
                tab4 = view.findViewById(R.id.tab4);
            }

        }


    }
}
