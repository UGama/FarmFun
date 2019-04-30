package com.gama.farm_fun;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Payment extends AppCompatActivity implements View.OnClickListener {

    private int price;

    private TextView priceText;

    private View weChatPay;
    private ImageView weChatIcon;
    private TextView weChatPayText;
    private ImageView choseWeChat;

    private View aliPay;
    private ImageView aliPayIcon;
    private TextView aliPayText;
    private ImageView choseAliPay;

    private Button pay;

    private String way;

    private Toast toast;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Intent intent = getIntent();
        price = intent.getIntExtra("price", 0);
        Log.i("price", String.valueOf(price));
        initUI();
    }
    public void initUI() {
        way = null;
        priceText = findViewById(R.id.price);
        priceText.setText(String.valueOf(price));

        weChatPay = findViewById(R.id.weChatPay);
        weChatPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choseWeChat.setImageResource(R.drawable.chose_pay_way);
                choseAliPay.setImageResource(R.drawable.shape_circle3);
                way = "微信支付";
            }
        });
        weChatIcon = weChatPay.findViewById(R.id.common_icon);
        weChatIcon.setImageResource(R.drawable.wechat);
        weChatPayText = weChatPay.findViewById(R.id.common_name);
        weChatPayText.setText("微信支付");
        choseWeChat = weChatPay.findViewById(R.id.more);
        choseWeChat.setImageResource(R.drawable.shape_circle3);

        aliPay = findViewById(R.id.aliPay);
        aliPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choseAliPay.setImageResource(R.drawable.chose_pay_way);
                choseWeChat.setImageResource(R.drawable.shape_circle3);
                way = "支付宝支付";
            }
        });
        aliPayIcon = aliPay.findViewById(R.id.common_icon);
        aliPayIcon.setImageResource(R.drawable.alipay);
        aliPayText = aliPay.findViewById(R.id.common_name);
        aliPayText.setText("支付宝支付");
        choseAliPay = aliPay.findViewById(R.id.more);
        choseAliPay.setImageResource(R.drawable.shape_circle3);

        pay = findViewById(R.id.pay);
        pay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay:
                if (way == null) {
                    showToast("请选择支付方式。");
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("way", way);
                    setResult(RESULT_OK, intent);
                    finish();
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
