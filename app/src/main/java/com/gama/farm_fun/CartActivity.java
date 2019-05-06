package com.gama.farm_fun;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class CartActivity extends AppCompatActivity implements View.OnClickListener {
    private String UserId;

    private View topBar;
    private TextView title;
    private Button back;

    private RecyclerView cartRecyclerView;
    private List<Item> itemList;

    private TextView emptyCart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_cart);

        Intent intent = getIntent();
        UserId = intent.getStringExtra("UserId");

        initUI();
    }
    public void initUI() {
        topBar = findViewById(R.id.bar_top);
        title = topBar.findViewById(R.id.title);
        title.setText("购物车");
        back = topBar.findViewById(R.id.back);
        back.setOnClickListener(this);


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
        private List<Item> itemList;

        private CartAdapter(List<Item> itemList) {
            this.itemList = itemList;
        }

        @Override
        public CartAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cart, parent, false);
            final CartAdapter.ViewHolder holder = new CartAdapter.ViewHolder(view);
            /*holder.recommendProjectPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });*/
            return holder;
        }

        @Override
        public void onBindViewHolder(final CartAdapter.ViewHolder holder, int position) {
            Item item = itemList.get(position);
            holder.itemName.setText(item.name);

        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView itemName;
            private TextView itemCount;
            private TextView itemPrice;
            private TextView itemKind;
            private SimpleDraweeView itemPic;
            private ImageView chose;

            private ViewHolder(View view) {
                super(view);
                itemName = view.findViewById(R.id.commodity_name);
                itemCount = view.findViewById(R.id.commodity_count);
                itemPrice = view.findViewById(R.id.commodity_price);
                itemKind = view.findViewById(R.id.commodity_kind);
                itemPic = view.findViewById(R.id.commodity_pic);
                chose = view.findViewById(R.id.chose);
            }

        }


    }


}
