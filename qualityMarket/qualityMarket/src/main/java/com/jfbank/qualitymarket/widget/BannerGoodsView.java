package com.jfbank.qualitymarket.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.flyco.banner.widget.Banner.BaseIndicatorBanner;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.model.BannerGoodsBean;
import com.jfbank.qualitymarket.model.BaseBannerBean;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * 功能：首页商品图片Banner<br>
 * 作者：zhaohai
 * 时间： 2016/11/16 0016<br>.
 * 版本：1.0.0
 */

public class BannerGoodsView extends BaseIndicatorBanner<BannerGoodsBean, BannerGoodsView> {
    private ColorDrawable colorDrawable;
    float scale = 1;
    OnItemClickL onItemClickLOne;
    ArrayList<View> rootView = new ArrayList<>();
    View oneView;
    ImageView oneImageView;

    public BannerGoodsView(Context context) {
        this(context, null, 0);
    }

    public BannerGoodsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerGoodsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, com.flyco.banner.R.styleable.BaseBanner);
        scale = ta.getFloat(com.flyco.banner.R.styleable.BaseBanner_bb_scale, -1);
        colorDrawable = new ColorDrawable(Color.parseColor("#555555"));
    }

    @Override
    public void startScroll() {
        if (this.mDatas.size() == 1) {
            if (rootView.size() == 0) {
                for (int i = 0; i < getChildCount(); i++) {
                    rootView.add(getChildAt(i));
                    rootView.get(i).setVisibility(View.GONE);
                }
                if (oneView == null) {
                    oneView = onCreateItemView(0);
                    oneImageView = (ImageView) oneView.findViewById(R.id.iv_banner);
                    this.addView(oneView);
                }
            }
            final BannerGoodsBean item = mDatas.get(0);
            if (TextUtils.isEmpty(item.getPicUrl())) {
                Picasso.with(getContext()).load(R.drawable.icon_default_product_banner).into(oneImageView);
            } else {
                Picasso.with(getContext()).load(item.getPicUrl()).placeholder(R.drawable.icon_default_product_banner).into(oneImageView);
            }
            for (int i = 0; i < rootView.size(); i++) {
                rootView.get(i).setVisibility(View.GONE);
            }
            oneView.setVisibility(View.VISIBLE);
            oneView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickLOne != null) {
                        onItemClickLOne.onItemClick(0);
                    }
                }
            });
            return;
        } else {
            for (int i = 0; i < rootView.size(); i++) {
                rootView.get(i).setVisibility(View.VISIBLE);
            }
            if (oneView != null)
                oneView.setVisibility(View.GONE);
            super.startScroll();
        }
    }

    @Override
    public void setOnItemClickL(OnItemClickL onItemClickL) {
        super.setOnItemClickL(onItemClickL);
        this.onItemClickLOne = onItemClickL;
    }

    @Override
    public View onCreateItemView(int position) {
        View inflate = View.inflate(mContext, R.layout.item_banner_view, null);
        ImageView ivBanner = (ImageView) inflate.findViewById(R.id.iv_banner);
        final BannerGoodsBean item = mDatas.get(position);
        int itemWidth = mDisplayMetrics.widthPixels;
        int itemHeight = (int) (itemWidth * scale);
        ivBanner.setScaleType(ImageView.ScaleType.FIT_XY);
        ivBanner.setLayoutParams(new LinearLayout.LayoutParams(itemWidth, itemHeight));
        if (TextUtils.isEmpty(item.getPicUrl())) {
            Picasso.with(getContext()).load(R.drawable.icon_default_product_banner).into(ivBanner);
        } else {
            Picasso.with(getContext()).load(item.getPicUrl()).placeholder(R.drawable.icon_default_product_banner).into(ivBanner);
        }

        return inflate;
    }
}
