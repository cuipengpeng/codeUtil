package com.caishi.chaoge.ui.adapter;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.bean.NewMessageBean;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.utils.GlideUtil;
import com.caishi.chaoge.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

public class MessageDetailsAdapter extends BaseQuickAdapter<NewMessageBean, BaseViewHolder> {
    private int pageFlag;

    public MessageDetailsAdapter(int pageFlag) {
        super(R.layout.item_message_details);
        this.pageFlag = pageFlag;
    }

    @Override
    protected void convert(BaseViewHolder helper, final NewMessageBean item) {
        ImageView img_msgDetails_icon = helper.getView(R.id.img_msgDetails_icon);
        TextView tv_msgDetails_title = helper.getView(R.id.tv_msgDetails_title);
        TextView tv_msgDetails_msg = helper.getView(R.id.tv_msgDetails_msg);
        TextView tv_msgDetails_time = helper.getView(R.id.tv_msgDetails_time);
        ImageView img_msgDetails_cover = helper.getView(R.id.img_msgDetails_cover);
        final Button btn_msgDetails_attention = helper.getView(R.id.btn_msgDetails_attention);
        //显示关注按钮还是 显示封面图
        btn_msgDetails_attention.setVisibility(pageFlag == 0 ? View.VISIBLE : View.GONE);
        img_msgDetails_cover.setVisibility(pageFlag == 0 ? View.GONE : View.VISIBLE);
        switch (pageFlag) {
            case 0:
                tv_msgDetails_msg.setText("关注了你");
                tv_msgDetails_time.setText(item.createTime);
                break;

            case 1:
                tv_msgDetails_msg.setText("赞了你" + (item.type == 1 ? "作品" : "评论"));
                tv_msgDetails_time.setText(item.createTime);
                break;

            case 2:
                tv_msgDetails_msg.setText(item.content);
                String text = (item.type == 1 ? "评论" : "回复") + "了你的" + (item.type == 1 ? "作品 " : "评论 ") + item.createTime;
                tv_msgDetails_time.setText(text);
                break;

        }
        if (item.followStatus != -1) {
            btn_msgDetails_attention.setEnabled(item.followStatus == 0);
            btn_msgDetails_attention.setText(item.followStatus == 0 ? "关注" : "互相关注");
        }

        GlideUtil.loadCircleImg(item.avatar, img_msgDetails_icon);
        GlideUtil.loadRoundImg(Utils.isUrl(item.poster), img_msgDetails_cover);
        String title = item.nickname.length() > 8 ? item.nickname.substring(0, 8) + "..." : item.nickname;
        tv_msgDetails_title.setText(title);
        btn_msgDetails_attention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> paramsMap = new HashMap<>();
                paramsMap.put("userId", item.userId);
                paramsMap.put("status", "1");

                HttpRequest.post(false, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.FOLLOW, paramsMap, new HttpRequest.HttpResponseCallBank() {
                    @Override
                    public void onSuccess(String response) {
                        btn_msgDetails_attention.setEnabled(false);
                        btn_msgDetails_attention.setText("互相关注");
                    }

                    @Override
                    public void onFailure(String t) {
                    }
                });

            }
        });


    }

}