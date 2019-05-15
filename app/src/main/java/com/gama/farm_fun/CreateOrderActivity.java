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
    private String[] names;
    private String[] codes;
    private String[] kinds;
    private String[] urls;
    private int[] counts;
    private int[] prices;
    private String[] cartIds;

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

    private ConstraintLayout timeComePanel;

    private String way;
    private int cartSupport;

    private String name;
    private String phone;
    private String address;

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
        Log.i("userId", userId);
        orderId = orderIntent.getStringExtra("OrderId");
        Log.i("OrderId", orderId);
        type = orderIntent.getStringExtra("Type");
        if (type.equals("manyC")) {
            Log.i("type", type);
            names = orderIntent.getStringArrayExtra("Project");
            kinds = orderIntent.getStringArrayExtra("Item");
            urls = orderIntent.getStringArrayExtra("Url");
            counts = orderIntent.getIntArrayExtra("Count");
            prices = orderIntent.getIntArrayExtra("Price");
            codes = orderIntent.getStringArrayExtra("Code");
            cartIds = orderIntent.getStringArrayExtra("CartId");
            for (int i = 0; i < cartIds.length; i++) {
                Log.i("cartId1", cartIds[i]);
            }
            orderPrice = 0;
            for (int i = 0; i < names.length; i++) {
                orderPrice += prices[i] * counts[i];
            }
        } else {
            Log.i("type", type);
            project = orderIntent.getStringExtra("Project");
            itemName = orderIntent.getStringExtra("Item");
            url = orderIntent.getStringExtra("Url");
            detail = orderIntent.getStringExtra("Detail");
            count = orderIntent.getIntExtra("Count", 0);
            price = orderIntent.getIntExtra("Price", 0);

            orderPrice = price * count;
        }

        cartSupport = 0;
        initUI();
    }

    public void initUI() {
        topBar = findViewById(R.id.topBar);
        title = topBar.findViewById(R.id.title);
        if (type.equals("homeStay")) {
            title.setText("民宿订单");
        } else if (type.equals("restaurant")) {
            title.setText("用餐订单");
        } else if (type.length() == 2) {
            title.setText("商品订单");
        } else if (type.equals("manyC")) {
            title.setText("商品订单");
        } else {
            title.setText("娱乐订单");
        }
        back = topBar.findViewById(R.id.back);
        back.setOnClickListener(this);

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

        if (!type.equals("homeStay") & type.length() != 2 & !type.equals("manyC")) {
            countChose = findViewById(R.id.countChose);
            countChose.setVisibility(View.VISIBLE);
            plus = countChose.findViewById(R.id.plus);
            plus.setOnClickListener(this);
            minus = countChose.findViewById(R.id.minus);
            minus.setOnClickListener(this);
            numberTextView = countChose.findViewById(R.id.count);
            numberTextView.setText("1");
            number = Integer.parseInt(numberTextView.getText().toString());
            addressPanel = findViewById(R.id.addressPanel);
            addressPanel.setVisibility(View.INVISIBLE);
        }

        if (type.equals("homeStay")) {
            addressPanel = findViewById(R.id.addressPanel);
            addressPanel.setVisibility(View.INVISIBLE);
            timeComePanel = findViewById(R.id.time_come);
            timeComePanel.setVisibility(View.VISIBLE);
        }

        if (type.equals("manyC") || type.length() == 2) {
            addressPanel = findViewById(R.id.addressPanel);
            addressPanel.setVisibility(View.VISIBLE);
            addressPanel.setOnClickListener(this);
            addressText = findViewById(R.id.address);
            nameText = findViewById(R.id.name);
            phoneText = findViewById(R.id.phone);
        }

        if (type.equals("manyC")) {
            initOrderRecyclerView2();
        } else {
            initOrderRecyclerView();
        }

    }

    public void initOrderRecyclerView() {
        Item item = new Item(itemName, url, detail, count, price);
        itemList.add(item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        orderRecyclerView.setLayoutManager(linearLayoutManager);
        ItemAdapter itemAdapter = new ItemAdapter(itemList);
        orderRecyclerView.setAdapter(itemAdapter);
    }

    public void initOrderRecyclerView2() {
        for (int i = 0; i < names.length; i++) {
            Item item = new Item(names[i], urls[i], "", counts[i], prices[i]);
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
                Intent remarkIntent = new Intent(CreateOrderActivity.this, EditRemarkActivity.class);
                startActivityForResult(remarkIntent, 0);
                break;
            case R.id.submit:
                if (type.equals("manyC") || type.length() == 2) {
                    if (phoneText.getText().toString().equals("")) {
                        showToast("请先选择收货地址。");
                    } else {
                        Intent intent = new Intent(CreateOrderActivity.this, PaymentActivity.class);
                        intent.putExtra("price", orderPrice);
                        startActivityForResult(intent, 1);
                    }
                } else {
                    Intent intent = new Intent(CreateOrderActivity.this, PaymentActivity.class);
                    intent.putExtra("price", orderPrice);
                    startActivityForResult(intent, 1);
                }
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
            case R.id.back:
                finish();
                break;
            case R.id.addressPanel:
                Intent intent1 = new Intent(CreateOrderActivity.this, AddressActivity.class);
                intent1.putExtra("UserId", userId);
                intent1.putExtra("Type", "chose");
                startActivityForResult(intent1, 2);
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
            case 1:
                if (resultCode == RESULT_OK) {
                    way = data.getStringExtra("way");
                    AVObject avObject = AVObject.createWithoutData("Order", orderId);
                    avObject.put("status", "已支付");
                    if (!type.equals("homeStay") & !type.equals("manyC") & type.length() != 2) {
                        avObject.put("count", number);
                        avObject.put("price", price * number);
                    }
                    avObject.put("payment", way);
                    avObject.put("comment", true);
                    avObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                if (type.equals("manyC")) {
                                    updateCart();
                                } else if (type.length() == 2) {
                                    updateAddressInformation();
                                } else {
                                    Log.i("Save", "Succeed");
                                    showToast("支付成功！");
                                    Intent intent = new Intent();
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            }
                        }
                    });
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    name = data.getStringExtra("name");
                    phone = data.getStringExtra("phone");
                    address = data.getStringExtra("address");
                    nameText.setText(data.getStringExtra("name"));
                    phoneText.setText(data.getStringExtra("phone"));
                    addressText.setText(data.getStringExtra("address"));
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
                itemPic = view.findViewById(R.id.project_pic);
                itemName = view.findViewById(R.id.project_name);
                itemDetail = view.findViewById(R.id.item_detail);
                itemCount = view.findViewById(R.id.project_locate);
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

    public void updateCart() {
        AVObject avObject = AVObject.createWithoutData("Cart", cartIds[cartSupport]);
        avObject.put("exist", false);
        avObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    cartSupport++;
                    if (cartSupport == cartIds.length) {
                        updateAddressInformation();
                    } else {
                        updateCart();
                    }
                }
            }
        });
    }

    public void updateAddressInformation() {
        AVObject avObject = AVObject.createWithoutData("Order", orderId);
        avObject.put("address", address);
        avObject.put("name", name);
        avObject.put("phone", phone);
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
    }
}
