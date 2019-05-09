package com.gama.farm_fun;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
import com.avos.avoscloud.GetCallback;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class CreateTravelPlanOrderActivity extends AppCompatActivity implements View.OnClickListener {

    private String userId;
    private String orderId;
    private String type;
    private String project;
    private String itemName;
    private String url;
    private String detail;
    private int count;
    private int price;
    private int orderPrice;
    private String[] names;
    private String[] types;
    private String[] kinds;
    private String[] urls;
    private int[] counts;
    private int[] prices;
    private int dayCount;
    private int startDay;

    private View topBar;
    private TextView title;
    private Button back;

    private TextView projectName;
    private RecyclerView orderRecyclerView;
    private List<Item> itemList;
    private TextView totalPrice;
    private View remarkPanel;
    private TextView remark;

    private View submitPanel;
    private TextView totalPrice2;
    private Button submit;

    private ConstraintLayout countChose;
    private ImageView plus;
    private ImageView minus;
    private TextView numberTextView;
    private int number;

    private ConstraintLayout addressPanel;
    private TextView addressText;
    private TextView nameText;
    private TextView phoneText;

    private String way;
    private int cartSupport;


    private Toast toast;
    private int logSupport;
    private int support;

    private View loading;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_createorder);

        logSupport = 0;
        Intent intent = getIntent();
        dayCount = intent.getIntExtra("dayCount", 0);
        startDay = intent.getIntExtra("startDay", 0);
        names = intent.getStringArrayExtra("names");
        checkName(names);
        counts = intent.getIntArrayExtra("counts");
        types = intent.getStringArrayExtra("types");
        prices = intent.getIntArrayExtra("prices");
        checkName(types);

        loading = findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);

        initUI();
    }

    public void initUI() {
        topBar = findViewById(R.id.topBar);
        title = topBar.findViewById(R.id.title);
        title.setText("旅行计划订单");
        back = topBar.findViewById(R.id.back);
        back.setOnClickListener(this);

        projectName = findViewById(R.id.project_name);
        projectName.setText("旅行计划");
        orderRecyclerView = findViewById(R.id.orderRecyclerView);
        itemList = new ArrayList<>();
        totalPrice = findViewById(R.id.total_price);
        remarkPanel = findViewById(R.id.panel_remark);
        remarkPanel.setOnClickListener(this);
        remark = remarkPanel.findViewById(R.id.remark);

        submitPanel = findViewById(R.id.submit_order);
        totalPrice2 = submitPanel.findViewById(R.id.price);
        submit = submitPanel.findViewById(R.id.submit);
        submit.setOnClickListener(this);

        countChose = findViewById(R.id.countChose);
        countChose.setVisibility(View.INVISIBLE);


        addressPanel = findViewById(R.id.addressPanel);
        addressPanel.setVisibility(View.INVISIBLE);

        orderRecyclerView = findViewById(R.id.orderRecyclerView);

        support = 0;
        orderPrice = 0;

        urls = new String[names.length];
        initData();
    }

    public void initData() {
        AVQuery<AVObject> query = new AVQuery<>("_File");
        if (types[support].equals("HomeStay")) {
            query.whereEqualTo("name", "standardroom.jpg");
        } else if (types[support].equals("Restaurant")) {
            query.whereEqualTo("name", "restaurantmain.jpg");
        } else {
            query.whereEqualTo("name", types[support] + "main.jpg");
        }
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                urls[support] = object.getString("url");
                support++;
                if (support == names.length) {
                    initItemRecyclerView();
                } else {
                    initData();
                }
            }
        });
    }

    public void initItemRecyclerView() {
        for (int i = 0; i < names.length; i++) {
            if (types[i].equals("HomeStay")) {
                String name = names[i].substring(0, 3);
                String detail = names[i].substring(4);
                Item item = new Item(name, urls[i], detail, counts[i], prices[i]);
                item.setType(types[i]);
                itemList.add(item);
            } else if (types[i].equals("Restaurant")) {
                String name = names[i].substring(0, 2);
                String detail = names[i].substring(3);
                Item item = new Item(name, urls[i], detail, counts[i], prices[i]);
                item.setType(types[i]);
                itemList.add(item);
            } else {
                String detail = "6月" + String.valueOf(startDay + i) + "日";
                Item item = new Item(names[i], urls[i], detail, counts[i], prices[i]);
                item.setType(types[i]);
                itemList.add(item);
            }
            orderPrice += prices[i];
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        orderRecyclerView.setLayoutManager(linearLayoutManager);
        ItemAdapter itemAdapter = new ItemAdapter(itemList);
        orderRecyclerView.setAdapter(itemAdapter);

        totalPrice.setText(String.valueOf(orderPrice));
        totalPrice2.setText(String.valueOf(orderPrice));

        loading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                Intent intent = new Intent(CreateTravelPlanOrderActivity.this, PaymentActivity.class);
                intent.putExtra("price", orderPrice);
                startActivityForResult(intent, 1);
                break;
        }

    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
        private List<Item> itemList;

        private ItemAdapter(List<Item> itemList) {
            this.itemList = itemList;
        }


        @NonNull
        @Override
        public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_item, parent, false);
            ItemAdapter.ViewHolder holder = new ItemAdapter.ViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final ItemAdapter.ViewHolder holder, int position) {
            Item item = itemList.get(position);
            holder.itemName.setText(item.name);
            holder.itemPic.setImageURI(Uri.parse(item.picUrl));
            RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);
            holder.itemPic.getHierarchy().setRoundingParams(roundingParams);
            holder.itemDetail.setText(item.detail);
            if (item.type.equals("HomeStay")) {
                holder.itemCount.setText("共" + String.valueOf(item.count) + "晚");
            } else {
                holder.itemCount.setText("x" + String.valueOf(item.count));
            }
            holder.itemPrice.setText(String.valueOf(item.price));
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private SimpleDraweeView itemPic;
            private TextView itemName;
            private TextView itemDetail;
            private TextView itemCount;
            private TextView itemPrice;

            private ViewHolder(View view) {
                super(view);
                itemPic = view.findViewById(R.id.project_pic);
                itemName = view.findViewById(R.id.project_name);
                itemDetail = view.findViewById(R.id.item_detail);
                itemCount = view.findViewById(R.id.project_locate);
                itemPrice = view.findViewById(R.id.item_price);
            }
        }
    }


    public void checkName(String[] strings) {
        for (int i = 0; i < strings.length; i++) {
            Log.i("check" + String.valueOf(logSupport), strings[i]);
            logSupport++;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    showToast("支付成功！");
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }
}
