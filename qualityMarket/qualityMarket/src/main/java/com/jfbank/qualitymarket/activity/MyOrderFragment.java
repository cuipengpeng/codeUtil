package com.jfbank.qualitymarket.activity;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.model.Order;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.StringUtil;
import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
*	我的订单fragment
* @author 崔朋朋
*/

public class MyOrderFragment extends Fragment {
	public static final String TAG = MyAccountFragment.class.getName();
	@InjectView(R.id.iv_myOrderFragment_noData)
	ImageView noDataImageView;
	
    private ListView originalListView;
    private List<Order> allOrderList = new ArrayList<Order>();
    private MyOrderAdapter allOrdersAdapter;
    private String orderStatus;
    private int pageNo = 1;
    private int pageSize = 10;
    private int pageCount = -1;
    //防止当前页码在中间，用户上拉、下拉刷新时重复加载数据
    private int headPageNo = 1;//记录当前orderList中的顶部页码
    private int bottomPageNo = 1;//记录当前orderList中的底部页码
    //上拉刷新的基页码
    private final int BASE_PAGE_NO = 1; 
	/**网编请求时加载框*/
	private LoadingAlertDialog mDialog;
    
    private PullToRefreshListView myOrderListView;

    public static final String KEY_OF_ORDER_ID = "orderIdKey";
    
    public static MyOrderFragment newInstance(String content) {
        MyOrderFragment fragment = new MyOrderFragment();
        fragment.allOrderList.clear();
        fragment.orderStatus = content;
        return fragment;
    }


    @SuppressWarnings("unchecked")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_my_order, container, false);
		ButterKnife.inject(this, view);
		
		allOrderList.clear();
		myOrderListView = (PullToRefreshListView) view.findViewById(R.id.lv_myOrderFragment_myOrder);
		allOrdersAdapter = new MyOrderAdapter(getActivity(), allOrderList);
		myOrderListView.setAdapter(allOrdersAdapter);
		myOrderListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
				intent.putExtra(KEY_OF_ORDER_ID, allOrderList.get(position-1).getOrderId());
				startActivity(intent);
			}
		});
		myOrderListView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				//下拉到一定的页码后，页面刷新，数据清空了。需重新获取数据
				if(pageNo>BASE_PAGE_NO && (pageCount*pageSize-pageSize)>allOrderList.size() && headPageNo>= BASE_PAGE_NO){
					pageNo -=1;
					headPageNo = pageNo;
					getOrderList(false, true);
				}else {
					cancelRefreshView();
				}

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				if(pageNo<pageCount  && (pageCount*pageSize)>allOrderList.size() && bottomPageNo<pageCount){
					pageNo +=1;
					bottomPageNo = pageNo;
					getOrderList(false, false);
				}else {
					cancelRefreshView();
				}
			}
		});

		myOrderListView.setMode(Mode.BOTH);
		ILoadingLayout startLabels = myOrderListView
				.getLoadingLayoutProxy(false, true);
		startLabels.setPullLabel(ConstantsUtil.PULL_TO_REFRESH_PROMPT);// 刚下拉时，显示的提示
		originalListView = myOrderListView.getRefreshableView();

		noDataImageView.setVisibility(View.GONE);
		myOrderListView.setVisibility(View.VISIBLE);
		
        return view;
    }
    
    @Override
    public void onResume() {
    	super.onResume();
//    	pageNo = currentMaxPageNo;
//    	pageNo = 1;
    	headPageNo = pageNo;
    	bottomPageNo = pageNo;
    	getOrderList(true, false);
    }

    /**
     * 取消下拉刷新的view
     */
	private void cancelRefreshView() {
		Toast.makeText(getActivity(), ConstantsUtil.NO_MORE_DATA_PROMPT, Toast.LENGTH_SHORT).show();
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				allOrdersAdapter.notifyDataSetChanged();
				myOrderListView.onRefreshComplete();
			}
		}, 500);
	}

    
    /**
     * 获取订单列表
     * @param initRequest 是否是初始化请求
     * @param pullDownToRefresh 是否是下拉刷新
     */
	private void getOrderList(final boolean initRequest, final boolean pullDownToRefresh) {
		if (null == mDialog) {
    		mDialog = new LoadingAlertDialog(getActivity());
		}
    	mDialog.show("网络请求中...");
		
		RequestParams params = new RequestParams();
		params.put("uid", AppContext.user.getUid());
		params.put("token", AppContext.user.getToken());
		 params.put("orderStatus", orderStatus);
		 params.put("pageNo", pageNo);
		 
		 HttpRequest.post(HttpRequest.QUALITY_MARKET_WEB_URL+HttpRequest.GET_ALL_ORDERS,params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				if (mDialog.isShowing()) {
					mDialog.dismiss();
				}
				if(!((MyOrderActivity)getActivity()).init){
					Toast.makeText(MyOrderFragment.this.getActivity(), ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
				}
				((MyOrderActivity)getActivity()).init = false;
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				if (mDialog.isShowing()) {
					mDialog.dismiss();
				}
				
				String jsonStr = new String(arg2);
				LogUtil.printLog("获取订单：" + jsonStr);

				JSONObject jsonObject = JSON.parseObject(jsonStr);
				
				if(initRequest){
					allOrderList.clear();
				}
				if(ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)){
					if(!((MyOrderActivity)getActivity()).init){
						((MyOrderActivity)getActivity()).init = false;
						LoginActivity.tokenFailDialog(MyOrderFragment.this.getActivity(), jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
					}
				}else if((ConstantsUtil.RESPONSE_SUCCEED == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) || allOrderList.size()>0){
					pageCount = jsonObject.getIntValue("pageCount");
					if(jsonObject.containsKey(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME)){
						JSONArray orderJsonArray = jsonObject.getJSONArray(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME);
						Order order = null;
						for(int i=0; i<orderJsonArray.size();i++){
							order = JSON.parseObject(orderJsonArray.get(i).toString(), Order.class);
							if(pullDownToRefresh){
								allOrderList.add(i, order);
							}else {
								allOrderList.add(order); 
							}
						}
					}else {
						Toast.makeText(getActivity(), ConstantsUtil.NO_MORE_DATA_PROMPT, Toast.LENGTH_SHORT).show();
					}
					allOrdersAdapter.notifyDataSetChanged();
					myOrderListView.onRefreshComplete();
					if(pullDownToRefresh){
						originalListView.setSelection(pageSize);
					}
				}else if((ConstantsUtil.RESPONSE_NO_DATA == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) && allOrderList.size()<=0){
					noDataImageView.setVisibility(View.VISIBLE);
					myOrderListView.setVisibility(View.GONE);
				}else {
					Toast.makeText(MyOrderFragment.this.getActivity(), "查询订单失败", Toast.LENGTH_SHORT).show();
				}
				
				((MyOrderActivity)getActivity()).init = false;
				
			}});
	}


    
    class MyOrderAdapter extends BaseAdapter {
    	private List<Order> orderList;
    	private Activity activity;
//    	private Order order;
    	
		public MyOrderAdapter(Activity activity, List<Order> orderList) {
			super();
			this.orderList = orderList;
			this.activity = activity;
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
			
			final Order order = orderList.get(position);
			Picasso.with(getActivity()).load(order.getProductImage()).placeholder(R.drawable.ic_launcher).resize(ConstantsUtil.PRODUCT_IMAGE_WIDTH, ConstantsUtil.PRODUCT_IMAGE_WIDTH).into(viewHolder.productImageView);
			
				viewHolder.orderStatusTextView.setText(MyOrderActivity.getOrderStatus(order.getOrderStatus()));
			if(MyOrderActivity.ORDER_STATUS_WAIT_FOR_PAY.equals(order.getOrderStatus())){
				viewHolder.paidDownPaymentTextView.setVisibility(View.VISIBLE);
				viewHolder.viewLogisticsTextView.setVisibility(View.GONE);
				viewHolder.applyAfterSaleTextView.setVisibility(View.GONE);
			}else if(MyOrderActivity.ORDER_STATUS_WAIT_FOR_TAKE_OVER_GOODS.equals(order.getOrderStatus())){
				viewHolder.paidDownPaymentTextView.setVisibility(View.GONE);
				viewHolder.viewLogisticsTextView.setVisibility(View.VISIBLE);
				viewHolder.applyAfterSaleTextView.setVisibility(View.GONE);
			}else  if(MyOrderActivity.ORDER_STATUS_FINISHED.equals(order.getOrderStatus())){
				viewHolder.paidDownPaymentTextView.setVisibility(View.GONE);
				viewHolder.viewLogisticsTextView.setVisibility(View.GONE);
				viewHolder.applyAfterSaleTextView.setVisibility(View.VISIBLE);
			}else{
				viewHolder.paidDownPaymentTextView.setVisibility(View.GONE);
				viewHolder.viewLogisticsTextView.setVisibility(View.GONE);
				viewHolder.applyAfterSaleTextView.setVisibility(View.GONE);
			}
			
			viewHolder.productPriceTextView.setText("一次性付: "+MyAccountFragment.moneyDecimalFormat.format(order.getProductPrice())+"元");
			viewHolder.productNameTextView.setText(order.getProductName());

			String productAmount = "共1件商品";
			 String downPayment  = "首付:"+MyAccountFragment.moneyDecimalFormat.format(order.getFirstPayment())+"元";
			 String installment = "月付:"+MyAccountFragment.moneyDecimalFormat.format(order.getMonthPay())+"元*"+order.getMonthNum()+"期";
			SpannableString productAmountStyledText = new SpannableString(productAmount);
			productAmountStyledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.my_order_style_gray), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			productAmountStyledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.my_order_style_black), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			productAmountStyledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.my_order_style_gray), 2, productAmount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

			 SpannableString downPaymentStyledText = new SpannableString(downPayment);
			 downPaymentStyledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.my_order_style_gray), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			 downPaymentStyledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.my_order_style_black), 3, downPayment.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			 downPaymentStyledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.my_order_style_gray), downPayment.length()-1, downPayment.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				 
			 SpannableString installmentStyledText = new SpannableString(installment);
			 installmentStyledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.my_order_style_gray), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			 installmentStyledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.my_order_style_black), 3, installment.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			 installmentStyledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.my_order_style_gray), installment.length()-1, installment.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			 viewHolder.productAmountTextView.setText(productAmountStyledText,TextView.BufferType.SPANNABLE);
			viewHolder.downPaymentTextView.setText(downPaymentStyledText,TextView.BufferType.SPANNABLE);
			viewHolder.installmentTextView.setText(installmentStyledText,TextView.BufferType.SPANNABLE);
			viewHolder.paidDownPaymentTextView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(activity, PayActivity.class);
					String productPrice = MyAccountFragment.moneyDecimalFormat.format(order.getProductPrice())+"元";
					intent.putExtra(ConfirmOrderActivity.KEY_OF_PRODUCT_NAME, order.getProductName());
					intent.putExtra(ConfirmOrderActivity.KEY_OF_PRODUCT_IMAGE_URL, order.getProductImage());
					intent.putExtra(ConfirmOrderActivity.KEY_OF_PRODUCT_PRICE, "一次性付: "+productPrice);
					intent.putExtra(ConfirmOrderActivity.KEY_OF_DOWNPAYMENT_PRICE, MyAccountFragment.moneyDecimalFormat.format(order.getFirstPayment())+"元");
					intent.putExtra(ConfirmOrderActivity.KEY_OF_INSTALLMENT, MyAccountFragment.moneyDecimalFormat.format(order.getMonthPay())+"元");
					intent.putExtra(ConfirmOrderActivity.KEY_OF_ORDER_NO, order.getOrderId());
					intent.putExtra(ConfirmOrderActivity.KEY_OF_ORDER_MOENY, productPrice);
					if(StringUtil.isNull(order.getRedbagValue()) || Double.valueOf(order.getRedbagValue()) == 0){
						intent.putExtra(ConfirmOrderActivity.KEY_OF_COUPON_MOENY, "");
						intent.putExtra(ConfirmOrderActivity.KEY_OF_ACTUAL_MOENY, productPrice);					
					}else  {
						//使用红包
						intent.putExtra(ConfirmOrderActivity.KEY_OF_COUPON_MOENY, order.getRedbagValue()+"元");
						intent.putExtra(ConfirmOrderActivity.KEY_OF_ACTUAL_MOENY, order.getActualAmount()+"元");					
					}
					activity.startActivity(intent);
					activity.finish();					
				}
			});
			viewHolder.viewLogisticsTextView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(activity, LogisticsDetailsActivity.class);
					intent.putExtra(KEY_OF_ORDER_ID, order.getOrderId());
					activity.startActivity(intent);					
				}
			});
			viewHolder.applyAfterSaleTextView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					OrderDetailActivity.applyAfterSale(activity);
				}
			});
			
			
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
    	@InjectView(R.id.tv_myOrderActivity_orderItem_viewLogistics)
    	TextView viewLogisticsTextView;
    	@InjectView(R.id.tv_myOrderActivity_orderItem_applyAfterSale)
    	TextView applyAfterSaleTextView;
    	
		public ViewHolder(View v) {
			super();
			ButterKnife.inject(this, v);
		}
    	
    }
    
    /**
     * 获取待支付订单的剩余时间
     * @param order
     * @return
     */
    public static int getOrderRemainTime(Order order){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long usageTime = 0;
		try {
			usageTime = simpleDateFormat.parse(order.getCurrentTime()).getTime() - simpleDateFormat.parse(order.getOrderTime()).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//倒计时时间  单位：秒
		int countDownTime = (int) ((60*60*24*1000 - usageTime)/1000);
		return countDownTime;
    }
    
}
