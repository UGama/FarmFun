package com.gama.farm_fun;

import android.content.Intent;
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
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class MyCOrderActivity extends AppCompatActivity implements View.OnClickListener {
    public String userId;

    private View topBar;
    private Button back;
    private TextView title;

    private TextView noMoreOrder;

    private RecyclerView cOrderRecyclerView;
    private List<COrder> cOrderList;
    private int support;
    private int support2;
    private String[] urlSupport;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_my_c_order);

        Intent intent = getIntent();
        userId = intent.getStringExtra("UserId");

        initUI();
    }

    public void initUI() {
        cOrderRecyclerView = findViewById(R.id.cOrderRecyclerView);
        topBar = findViewById(R.id.bar_top);
        back = topBar.findViewById(R.id.back);
        back.setOnClickListener(this);
        title = topBar.findViewById(R.id.title);
        title.setText("商品订单");

        noMoreOrder = findViewById(R.id.no_more_order);

        getOrderInformation();
    }

    public void getOrderInformation() {
        noMoreOrder.setVisibility(View.INVISIBLE);
        AVQuery<AVObject> query = new AVQuery<>("Order");
        query.whereGreaterThan("counts", "");
        query.orderByDescending("createdAt");
        cOrderList = new ArrayList<>();
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                Log.i("list", String.valueOf(avObjects.size()));
                if (avObjects.size() == 0) {
                    noMoreOrder.setVisibility(View.VISIBLE);
                } else {
                    for (AVObject avObject : avObjects) {
                        //checkStringArray(toStringArray(avObject.getString("project")));
                        //checkIntArray(toIntArray(avObject.getString("counts")));
                        COrder cOrder = new COrder(avObject.getObjectId(),
                                toStringArray(avObject.getString("item")),
                                toStringArray(avObject.getString("project")),
                                toStringArray(avObject.getString("codes")),
                                toIntArray(avObject.getString("counts")),
                                avObject.getInt("price"),
                                avObject.getString("address"),
                                avObject.getString("name"),
                                avObject.getString("phone"),
                                avObject.getBoolean("comment"),
                                avObject.getString("status"),
                                transToPicName(toStringArray(avObject.getString("codes"))));
                        checkStringArray(transToPicName(toStringArray(avObject.getString("codes"))));
                        cOrder.setType(avObject.getString("type"));
                        cOrder.setCodesString(avObject.getString("codes"));
                        cOrder.setNameString(avObject.getString("project"));
                        cOrderList.add(cOrder);
                        Log.i("comment0", String.valueOf(avObject.getBoolean("comment")));
                        Log.i("comment1", String.valueOf(cOrder.comment));
                    }

                    support = 0;
                    support2 = 0;
                    urlSupport = new String[cOrderList.get(0).picName.length];
                    getCOrderUrl();
                }
            }
        });
    }

    public void getCOrderUrl() {
        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereEqualTo("name", cOrderList.get(support).picName[support2]);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                urlSupport[support2] = object.getString("url");
                support2++;
                if (support2 == cOrderList.get(support).picName.length) {
                    support2 = 0;
                    cOrderList.get(support).setUrls(urlSupport);
                    support++;
                    if (support == cOrderList.size()) {
                        Log.i("getUrl", "succeed");
                        initCOrderRecyclerView();
                    } else {
                        urlSupport = new String[cOrderList.get(support).picName.length];
                        getCOrderUrl();
                    }
                } else {
                    getCOrderUrl();
                }
            }
        });
    }

    public void initCOrderRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        cOrderRecyclerView.setLayoutManager(linearLayoutManager);
        COrderAdapter cOrderAdapter = new COrderAdapter(cOrderList);
        cOrderRecyclerView.setAdapter(cOrderAdapter);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }


    private class COrderAdapter extends RecyclerView.Adapter<COrderAdapter.ViewHolder> {
        private List<COrder> cOrderList;

        private COrderAdapter(List<COrder> cOrderList) {
            this.cOrderList = cOrderList;
        }

        @Override
        public COrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_c_order, parent, false);
            final COrderAdapter.ViewHolder holder = new COrderAdapter.ViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(final COrderAdapter.ViewHolder holder, int position) {
            final COrder cOrder = cOrderList.get(position);
            holder.price.setText(String.valueOf(cOrder.price));
            holder.status.setText(cOrder.status);

            holder.setId(cOrder.id);

            if (cOrder.comment) {
                holder.comment.setVisibility(View.VISIBLE);
            } else {
                holder.comment.setVisibility(View.INVISIBLE);
            }
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
            holder.itemsRecyclerView.setLayoutManager(linearLayoutManager);
            COrderItemAdapter cOrderItemAdapter = new COrderItemAdapter(transToItems(cOrder));
            holder.itemsRecyclerView.setAdapter(cOrderItemAdapter);

            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyCOrderActivity.this, EditCommentActivity.class);
                    intent.putExtra("project", "线上商城");
                    intent.putExtra("url", "");
                    intent.putExtra("item", cOrder.nameString);
                    intent.putExtra("detail", "");
                    intent.putExtra("count", "等" + String.valueOf(cOrder.names.length) + "件商品");
                    intent.putExtra("orderId", holder.id);
                    intent.putExtra("type", "manyC");
                    intent.putExtra("userId", userId);
                    startActivityForResult(intent, 0);
                }
            });
        }

        @Override
        public int getItemCount() {
            return cOrderList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView status;
            private TextView price;
            private Button detail;
            private Button comment;
            private RecyclerView itemsRecyclerView;
            private String id;

            private ViewHolder(View view) {
                super(view);
                status = view.findViewById(R.id.status);
                price = view.findViewById(R.id.price);
                detail = view.findViewById(R.id.orderDetail);
                comment = view.findViewById(R.id.comment);
                itemsRecyclerView = view.findViewById(R.id.itemsRecyclerView);
            }

            public void setId(String id) {
                this.id = id;
            }
        }


    }

    private class COrderItemAdapter extends RecyclerView.Adapter<COrderItemAdapter.ViewHolder> {
        private List<COrderItem> cOrderItemList;

        private COrderItemAdapter(List<COrderItem> cOrderItemList) {
            this.cOrderItemList = cOrderItemList;
        }


        @NonNull
        @Override
        public COrderItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_item3, parent, false);
            COrderItemAdapter.ViewHolder holder = new COrderItemAdapter.ViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final COrderItemAdapter.ViewHolder holder, int position) {
            COrderItem cOrderItem = cOrderItemList.get(position);
            holder.projectPic.setImageURI(cOrderItem.url);
            RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);
            holder.projectPic.getHierarchy().setRoundingParams(roundingParams);

            holder.name.setText(cOrderItem.name);
            holder.kind.setText(cOrderItem.kind);
            holder.count.setText("x" + String.valueOf(cOrderItem.count));
        }

        @Override
        public int getItemCount() {
            return cOrderItemList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private SimpleDraweeView projectPic;
            private TextView name;
            private TextView kind;
            private TextView count;

            private ViewHolder(View view) {
                super(view);
                projectPic = view.findViewById(R.id.pic);
                name = view.findViewById(R.id.item);
                kind = view.findViewById(R.id.kind);
                count = view.findViewById(R.id.count);
            }
        }
    }

    public String[] toStringArray(String string) {
        char[] chars = string.toCharArray();
        int count = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ';') {
                count++;
            }
        }
        String[] results = new String[count];
        int k = 0;
        int l = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ';') {
                for (int c = l; c < i; c++) {
                    if (c == l) {
                        results[k] = String.valueOf(chars[c]);
                    } else {
                        results[k] += String.valueOf(chars[c]);
                    }
                }
                l = i + 1;
                k++;
            }
        }
        return results;
    }

    public int[] toIntArray(String string) {
        char[] chars = string.toCharArray();
        int count = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ';') {
                count++;
            }
        }
        String[] results = new String[count];
        int k = 0;
        int l = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ';') {
                for (int c = l; c < i; c++) {
                    if (c == l) {
                        results[k] = String.valueOf(chars[c]);
                    } else {
                        results[k] += String.valueOf(chars[c]);
                    }
                }
                l = i + 1;
                k++;
            }
        }
        int[] results2 = new int[count];
        for (int i = 0; i < results.length; i++) {
            results2[i] = Integer.parseInt(results[i]);
        }
        return results2;
    }

    public void checkStringArray(String[] strings) {
        for (int i = 0; i < strings.length; i++) {
            Log.i("check", strings[i]);
        }
    }

    public void checkIntArray(int[] ints) {
        for (int i = 0; i < ints.length; i++) {
            Log.i("check", String.valueOf(ints[i]));
        }
    }

    public String[] transToPicName(String[] strings) {
        for (int i = 0; i < strings.length; i++) {
            strings[i] += "Kind1.jpg";
        }
        return strings;
    }

    public List<COrderItem> transToItems(COrder cOrder) {
        String[] names = cOrder.names;
        String[] kinds = cOrder.kinds;
        String[] picNames = cOrder.picName;
        int[] counts = cOrder.counts;
        String[] urls = cOrder.urls;
        List<COrderItem> cOrderItemList = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            COrderItem cOrderItem = new COrderItem(names[i],
                    kinds[i],
                    picNames[i],
                    counts[i]);
            cOrderItem.setUrl(urls[i]);
            cOrderItemList.add(cOrderItem);
        }
        return cOrderItemList;
    }

}
