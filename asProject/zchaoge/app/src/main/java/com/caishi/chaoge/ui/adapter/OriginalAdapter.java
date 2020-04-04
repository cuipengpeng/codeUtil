package com.caishi.chaoge.ui.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.bean.OriginalBean;
import com.caishi.chaoge.utils.GlideUtil;
import com.caishi.chaoge.utils.LogUtil;
import com.caishi.chaoge.utils.Utils;
import com.othershe.library.NiceImageView;

import java.util.ArrayList;
import java.util.List;

public class OriginalAdapter extends RecyclerView.Adapter<OriginalAdapter.ViewHolder> {

    private BaseActivity mContext;
    private ArrayList<OriginalBean> originalList = new ArrayList<>();
    private OnUpdateListener onUpdateListener;
    private OnItemClickListener onItemClickListener;

    public OriginalAdapter(BaseActivity baseActivity) {
        this.mContext = baseActivity;
    }

    public void setData(List<OriginalBean> data) {
        this.originalList.clear();
        this.originalList.addAll(data);
        notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_original_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        final OriginalBean originalBean = originalList.get(position);
        holder.tv_originalList_text.setText(originalBean.text);
        holder.tv_originalList_text.setEnabled(originalBean.isEditor);
        if (originalBean.fontPath != null) {
            try {
                Typeface mTypeface = Typeface.createFromFile(originalBean.fontPath);
                holder.tv_originalList_text.setTypeface(mTypeface);
            } catch (RuntimeException e) {
                holder.tv_originalList_text.setTypeface(Typeface.DEFAULT);
            }
        } else {
            holder.tv_originalList_text.setTypeface(Typeface.DEFAULT);
        }
        holder.tv_originalList_text.setTextColor(Color.parseColor(originalBean.specialColor));
        if (originalBean.isSelect) {
            holder.img_originalList_select.setImageBitmap(Utils.RGB2Bitmap("#000000"));
        }
        if (originalList.get(position).editFlag == 1) {//文字颜色显示勾选框
            holder.img_originalList_select.setVisibility(View.VISIBLE);
            holder.view_originalList_view.setVisibility(View.VISIBLE);
            holder.rl_originalList_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    originalList.get(position).isSelect = !originalList.get(position).isSelect;
                    if (onUpdateListener != null)
                        onUpdateListener.onUpdate(originalList);
                    notifyDataSetChanged();
                }
            });
            if (position == originalList.size() - 1) {
                holder.view_originalList_view.setVisibility(View.GONE);
            } else {
                holder.view_originalList_view.setVisibility(View.VISIBLE);
            }
        } else {
            holder.view_originalList_view.setVisibility(View.INVISIBLE);
            holder.img_originalList_spot.setVisibility(View.VISIBLE);
        }

        holder.tv_originalList_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                originalBean.text = s.toString();

            }
        });

        holder.tv_originalList_text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    LogUtil.d("删除");
                    deleteData(position, holder.tv_originalList_text.getSelectionEnd());
                }
                if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
                    LogUtil.d("回车");
                    updateData(position, holder.tv_originalList_text.getSelectionEnd(), originalBean.text.length());
                }
                return false;
            }
        });

    }

    private void deleteData(int position, int selection) {
        LogUtil.d("selectionEnd===" + selection);
        LogUtil.d("position===" + position);
        if (selection == 0 && position > 0) {
            OriginalBean originalBean = new OriginalBean();
            originalBean.text = originalList.get(position - 1).text + originalList.get(position).text;
            originalBean.startTime = originalList.get(position - 1).startTime;
            originalBean.endTime = originalList.get(position).endTime;
            originalBean.specialColor = originalList.get(position - 1).specialColor;
            originalBean.isSelect = originalList.get(position - 1).isSelect;
            originalBean.fontPath = originalList.get(position - 1).fontPath;
            originalList.remove(position - 1);
            originalList.remove(position - 1);
            originalList.add(position - 1, originalBean);
            notifyDataSetChanged();
            if (onUpdateListener != null)
                onUpdateListener.onUpdate(originalList);
        }
    }

    private void updateData(int position, int selection, int length) {
        LogUtil.d("selectionEnd===" + selection);
        LogUtil.d("position===" + position);
        if (selection > 0 && selection < length && position >= 0) {
            OriginalBean listOriginalBean = originalList.get(position);
            OriginalBean originalBean = new OriginalBean();
            originalBean.text = listOriginalBean.text.substring(0, selection);
            originalBean.startTime = listOriginalBean.startTime;
            originalBean.endTime = listOriginalBean.startTime + ((listOriginalBean.endTime - listOriginalBean.startTime) / length * (selection + 1));
            originalBean.specialColor = listOriginalBean.specialColor;
            originalBean.isSelect = listOriginalBean.isSelect;
            originalBean.fontPath = listOriginalBean.fontPath;

            OriginalBean originalBean1 = new OriginalBean();
            originalBean1.text = listOriginalBean.text.substring(selection, length);
            originalBean1.startTime = listOriginalBean.startTime + ((listOriginalBean.endTime - listOriginalBean.startTime) / length * (selection + 1));
            originalBean1.endTime = originalList.get(position).endTime;
            originalBean1.specialColor = listOriginalBean.specialColor;
            originalBean1.isSelect = listOriginalBean.isSelect;
            originalBean1.fontPath = listOriginalBean.fontPath;

            originalList.remove(position);
            originalList.add(position, originalBean1);
            originalList.add(position, originalBean);
            notifyDataSetChanged();
            if (onUpdateListener != null)
                onUpdateListener.onUpdate(originalList);
        }


    }


    @Override
    public int getItemCount() {
        return originalList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        EditText tv_originalList_text;
        NiceImageView img_originalList_select;
        ImageView img_originalList_spot;
        View view_originalList_view;
        RelativeLayout rl_originalList_layout;

        private ViewHolder(View itemView) {
            super(itemView);
            img_originalList_select = itemView.findViewById(R.id.img_originalList_select);
            img_originalList_spot = itemView.findViewById(R.id.img_originalList_spot);
            tv_originalList_text = itemView.findViewById(R.id.tv_originalList_text);
            view_originalList_view = itemView.findViewById(R.id.view_originalList_view);
            rl_originalList_layout = itemView.findViewById(R.id.rl_originalList_layout);
        }
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;


    }

    public interface OnItemClickListener {
        void onItemClick(OriginalAdapter.ViewHolder viewHolder, int position);
    }

    public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
        this.onUpdateListener = onUpdateListener;


    }

    public interface OnUpdateListener {
        void onUpdate(ArrayList<OriginalBean> originalList);
    }


}
