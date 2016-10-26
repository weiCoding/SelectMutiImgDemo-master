package com.hankkin.SelectMutiImgDemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.hankkin.SelectMutiImgDemo.Bimp;
import com.hankkin.SelectMutiImgDemo.R;
import com.hankkin.SelectMutiImgDemo.Utils.BitmapUtils;
import com.hankkin.SelectMutiImgDemo.Utils.FileUtils;
import com.hankkin.SelectMutiImgDemo.adapter.PictureAdapter;
import com.hankkin.SelectMutiImgDemo.model.ImageBean;
import com.hankkin.SelectMutiImgDemo.popwindow.SelectPicPopupWindow;
import com.hankkin.SelectMutiImgDemo.view.NoScrollGridView;

import java.io.File;

/**
 * Created by wcz on 2016/10/14.
 */
public class MainActivity extends Activity {

    /*不滚动的GridView*/
    private NoScrollGridView noScrollGridView;
    /*图片适配器*/
    private PictureAdapter adapter;
    private SelectPicPopupWindow menuWindow;
    private MainActivity instence;
    private String filePath;
    private Button btnUpload;
    /*公共静态Bitmap*/
    public static Bitmap bimap;

    private static final int TAKE_PICTURE = 1000;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instence = this;
        setContentView(R.layout.main);
        initViews();
    }

    private void initViews() {
        noScrollGridView = (NoScrollGridView) findViewById(R.id.noScrollgridview);
        noScrollGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        // 开始上传
        btnUpload = (Button) findViewById(R.id.btn_upload);
        btnUpload.setOnClickListener(itemsOnClick);
        adapter = new PictureAdapter(this, new ItemClickListener() {
            @Override
            public void itemClickListener(View v, int position) {
                Bimp.tempSelectBitmap.remove(position);
                adapter.notifyDataSetChanged();
            }
        });

        noScrollGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == Bimp.getTempSelectBitmap().size()) {
                    selectImgs();
                } else {
                    Intent intent = new Intent(instence,
                            GalleryActivity.class);
                    intent.putExtra("ID", i);
                    startActivity(intent);
                }
            }
        });
        noScrollGridView.setAdapter(adapter);
    }

    private void selectImgs() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(instence.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        menuWindow = new SelectPicPopupWindow(MainActivity.this, itemsOnClick);
        //设置弹窗位置
        menuWindow.showAtLocation(MainActivity.this.findViewById(R.id.llImage), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            if (menuWindow != null) {
                menuWindow.dismiss();
            }
            switch (v.getId()) {
                case R.id.item_popupwindows_camera:        //点击拍照按钮
                    goCamera();
                    break;
                case R.id.item_popupwindows_Photo:       //点击从相册中选择按钮
                    Intent intent = new Intent(instence,
                            AlbumActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_upload:         // 开始上传
                {
                    if (Bimp.tempSelectBitmap.size() == 0) {
                        Toast.makeText(MainActivity.this, "没有可上传的图片", Toast.LENGTH_SHORT).show();
                    } else {
                        UploadTask uTask = new UploadTask();
                        uTask.execute(100);
                    }
                }
                break;
                default:
                    break;
            }
        }

    };

    private void goCamera() {
        filePath = FileUtils.SDPATH + "photo.jpg";
        File file1 = new File(filePath);
        if (!file1.exists()) {
            File vDirPath = file1.getParentFile();
            vDirPath.mkdirs();
        }
        Uri uri = Uri.fromFile(file1);
        // 启动Camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) { //

                    String sdStatus = Environment.getExternalStorageState();
                    if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {  // 检测sd是否可用
                        Log.i("TestFile", "SD card is not avaiable/writeable right now.");
                        return;
                    }
                    String fileName = String.valueOf(System.currentTimeMillis());

                    Bitmap bm = BitmapUtils.getCompressedBitmap(instence, filePath);
                    FileUtils.saveBitmap(bm, fileName);

                    ImageBean takePhoto = new ImageBean();
                    takePhoto.setBitmap(bm);
                    takePhoto.setPath(filePath);
                    Bimp.tempSelectBitmap.add(takePhoto);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 刷新数据
        adapter.notifyDataSetChanged();
    }

    // AsyncTask上传
    class UploadTask extends AsyncTask<Integer, Integer, String> {

        @Override
        protected String doInBackground(Integer... params) {
            for (int i=0; i<=100;i++) {
                updateProgress(i);
                publishProgress(i);
                try {
                    Thread.sleep(params[0]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "";
        }

        // 更新progress数据
        public void updateProgress(int progress) {
            for (int i=0; i<Bimp.tempSelectBitmap.size();i++) {
                ImageBean bean = Bimp.tempSelectBitmap.get(i);
                bean.setProgress(progress);
                bean.setShowIcon(true);
                // 根据是否加载完成来显示或隐藏图标
//                if (progress==100) {
//                    bean.setShowIcon(false);
//                } else {
//                    bean.setShowIcon(true);
//                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // 更新界面
            adapter.notifyDataSetChanged();
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            // 线程执行完了之后触发
            setTitle(s);
            super.onPostExecute(s);
        }
    }
}