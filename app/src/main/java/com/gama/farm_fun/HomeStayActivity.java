package com.gama.farm_fun;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeStayActivity extends AppCompatActivity {

    private ObservableScrollView observableScrollView;

    private ImageView homeStayPic;
    private String mainPicName;
    private TextView homeStayName;
    private TextView describe;
    private TextView address;

    private TextView reserve;
    private View homeStayPanel;
    private ConstraintLayout panelTopLayout;
    private TextView panelChosenDate;
    private RecyclerView roomRecyclerView;
    private List<Room> roomList;
    private int[][] roomSupport;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_homestay);


        initUI();
    }

    public void initUI() {
        observableScrollView = findViewById(R.id.observableScrollView);

        homeStayPic = findViewById(R.id.mainPic);
        homeStayName = findViewById(R.id.homeStayName);
        describe = findViewById(R.id.describe);
        address = findViewById(R.id.address);
        reserve = findViewById(R.id.reserve);
        reserve.setText("房间预订");
        homeStayPanel = findViewById(R.id.panel_homeStay);
        panelTopLayout = homeStayPanel.findViewById(R.id.topLayout);
        panelChosenDate = homeStayPanel.findViewById(R.id.chosenDate);
        roomRecyclerView = findViewById(R.id.roomRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        roomRecyclerView.setLayoutManager(linearLayoutManager);
        roomList = new ArrayList<>();

        getHomeStayInformation();
    }

    public void getHomeStayInformation() {
        AVQuery<AVObject> query = new AVQuery<>("HomeStay");
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                homeStayName.setText(object.getString("name"));
                describe.setText(object.getString("describe"));
                address.setText(object.getString("locateDescribe"));
                mainPicName = object.getString("mainPicName");

                loadMainPic();
            }
        });
    }
    public void loadMainPic() {
        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereEqualTo("name", mainPicName);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                AVFile avFile = new AVFile("HomeStay.png", object.getString("url"), new HashMap<String, Object>());
                avFile.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, AVException e) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Log.i("bitmap(width/height)", String.valueOf(bitmap.getWidth()) + "/" + String.valueOf(bitmap.getHeight()));
                        homeStayPic.setImageBitmap(bitmap);

                    }
                }, new ProgressCallback() {
                    @Override
                    public void done(Integer percentDone) {
                        if (percentDone == 100) {
                            initRoomInformation();
                        }
                    }
                });
            }
        });
    }
    public void initRoomInformation() {
        AVQuery<AVObject> query = new AVQuery<>("Room");
        query.whereEqualTo("date", "2019/06/01");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                int i = 0;
                roomSupport = new int[avObjects.size()][2];
                for (AVObject avObject : avObjects) {
                    Room room = new Room(avObject.getString("roomType"),
                            avObject.getString("describe"),
                            avObject.getInt("price"),
                            avObject.getInt("remain"),
                            avObject.getString("roomPicName"));
                    roomList.add(room);
                    roomSupport[i][0] = i;
                    roomSupport[i][1] = avObject.getInt("price");
                    Log.i("辅助数组", String.valueOf(roomSupport[i][0])
                            + "/" + String.valueOf(roomSupport[i][1]));
                    i++;
                }
                roomList = sortRoom(roomList, roomSupport);

                RoomAdapter roomAdapter = new RoomAdapter(roomList);
                roomRecyclerView.setAdapter(roomAdapter);
            }
        });
    }

    private class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
        private List<Room> roomsList;

        private RoomAdapter(List<Room> roomsList) {
            this.roomsList = roomsList;
        }

        @Override
        public RoomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_room, parent, false);
            final RoomAdapter.ViewHolder holder = new RoomAdapter.ViewHolder(view);
            /*holder.recommendProjectPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });*/
            return holder;
        }

        @Override
        public void onBindViewHolder(RoomAdapter.ViewHolder holder, int position) {
            Room room = roomsList.get(position);
            Log.i(String.valueOf(position), room.roomType);
            holder.roomType.setText(room.roomType);
            holder.roomDescribe.setText(room.roomDescribe);
            holder.roomRemain.setText(String.valueOf(room.remain));
            holder.price.setText(String.valueOf(room.price));
            holder.storePrice.setText(String.valueOf(room.price + 20));
            final SimpleDraweeView roomPic = holder.roomPic;
            AVQuery<AVObject> query = new AVQuery<>("_File");
            query.whereEqualTo("name", room.roomPicName);
            query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject object, AVException e) {
//                    AVFile avFile = new AVFile("seat.png", object.getString("url"), new HashMap<String, Object>());
//                    avFile.getDataInBackground(new GetDataCallback() {
//                        @Override
//                        public void done(byte[] data, AVException e) {
//                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                            roomPic.setImageBitmap(bitmap);
//                        }
//                    }, new ProgressCallback() {
//                        @Override
//                        public void done(Integer percentDone) {
//                            if (percentDone == 100) {
//
//                            }
//                        }
//                    });
                    Uri imageUri = Uri.parse(object.getString("url"));
                    roomPic.setImageURI(imageUri);
                    RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);
                    roomPic.getHierarchy().setRoundingParams(roundingParams);
                }
            });

        }

        @Override
        public int getItemCount() {
            return roomsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView roomType;
            private TextView roomDescribe;
            private TextView roomRemain;
            private TextView price;
            private TextView storePrice;
            private SimpleDraweeView roomPic;

            private ViewHolder(View view) {
                super(view);
                roomType = view.findViewById(R.id.roomType);
                roomDescribe = view.findViewById(R.id.describe);
                roomRemain = view.findViewById(R.id.roomRemain);
                price = view.findViewById(R.id.price);
                storePrice = view.findViewById(R.id.physicalStorePrice);
                roomPic = view.findViewById(R.id.roomPic);
            }
        }
    }

    public List<Room> sortRoom(List<Room> roomList,int[][] roomSupport) {
        List<Room> rooms = new ArrayList<>();
        int supportPrice[] = new int[2];
        int minPrice = 9999;
        int count = 0;
        for (int i = 0; i < roomList.size(); i++) {
            for (int j = i; j < roomList.size(); j++) {
                if (roomSupport[j][1] < minPrice) {
                    minPrice = roomSupport[j][1];
                    count = j;
                }
            }
            supportPrice[0] = roomSupport[count][0];
            supportPrice[1] = roomSupport[count][1];
            roomSupport[count][0] = roomSupport[i][0];
            roomSupport[count][1] = roomSupport[i][1];
            roomSupport[i][0] = supportPrice[0];
            roomSupport[i][1] = supportPrice[1];
            minPrice = 9999;
            count = 0;
        }
        for (int i = 0; i < roomList.size(); i++) {
            rooms.add(roomList.get(roomSupport[i][0]));
            Log.i("排序后辅助数组", String.valueOf(roomSupport[i][0])
                    + "/" + String.valueOf(roomSupport[i][1]));
        }
        return rooms;
    }
}
