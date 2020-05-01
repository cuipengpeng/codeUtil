package com.jfbank.qualitymarket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.dao.LogisticsDetailseBean;
import com.jfbank.qualitymarket.model.GoodsRejectedBean;

import java.util.Collections;
import java.util.List;

/**
 * 物流详情对应的adapter
 * @author 彭爱军
 * @date 2016年8月25日
 */
public class LogisticsDetailseAdapter<T> extends BaseAdapter{
	private Context mContext;
//	private List<LogisticsDetailseBean.DataBean> mData;
	private List<T> mData;
	private LayoutInflater mInflater;
	private ViewHolder mViewHolder;

	public void updateData(List<T> mData){
		this.mData = mData;
		Collections.reverse(mData);			//倒序
		notifyDataSetChanged();
	}

	public LogisticsDetailseAdapter(Context mContext, List<T> mData) {
		super();
		this.mContext = mContext;
		this.mData = mData;
		Collections.reverse(mData);			//倒序
		mInflater = LayoutInflater.from(mContext);
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
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (null == convertView) {
			 convertView = mInflater.inflate(R.layout.item_logistics_details, parent, false);
			mViewHolder = ViewHolder.create(convertView);
			convertView.setTag(mViewHolder);
		}else{
			mViewHolder = (ViewHolder) convertView.getTag();
		}

		T bean = mData.get(position);
		if( bean instanceof  LogisticsDetailseBean.DataBean ){
			LogisticsDetailseBean.DataBean dataBean =(LogisticsDetailseBean.DataBean) bean;
			mViewHolder.itemLogisticsTvAddress.setText(dataBean.getContent());
			mViewHolder.itemLogisticsTvTime.setText(dataBean.getMsgTime());
		}else if( bean instanceof GoodsRejectedBean.Data){
			GoodsRejectedBean.Data data= (GoodsRejectedBean.Data) bean;
			mViewHolder.itemLogisticsTvAddress.setText(data.getContent());
			mViewHolder.itemLogisticsTvTime.setText(data.getMsgTime());
		}

		mViewHolder.itemLogisticsIvIcon.setBackgroundResource(R.drawable.tuoyuan);
		mViewHolder.itemLogisticsAboveLine.setVisibility(View.VISIBLE);
		mViewHolder.itemLogisticsBelowLine.setVisibility(View.VISIBLE);

		if (0 == position) {
			mViewHolder.itemLogisticsIvIcon.setBackgroundResource(R.drawable.yuan);
			mViewHolder.itemLogisticsAboveLine.setVisibility(View.INVISIBLE);
		}

		if(position == (mData.size()-1)){
			mViewHolder.itemLogisticsBelowLine.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

	/**
	 * ViewHolder
	 * @author 彭爱军
	 * @date 2016年8月25日
	 */
	private static class ViewHolder {
		public final ImageView itemLogisticsIvIcon;
		public final View itemLogisticsAboveLine;
		public final View itemLogisticsBelowLine;
		public final TextView itemLogisticsTvAddress;
		public final TextView itemLogisticsTvTime;

		private ViewHolder(ImageView itemLogisticsIvIcon, View itemLogisticsAboveLine, View itemLogisticsBelowLine,TextView itemLogisticsTvAddress, TextView itemLogisticsTvTime) {
			this.itemLogisticsIvIcon = itemLogisticsIvIcon;
			this.itemLogisticsAboveLine = itemLogisticsAboveLine;
			this.itemLogisticsBelowLine = itemLogisticsBelowLine;
			this.itemLogisticsTvAddress = itemLogisticsTvAddress;
			this.itemLogisticsTvTime = itemLogisticsTvTime;
		}

		public static ViewHolder create(View rootView) {
			ImageView itemLogisticsIvIcon = (ImageView)rootView.findViewById( R.id.item_logistics_iv_icon );
			View itemLogisticsAboveLine = (View)rootView.findViewById( R.id.item_logistics_above_line );
			View itemLogisticsBelowLine = (View)rootView.findViewById( R.id.item_logistics_below_line);
			TextView itemLogisticsTvAddress = (TextView)rootView.findViewById( R.id.item_logistics_tv_address );
			TextView itemLogisticsTvTime = (TextView)rootView.findViewById( R.id.item_logistics_tv_time );
			return new ViewHolder(itemLogisticsIvIcon, itemLogisticsAboveLine, itemLogisticsBelowLine, itemLogisticsTvAddress, itemLogisticsTvTime );
		}
	}

}
