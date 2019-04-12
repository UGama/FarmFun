package com.gama.farm_fun;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;

import java.util.HashMap;
import java.util.List;

public class PickActivity extends AppCompatActivity {

    private ObservableScrollView observableScrollView;
    private ImageView mainPic;
    private TextView projectName;
    private TextView describe;
    private TextView address;
    private TextView reserve;
    private RecyclerView ticketRecyclerView;

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
        reserve = findViewById(R.id.reserve);
        reserve.setText("门票预订");
        ticketRecyclerView = findViewById(R.id.ticketRecyclerView);

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
            }
        });
    };
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
                    .inflate(R.layout.item_recommendproject, parent, false);
            final MainActivity.RecommendProjectsAdapter.ViewHolder holder = new MainActivity.RecommendProjectsAdapter.ViewHolder(view);
            /*holder.recommendProjectPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });*/
            return holder;
        }

        @Override
        public void onBindViewHolder(MainActivity.RecommendProjectsAdapter.ViewHolder holder, int position) {
            Project project = projectsList.get(position);
            Log.i(String.valueOf(position), project.title);
            holder.recommendProjectText.setText(project.title);
        }

        @Override
        public int getItemCount() {
            return projectsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView recommendProjectPic;
            private TextView recommendProjectText;

            private ViewHolder(View view) {
                super(view);
                recommendProjectPic = view.findViewById(R.id.projectPic);
                recommendProjectText = view.findViewById(R.id.projectText);
            }
        }


    }
}
