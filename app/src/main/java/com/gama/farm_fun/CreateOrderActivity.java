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
import com.avos.avoscloud.SaveCallback;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class CreateOrderActivity extends AppCompatActivity implements View.OnClickListener {

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

    private ConstraintLayout countChose;
    private ImageView plus;
    private ImageView minus;
    private TextView numberTextView;
    private int number;

    private Toast toast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_createorder);

        getOrderInformation();
    }
    public void getOrderInformation() {
        Intent orderIntent = getIntent();
        userId = orderIntent.getStringExtra("UserId");
        orderId = orderIntent.getStringExtra("OrderId");
        Log.i("OrderId", orderId);
        type = orderIntent.getStringExtra("Type");
        Log.i("type", type);
        project = orderIntent.getStringExtra("Project");
        itemName = orderIntent.getStringExtra("Item");
        url = orderIntent.getStringExtra("Url");
        detail = orderIntent.getStringExtra("Detail");
        count = orderIntent.getIntExtra("Count", 0);
        price = orderIntent.getIntExtra("Price", 0);

        orderPrice = price * count;
        initUI();
    }

    public void initUI() {
        topBar = findViewById(R.id.topBar);
        title = topBar.findViewById(R.id.title);
        if (type.equals("homeStay")) {
            title.setText("民宿订单");
        } else if (type.equals("restaurant")) {
            title.setText("用餐订单");
        }

        projectName = findViewById(R.id.project_name);
        projectName.setText(project);
        orderRecyclerView = findViewById(R.id.orderRecyclerView);
        itemList = new ArrayList<>();
        totalPrice = findViewById(R.id.total_price);
        totalPrice.setText(String.valueOf(orderPrice));
        remarkPanel = findViewById(R.id.panel_remark);
        remarkPanel.setOnClickListener(this);
        remark = remarkPanel.findViewById(R.id.remark);

        submitPanel = findViewById(R.id.submit_order);
        totalPrice2 = submitPanel.findViewById(R.id.price);
        totalPrice2.setText(String.valueOf(orderPrice));
        submit = submitPanel.findViewById(R.id.submit);
        submit.setOnClickListener(this);

        if (!type.equals("homeStay")) {
            countChose = findViewById(R.id.countChose);
            countChose.setVisibility(View.VISIBLE);
            plus = countChose.findViewById(R.id.plus);
            plus.setOnClickListener(this);
            minus = countChose.findViewById(R.id.minus);
            minus.setOnClickListener(this);
            numberTextView = countChose.findViewById(R.id.count);
            numberTextView.setText("1");
            number = Integer.parseInt(numberTextView.getText().toString());
        }
        initOrderRecyclerView();
    }

    public void initOrderRecyclerView() {
        Item item = new Item(itemName, url, detail, count, price);
        itemList.add(item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        orderRecyclerView.setLayoutManager(linearLayoutManager);
        ItemAdapter itemAdapter = new ItemAdapter(itemList);
        orderRecyclerView.setAdapter(itemAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.panel_remark:
                Intent remarkIntent = new Intent(CreateOrderActivity.this, EditRemarkActivity.class);
                startActivityForResult(remarkIntent, 1);
                break;
            case R.id.submit:
                AVObject avObject = AVObject.createWithoutData("Order", orderId);
                avObject.put("status", "已支付");
                if (!type.equals("homeStay")) {
                    avObject.put("count", number);
                    avObject.put("price", price * number);
                }
                avObject.put("comment", true);
                avObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            Log.i("Save", "Succeed");
                            showToast("支付成功！");
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                });
                break;
            case R.id.plus:
                number++;
                numberTextView.setText(String.valueOf(number));
                totalPrice.setText(String.valueOf(price * number));
                totalPrice2.setText(String.valueOf(price * number));
                if (number == 2) {
                    minus.setBackground(getResources().getDrawable(R.drawable.shape_circle));
                }
                break;
            case R.id.minus:
                if (number > 1) {
                    number--;
                    totalPrice.setText(String.valueOf(price * number));
                    totalPrice2.setText(String.valueOf(price * number));
                    numberTextView.setText(String.valueOf(number));
                    if (number == 1) {
                        minus.setBackground(getResources().getDrawable(R.drawable.shape_circle2));
                    }
                }
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
            if (type.equals("homeStay")) {
                holder.itemCount.setText("共" + String.valueOf(item.count) + "晚");
            } else {
                holder.itemCount.setText("x" + String.valueOf(item.count));
            }
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
