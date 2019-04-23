package com.gama.farm_fun;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity implements View.OnClickListener {

    private String userId;
    private String orderId;
    private String type;
    private String project;
    private String[] items;
    private String[] urls;
    private String[] details;
    private int[] counts;
    private int[] prices;

    private View topBar;
    private TextView title;

    private TextView projectName;
    private RecyclerView orderRecyclerView;
    private List<Item> itemList;
    private TextView totalPrice;
    private View remarkPanel;
    private TextView remark;

    private View submitPanel;
    private TextView totalPrice2;
    private Button submit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        getOrderInformation();
    }
    public void getOrderInformation() {
        Intent orderIntent = getIntent();
        userId = orderIntent.getStringExtra("UserId");
        orderId = orderIntent.getStringExtra("OrderId");
        type = orderIntent.getStringExtra("Type");
        Log.i("type", type);
        project = orderIntent.getStringExtra("Project");
        if (type.equals("homeStay")) {
            items = new String[1];
            items[0] = orderIntent.getStringExtra("Item");
            urls = new String[1];
            urls[0] = orderIntent.getStringExtra("Url");
            details = new String[1];
            details[0] = orderIntent.getStringExtra("Detail");
            counts = new int[1];
            counts[0] = orderIntent.getIntExtra("Count", 0);
            prices = new int[1];
            prices[0] = orderIntent.getIntExtra("Price", 0);
        }
        initUI();
    }

    public void initUI() {
        topBar = findViewById(R.id.topBar);
        title = topBar.findViewById(R.id.title);
        if (type.equals("homeStay")) {
            title.setText("民宿订单");
        }

        projectName = findViewById(R.id.project_name);
        projectName.setText(project);
        orderRecyclerView = findViewById(R.id.orderRecyclerView);
        itemList = new ArrayList<>();
        totalPrice = findViewById(R.id.total_price);
        remarkPanel = findViewById(R.id.panel_remark);
        remarkPanel.setOnClickListener(this);
        remark = remarkPanel.findViewById(R.id.remark);

        submitPanel = findViewById(R.id.submit_order);
        totalPrice2 = submitPanel.findViewById(R.id.price);
        submit = submitPanel.findViewById(R.id.submit);

        initOrderRecyclerView();
    }

    public void initOrderRecyclerView() {
        for (int i = 0; i < items.length; i++) {
            Item item = new Item(items[i], urls[i], details[i], counts[i], prices[i]);
            Log.i("items", items[i]);
            itemList.add(item);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        orderRecyclerView.setLayoutManager(linearLayoutManager);
        ItemAdapter itemAdapter = new ItemAdapter(itemList);
        orderRecyclerView.setAdapter(itemAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.panel_remark:
                Intent remarkIntent = new Intent(OrderActivity.this, EditRemarkActivity.class);
                startActivityForResult(remarkIntent, 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    remark.setText(data.getStringExtra("remark"));
                }
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
            holder.itemCount.setText("x" + String.valueOf(item.count));
            holder.itemPrice.setText(String.valueOf(item.count * item.price));
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
                itemPic = view.findViewById(R.id.item_pic);
                itemName = view.findViewById(R.id.item_name);
                itemDetail = view.findViewById(R.id.item_detail);
                itemCount = view.findViewById(R.id.item_count);
                itemPrice = view.findViewById(R.id.item_price);
            }
        }
    }
}
