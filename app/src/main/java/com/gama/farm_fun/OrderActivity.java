package com.gama.farm_fun;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {

    private String userId;
    private String orderId;
    private String type;
    private String project;
    private String[] items;
    private String[] detail;
    private int[] count;
    private int[] price;

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
        project = orderIntent.getStringExtra("Project");
        if (type.equals("homeStay")) {
            items = new String[1];
            items[0] = orderIntent.getStringExtra("Item");
            detail = new String[1];
            detail[0] = orderIntent.getStringExtra("Detail");
            count = new int[1];
            count[0] = orderIntent.getIntExtra("Count", 0);
            price = new int[1];
            price[0] = orderIntent.getIntExtra("Price", 0);
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
        remark = remarkPanel.findViewById(R.id.remark);

        submitPanel = findViewById(R.id.submit_order);
        totalPrice2 = submitPanel.findViewById(R.id.price);
        submit = submitPanel.findViewById(R.id.submit);

        initOrderRecyclerView();
    }

    public void initOrderRecyclerView() {
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
                    .inflate(R.layout.item_months, parent, false);
            ItemAdapter.ViewHolder holder = new ItemAdapter.ViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final ItemAdapter.ViewHolder holder, int position) {
            Item item = itemList.get(position);
            holder.itemPic.setImageURI(Uri.parse(item.picUrl));
            RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);
            holder.itemPic.getHierarchy().setRoundingParams(roundingParams);
            holder.itemName.setText(item.name);
            holder.itemDetail.setText(item.detail);
            holder.itemCount.setText(String.valueOf(item.count));
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
                itemPic = findViewById(R.id.item_pic);
                itemName = findViewById(R.id.item_name);
                itemDetail = findViewById(R.id.item_detail);
                itemCount = findViewById(R.id.item_count);
                itemPrice = findViewById(R.id.item_price);
            }
        }
    }
}
