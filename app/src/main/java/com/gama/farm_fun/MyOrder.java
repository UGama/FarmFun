package com.gama.farm_fun;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class MyOrder extends AppCompatActivity {

    private View topBar;
    private TextView title;

    private RecyclerView myOrderRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myorder);

        initUI();
    }

    public void initUI() {
        topBar = findViewById(R.id.topBar);
        title = topBar.findViewById(R.id.title);
        title.setText("我的订单");

        myOrderRecyclerView = findViewById(R.id.myorderRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        myOrderRecyclerView.setLayoutManager(linearLayoutManager);

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
            return orderList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private SimpleDraweeView projectPic;
            private TextView projectName;
            private TextView status;
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
