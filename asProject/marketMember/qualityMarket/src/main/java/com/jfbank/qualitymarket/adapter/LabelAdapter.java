package com.jfbank.qualitymarket.adapter;

import java.util.List;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.model.LabelBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 选择地址标签对应的adapter
 * 
 * @author 彭爱军
 * @date 2016年10月09日
 */
public class LabelAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private List<LabelBean.LabelData> mData;
	private ViewHolder mViewHolder;

	public LabelAdapter(Context mContext, List<LabelBean.LabelData> mData) {
		super();
		this.mContext = mContext;
		this.mData = mData;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() { 
		return mData.size();
	}

	/**
	 * 设置Data
	 * 
	 * @param mData
	 */
	public void setData(List<LabelBean.LabelData> mData) {
		this.mData = mData;
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.item_textview, parent, false);
			mViewHolder = new ViewHolder();
			mViewHolder.mTextView = (TextView) convertView.findViewById(R.id.item_tv_address_name);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		LabelBean.LabelData bean = mData.get(position);

		mViewHolder.mTextView.setText(bean.getParameterName());

		return convertView;
	}

	public static class ViewHolder {
		private TextView mTextView;
	}

}
