package com.hankkin.SelectMutiImgDemo.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.hankkin.SelectMutiImgDemo.Bimp;
import com.hankkin.SelectMutiImgDemo.R;
import com.hankkin.SelectMutiImgDemo.activity.ItemClickListener;
import com.hankkin.SelectMutiImgDemo.model.ImageBean;
import com.hankkin.SelectMutiImgDemo.progress.WaterWaveProgress;


/**
 * Created by wcz on 2016/10/14.
 */
@SuppressLint("HandlerLeak")
public class PictureAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private int selectedPosition = -1;
    private boolean shape;

    private ItemClickListener itemClickListener;

    public boolean isShape() {
        return shape;
    }

    private Activity context;

    public void setShape(boolean shape) {
        this.shape = shape;
    }

    public PictureAdapter(Activity context, ItemClickListener itemClickListener) {
        this.context = context;
        this.itemClickListener = itemClickListener;
        inflater = LayoutInflater.from(context);
    }


    public int getCount() {
        if (Bimp.tempSelectBitmap.size() == 9) {
            return 9;
        }
        return (Bimp.tempSelectBitmap.size() + 1);
    }


    public Object getItem(int arg0) {
        return 0;
    }

    public long getItemId(int arg0) {
        return arg0;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_published_singal_item,
                    parent, false);

            holder = new ViewHolder();
            // 照片
            holder.image = (ImageView) convertView.findViewById(R.id.item_grida_image);
            // 删除图标
            holder.img_delete = (ImageView) convertView.findViewById(R.id.img_delete);
            // progressbar
            holder.waveProgress = (WaterWaveProgress) convertView.findViewById(R.id.waterWaveProgress1);
            // 是否显示进度条
            holder.waveProgress.setShowProgress(false);
            // 是否显示进度值
            holder.waveProgress.setShowNumerical(false);
            // 有波浪动画
            holder.waveProgress.animateWave();
            // 水的透明度
            holder.waveProgress.setWaterAlpha(0.5f);
            // 背景
            holder.waveProgress.setWaterBgColor(context.getResources().getColor(R.color.transparent));

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null){
                    Log.e("POSITION", position+"");
                    itemClickListener.itemClickListener(v, position);
                }
            }
        });

        if (position == Bimp.tempSelectBitmap.size()) {
            holder.img_delete.setVisibility(View.GONE);
            holder.waveProgress.setVisibility(View.GONE);
            holder.image.setImageBitmap(BitmapFactory.decodeResource(
                    convertView.getResources(), R.drawable.icon_add_pic_unfocused));
            // 图片不能超过9张
            if (position == 9) {
                holder.image.setVisibility(View.GONE);
            }
        } else {
            ImageBean bean = Bimp.tempSelectBitmap.get(position);
            if (bean.isShowIcon()) {
                holder.img_delete.setVisibility(View.GONE);
                holder.waveProgress.setVisibility(View.VISIBLE);
            } else {
                holder.img_delete.setVisibility(View.VISIBLE);
                holder.waveProgress.setVisibility(View.GONE);
            }
            holder.image.setImageBitmap(bean.getBitmap());
            holder.waveProgress.setProgress(bean.getProgress());
        }
        return convertView;
    }

    public class ViewHolder {
        public ImageView image;
        private ImageView img_delete;
        private WaterWaveProgress waveProgress;
    }
}