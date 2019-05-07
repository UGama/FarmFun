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
import com.avos.avoscloud.SaveCallback;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class OrderDetail extends AppCompatActivity implements View.OnClickListener {
    private String orderId;
    private String userId;
    private String projectPicUrl;
    private String orderDetail;
    private String orderItem;
    private String itemUrl;
    private String orderCount;
    private String orderType;
    private String projectNameString;
    private int itemPrice;
    private int totalPrice;

    private TextView projectName;
    private TextView totalPriceText;

    private RecyclerView orderRecyclerView;
    private List<Item> itemList;
    private TextView contactText;
    private TextView orderIdText;
    private TextView userNameText;
    private TextView statusText;
    private TextView remarkText;

    private Button comment;
    private Button refund;

    private ConstraintLayout confirmPanel;
    private Button confirm;
    private Button cancel;

    private ImageView shelter;

    private Toast toast;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_order_detail);

        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        Log.i("Id", orderId);
        projectNameString = intent.getStringExtra("project");
        projectPicUrl = intent.getStringExtra("url");
        orderDetail = intent.getStringExtra("detail");
        orderCount = intent.getStringExtra("count");
        orderItem = intent.getStringExtra("item");
        orderType = intent.getStringExtra("type");
        userId = intent.getStringExtra("userId");
        totalPrice = intent.getIntExtra("totalPrice", 0);
        Log.i("count", orderCount);

        initUI();
    }
    public void initUI() {
        projectName = findViewById(R.id.project_name);
        projectName.setText(projectNameString);
        totalPriceText = findViewById(R.id.total_price);
        totalPriceText.setText(String.valueOf(totalPrice));
        orderIdText = findViewById(R.id.orderId);
        orderIdText.setText(orderId);

        userNameText = findViewById(R.id.userName);
        contactText = findViewById(R.id.mobile);
        statusText = findViewById(R.id.status);

        orderRecyclerView = findViewById(R.id.orderRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        orderRecyclerView.setLayoutManager(linearLayoutManager);
        itemList = new ArrayList<>();

        comment = findViewById(R.id.comment);
        comment.setOnClickListener(this);

        refund = findViewById(R.id.refund);
        refund.setOnClickListener(this);

        confirmPanel = findViewById(R.id.panel_confirm);
        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(this);
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        shelter = findViewById(R.id.shelter);
        shelter.setOnClickListener(this);

        getUserInformation();
    }

    public void getUserInformation() {
        AVQuery<AVObject> query = new AVQuery<>("Users");
        query.whereEqualTo("objectId", userId);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                userNameText.setText(object.getString("netName"));
                contactText.setText(object.getString("mobilePhone"));
                getOrderInformation();
            }
        });
    }

    public void getOrderInformation() {
        AVQuery<AVObject> query = new AVQuery<>("Order");
        query.whereEqualTo("objectId", orderId);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                if (object.getString("status").equals("已支付")) {
                    comment.setVisibility(View.VISIBLE);
                } else {
                    comment.setVisibility(View.INVISIBLE);
                }
                if (object.getString("status").equals("已支付") & orderType.equals("HomeStay")) {
                    refund.setVisibility(View.VISIBLE);
                }
                statusText.setText(object.getString("status"));
                if (orderType.equals("HomeStay") || orderType.equals("Restaurant")) {
                    getItemPic();
                } else {
                    itemUrl = "";
                    initOrderRecyclerView();
                }
            }
        });
    }

    public void getItemPic() {
        if (orderType.equals("HomeStay")) {
            AVQuery<AVObject> query = new AVQuery<>("Room");
            query.whereEqualTo("roomType", orderItem);
            query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject object, AVException e) {
                    itemPrice = object.getInt("price");
                    String itemPicName = object.getString("roomPicName");
                    AVQuery<AVObject> query1 = new AVQuery<>("_File");
                    query1.whereEqualTo("name", itemPicName);
                    query1.getFirstInBackground(new GetCallback<AVObject>() {
                        @Override
                        public void done(AVObject object, AVException e) {
                            itemUrl = object.getString("url");
                            initOrderRecyclerView();
                        }
                    });
                }
            });
        } else if (orderType.equals("Restaurant")) {
            AVQuery<AVObject> query = new AVQuery<>("Seat");
            query.whereEqualTo("type", orderItem);
            query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject object, AVException e) {
                    itemPrice = 20;
                    String itemPicName = object.getString("picName");
                    AVQuery<AVObject> query1 = new AVQuery<>("_File");
                    query1.whereEqualTo("name", itemPicName);
                    query1.getFirstInBackground(new GetCallback<AVObject>() {
                        @Override
                        public void done(AVObject object, AVException e) {
                            itemUrl = object.getString("url");
                            initOrderRecyclerView();
                        }
                    });
                }
            });
        } else {
            itemUrl = projectPicUrl;
        }
    }

    public void initOrderRecyclerView() {
        Item item = new Item(orderItem, itemUrl, orderDetail, orderCount, itemPrice);
        itemList.add(item);
        ItemAdapter itemAdapter = new ItemAdapter(itemList);
        orderRecyclerView.setAdapter(itemAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comment:
                Intent intent = new Intent(OrderDetail.this, EditCommentActivity.class);
                intent.putExtra("project", projectNameString);
                intent.putExtra("url", projectPicUrl);
                intent.putExtra("item", orderItem);
                intent.putExtra("detail", orderDetail);
                intent.putExtra("count", orderCount);
                intent.putExtra("orderId", orderId);
                intent.putExtra("type", orderType);
                intent.putExtra("userId", userId);
                startActivityForResult(intent, 0);
                break;
            case R.id.refund:
                shelter.setVisibility(View.VISIBLE);
                confirmPanel.setVisibility(View.VISIBLE);
                break;
            case R.id.shelter:
            case R.id.cancel:
                shelter.setVisibility(View.INVISIBLE);
                confirmPanel.setVisibility(View.INVISIBLE);
                break;
            case R.id.confirm:
                AVObject avObject = AVObject.createWithoutData("Order", orderId);
                avObject.put("status", "已退订");
                avObject.put("comment", false);
                avObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            showToast("退订成功！");
                            finish();
                        }
                    }
                });
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
            if (orderType.equals("homeStay")) {
                holder.itemCount.setText(item.countString);
            } else {
                holder.itemCount.setText(item.countString);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    comment.setVisibility(View.INVISIBLE);
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
