package com.jfbank.qualitymarket.adapter;

import java.util.List;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.model.BasicInfoJobUnitBean.JobUnitDataBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 选择工作单位对应的Adapter
 * @author 彭爱军
 * @date 2016年8月19日
 */
public class SelectionUnitAdapter extends BaseAdapter{
	private Context mContext;
	private LayoutInflater mInflater;
	private List<JobUnitDataBean> mData;
	private ViewHolder mViewHolder;
	
	public SelectionUnitAdapter(Context mContext, List<JobUnitDataBean> mData) {
		super();
		this.mContext = mContext;
		this.mData = mData;
		mInflater = LayoutInflater.from(mContext);
	}

	/**
	 * 设置数据
	 * @param mData
	 */
	public void setData(List<JobUnitDataBean> mData){
		this.mData = mData;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.item_textview, parent, false);
			mViewHolder = new ViewHolder();
			mViewHolder.mTextView = (TextView) convertView.findViewById(R.id.item_tv_address_name);
			convertView.setTag(mViewHolder);
		}else{
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		
		JobUnitDataBean bean = mData.get(position);
		
		mViewHolder.mTextView.setText(bean.getName());
		
		return convertView;
	}

	public static class ViewHolder{
		private TextView mTextView;
	}
}
