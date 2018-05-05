package com.test.bank.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.bean.MessageBean;
import com.test.bank.utils.StringUtil;
import com.test.bank.utils.UIUtils;
import com.test.bank.view.activity.FundTradeRecordDetailActivity;
import com.test.bank.view.activity.PutInResultActivity;

import java.util.List;

/**
 * 不带头布局的adapter
 * Created by 55 on 2017/11/30.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private Context context;
    private List<MessageBean> mData;

    public MessageAdapter(Context context, List<MessageBean> mData) {
        this.context = context;
        this.mData = mData;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater.from(context).inflate(R.layout.item_message, parent, false));
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, final int position) {
        if ("2".equals(mData.get(position).getShowModule())) {  //活期+ ： 跳转到活期+交易详情
            holder.tvTitleLeft.setTextColor(ContextCompat.getColor(context, R.color.color_cda76e));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PutInResultActivity.open(context, mData.get(position).getTradeno());
                }
            });
        } else if ("1".equals(mData.get(position).getShowModule())) {       //基金： 跳转到基金交易详情
            holder.tvTitleLeft.setTextColor(ContextCompat.getColor(context, R.color.color_0084ff));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FundTradeRecordDetailActivity.open(context, mData.get(position).getTradeno());
                }
            });
        }

        UIUtils.setText(holder.tvTitleLeft, mData.get(position).getFundname());
        UIUtils.setText(holder.tvTitleRight, getContentByTradeStatus(mData.get(position)));
        UIUtils.setText(holder.tvDate, StringUtil.transferTimeStampToDate(mData.get(position).getMsg_createtime(), "yyyy-MM-dd HH:mm"));
        UIUtils.setText(holder.tvContent, mData.get(position).getMsg_content());
    }

    private String getContentByTradeStatus(MessageBean messageBean) {    //(0待确认、1已确认、2已撤单、3异常、4交易关闭)
        StringBuilder sb = new StringBuilder();
        if ("1".equals(messageBean.getShowModule())) {        //来源 （分为活期+ 1  和基金 2）
            sb.append("基金");
            if ("1".equals(messageBean.getTradetype()) || "2".equals(messageBean.getTradetype())) { //交易类型(1普赎 2快赎 3申购)  基金没有快赎
                sb.append("卖出");
                if ("0".equals(messageBean.getTradestatus())) {
                    sb.append("受理成功");
                } else if ("1".equals(messageBean.getTradestatus())) {
                    sb.append("确认成功");
                } else if ("2".equals(messageBean.getTradestatus())) {
                    sb.append("撤单成功");
                } else if ("3".equals(messageBean.getTradestatus())) {
                    sb.append("确认失败");
                } else if ("4".equals(messageBean.getTradestatus())) {  //交易关闭
//                    sb.append("受理失败");
                }
            } else if ("3".equals(messageBean.getTradetype())) {
                sb.append("买入");
                if ("0".equals(messageBean.getTradestatus())) {
                    if ("0".equals(messageBean.getPaystatus())) {
                        sb.append("受理成功");
                    } else if ("1".equals(messageBean.getPaystatus())) {
                        sb.append("受理失败");
                    }
                } else if ("1".equals(messageBean.getTradestatus())) {
                    sb.append("确认成功");
                } else if ("2".equals(messageBean.getTradestatus())) {
                    sb.append("撤单成功");
                } else if ("3".equals(messageBean.getTradestatus())) {      ////(0待确认、1已确认、2已撤单、3异常、4交易关闭)
                    if ("0".equals(messageBean.getPaystatus())) {
                        sb.append("确认失败");
                    } else if ("1".equals(messageBean.getPaystatus())) {
                        sb.append("受理失败");
                    }
                } else if ("4".equals(messageBean.getTradestatus())) {
                    sb.append("受理失败");
                }
            }
        } else if ("2".equals(messageBean.getShowModule())) {
            sb.append("活期+");
            if ("1".equals(messageBean.getTradetype())) { //交易类型(1普赎 2快赎 3申购)
                sb.append("普通取出");
                if ("0".equals(messageBean.getTradestatus())) {
                    sb.append("受理成功");
                } else if ("1".equals(messageBean.getTradestatus())) {
                    sb.append("确认成功");
                } else if ("2".equals(messageBean.getTradestatus())) {
                    sb.append("撤单成功");
                } else if ("3".equals(messageBean.getTradestatus())) {
                    sb.append("确认失败");
                } else if ("4".equals(messageBean.getTradestatus())) {  //交易关闭
//                    sb.append("受理失败");
                }
            } else if ("2".equals(messageBean.getTradetype())) {
                sb.append("快速取出");
                if ("0".equals(messageBean.getTradestatus())) {
                    sb.append("受理成功");
                } else if ("1".equals(messageBean.getTradestatus())) {
                    sb.append("确认成功");
                } else if ("2".equals(messageBean.getTradestatus())) {
                    sb.append("撤单成功");
                } else if ("3".equals(messageBean.getTradestatus())) {
                    sb.append("确认失败");
                } else if ("4".equals(messageBean.getTradestatus())) {  //交易关闭
//                    sb.append("受理失败");
                }
            } else if ("3".equals(messageBean.getTradetype())) {
                sb.append("买入");
                if ("0".equals(messageBean.getTradestatus())) {
                    if ("0".equals(messageBean.getPaystatus())) {
                        sb.append("受理成功");
                    } else if ("1".equals(messageBean.getPaystatus())) {
                        sb.append("受理失败");
                    }
                } else if ("1".equals(messageBean.getTradestatus())) {
                    sb.append("确认成功");
                } else if ("2".equals(messageBean.getTradestatus())) {
                    sb.append("撤单成功");
                } else if ("3".equals(messageBean.getTradestatus())) {
                    if ("0".equals(messageBean.getPaystatus())) {
                        sb.append("确认失败");
                    } else if ("1".equals(messageBean.getPaystatus())) {
                        sb.append("受理失败");
                    }
                } else if ("4".equals(messageBean.getTradestatus())) {
                    sb.append("受理失败");
                }
            }
        }
        return sb.toString();
    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitleLeft;
        TextView tvTitleRight;
        TextView tvDate;
        TextView tvContent;
        TextView tvMore;

        public MessageViewHolder(View itemView) {
            super(itemView);
            tvTitleLeft = itemView.findViewById(R.id.tv_item_message_title_left);
            tvTitleRight = itemView.findViewById(R.id.tv_item_message_title_right);
            tvContent = itemView.findViewById(R.id.tv_item_message_content);
            tvDate = itemView.findViewById(R.id.tv_item_message_date);
            tvMore = itemView.findViewById(R.id.tv_item_message_showMore);
        }
    }
}
