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
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                for (AVObject avObject : avObjects) {
                    Order order = new Order(avObject.getString("project"),
                            avObject.getString("item"),
                            avObject.getString("detail"),
                            avObject.getInt("price"),
                            avObject.getString("status"),
                            avObject.getInt("count"),
                            avObject.getString("type"));
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
            }
            if (order.type.equals("HomeStay")) {
                holder.count.setText("共" + String.valueOf(order.count) + "晚");
            } else {
                holder.count.setText("x" + String.valueOf(order.count));
            }
            holder.itemDetail.setText(order.detail);
            holder.itemName.setText(order.item);
            holder.itemPrice.setText(String.valueOf(order.price));
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
        }
    }
}
