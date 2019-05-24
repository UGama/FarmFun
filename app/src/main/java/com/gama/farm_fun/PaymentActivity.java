package com.gama.farm_fun;

import android.content.Context;
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

import com.payelves.sdk.EPay;
import com.payelves.sdk.enums.EPayResult;
import com.payelves.sdk.listener.PayResultListener;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener {
    private View topBar;
    private TextView title;
    private Button back;

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

    public void initEPay() {
        EPay.getInstance(getApplicationContext()).init("C1TH0PvSH",
                "e6e8af2747ab460796077347e1b9fcc6",
                "8303661592018953",
                "baidu");
        //TrPay.getInstance(PaymentActivity.this).initPaySdk("8a52c50e25b2469dae9302cfa1d4b061","baidu");
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

        topBar = findViewById(R.id.bar_top);
        title = topBar.findViewById(R.id.title);
        title.setText("选择支付方式");
        back = topBar.findViewById(R.id.back);
        back.setOnClickListener(this);

        initEPay();
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
                    EPay();
                    //trPay();
                }
                break;
            case R.id.back:
                finish();
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

    public void trPay() {
        /*TrPay.getInstance(this).callAlipay("农家乐预定", "adminOrder", Long.valueOf("10"), null, null, "xiaonong101", new PayResultListener() {
            @Override
            public void onPayFinish(Context context, String s, int i, String s1, int i1, Long aLong, String s2) {
                if (i1 == TrPayResult.RESULT_CODE_SUCC.getId()) {
                    //支付成功逻辑处理
                    showToast("图灵支付：成功");
                } else if (i1 == TrPayResult.RESULT_CODE_FAIL.getId()) {
                    //支付失败逻辑处理
                    showToast("图灵支付：失败");
                }
            }
        });*/

    }
    public void EPay() {
        EPay.getInstance(this).pay("农家乐预定", "竹筏漂流（成人票）", 11100, "adminOrder","admin"
                            , null, new PayResultListener() {
                                @Override
                                public void onFinish(Context context, Long payId, String orderId, String payUserId, EPayResult payResult, int payType, Integer amount) {
                                    EPay.getInstance(context).closePayView();//关闭快捷支付页面
                                    if(payResult.getCode() == EPayResult.SUCCESS_CODE.getCode()){
                                        //支付成功逻辑处理
                                        Toast.makeText(PaymentActivity.this, payResult.getMsg(), Toast.LENGTH_LONG).show();
                                    }else if(payResult.getCode() == EPayResult.FAIL_CODE.getCode()){
                                        //支付失败逻辑处理
                                        Toast.makeText(PaymentActivity.this, payResult.getMsg(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
    }

}
