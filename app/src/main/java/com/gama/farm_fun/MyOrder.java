package com.gama.farm_fun;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class MyOrder extends AppCompatActivity {
    private String userId;

    private View topBar;
    private TextView title;

    private RecyclerView myOrderRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myorder);

        Intent intent = getIntent();
        userId = intent.getStringExtra("UserId");

        initUI();
    }

    public void initUI() {
        topBar = findViewById(R.id.topBar);
        title = topBar.findViewById(R.id.title);
        title.setText("我的订单");

        myOrderRecyclerView = findViewById(R.id.myorderRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        myOrderRecyclerView.setLayoutManager(linearLayoutManager);

        getOrderInformation();
    }

    public void getOrderInformation() {
        AVQuery<AVObject> query = new AVQuery<>("Order");
        query.whereEqualTo("userId", userId);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                for (AVObject avObject : avObjects) {

                }
            }
        });
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
            private Button comment;

            private ViewHolder(View view) {
                super(view);
                projectPic = view.findViewById(R.id.project_pic);
                projectName = view.findViewById(R.id.project_name);
                status = view.findViewById(R.id.status);
                itemName = view.findViewById(R.id.item);
                itemDetail = view.findViewById(R.id.detail);
                itemPrice = view.findViewById(R.id.price);
                comment = view.findViewById(R.id.comment);
            }
        }
    }
}
