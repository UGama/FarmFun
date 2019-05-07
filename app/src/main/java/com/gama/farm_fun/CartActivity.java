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

public class CartActivity extends AppCompatActivity implements View.OnClickListener {
    private String userId;

    private View topBar;
    private TextView title;
    private Button back;
    private TextView delete;
    private int deleteSupport;

    private RecyclerView cartRecyclerView;
    private List<CartCommodity> cartCommodityList;
    private int support;
    private String[] cartIds;

    private boolean[] chosen;
    private int chosenSupport;
    private AVObject orderAVObject;

    private Button buy;
    private TextView wholePriceText;
    private int wholePrice;

    private TextView emptyCart;

    private Toast toast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_cart);

        Intent intent = getIntent();
        userId = intent.getStringExtra("UserId");
        Log.i("UserId", userId);

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
        delete.setVisibility(View.INVISIBLE);
        chosenSupport = 0;
        deleteSupport = 0;
        cartCommodityList = new ArrayList<>();
        wholePriceText.setText("0");
        AVQuery<AVObject> query = new AVQuery<>("Cart");
        query.whereEqualTo("UseId", userId);
        query.whereEqualTo("exist", true);
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                if (avObjects.size() == 0) {
                    emptyCart.setVisibility(View.VISIBLE);
                    Log.i("tip", "购物车无商品。");
                    cartRecyclerView.setVisibility(View.INVISIBLE);
                } else {
                    cartIds = new String[avObjects.size()];
                    for (AVObject avObject : avObjects) {
                        CartCommodity cartCommodity = new CartCommodity(avObject.getString("name"),
                                avObject.getString("kind"),
                                avObject.getString("code"),
                                avObject.getString("code") + "Kind1.jpg",
                                avObject.getInt("count"),
                                avObject.getInt("singlePrice"));
                        cartCommodity.setNumber(chosenSupport);
                        cartIds[chosenSupport] = avObject.getObjectId();
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
                Log.i("Url1", cartCommodityList.get(support).url);
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
            case R.id.buy:
                if (cartCommodityList.size() == 0) {
                    showToast("请选择商品加入购物车");
                } else if (!deleteShow()) {
                    showToast("请选择商品。");
                } else {
                    orderAVObject = new AVObject("Order");
                    orderAVObject.put("userId", userId);
                    orderAVObject.put("type", "manyC");
                    orderAVObject.put("project", getItemsNameString());
                    orderAVObject.put("status", "待支付");
                    orderAVObject.put("item", getItemsKindString());
                    orderAVObject.put("detail", "");
                    orderAVObject.put("price", wholePrice);
                    orderAVObject.put("counts", getCountString());
                    orderAVObject.put("comment", false);
                    orderAVObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                Intent orderIntent = new Intent(CartActivity.this, CreateOrderActivity.class);
                                orderIntent.putExtra("UserId", userId);
                                orderIntent.putExtra("Type", "manyC");
                                orderIntent.putExtra("Code", getItemsCode());
                                orderIntent.putExtra("Project", getItemsName());
                                orderIntent.putExtra("Item", getItemsKind());
                                orderIntent.putExtra("Url", getItemsUrl());
                                orderIntent.putExtra("Count", getItemsCount());
                                orderIntent.putExtra("Price", getItemsPrice());
                                orderIntent.putExtra("CartId", getCartIds());
                                orderIntent.putExtra("OrderId", orderAVObject.getObjectId());
                                startActivityForResult(orderIntent, 0);
                            }
                        }
                    });
                }
                break;
            case R.id.finish:
                for (int i = 0; i < chosen.length; i++) {
                    if (chosen[i]) {
                        deleteSupport++;
                        AVObject avObject = AVObject.createWithoutData("Cart", cartIds[i]);
                        avObject.put("exist", false);
                        avObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    if (deleteSupport == getChosenNumber()) {
                                        getCartInformation();
                                    }
                                }
                            }
                        });
                    }
                }
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
                    if (deleteShow()) {
                        delete.setVisibility(View.VISIBLE);
                    } else {
                        delete.setVisibility(View.INVISIBLE);
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

    public boolean deleteShow() {
        boolean show = false;
        for (int i = 0; i < chosen.length; i++) {
            if (chosen[i]) {
                show = true;
                break;
            }
        }
        return show;
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

    private int getChosenNumber() {
        int count = 0;
        for (int i = 0; i < chosen.length; i++) {
            if (chosen[i]) {
                count++;
            }
        }
        return count;
    }

    
    private String getItemsNameString() {
        String itemsNameString = "";
        for (int i = 0; i < cartCommodityList.size(); i++) {
            if (chosen[i]) {
                itemsNameString += cartCommodityList.get(i).name + ";";
            }
        }
        Log.i("itemNameString", itemsNameString);
        return itemsNameString;
    }
    private String getItemsKindString() {
        String itemsNameString = "";
        for (int i = 0; i < cartCommodityList.size(); i++) {
            if (chosen[i]) {
                itemsNameString += cartCommodityList.get(i).kind + ";";
            }
        }
        Log.i("itemKindString", itemsNameString);
        return itemsNameString;
    }

    private String[] getCartIds() {
        String[] items = new String[getChosenNumber()];
        int k = 0;
        for (int i = 0; i < cartCommodityList.size(); i++) {
            if (chosen[i]) {
                items[k] = cartIds[i];
                Log.i("cartId0", items[k]);
                k++;
            }
        }
        return items;
    }

    private String[] getItemsName() {

        String[] items = new String[getChosenNumber()];
        int k = 0;
        for (int i = 0; i < cartCommodityList.size(); i++) {
            if (chosen[i]) {
                items[k] = cartCommodityList.get(i).name;
                k++;
            }
        }
        return items;
    }
    private String[] getItemsKind() {
        String[] items = new String[getChosenNumber()];
        int k = 0;
        for (int i = 0; i < cartCommodityList.size(); i++) {
            if (chosen[i]) {
                items[k] = cartCommodityList.get(i).kind;
                k++;
            }
        }
        return items;
    }

    private int[] getItemsCount() {
        int[] items = new int[getChosenNumber()];
        int k = 0;
        for (int i = 0; i < cartCommodityList.size(); i++) {
            if (chosen[i]) {
                items[k] = cartCommodityList.get(i).count;
                k++;
            }
        }
        return items;
    }

    private int[] getItemsPrice() {
        int[] items = new int[getChosenNumber()];
        int k = 0;
        for (int i = 0; i < cartCommodityList.size(); i++) {
            if (chosen[i]) {
                items[k] = cartCommodityList.get(i).singlePrice;
                k++;
            }
        }
        return items;
    }

    private String[] getItemsUrl() {
        String[] items = new String[getChosenNumber()];
        int k = 0;
        for (int i = 0; i < cartCommodityList.size(); i++) {
            if (chosen[i]) {
                items[k] = cartCommodityList.get(i).url;
                Log.i("url1", items[k]);
                k++;
            }
        }
        return items;
    }

    private String[] getItemsCode() {
        String[] items = new String[getChosenNumber()];
        int k = 0;
        for (int i = 0; i < cartCommodityList.size(); i++) {
            if (chosen[i]) {
                items[k] = cartCommodityList.get(i).code;
                k++;
            }
        }
        return items;
    }

    private String getCountString() {
        String itemsNameString = "";
        for (int i = 0; i < cartCommodityList.size(); i++) {
            if (chosen[i]) {
                itemsNameString += String.valueOf(cartCommodityList.get(i).count) + ";";
            }
        }
        return itemsNameString;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    initUI();
                }
                break;
        }
    }

}
