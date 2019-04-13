package com.gama.farm_fun;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PickActivity extends AppCompatActivity {

    private ObservableScrollView observableScrollView;
    private ImageView mainPic;
    private TextView projectName;
    private TextView describe;
    private TextView address;
    private TextView ticket;
    private TextView allTicketType;
    private RecyclerView ticketRecyclerView;
    private List<Ticket> ticketList;

    private ImageView loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amusementproject2);

        getData();
        initUI();
    }
    public void getData() {
        Log.i("initData", "Start");
        AVQuery<AVObject> query = new AVQuery<>("Amusement");
        query.whereEqualTo("type", "pick");
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                String projectName = object.getString("name");
                Log.i("ProjectName", projectName);
            }
        });

    }

    public void initUI() {
        observableScrollView = findViewById(R.id.observableScrollView);
        mainPic = findViewById(R.id.mainPic);
        projectName = findViewById(R.id.projectName);
        describe = findViewById(R.id.describe);
        address = findViewById(R.id.address);
        ticket = findViewById(R.id.ticket);
        ticket.setText("门票预订");
        allTicketType = findViewById(R.id.allTicketType);
        ticketList = new ArrayList<>();
        ticketRecyclerView = findViewById(R.id.ticketRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        ticketRecyclerView.setLayoutManager(linearLayoutManager);

        loading = findViewById(R.id.loading);

        getProjectInformation();
    }
    public void getProjectInformation (){
        AVQuery<AVObject> projectQuery = new AVQuery<>("Amusement");
        projectQuery.whereEqualTo("type", "pick");
        projectQuery.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                projectName.setText(object.getString("name"));
                describe.setText(object.getString("describe"));
                address.setText(object.getString("locateDescribe"));
                loadMainPic();
                getTicket();
            }
        });
    };

    public void getTicket() {
        AVQuery<AVObject> ticketQuery = new AVQuery<>("Price");
        ticketQuery.whereEqualTo("name",projectName.getText().toString());
        ticketQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                allTicketType.setText("全部" + String.valueOf(avObjects.size()) + "种票型");
                if (avObjects.size() <= 3) {
                    for (int i = 0; i < avObjects.size(); i++) {
                        AVObject avObject = avObjects.get(i);
                        Ticket ticket = new Ticket(avObject.getString("name"), avObject.getString("ticketType"),
                                avObject.getInt("price"), avObject.getInt("sales"));
                        ticketList.add(ticket);
                        Log.i("票型：", ticket.ticketType);
                    }
                } else {
                    for (int i = 0; i < 3; i++) {
                        AVObject avObject = avObjects.get(i);
                        Ticket ticket = new Ticket(avObject.getString("name"), avObject.getString("ticketType"),
                                avObject.getInt("price"), avObject.getInt("sales"));
                        ticketList.add(ticket);
                    }
                }
                Log.i("TicketList.size", String.valueOf(ticketList.size()));
                ticketRecyclerView = findViewById(R.id.ticketRecyclerView);
                LinearLayoutManager linearLayout = new LinearLayoutManager(PickActivity.this);
                ticketRecyclerView.setLayoutManager(linearLayout);
                TicketAdapter ticketAdapter = new TicketAdapter(ticketList);
                ticketRecyclerView.setAdapter(ticketAdapter);
                loading.setVisibility(View.INVISIBLE);

            }
        });

    }

    public void loadMainPic(){
        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereEqualTo("name", "pickmain.jpg");
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                AVFile avFile = new AVFile("Pick.png", object.getString("url"), new HashMap<String, Object>());
                avFile.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, AVException e) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        mainPic.setImageBitmap(bitmap);
                    }
                }, new ProgressCallback() {
                    @Override
                    public void done(Integer percentDone) {
                        if (percentDone == 100) {

                        }
                    }
                });
            }
        });
    }

    private class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.ViewHolder> {
        private List<Ticket> ticketList;

        private TicketAdapter(List<Ticket> ticketList) {
            this.ticketList = ticketList;
        }

        @Override
        public TicketAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_ticket, parent, false);
            final TicketAdapter.ViewHolder holder = new TicketAdapter.ViewHolder(view);
            /*holder.recommendProjectPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });*/
            return holder;
        }

        @Override
        public void onBindViewHolder(TicketAdapter.ViewHolder holder, int position) {
            Ticket ticket = ticketList.get(position);
            Log.i(String.valueOf(position), ticket.ticketType);
            holder.ticketType.setText(ticket.ticketType);
            holder.sales.setText(String.valueOf(ticket.sales));
            holder.price.setText(String.valueOf(ticket.price));
        }

        @Override
        public int getItemCount() {
            return ticketList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView ticketType;
            private TextView price;
            private TextView sales;

            private ViewHolder(View view) {
                super(view);
                ticketType = view.findViewById(R.id.ticketType);
                price = view.findViewById(R.id.price);
                sales = view.findViewById(R.id.sales);
            }
        }


    }
}
