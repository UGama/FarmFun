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
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConversationActivity extends AppCompatActivity implements View.OnClickListener {
    private String userId;
    private String friendId;
    private String friendName;

    private View topBar;
    private Button back;
    private TextView title;

    private EditText input;
    private String inputString;
    private Button send;

    private RecyclerView messageRecyclerView;
    private List<Message> messageList;

    private ObservableScrollView observableScrollView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_conversation);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        friendId = intent.getStringExtra("receiverId");
        friendName = intent.getStringExtra("receiverName");

        AVOSCloud.initialize(this,"kdUtqr57OFKrSuNO5VBChQn3-gzGzoHsz","j5dBUyNfKLg36UM1AR3YXmJ7");
        AVIMMessageManager.registerDefaultMessageHandler(new CustomMessageHandler());

        openMessage();

        initUI();
    }

    public void initUI() {
        topBar = findViewById(R.id.bar_top);
        back = topBar.findViewById(R.id.back);
        back.setOnClickListener(this);
        title = topBar.findViewById(R.id.title);
        title.setText(friendName);

        input = findViewById(R.id.inputText);
        send = findViewById(R.id.send);
        send.setOnClickListener(this);

        observableScrollView = findViewById(R.id.observableScrollView);

        messageRecyclerView = findViewById(R.id.messageRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        messageRecyclerView.setLayoutManager(linearLayoutManager);
        messageList = new ArrayList<>();

        initMessageRecyclerView();
    }

    public void initMessageRecyclerView() {
        MessageAdapter messageAdapter = new MessageAdapter(messageList);
        messageRecyclerView.setAdapter(messageAdapter);
        messageRecyclerView.smoothScrollToPosition(messageList.size());
        observableScrollView.smoothScrollTo(0, messageRecyclerView.getBottom());
    }

    public void sendMessage() {
        if (input.getText().toString().equals("")) {

        } else {
            inputString = input.getText().toString();
            input.setText("");
            AVIMClient avimClient = AVIMClient.getInstance(userId);
            avimClient.open(new AVIMClientCallback() {
                @Override
                public void done(AVIMClient client, AVIMException e) {
                    client.createConversation(Arrays.asList(friendId), "Conversation", null,
                            new AVIMConversationCreatedCallback() {
                                @Override
                                public void done(AVIMConversation conversation, AVIMException e) {
                                    if (e == null) {
                                        AVIMTextMessage avimTextMessage = new AVIMTextMessage();
                                        avimTextMessage.setText(inputString);
                                        conversation.sendMessage(avimTextMessage, new AVIMConversationCallback() {
                                            @Override
                                            public void done(AVIMException e) {
                                                if (e == null) {
                                                    Log.d("send", "发送成功！");
                                                    Message message = new Message("me", inputString);
                                                    messageList.add(message);
                                                    initMessageRecyclerView();
                                                }
                                            }
                                        });
                                    }
                                }
                            });

                }
            });
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                sendMessage();
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    private class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
        private List<Message> messageList;

        private MessageAdapter(List<Message> messageList) {
            this.messageList = messageList;
        }

        @NonNull
        @Override
        public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message, parent, false);
            MessageAdapter.ViewHolder holder = new MessageAdapter.ViewHolder(view);
            /*holder.recommendProjectPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });*/
            return holder;
        }

        @Override
        public void onBindViewHolder(final MessageAdapter.ViewHolder holder, int position) {
            Message message = messageList.get(position);

            if (message.sender.equals("friend")) {
                holder.friendView.setVisibility(View.VISIBLE);
                holder.friendText.setText(message.content);
            } else {
                holder.myView.setVisibility(View.VISIBLE);
                holder.myText.setText(message.content);
            }
        }

        @Override
        public int getItemCount() {
            return messageList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private View friendView;
            private View myView;
            private SimpleDraweeView friendPic;
            private TextView friendText;
            private TextView myText;
            private SimpleDraweeView myPic;

            private ViewHolder(View view) {
                super(view);
                friendView = view.findViewById(R.id.friend);
                myView = view.findViewById(R.id.me);
                friendText = view.findViewById(R.id.friendText);
                myText = view.findViewById(R.id.myText);
                friendPic = view.findViewById(R.id.friendPic);
                myPic = view.findViewById(R.id.myPic);
            }

        }

    }

    public class CustomMessageHandler extends AVIMMessageHandler {
        @Override
        public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
            super.onMessage(message, conversation, client);
            if (message instanceof AVIMTextMessage) {
                Log.d("test", ((AVIMTextMessage) message).getText());
                Message getMessage = new Message("friend", ((AVIMTextMessage) message).getText());
                messageList.add(getMessage);
                initMessageRecyclerView();
            }
        }

        @Override
        public void onMessageReceipt(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
            super.onMessageReceipt(message, conversation, client);
        }
    }

    public void openMessage(){
        AVIMClient jerry = AVIMClient.getInstance(userId);
        jerry.open(new AVIMClientCallback(){
            @Override
            public void done(AVIMClient client, AVIMException e){
                if (e == null) {
                    //登录成功后的逻辑
                    Log.i("test", "succeed");
                }
            }
        });
    }

}
