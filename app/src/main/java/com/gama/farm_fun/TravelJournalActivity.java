package com.gama.farm_fun;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class TravelJournalActivity extends AppCompatActivity {

    private int number;
    private String title;
    private String subTitle;
    private String article;
    private char[] articleChar;
    private String supportString;
    private int supportNumber;
    private List<Paragraph> paragraphList;
    private Paragraph endParagraph;
    private int support;

    private TextView titleText;
    private TextView subTitleText;
    private RecyclerView articleRecyclerView;

    private View loading;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_traveljournal);

        loading = findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        number = intent.getIntExtra("number", 0);

        getTravelJournal();
    }

    public void getTravelJournal() {
        AVQuery<AVObject> query = new AVQuery<>("TravelJournal");
        query.whereEqualTo("number", number);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                title = object.getString("title");
                subTitle = object.getString("subTitle");
                article = object.getString("article");

                getParagraphList();
            }
        });
    }

    public void getParagraphList() {
        paragraphList = new ArrayList<>();
        articleChar = article.toCharArray();
        supportString = "";
        supportNumber = 1;
        for (int i = 0; i < articleChar.length; i++) {
            if (articleChar[i] == '#') {
                Paragraph paragraph = new Paragraph(supportString, supportNumber);
                Log.i("para", supportString);
                paragraphList.add(paragraph);
                supportString = "";
                supportNumber++;
            } else {
                supportString += articleChar[i];
            }
        }
        Paragraph paragraph = new Paragraph(supportString, 0);
        paragraphList.add(paragraph);
        Log.i("para", supportString);
        getUrl();
    }

    public void getUrl() {
        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereStartsWith("name", "journal" + String.valueOf(number));
        query.orderByAscending("name");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                support = 0;
                for (AVObject avObject : avObjects) {
                    paragraphList.get(support).setUrl(avObject.getString("url"));
                    support++;
                }
                initUI();
            }
        });
    }

    public void initUI() {
        titleText = findViewById(R.id.title);
        titleText.setText(title);
        subTitleText = findViewById(R.id.subtitle);
        subTitleText.setText(subTitle);

        articleRecyclerView = findViewById(R.id.articleRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        articleRecyclerView.setLayoutManager(linearLayoutManager);
        initParagraphRecyclerView();
    }

    public void initParagraphRecyclerView() {
        Log.i("size", String.valueOf(paragraphList.size()));

        ParagraphAdapter paragraphAdapter = new ParagraphAdapter(paragraphList);
        articleRecyclerView.setAdapter(paragraphAdapter);

        loading.setVisibility(View.INVISIBLE);

    }

    private class ParagraphAdapter extends RecyclerView.Adapter<ParagraphAdapter.ViewHolder> {
        private List<Paragraph> paragraphList;

        private ParagraphAdapter(List<Paragraph> paragraphList) {
            this.paragraphList = paragraphList;
        }

        @Override
        public ParagraphAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_article, parent, false);
            final ParagraphAdapter.ViewHolder holder = new ParagraphAdapter.ViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(final ParagraphAdapter.ViewHolder holder, int position) {
            Paragraph paragraph = paragraphList.get(position);
            Log.i("paragraph", String.valueOf(paragraph.number));
            holder.words.setText(paragraph.words);
            if (paragraph.url != null) {
                Uri uri = Uri.parse(paragraph.url);
                holder.image.setImageURI(uri);
            } else {
                holder.image.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        public int getItemCount() {
            return paragraphList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView words;
            private SimpleDraweeView image;

            private ViewHolder(View view) {
                super(view);
                words = view.findViewById(R.id.words);
                image = view.findViewById(R.id.image);
            }


        }


    }
}
