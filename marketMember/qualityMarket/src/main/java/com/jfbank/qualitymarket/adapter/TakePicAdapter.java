package com.jfbank.qualitymarket.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.flyco.dialog.entity.DialogMenuItem;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BasePresenter;
import com.jfbank.qualitymarket.listener.DialogListener;
import com.jfbank.qualitymarket.mvp.SubmitSalesMVP;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.DialogUtils;
import com.jfbank.qualitymarket.util.StringUtil;
import com.jfbank.qualitymarket.util.ToastUtil;
import com.jfbank.qualitymarket.widget.ForceClickImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * 文 件 名：TakePicAdapter
 * 功    能： 相册选择照片后结果
 * 作    者：赵海
 * 时    间：2016/7/19
 **/
public class TakePicAdapter extends BaseAdapter {
    private static final String TAG = "TakePicAdapter";
    Context mContext;
    ArrayList<DialogMenuItem> mMenuReasonItems = new ArrayList<>();
    String dialogTitle;

    public ArrayList<String> getDatas() {
        return datas;
    }

    public void setDatas(ArrayList<String> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    public void addPic(String datas) {
        this.datas.add(datas);
        notifyDataSetChanged();
    }

    public void addPic(ArrayList datas) {
        if (!CommonUtils.isEmptyList(datas)) {
            this.datas.addAll(datas);
            notifyDataSetChanged();
        }
    }

    private ArrayList<String> datas = new ArrayList<>();
    BasePresenter mPresenter;
    int maxSize = 0;
    public final static String ADD_PIC = "ADD_PIC";

    public TakePicAdapter(Context mContext, BasePresenter iTakePresenter, String dialogTitle, int maxSize) {

        this.mContext = mContext;
        this.mPresenter = iTakePresenter;
        this.dialogTitle = dialogTitle;
        this.maxSize = maxSize;
        mMenuReasonItems.add(0, new DialogMenuItem("相机", 0));
        mMenuReasonItems.add(1, new DialogMenuItem("相册", 0));
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_take_pic, null);
            viewHolder.img = (ForceClickImageView) convertView.findViewById(R.id.img);
            viewHolder.ivDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (TextUtils.equals(datas.get(position), ADD_PIC)) {
            viewHolder.ivDelete.setVisibility(View.GONE);
            Picasso.with(mContext).load(R.mipmap.ic_select_add_pic).into(viewHolder.img);
            viewHolder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.equals(datas.get(position), ADD_PIC)) {
                        DialogUtils.showSheetDialog(mContext, dialogTitle, new String[]{"拍照", "相册"}, new DialogListener.DialogItemLisenter() {
                            @Override
                            public void onDialogClick(int position) {
                                if (position == 0) {
                                    mPresenter.onPickFromCapture();
                                } else {
                                    mPresenter.onPickFromGallery();
                                }
                            }
                        });
                    } else {
                        ArrayList<String> data = new ArrayList<>();
                        for (int i = 0; i < datas.size(); i++) {
                            if (!datas.get(i).equals(ADD_PIC))
                                data.add(datas.get(i));
                        }
                        mPresenter.showPhotoList(parent, data, position, 1);
                    }
                }
            });
        } else {
            viewHolder.ivDelete.setVisibility(View.VISIBLE);
            if (datas.get(position).contains("http")) {
                Picasso.with(mContext).load(datas.get(position)).into(viewHolder.img);
            } else {
                Picasso.with(mContext).load(new File(datas.get(position))).config(Bitmap.Config.RGB_565).resize(60, 60).centerCrop().into(viewHolder.img);
            }
            viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUtils.showTwoBtnDialog(mContext, null, "是否确认删除图片？", null, "删除", new DialogListener.DialogClickLisenter() {
                        @Override
                        public void onDialogClick(int type) {
                            if (type == CLICK_SURE) {
                                datas.remove(position);
                                notifyDataSetChanged();
                            }
                        }
                    });

                }
            });
        }
        return convertView;
    }


    @Override
    public void notifyDataSetChanged() {
        datas.remove(ADD_PIC);
        if (this.datas.size() < maxSize) {
            this.datas.add(ADD_PIC);
        }
        super.notifyDataSetChanged();
    }

    class ViewHolder {
        ForceClickImageView img;
        ImageView ivDelete;

    }
}