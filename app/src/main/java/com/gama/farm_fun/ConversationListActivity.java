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
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class ConversationListActivity extends AppCompatActivity implements View.OnClickListener {
    private String userId;
    private boolean customerService;

    private View topBar;
    private Button back;
    private TextView title;

    private RecyclerView conversationRecyclerView;
    private List<Conversation> conversationList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_conversationlist);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        customerService = intent.getBooleanExtra("customer_service", false);
        if (customerService) {
            Intent intent1 = new Intent(ConversationListActivity.this, ConversationActivity.class);
            intent1.putExtra("userId", userId);
            intent1.putExtra("receiverId", "5cc68dada91c93006c16c221");
            intent1.putExtra("receiverName", "专属客服");
            startActivity(intent1);
        }

        topBar = findViewById(R.id.bar_top);
        back = topBar.findViewById(R.id.back);
        back.setOnClickListener(this);
        title = topBar.findViewById(R.id.title);
        title.setText("消息");

        conversationRecyclerView = findViewById(R.id.conversationRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        conversationRecyclerView.setLayoutManager(linearLayoutManager);
        conversationList = new ArrayList<>();

        Conversation conversation = new Conversation("kefu", "客服助手", "");
        conversationList.add(conversation);
        getFriendList();
    }

    public void getFriendList() {
        AVQuery<AVObject> query = new AVQuery<>("Friend");
        query.whereEqualTo("adderId", userId);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                for (AVObject object : avObjects) {
                    Conversation conversation = new Conversation(object.getString("receiverId"),
                            object.getString("receiverName"), "");
                    conversationList.add(conversation);
                }
                initConversationRecyclerView();
            }
        });
    }

    public void initConversationRecyclerView() {
        ConversationAdapter conversationAdapter = new ConversationAdapter(conversationList);
        conversationRecyclerView.setAdapter(conversationAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    private class ConversationAdapter extends RecyclerView.Adapter<ConversationListActivity.ConversationAdapter.ViewHolder> {
        private List<Conversation> conversationList;

        private ConversationAdapter(List<Conversation> conversationList) {
            this.conversationList = conversationList;
        }

        @Override
        public ConversationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_conversation, parent, false);
            ConversationAdapter.ViewHolder holder = new ConversationAdapter.ViewHolder(view);
            /*holder.recommendProjectPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });*/
            return holder;
        }

        @Override
        public void onBindViewHolder(final ConversationAdapter.ViewHolder holder, int position) {
            Conversation conversation = conversationList.get(position);

            holder.setFriendId(conversation.friendId);

            holder.friendName.setText(conversation.friendName);
            if (conversation.friendId.equals("kefu")) {
                holder.headPic.setBackground(getResources().getDrawable(R.drawable.customer_service));
            }
            holder.conversationView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ConversationListActivity.this, ConversationActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("receiverId", holder.friendId);
                    intent.putExtra("receiverName", holder.friendName.getText().toString());
                    startActivity(intent);
                }
            });


        }

        @Override
        public int getItemCount() {
            return conversationList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView friendName;
            private SimpleDraweeView headPic;
            private View conversationView;
            private String friendId;

            private ViewHolder(View view) {
                super(view);
                friendName = view.findViewById(R.id.netName);
                headPic = view.findViewById(R.id.headPic);
                conversationView = view.findViewById(R.id.conversation);
            }

            public void setFriendId(String friendId) {
                this.friendId = friendId;
            }
        }


    }
}
