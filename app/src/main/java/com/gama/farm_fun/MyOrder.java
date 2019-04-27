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

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class MyOrder extends AppCompatActivity {
    private String userId;
    private String type;

    private View topBar;
    private TextView title;

    private RecyclerView myOrderRecyclerView;
    private List<Order> orderList;
    private int projectPicSupport;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myorder);

        Intent intent = getIntent();
        userId = intent.getStringExtra("UserId");
        type = intent.getStringExtra("Type");

        initUI();
    }

    public void initUI() {
        topBar = findViewById(R.id.topBar);
        title = topBar.findViewById(R.id.title);
        title.setText("我的订单");

        myOrderRecyclerView = findViewById(R.id.myorderRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        myOrderRecyclerView.setLayoutManager(linearLayoutManager);
        orderList = new ArrayList<>();

        getOrderInformation();
    }

    public void getOrderInformation() {
        AVQuery<AVObject> query = new AVQuery<>("Order");
        query.whereEqualTo("userId", userId);
        if (type.equals("Restaurant")) {
            query.whereEqualTo("type", type);
        } else if (type.equals("HomeStay")) {
            query.whereEqualTo("type", type);
        }
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                if (avObjects.size() == 0) {
                } else {
                    for (AVObject avObject : avObjects) {
                        Order order = new Order(avObject.getObjectId(),
                                avObject.getString("project"),
                                avObject.getString("item"),
                                avObject.getString("detail"),
                                avObject.getInt("price"),
                                avObject.getString("status"),
                                avObject.getInt("count"),
                                avObject.getString("type"),
                                avObject.getBoolean("comment"));
                        if (type.equals("Amusement")) {
                            if (avObject.getString("type").equals("HomeStay") ||
                                    avObject.getString("type").equals("Restaurant")) {
                            } else {
                                orderList.add(order);
                                Log.i("order", order.item);
                            }
                        } else {
                            orderList.add(order);
                        }
                    }
                    Log.i("orderList", String.valueOf(orderList.size()));
                    projectPicSupport = 0;
                    getProjectPic();
                }
            }
        });
    }

    public void getProjectPic() {
        AVQuery<AVObject> query = new AVQuery<>("_File");
        if (orderList.get(projectPicSupport).type.equals("HomeStay")) {
            query.whereEqualTo("name", "homestaymain.jpg");
        } else if (orderList.get(projectPicSupport).type.equals("Restaurant")) {
            query.whereEqualTo("name", "restaurantmain.jpg");
        } else {
            query.whereEqualTo("name", orderList.get(projectPicSupport).type + "main.jpg");
        }
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                orderList.get(projectPicSupport).setProjectPicUrl(object.getString("url"));
                Log.i(String.valueOf(projectPicSupport), object.getString("url"));
                projectPicSupport++;
                if (projectPicSupport == orderList.size()) {
                    initMyOrderRecyclerView();
                } else {
                    getProjectPic();
                }

            }
        });


    }

    public void initMyOrderRecyclerView() {
        MyOrderAdapter myOrderAdapter = new MyOrderAdapter(orderList);
        myOrderRecyclerView.setAdapter(myOrderAdapter);
    }

    private class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder> {
        private List<Order> orderList;

        private MyOrderAdapter(List<Order> orderList) {
            this.orderList = orderList;
        }


        @NonNull
        @Override
        public MyOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_myorder, parent, false);
            MyOrderAdapter.ViewHolder holder = new MyOrderAdapter.ViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final MyOrderAdapter.ViewHolder holder, int position) {
            Order order = orderList.get(position);
            holder.setId(order.id);
            holder.setCommentJudge(order.comment);
            if (holder.commentJudge) {
                holder.comment.setVisibility(View.VISIBLE);
            } else {
                holder.comment.setVisibility(View.INVISIBLE);
            }
            holder.setUrl(order.projectPicUrl);
            Uri imageUri = Uri.parse(order.projectPicUrl);
            holder.projectPic.setImageURI(imageUri);
            RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);
            holder.projectPic.getHierarchy().setRoundingParams(roundingParams);

            holder.projectName.setText(order.project);
            holder.status.setText(order.status);
            if (order.status.equals("已支付")) {
                holder.status.setTextColor(getResources().getColor(R.color.colorGreen));
            } else if (order.status.equals("待支付")) {
                holder.status.setTextColor(getResources().getColor(R.color.colorRemind));
            } else if (order.status.equals("已评价")) {
                holder.status.setTextColor(getResources().getColor(R.color.colorBlack));
            }
            if (order.type.equals("HomeStay")) {
                holder.count.setText("共" + String.valueOf(order.count) + "晚");
            } else {
                holder.count.setText("x" + String.valueOf(order.count));
            }
            holder.itemDetail.setText(order.detail);
            holder.itemName.setText(order.item);
            holder.itemPrice.setText(String.valueOf(order.price));

            holder.setType(order.type);

            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyOrder.this, EditCommentActivity.class);
                    intent.putExtra("project", holder.projectName.getText().toString());
                    intent.putExtra("url", holder.url);
                    intent.putExtra("item", holder.itemName.getText().toString());
                    intent.putExtra("detail", holder.itemDetail.getText().toString());
                    intent.putExtra("count", holder.count.getText().toString());
                    intent.putExtra("orderId", holder.id);
                    intent.putExtra("type", holder.type);
                    startActivityForResult(intent, 0);
                }
            });
        }

        @Override
        public int getItemCount() {
            return orderList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private SimpleDraweeView projectPic;
            private TextView projectName;
            private TextView status;
            private TextView itemName;
            private TextView itemDetail;
            private TextView itemPrice;
            private TextView count;
            private Button comment;
            private String id;
            private String url;
            private boolean commentJudge;
            private String type;

            private ViewHolder(View view) {
                super(view);
                projectPic = view.findViewById(R.id.project_pic);
                projectName = view.findViewById(R.id.project_name);
                status = view.findViewById(R.id.status);
                itemName = view.findViewById(R.id.item);
                itemDetail = view.findViewById(R.id.detail);
                itemPrice = view.findViewById(R.id.price);
                count = view.findViewById(R.id.count);
                comment = view.findViewById(R.id.comment);
            }

            private void setUrl(String url) {
                this.url = url;
            }

            private void setId(String id) {
                this.id = id;
            }

            private void setCommentJudge(boolean commentJudge) {
                this.commentJudge = commentJudge;
            }

            private void setType(String type) {
                this.type = type;
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
        }
    }
}
