package com.gama.farm_fun;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class CartActivity extends AppCompatActivity implements View.OnClickListener {
    private String userId;

    private View topBar;
    private TextView title;
    private Button back;
    private TextView delete;

    private RecyclerView cartRecyclerView;
    private List<CartCommodity> cartCommodityList;
    private int support;

    private boolean[] chosen;
    private int chosenSupport;

    private Button buy;
    private TextView wholePriceText;
    private int wholePrice;

    private TextView emptyCart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_cart);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        initUI();
    }
    public void initUI() {
        topBar = findViewById(R.id.bar_top);
        title = topBar.findViewById(R.id.title);
        title.setText("购物车");
        back = topBar.findViewById(R.id.back);
        back.setOnClickListener(this);
        delete = topBar.findViewById(R.id.finish);
        delete.setText("删除");
        delete.setOnClickListener(this);

        buy = findViewById(R.id.buy);
        buy.setOnClickListener(this);
        wholePriceText = findViewById(R.id.price);
        wholePrice = 0;

        emptyCart = findViewById(R.id.cart_empty);

        getCartInformation();

    }

    public void getCartInformation() {
        chosenSupport = 0;
        cartCommodityList = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("Cart");
        query.whereEqualTo("UseId", userId);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                if (avObjects.size() == 0) {
                    emptyCart.setVisibility(View.VISIBLE);
                    Log.i("tip", "购物车无商品。");
                } else {
                    for (AVObject avObject : avObjects) {
                        CartCommodity cartCommodity = new CartCommodity(avObject.getString("name"),
                                avObject.getString("kind"),
                                avObject.getString("code"),
                                avObject.getString("code") + "Kind1.jpg",
                                avObject.getInt("count"),
                                avObject.getInt("singlePrice"));
                        cartCommodity.setNumber(chosenSupport);
                        chosenSupport++;
                        cartCommodityList.add(cartCommodity);
                    }
                    support = 0;
                    getCartPic();
                }
            }
        });
    }

    public void getCartPic() {
        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereEqualTo("name", cartCommodityList.get(support).picName);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                cartCommodityList.get(support).setUrl(object.getString("url"));
                support++;
                if (support == cartCommodityList.size()) {
                    initCartRecyclerView();
                } else {
                    getCartPic();
                }
            }
        });
    }

    public void initCartRecyclerView() {
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        cartRecyclerView.setLayoutManager(linearLayoutManager);
        CartAdapter cartAdapter = new CartAdapter(cartCommodityList);
        cartRecyclerView.setAdapter(cartAdapter);

        chosen = new boolean[cartCommodityList.size()];
        for (int i = 0; i < chosen.length; i++) {
            chosen[i] = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    private class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
        private List<CartCommodity> cartCommodityList;

        private CartAdapter(List<CartCommodity> cartCommodityList) {
            this.cartCommodityList = cartCommodityList;
        }

        @Override
        public CartAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cart, parent, false);
            final CartAdapter.ViewHolder holder = new CartAdapter.ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final CartAdapter.ViewHolder holder, int position) {
            CartCommodity cartCommodity = cartCommodityList.get(position);
            holder.itemName.setText(cartCommodity.name);
            holder.itemCount.setText("x" + String.valueOf(cartCommodity.count));
            holder.itemKind.setText(cartCommodity.kind);
            holder.itemPrice.setText(String.valueOf(cartCommodity.count * cartCommodity.singlePrice));
            holder.itemPic.setImageURI(cartCommodity.url);
            RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);
            holder.itemPic.getHierarchy().setRoundingParams(roundingParams);
            holder.setNumber(cartCommodity.number);
            holder.setCount(cartCommodity.count);
            holder.setSinglePrice(cartCommodity.singlePrice);
            holder.chose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chosen[holder.number]) {
                        holder.chose.setBackground(getResources().getDrawable(R.drawable.shape_circle3));
                        chosen[holder.number] = false;
                        wholePrice -= holder.singlePrice * holder.count;
                        wholePriceText.setText(String.valueOf(wholePrice));
                    } else {
                        holder.chose.setBackground(getResources().getDrawable(R.drawable.chose_pay_way));
                        chosen[holder.number] = true;
                        wholePrice += holder.singlePrice * holder.count;
                        wholePriceText.setText(String.valueOf(wholePrice));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return cartCommodityList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView itemName;
            private TextView itemCount;
            private TextView itemPrice;
            private TextView itemKind;
            private SimpleDraweeView itemPic;
            private ImageView chose;
            private int number;
            private int count;
            private int singlePrice;

            private ViewHolder(View view) {
                super(view);
                itemName = view.findViewById(R.id.commodity_name);
                itemCount = view.findViewById(R.id.commodity_count);
                itemPrice = view.findViewById(R.id.commodity_price);
                itemKind = view.findViewById(R.id.commodity_kind);
                itemPic = view.findViewById(R.id.commodity_pic);
                chose = view.findViewById(R.id.chose);
            }

            public void setNumber(int number) {
                this.number = number;
            }

            public void setCount(int count) {
                this.count = count;
            }

            public void setSinglePrice(int singlePrice) {
                this.singlePrice = singlePrice;
            }
        }


    }


}
