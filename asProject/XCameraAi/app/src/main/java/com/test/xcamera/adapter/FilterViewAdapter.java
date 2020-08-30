package com.test.xcamera.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.xcamera.R;
import com.test.xcamera.bean.ItemBean;

import java.util.ArrayList;

/**
 * Created by zhouxuecheng on
 * Create Time 2019/10/30
 * e-mail zhouxuecheng1991@163.com
 */

public class FilterViewAdapter extends RecyclerView.Adapter<FilterViewAdapter.ViewHolder> {
    private final Context mContext;
    private ArrayList<ItemBean.ItemData> arrayList;
    private ItemListener itemListener;
    private int rotate;

    public void updataData(ArrayList<ItemBean.ItemData> arrayList) {
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }

    public FilterViewAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public FilterViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(mContext, R.layout.filter_dialog_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewAdapter.ViewHolder viewHolder, final int i) {
        ItemBean.ItemData itemBean = arrayList.get(i);
        if (!TextUtils.isEmpty(itemBean.modelType) && itemBean.isOperation) {
            viewHolder.circle.setVisibility(View.VISIBLE);
        } else {
            viewHolder.circle.setVisibility(View.INVISIBLE);
        }
        viewHolder.name.setText(itemBean.name);
        viewHolder.name.setTextColor(itemBean.isSlect ?
                mContext.getResources().getColor(R.color.appThemeColor)
                : Color.parseColor("#666666"));
        viewHolder.viewBg.setVisibility(itemBean.isSlect ? View.VISIBLE : View.GONE);
//        viewHolder.filterIm.setImageResource(itemBean.rId);

        Matrix matrix = new Matrix();
        Bitmap bitmap = ((BitmapDrawable) mContext.getResources().getDrawable(itemBean.rId)).getBitmap();
        // 设置旋转角度
        matrix.setRotate(rotate);
        // 重新绘制Bitmap
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        viewHolder.filterIm.setImageBitmap(bitmap);


        viewHolder.itemRootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemListener.onClick(i);
            }
        });
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return arrayList == null ? 0 : arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public RelativeLayout itemRootLayout;
        public View viewBg;
        public ImageView filterIm;
        public ImageView circle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            itemRootLayout = itemView.findViewById(R.id.itemRootLayout);
            viewBg = itemView.findViewById(R.id.viewBg);
            filterIm = itemView.findViewById(R.id.filterIm);
            circle = itemView.findViewById(R.id.circle);
        }
    }

    public void setItemListener(ItemListener itemListener) {
        this.itemListener = itemListener;
    }

    public interface ItemListener {
        void onClick(int position);
    }
}
