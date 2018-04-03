package com.jfbank.qualitymarket.activity;

import java.util.ArrayList;
import java.util.List;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.model.Order;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 *	搜索商品页面
 * @author 崔朋朋
 */
public class SearchGoodsFragment extends Fragment {
    private ListView originalListView;
    private List<Order> allOrders = new ArrayList<Order>();
    private MyOrderAdapter allOrdersAdapter;
    
    private PullToRefreshListView myOrderListView;
    public static final String KEY_OF_ORDER_ID = "orderIdKey";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_my_order, container, false);
		ButterKnife.inject(this, view);
		myOrderListView = (PullToRefreshListView) view.findViewById(R.id.lv_myOrderFragment_myOrder);
		allOrdersAdapter = new MyOrderAdapter(allOrders);
		myOrderListView.setAdapter(allOrdersAdapter);
		myOrderListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
				intent.putExtra(KEY_OF_ORDER_ID, allOrders.get(position-1).getOrderId());
				startActivity(intent);
			}
		});
		
		myOrderListView.setMode(Mode.PULL_FROM_START);
		ILoadingLayout startLabels = myOrderListView
				.getLoadingLayoutProxy(true, false);
		startLabels.setPullLabel("人人");// 刚下拉时，显示的提示
		startLabels.setRefreshingLabel("人人");// 刷新时
		startLabels.setReleaseLabel("人人");// 下来达到一定距离时，显示的提示
		originalListView = myOrderListView.getRefreshableView();

        return view;
	}
	
	
	class MyOrderAdapter extends BaseAdapter {
    	private List<Order> orderList;
    	
		public MyOrderAdapter(List<Order> orderList) {
			super();
			this.orderList = orderList;
		}

		@Override
		public int getCount() {
			return orderList.size();
		}

		@Override
		public Object getItem(int position) {
			return orderList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if(convertView == null){
				convertView = View.inflate(getActivity(), R.layout.order_item, null);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			}else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			Order order = orderList.get(position);
			Picasso.with(getActivity()).load(order.getProductImage()).placeholder(R.drawable.ic_launcher).resize(100, 100).into(viewHolder.productImageView);
			viewHolder.orderStatusTextView.setText(order.getOrderStatus());
			viewHolder.productPriceTextView.setText("售价: "+order.getProductPrice()+"元");
			viewHolder.productNameTextView.setText(order.getProductName());
			viewHolder.downPaymentTextView.setText("首付："+order.getFirstPayment()+"元");
			viewHolder.installmentTextView.setText("月供："+order.getMonthPay()+"元");
			return convertView;
		}
    	
    }
    
    static class ViewHolder {
    	@InjectView(R.id.tv_myOrderActivity_orderItem_orderStatus)
    	TextView orderStatusTextView;
    	@InjectView(R.id.iv_myOrderActivity_orderItem_productImage)
    	ImageView productImageView;
    	@InjectView(R.id.tv_myOrderActivity_orderItem_productName)
    	TextView productNameTextView;
    	@InjectView(R.id.tv_myOrderActivity_orderItem_productPrice)
    	TextView productPriceTextView;
    	@InjectView(R.id.tv_myOrderActivity_orderItem_productAmount)
    	TextView productAmountTextView;
    	@InjectView(R.id.tv_myOrderActivity_orderItem_downPaymentPrice)
    	TextView downPaymentTextView;
    	@InjectView(R.id.tv_myOrderActivity_orderItem_installment)
    	TextView installmentTextView;
    	@InjectView(R.id.tv_myOrderActivity_orderItem_paidDownPayment)
    	TextView paidDownPaymentTextView;
    	
		public ViewHolder(View v) {
			super();
			ButterKnife.inject(this, v);
		}
    	
    }
}
