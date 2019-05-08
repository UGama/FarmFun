package com.gama.farm_fun;

import android.content.Intent;
import android.os.Bundle;
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
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends AppCompatActivity implements View.OnClickListener {
    private String userId;
    private String type;

    private View topBar;
    private TextView title;
    private Button back;

    private RecyclerView addressRecyclerView;
    private List<Address> addressList;

    private Button add;

    private TextView emptyAddress;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_address);

        Intent intent = getIntent();
        userId = intent.getStringExtra("UserId");
        type = intent.getStringExtra("Type");

        topBar = findViewById(R.id.bar_top);
        title = topBar.findViewById(R.id.title);
        title.setText("我的地址");
        back = topBar.findViewById(R.id.back);
        back.setOnClickListener(this);

        add = findViewById(R.id.add_address);
        add.setOnClickListener(this);

        emptyAddress = findViewById(R.id.address_empty);

        getAddressInformation();
    }

    public void getAddressInformation() {
        addressList = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("Address");
        query.whereEqualTo("userId", userId);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                if (avObjects.size() == 0) {
                    emptyAddress.setVisibility(View.VISIBLE);
                } else {
                    emptyAddress.setVisibility(View.INVISIBLE);
                    for (AVObject avObject : avObjects) {
                        Address address = new Address(avObject.getString("address"),
                                avObject.getString("phone"),
                                avObject.getString("name"));
                        addressList.add(address);
                    }
                    initAddressRecyclerView();
                }
            }
        });
    }

    public void initAddressRecyclerView() {
        addressRecyclerView = findViewById(R.id.addressRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        addressRecyclerView.setLayoutManager(linearLayoutManager);
        AddressAdapter addressAdapter = new AddressAdapter(addressList);
        addressRecyclerView.setAdapter(addressAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.add_address:
                Intent intent = new Intent(AddressActivity.this, AddAddressActivity.class);
                intent.putExtra("UserId", userId);
                startActivityForResult(intent, 0);
                break;
        }
    }
    
    private class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
        private List<Address> addressList;

        private AddressAdapter(List<Address> addressList) {
            this.addressList = addressList;
        }

        @Override
        public AddressAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_address, parent, false);
            final AddressAdapter.ViewHolder holder = new AddressAdapter.ViewHolder(view);
            /*holder.recommendProjectPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });*/
            return holder;
        }

        @Override
        public void onBindViewHolder(final AddressAdapter.ViewHolder holder, int position) {
            Address address = addressList.get(position);

            holder.name.setText(address.name);
            holder.address.setText(address.address);
            holder.phone.setText(address.phone);

            if (type.equals("chose")) {
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("name", holder.name.getText().toString());
                        intent.putExtra("phone", holder.phone.getText().toString());
                        intent.putExtra("address", holder.address.getText().toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return addressList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView name;
            private TextView address;
            private TextView phone;
            private View view;

            private ViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.name);
                address = view.findViewById(R.id.address);
                phone = view.findViewById(R.id.phone);
                this.view = view;
            }

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    getAddressInformation();
                }
                break;
        }
    }
}
