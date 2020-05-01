/**
 * 文件名：MyRedPacketAdapter.java
 * 全路径：com.jfbank.qualitymarket.adapter.MyRedPacketAdapter
 */
package com.jfbank.qualitymarket.adapter;

import java.util.ArrayList;
import java.util.List;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.activity.MyRedPacketActivity;
import com.jfbank.qualitymarket.model.CouponBeqan;
import com.jfbank.qualitymarket.util.TimeUtils;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 功能：我的红包 <br>
 * 作者：赵海 <br>
 * 时间：2016年10月19日 下午5:49:14 <br>
 * 版本：1.1.0 <br>
 * 
 */
public class MyRedPacketAdapter extends BaseAdapter {
	Context context;
	List<CouponBeqan> data = new ArrayList<CouponBeqan>();
	String[] states = { "已使用 ", "未使用 ", "已过期 " };

	/**
	 * @return the data
	 */
	public List<CouponBeqan> getData() {
		return data;
	}

	public MyRedPacketAdapter(Context context) {
		this.context = context;
	}

	/**
	 * 更新数据
	 * 
	 * @param data
	 */
	public void updateData(List<CouponBeqan> data,boolean isRefresh) {
		if (data != null && data.size() > 0) {
			if (isRefresh) {
				this.data = data;
			}else{
				this.data.addAll(data);
			}
			notifyDataSetChanged();
		}

	}

	@Override
	public int getCount() {
		return data == null ? 0 : data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView != null) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_myredpacket, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}
		// holder.ll_redpacket_content.setEnabled(enabled);
		holder.tv_redpacket_name.setText(data.get(position).getShowName());
		holder.tv_redpacket_sum.setText(data.get(position).getParValue() + "");
		holder.tv_redpacket_scope.setText(data.get(position).getOtherRuleDesc());
		if (!TextUtils.isEmpty(data.get(position).getEndTime())) {
//			holder.tv_redpacket_dateline.setText("有效期至" + DateUtils.getYYYYMMDate(Long.parseLong(data.get(position).getEndTime())));
			holder.tv_redpacket_dateline.setText("有效期至" + TimeUtils.formatWithString(Long.parseLong(data.get(position).getEndTime()), TimeUtils.DATE_FORMAT));
		} else {
			holder.tv_redpacket_dateline.setText("有效期至");
		}
		if (!TextUtils.isEmpty(data.get(position).getMinOrderMoney())
				&& Float.parseFloat(data.get(position).getMinOrderMoney()) > 0) {
			holder.tv_redpacket_declare.setText("满" + data.get(position).getMinOrderMoney() + "立减");
		} else {
			holder.tv_redpacket_declare.setText("");
		}

		int useStateNo = data.get(position).getUseStatus();
		if (useStateNo > 0 && useStateNo < 4) {
			holder.tv_redpacket_state.setText(states[useStateNo - 1]);
		} else {
			holder.tv_redpacket_state.setText("");
		}

		boolean usestate = data.get(position).getUseStatus() == 2 ? true : false;
		holder.ll_redpacket_content.setEnabled(usestate);
		holder.tv_redpacket_name.setEnabled(usestate);
		holder.tv_redpacket_declare.setEnabled(usestate);
		holder.tv_redpacket_scope.setEnabled(usestate);
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((MyRedPacketActivity) context).onItemClick(position);

			}
		});
		return convertView;
	}

	static class ViewHolder {
		@InjectView(R.id.tv_redpacket_state)
		TextView tv_redpacket_state;// 红包状态，是否可用
		@InjectView(R.id.tv_redpacket_sum)
		TextView tv_redpacket_sum;// 红包金额
		@InjectView(R.id.tv_redpacket_scope)
		TextView tv_redpacket_scope;// 使用范围
		@InjectView(R.id.tv_redpacket_dateline)
		TextView tv_redpacket_dateline;// 截止日期
		@InjectView(R.id.tv_redpacket_declare)
		TextView tv_redpacket_declare;// 使用说明
		@InjectView(R.id.tv_redpacket_name)
		TextView tv_redpacket_name;// 红包名称
		@InjectView(R.id.ll_redpacket_content)
		LinearLayout ll_redpacket_content;

		public ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}
}
