package com.gama.farm_fun;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EditTravelJournalActivity extends AppCompatActivity implements View.OnClickListener {
    private String userId;

    public View topBar;
    public Button back;
    public TextView title;
    public TextView finish;

    private EditText journal;
    private TextView word;

    private File tempFile;

    private Dialog bottomDialog;
    private TextView openCamera;
    private TextView openAlbum;
    private TextView cancel;

    private RecyclerView chosenPicRecyclerView;
    private List<PreUploadPic> preUploadPicList;
    private int tempNumber;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_edit_traveljournal);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        initUI();
    }

    public void initUI() {
        topBar = findViewById(R.id.bar_top);
        back = topBar.findViewById(R.id.back);
        back.setOnClickListener(this);
        title = topBar.findViewById(R.id.title);
        title.setText("发表游记");
        finish = topBar.findViewById(R.id.finish);
        finish.setText("发布");
        finish.setVisibility(View.VISIBLE);

        word = findViewById(R.id.word);
        journal = findViewById(R.id.journal);
        journal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                if (length >= 140) {
                    word.setText("（字数超过限制）" + String.valueOf(length));
                    word.setTextColor(getResources().getColor(R.color.colorRemind));
                } else {
                    word.setText(String.valueOf(length));
                    word.setTextColor(getResources().getColor(R.color.colorBlack));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        chosenPicRecyclerView = findViewById(R.id.chosenPicRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        chosenPicRecyclerView.setLayoutManager(linearLayoutManager);
        preUploadPicList = new ArrayList<>();
        preUploadPicList.add(new PreUploadPic(0));

        tempNumber = 0;

        initChosenPicRecyclerView();
    }

    public void initChosenPicRecyclerView() {
        ChosenPicAdapter chosenPicAdapter = new ChosenPicAdapter(preUploadPicList);
        chosenPicRecyclerView.setAdapter(chosenPicAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.open_camera:
                openSysCamera();
                bottomDialog.cancel();
                break;
            case R.id.open_album:
                openSysAlbum();
                bottomDialog.cancel();
                break;
            case R.id.cancel:
                bottomDialog.cancel();
                break;
        }
    }

    private void openSysCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(
                new File(Environment.getExternalStorageDirectory(), "test")));
        startActivityForResult(cameraIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0://拍照返回
                if (resultCode == RESULT_OK) {
                    tempFile = new File(Environment.getExternalStorageDirectory(), "test");
                    cropPic(Uri.fromFile(tempFile));
                }
                break;
            case 1:
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        Bitmap bitmap = bundle.getParcelable("data");
                        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null,null));
                        // 把裁剪后的图片保存至本地 返回路径
                        String urlpath = FileUtilcll.saveFile(this, "crop.jpg", bitmap);
                        Log.i("urlpath", "裁剪图片地址->" + urlpath);
                        preUploadPicList.get(tempNumber).setUri(uri);
                        tempNumber++;
                        PreUploadPic preUploadPic = new PreUploadPic(tempNumber);
                        preUploadPicList.add(preUploadPic);
                        addPicString();
                        initChosenPicRecyclerView();
                    }
                }
                break;
            case 2://相册返回
                if (data != null) {
                    cropPic(data.getData());
                }
                break;
        }
    }
    private void cropPic(Uri data) {
        if (data == null) {
        } else {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(data, "image/*");

            // 开启裁剪：打开的Intent所显示的View可裁剪
            cropIntent.putExtra("crop", "true");
            // 裁剪宽高比
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // 裁剪输出大小
            cropIntent.putExtra("outputX", 320);
            cropIntent.putExtra("outputY", 320);
            cropIntent.putExtra("scale", true);
            /**
             * return-data
             * 这个属性决定我们在 onActivityResult 中接收到的是什么数据，
             * 如果设置为true 那么data将会返回一个bitmap
             * 如果设置为false，则会将图片保存到本地并将对应的uri返回，当然这个uri得有我们自己设定。
             * 系统裁剪完成后将会将裁剪完成的图片保存在我们所这设定这个uri地址上。我们只需要在裁剪完成后直接调用该uri来设置图片，就可以了。
             */
            cropIntent.putExtra("return-data", true);
            // 当 return-data 为 false 的时候需要设置这句
//        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            // 图片输出格式
//        cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            // 头像识别 会启动系统的拍照时人脸识别
//        cropIntent.putExtra("noFaceDetection", true);
            startActivityForResult(cropIntent, 1);
        }
    }

    private void openSysAlbum() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(albumIntent, 2);
    }
    private void showDialog() {
        bottomDialog = new Dialog(this, R.style.BottomDialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_content_circle, null);
        bottomDialog.setContentView(contentView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels - DensityUtil.dp2px(this, 16f);
        params.bottomMargin = DensityUtil.dp2px(this, 8f);
        contentView.setLayoutParams(params);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        openCamera = bottomDialog.findViewById(R.id.open_camera);
        openCamera.setOnClickListener(this);
        openAlbum = bottomDialog.findViewById(R.id.open_album);
        openAlbum.setOnClickListener(this);
        cancel = bottomDialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        bottomDialog.show();
    }

    private class ChosenPicAdapter extends RecyclerView.Adapter<ChosenPicAdapter.ViewHolder> {
        private List<PreUploadPic> preUploadPicList;

        private ChosenPicAdapter(List<PreUploadPic> preUploadPicList) {
            this.preUploadPicList = preUploadPicList;
        }

        @Override
        public ChosenPicAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chosen_pic, parent, false);
            final ChosenPicAdapter.ViewHolder holder = new ChosenPicAdapter.ViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(final ChosenPicAdapter.ViewHolder holder, int position) {
            PreUploadPic preUploadPic = preUploadPicList.get(position);
            holder.setNumber(preUploadPic.number);
            if (preUploadPic.uri == null) {
                holder.pic.setBackground(getResources().getDrawable(R.drawable.shape_add_pic));
                holder.pic.setImageResource(R.drawable.add);
                holder.pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog();
                    }
                });
            } else {
                holder.pic.setImageURI(preUploadPic.uri);
                RoundingParams roundingParams = RoundingParams.fromCornersRadius(30f);
                holder.pic.getHierarchy().setRoundingParams(roundingParams);
            }

        }

        @Override
        public int getItemCount() {
            return preUploadPicList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private SimpleDraweeView pic;
            private int number;

            private ViewHolder(View view) {
                super(view);
                pic = view.findViewById(R.id.pic);
            }

            public void setNumber(int number) {
                this.number = number;
            }
        }
    }

    public void addPicString() {
        int position = journal.getSelectionStart();//获取光标所在位置
        String text="#图片#";
        Editable editable = journal.getEditableText();//获取EditText的文字
        if (position < 0 || position >= editable.length() ){
            editable.append(text);
        }else{
            editable.insert(position, text);//光标所在位置插入文字
        }
    }
}
