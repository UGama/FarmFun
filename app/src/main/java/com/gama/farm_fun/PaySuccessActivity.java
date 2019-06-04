package com.gama.farm_fun;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class PaySuccessActivity extends AppCompatActivity {

    private RecyclerView recommendProjectsRecyclerView;
    private List<Project> projectList;
    private int support;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_pay_success);

        recommendProjectsRecyclerView = findViewById(R.id.recommendProjectsRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recommendProjectsRecyclerView.setLayoutManager(linearLayoutManager);

        projectList = new ArrayList<>();
        getProjectInformation();
    }

    public void getProjectInformation() {
        AVQuery<AVObject> query = new AVQuery<>("Amusement");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                for (AVObject avObject : avObjects) {
                    Project project = new Project(avObject.getString("name"),
                            avObject.getString("locateDescribe"),
                            avObject.getString("picName"));
                    projectList.add(project);
                }
                support = 0;
                getProjectUrl();
            }
        });
    }

    public void getProjectUrl() {
        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereEqualTo("name", projectList.get(support).picName);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                projectList.get(support).setUrl(object.getString("url"));
                support++;
                if (support == projectList.size()) {
                    setRecommendProjectsRecyclerView();
                } else {
                    getProjectUrl();
                }
            }
        });
    }

    public void setRecommendProjectsRecyclerView() {
        RecommendProjectsAdapter recommendProjectsAdapter = new RecommendProjectsAdapter(projectList);
        recommendProjectsRecyclerView.setAdapter(recommendProjectsAdapter);
    }

    private class RecommendProjectsAdapter extends RecyclerView.Adapter<RecommendProjectsAdapter.ViewHolder> {
        private List<Project> projectList;

        private RecommendProjectsAdapter(List<Project> projectList) {
            this.projectList = projectList;
        }

        @Override
        public RecommendProjectsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recommend_project, parent, false);
            final RecommendProjectsAdapter.ViewHolder holder = new RecommendProjectsAdapter.ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final RecommendProjectsAdapter.ViewHolder holder, int position) {
            Project project = projectList.get(position);
            holder.pic.setImageURI(project.url);
            RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);
            holder.pic.getHierarchy().setRoundingParams(roundingParams);
            holder.locate.setText(project.locateDescribe);
            holder.name.setText(project.name);
        }

        @Override
        public int getItemCount() {
            return projectList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView name;
            private TextView locate;
            private SimpleDraweeView pic;

            private ViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.name);
                locate = view.findViewById(R.id.locate);
                pic = view.findViewById(R.id.pic);
            }

        }


    }

}
