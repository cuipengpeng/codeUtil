package com.jfbank.qualitymarket.adapter;

import java.util.List;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.model.AddressBean;
import com.jfbank.qualitymarket.model.AddressBean.DataBean;
import com.jfbank.qualitymarket.model.DataProvincesBean;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 地址对应的adapter
 * @author 彭爱军
 * @date 2016年8月16日
 */
public class AddressAdapter extends BaseAdapter{
	private Context mContext;
	private LayoutInflater mInflater;
	private List<AddressBean.DataBean> mData;
	private ViewHolder mViewHolder;
	/**基本信息中对应的数据*/
	private List<DataProvincesBean> mDataProvinces;
	/**是否为基本信息*/
	private boolean mIsBaseInfo;
	
	public AddressAdapter(Context mContext, List<DataBean> mData,List<DataProvincesBean> mDataProvinces,boolean isBaseInfo) {
		super();
		this.mContext = mContext;
		this.mData = mData;
		this.mDataProvinces = mDataProvinces;
		this.mIsBaseInfo = isBaseInfo;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		if (mIsBaseInfo) {
			if (null != mDataProvinces) {
				return mDataProvinces.size();
			}else{
				return 0;
			}
		}else{
			if (null != mData) {
				return mData.size();
			}else{
				return 0;
			}
		}
	}
	
	/**
	 * 设置Data
	 * @param mData
	 */
	public void setData(List<DataBean> mData){
		this.mData = mData;
	}
	/**
	 * 设置mDataProvinces
	 * @param mData
	 */
	public void setDataProvinces(List<DataProvincesBean> mDataProvinces){
		this.mDataProvinces = mDataProvinces;
	}
	
	@Override
	public Object getItem(int position) {
		return mIsBaseInfo?mDataProvinces.get(position):mData.get(position);
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
		}else{
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		if (mIsBaseInfo) {
			DataProvincesBean bean = mDataProvinces.get(position);
			
			mViewHolder.mTextView.setText(bean.getName());
		}else{
			DataBean bean = mData.get(position);
			
			mViewHolder.mTextView.setText(bean.getAreaname());
		}
		
		return convertView;
	}
	
	
	public static class ViewHolder{
		private TextView mTextView;
	}
	
}
