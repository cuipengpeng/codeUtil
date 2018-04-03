package com.jf.jlfund.weight;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.http.HttpConfig;
import com.jf.jlfund.view.activity.SimpleH5Activity;

/**
 * Created by 55 on 2017/12/6.
 */

public class CommonBottomDesc extends LinearLayout {

    TextView tvDetail;

    public CommonBottomDesc(Context context) {
        super(context);
        init(context);
    }

    public CommonBottomDesc(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CommonBottomDesc(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(final Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_bottom_desc, this, true);
        tvDetail = rootView.findViewById(R.id.tv_commonbottom_detail);
        tvDetail.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvDetail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleH5Activity.open(context, HttpConfig.BOTTOM_DETAIL_URL, "金牛理财");
            }
        });
    }

}
