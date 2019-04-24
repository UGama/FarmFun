package com.gama.farm_fun;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ScrollView;
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

public class RestaurantActivity extends AppCompatActivity {

    private String userId;

    private ObservableScrollView observableScrollView;

    private ImageView restaurantPic;
    private TextView restaurantName;
    private TextView locationDescribe;
    private TextView distance;

    private View topPanel;
    private ConstraintLayout topPanelTopLayout;
    private TextView topPanelChosenDate;
    private View midPanel;
    private ConstraintLayout midPanelTopLayout;
    private TextView midPanelChosenDate;
    private ImageView barBottomLine;
    private ImageView panelBottomLine;

    private RecyclerView seatRecyclerView;
    private List<Seat> seatsList;
    private int[][] seatSupport;
    private String[] seatDescribe;

    private View restaurantCart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        Intent intent = getIntent();
        userId = intent.getStringExtra("UserId");
        initUI();

    }

    public void initUI() {
        observableScrollView = findViewById(R.id.observableScrollView);

        restaurantPic = findViewById(R.id.restaurantPic);
        restaurantName = findViewById(R.id.restaurantName);
        locationDescribe = findViewById(R.id.locationDescribe);
        topPanel = findViewById(R.id.topPanel);
        topPanelTopLayout = topPanel.findViewById(R.id.topLayout);
        topPanelChosenDate = topPanel.findViewById(R.id.chosenDate);
        topPanelChosenDate.setText("2019年06月01日");
        midPanel = findViewById(R.id.midPanel);
        midPanelTopLayout = midPanel.findViewById(R.id.topLayout);
        midPanelChosenDate = midPanel.findViewById(R.id.chosenDate);
        midPanelChosenDate.setText("2019年06月01日");
        barBottomLine = findViewById(R.id.barBottomLine);
        panelBottomLine = findViewById(R.id.panelBottomLine);

        seatRecyclerView = findViewById(R.id.seatRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        seatRecyclerView.setLayoutManager(linearLayoutManager);
        seatsList = new ArrayList<>();

        restaurantCart = findViewById(R.id.restaurantCart);
        restaurantCart.getBackground().setAlpha(0);

        setObservableScrollView();
    }

    public void setObservableScrollView() {
        observableScrollView.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy) {
                int[] panelLocation = new int[2];
                midPanel.getLocationOnScreen(panelLocation);
                int midPanelY = panelLocation[1];
                int statusBarHeight = getStatusBarHeight();
                if (midPanelY <= statusBarHeight + barBottomLine.getBottom()) {
                    topPanel.setVisibility(View.VISIBLE);
                    panelBottomLine.setVisibility(View.VISIBLE);
                } else {
                    topPanel.setVisibility(View.GONE);
                    panelBottomLine.setVisibility(View.GONE);
                }
            }
        });

        observableScrollView.smoothScrollTo(0, 20);
        getRestaurantInformation();
    }

    public void getRestaurantInformation() {
        AVQuery<AVObject> query = new AVQuery<>("Restaurant");
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                restaurantName.setText(object.getString("name"));
                locationDescribe.setText(object.getString("locateDescribe"));
                loadMainPic();
            }
        });
    }

    public void loadMainPic() {
        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereEqualTo("name", "restaurantmain.jpg");
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                AVFile avFile = new AVFile("Type.png", object.getString("url"), new HashMap<String, Object>());
                avFile.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, AVException e) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Log.i("bitmap(width/height)", String.valueOf(bitmap.getWidth()) + "/" + String.valueOf(bitmap.getHeight()));
                        restaurantPic.setImageBitmap(bitmap);

                    }
                }, new ProgressCallback() {
                    @Override
                    public void done(Integer percentDone) {
                        if (percentDone == 100) {
                            getSeatInformation();
                        }
                    }
                });
            }
        });
    }

    public void getSeatInformation() {
        AVQuery<AVObject> query = new AVQuery<>("Seat");
        query.whereEqualTo("date", "2019/06/01");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                int i = 0;
                seatSupport = new int[avObjects.size()][3];
                seatDescribe = new String[avObjects.size()];
                for (AVObject avObject : avObjects) {
                    seatSupport[i][0] = avObject.getInt("seatNumber");
                    seatSupport[i][2] = avObject.getInt("remain");
                    if (avObject.getString("meal").equals("lunch")) {
                        seatSupport[i][1] = 0;
                    } else {
                        seatSupport[i][1] = 1;
                    }
                    Log.i("seatNumber/meal/remain",
                            String.valueOf(seatSupport[i][0]) + "/" +
                                    String.valueOf(seatSupport[i][1]) + "/" + seatSupport[i][2]);
                    seatDescribe[i] = avObject.getString("describe");
                    i++;
                }
                seatsList = sortSeat(seatSupport, seatDescribe);

                SeatAdapter seatAdapter = new SeatAdapter(seatsList);
                seatRecyclerView.setAdapter(seatAdapter);
            }
        });
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public String NumberToDate(int year, int month, int day) {
        String date;
        date = String.valueOf(year) + "/" + String.valueOf(month) + "/" + String.valueOf(day);
        return date;
    }

    public int[] DateToNumber(String date) {
        int[] dateNumber = new int[3];
        char[] dateChar = date.toCharArray();
        int j = 0;
        for (int i = 0; i < dateChar.length; i++) {
            if (dateChar[i] != '/') {
                dateNumber[j] = dateNumber[j] * 10 + Integer.parseInt(String.valueOf(dateChar[i]));
            } else {
                j++;
            }
        }
        return dateNumber;
    }

    private class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.ViewHolder> {
        private List<Seat> seatsList;

        private SeatAdapter(List<Seat> seatsList) {
            this.seatsList = seatsList;
        }

        @Override
        public SeatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_seat, parent, false);
            final SeatAdapter.ViewHolder holder = new SeatAdapter.ViewHolder(view);
            /*holder.recommendProjectPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });*/
            return holder;
        }

        @Override
        public void onBindViewHolder(SeatAdapter.ViewHolder holder, int position) {
            Seat seat = seatsList.get(position);
            Log.i(String.valueOf(position), seat.seatDescribe);
            holder.seatDescribe.setText(seat.seatDescribe);
            holder.remain.setText(String.valueOf(seat.remain));
            holder.meal.setText(seat.meal);
            final ImageView seatPic = holder.seatPic;
            AVQuery<AVObject> query = new AVQuery<>("_File");
            query.whereEqualTo("name", seat.picName);
            query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject object, AVException e) {
                    AVFile avFile = new AVFile("seat.png", object.getString("url"), new HashMap<String, Object>());
                    avFile.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, AVException e) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            seatPic.setImageBitmap(bitmap);
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

        @Override
        public int getItemCount() {
            return seatsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView seatDescribe;
            private TextView remain;
            private TextView meal;
            private ImageView seatPic;

            private ViewHolder(View view) {
                super(view);
                seatDescribe = view.findViewById(R.id.seatDescribe);
                remain = view.findViewById(R.id.remainSeats);
                meal = view.findViewById(R.id.meal);
                seatPic = view.findViewById(R.id.seatPic);
            }
        }
    }

    public List<Seat> sortSeat(int[][] seatSupport, String[] seatDescribe) {
        final List<Seat> seatList = new ArrayList<>();
        int support[] = new int[3];
        String supportDescribe;
        int minSeat = 99;
        int count = 0;
        for (int i = 0; i < seatSupport.length; i++) {
            for (int j = i; j < seatSupport.length; j++) {
                if (seatSupport[j][0] < minSeat) {
                    minSeat = seatSupport[j][0];
                    count = j;
                } else if (seatSupport[j][0] == minSeat) {
                    if (seatSupport[j][1] < seatSupport[count][1]) {
                        count = j;
                    }
                }
            }
            support[0] = seatSupport[count][0];
            support[1] = seatSupport[count][1];
            support[2] = seatSupport[count][2];
            supportDescribe = seatDescribe[count];
            seatSupport[count][0] = seatSupport[i][0];
            seatSupport[count][1] = seatSupport[i][1];
            seatSupport[count][2] = seatSupport[i][2];
            seatDescribe[count] = seatDescribe[i];
            seatSupport[i][0] = support[0];
            seatSupport[i][1] = support[1];
            seatSupport[i][2] = support[2];
            seatDescribe[i] = supportDescribe;
            minSeat = 99;
            count = 0;
        }
        for (int i = 0; i < seatSupport.length; i++) {
            Log.i("AfterSort",
                    String.valueOf(seatSupport[i][0]) + "/" +
                            String.valueOf(seatSupport[i][1]) + "/" + seatSupport[i][2]);
            Seat seat;
            if (seatSupport[i][1] == 0) {
                seat = new Seat(seatDescribe[i],
                        "中饭", seatSupport[i][2],
                        "seats" + String.valueOf(seatSupport[i][0]) + ".jpg");
            } else {
                seat = new Seat(seatDescribe[i],
                        "晚饭", seatSupport[i][2],
                        "seats" + String.valueOf(seatSupport[i][0]) + ".jpg");
            }
            seatList.add(seat);
        }


        return seatList;
    }
}
