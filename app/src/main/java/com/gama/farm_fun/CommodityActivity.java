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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
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
    private String commodityName;
    private int commodityKindPrice;
    private String commodityKind;

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
    private TextView chosenText;
    private View chosenTab;
    private boolean firstTouch;

    private View countChosePanel;
    private TextView countText;
    private ImageView plus;
    private ImageView minus;
    private int count;
    private ImageView shelter;

    private View buy_cart;
    private Button buy;
    private Button addToCart;

    private TextView panelPrice;
    private TextView chosenKind;

    private AVObject orderAVObject;
    private Toast toast;

    private String url;

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
        buy_cart = commodityChosePanel.findViewById(R.id.buy_cart);
        buy = buy_cart.findViewById(R.id.buy);
        buy.setOnClickListener(this);
        addToCart = buy_cart.findViewById(R.id.addToCart);
        addToCart.setOnClickListener(this);
        panelPrice = commodityChosePanel.findViewById(R.id.price);
        chosenKind = commodityChosePanel.findViewById(R.id.chosen);

        shelter = findViewById(R.id.shelter);
        shelter.setOnClickListener(this);

        firstTouch = true;

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

                commodityName = commodity.name;
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
                    Poster poster = new Poster(avObject.getString("url"), "", commodity.code);
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
                    url = avObject.getString("url");
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
                if (!firstTouch) {
                    count++;
                    countText.setText(String.valueOf(count));
                    panelPrice.setText(String.valueOf(count * commodityKindPrice));
                    if (count == 2) {
                        minus.setBackground(getResources().getDrawable(R.drawable.shape_circle));
                    }
                } else {
                    showToast("请先选择产品种类。");
                }
                break;
            case R.id.minus:
                if (count > 1) {
                    count--;
                    countText.setText(String.valueOf(count));
                    panelPrice.setText(String.valueOf(count * commodityKindPrice));
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
            case R.id.buy:
                if (userId.equals("tourist")) {
                    Log.i("User", "click");
                    Intent loginIntent = new Intent(CommodityActivity.this, LoginActivity.class);
                    startActivityForResult(loginIntent, 1);
                } else if (firstTouch) {
                    showToast("请先选择商品种类和数量。");
                } else {
                    orderAVObject = new AVObject("Order");
                    orderAVObject.put("userId", userId);
                    orderAVObject.put("type", code);
                    orderAVObject.put("project", commodityName);
                    orderAVObject.put("status", "待支付");
                    orderAVObject.put("item", commodityKind);
                    orderAVObject.put("detail", "");
                    orderAVObject.put("count", count);
                    orderAVObject.put("price", commodityKindPrice);
                    orderAVObject.put("comment", false);
                    orderAVObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                Intent orderIntent = new Intent(CommodityActivity.this, CreateOrderActivity.class);
                                orderIntent.putExtra("UserId", userId);
                                orderIntent.putExtra("Type", code);
                                orderIntent.putExtra("Project", commodityName);
                                orderIntent.putExtra("Item", commodityKind);
                                orderIntent.putExtra("Url", url);
                                orderIntent.putExtra("Detail", "");
                                orderIntent.putExtra("Count", count);
                                orderIntent.putExtra("Price", commodityKindPrice);
                                orderIntent.putExtra("OrderId", orderAVObject.getObjectId());
                                orderIntent.putExtra("comment", false);
                                startActivityForResult(orderIntent, 0);
                            }
                        }
                    });


                }
                break;
            case R.id.addToCart:
                if (userId.equals("tourist")) {
                    Log.i("User", "click");
                    Intent loginIntent = new Intent(CommodityActivity.this, LoginActivity.class);
                    startActivityForResult(loginIntent, 1);
                } else if (firstTouch) {
                    showToast("请先选择商品种类和数量。");
                } else {
                    AVObject cartAVObject = new AVObject("Cart");
                    cartAVObject.put("userId", userId);
                    cartAVObject.put("kind", commodityKind);
                    cartAVObject.put("name", commodityName);
                    cartAVObject.put("count", count);
                    cartAVObject.put("singlePrice", commodityKindPrice);
                    cartAVObject.put("code", code);
                    cartAVObject.put("exist", true);
                    cartAVObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                showToast("成功加入购物车～");
                                firstTouch = true;
                                shelter.setVisibility(View.INVISIBLE);
                                commodityChosePanelQuit.start();
                            }
                        }
                    });
                }
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
                cItemArray = new CItemArray(cItemList.get(i));
                if (i == cItemList.size() - 1) {
                    cItemArray.setC2(new CItem(""));
                    cItemArray.setC3(new CItem(""));
                    cItemArray.setC4(new CItem(""));
                    cItemArrayList.add(cItemArray);
                }
            } else if (i % 4 == 1) {
                cItemArray.setC2(cItemList.get(i));
                if (i == cItemList.size() - 1) {
                    cItemArray.setC3(new CItem(""));
                    cItemArray.setC4(new CItem(""));
                    cItemArrayList.add(cItemArray);
                }
            } else if (i % 4 == 2) {
                cItemArray.setC3(cItemList.get(i));
                if (i == cItemList.size() - 1) {
                    cItemArray.setC4(new CItem(""));
                    cItemArrayList.add(cItemArray);
                }
            } else {
                cItemArray.setC4(cItemList.get(i));
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
            final CItemArray cItemArray = CItemArrayList.get(position);
            holder.itemName1.setText(cItemArray.c1.name);
            holder.itemName2.setText(cItemArray.c2.name);
            holder.itemName3.setText(cItemArray.c3.name);
            holder.itemName4.setText(cItemArray.c4.name);

            if (cItemArray.c2.name.equals("")) {
                holder.tab2.setVisibility(View.INVISIBLE);
            }
            if (cItemArray.c3.name.equals("")) {
                holder.tab3.setVisibility(View.INVISIBLE);
            }
            if (cItemArray.c4.name.equals("")) {
                holder.tab4.setVisibility(View.INVISIBLE);
            }

            holder.tab1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.tab1.setBackground(getResources().getDrawable(R.drawable.shape_kind_name2));
                    holder.itemName1.setTextColor(getResources().getColor(R.color.colorWhite));
                    Log.i("itemName", String.valueOf(holder.itemName1.getText().toString()));
                    commodityKind = holder.itemName1.getText().toString();
                    commodityKindPrice = cItemArray.c1.price;
                    chosenKind.setText(commodityKind);
                    panelPrice.setText(String.valueOf(cItemArray.c1.price * count));
                    if (firstTouch) {
                        chosenTab = holder.tab1;
                        chosenText = holder.itemName1;
                        firstTouch = false;
                    } else {
                        chosenText.setTextColor(getResources().getColor(R.color.colorTextGray));
                        chosenTab.setBackground(getResources().getDrawable(R.drawable.shape_kind_name));
                        chosenTab = holder.tab1;
                        chosenText = holder.itemName1;
                    }

                }
            });
            holder.tab2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.tab2.setBackground(getResources().getDrawable(R.drawable.shape_kind_name2));
                    holder.itemName2.setTextColor(getResources().getColor(R.color.colorWhite));
                    Log.i("itemName", String.valueOf(holder.itemName2.getText().toString()));
                    commodityKind = holder.itemName2.getText().toString();
                    commodityKindPrice = cItemArray.c2.price;
                    chosenKind.setText(commodityKind);
                    panelPrice.setText(String.valueOf(cItemArray.c2.price * count));
                    if (firstTouch) {
                        chosenTab = holder.tab2;
                        chosenText = holder.itemName2;
                        firstTouch = false;
                    } else {
                        chosenText.setTextColor(getResources().getColor(R.color.colorTextGray));
                        chosenTab.setBackground(getResources().getDrawable(R.drawable.shape_kind_name));
                        chosenTab = holder.tab2;
                        chosenText = holder.itemName2;
                    }
                }
            });
            holder.tab3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.tab3.setBackground(getResources().getDrawable(R.drawable.shape_kind_name2));
                    holder.itemName3.setTextColor(getResources().getColor(R.color.colorWhite));
                    commodityKind = holder.itemName3.getText().toString();
                    Log.i("itemName", String.valueOf(holder.itemName3.getText().toString()));
                    panelPrice.setText(String.valueOf(cItemArray.c3.price * count));
                    commodityKindPrice = cItemArray.c3.price;
                    chosenKind.setText(commodityKind);
                    if (firstTouch) {
                        chosenTab = holder.tab3;
                        chosenText = holder.itemName3;
                        firstTouch = false;
                    } else {
                        chosenText.setTextColor(getResources().getColor(R.color.colorTextGray));
                        chosenTab.setBackground(getResources().getDrawable(R.drawable.shape_kind_name));
                        chosenTab = holder.tab3;
                        chosenText = holder.itemName3;
                    }
                }
            });

            holder.tab4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.tab4.setBackground(getResources().getDrawable(R.drawable.shape_kind_name2));
                    holder.itemName4.setTextColor(getResources().getColor(R.color.colorWhite));
                    commodityKind = holder.itemName4.getText().toString();
                    Log.i("itemName", String.valueOf(holder.itemName4.getText().toString()));
                    panelPrice.setText(String.valueOf(cItemArray.c4.price * count));
                    commodityKindPrice = cItemArray.c4.price;
                    chosenKind.setText(commodityKind);
                    if (firstTouch) {
                        chosenTab = holder.tab4;
                        chosenText = holder.itemName4;
                        firstTouch = false;
                    } else {
                        chosenText.setTextColor(getResources().getColor(R.color.colorTextGray));
                        chosenTab.setBackground(getResources().getDrawable(R.drawable.shape_kind_name));
                        chosenTab = holder.tab4;
                        chosenText = holder.itemName4;
                    }
                }
            });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    finish();
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    userId = data.getStringExtra("UserId");
                    showToast("登录成功！");
                }
                break;
        }

    }
    private void showToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
            toast.show();
        } else {
            toast.setText(msg);
            toast.show();
        }
    }
}
