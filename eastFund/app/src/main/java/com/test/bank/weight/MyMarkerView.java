package com.test.bank.weight;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.test.bank.R;
import com.test.bank.utils.StringUtil;

/**
*自定义MyMarkerView
*/
public class MyMarkerView extends MarkerView {

    private TextView tvContent;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent= (TextView) findViewById(R.id.tvContent);
    }
    
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (e instanceof CandleEntry) {
            CandleEntry ce = (CandleEntry) e;
            tvContent.setText("111" + Utils.formatNumber(ce.getHigh(), 0, true));
        } else {
//            tvContent.setText("" + Utils.formatNumber(e.getY(), 0, true));
            tvContent.setText(StringUtil.moneyDecimalFormat4(e.getY()+""));
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}