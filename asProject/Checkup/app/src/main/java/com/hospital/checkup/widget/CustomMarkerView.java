package com.hospital.checkup.widget;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.hospital.checkup.R;

public class CustomMarkerView extends MarkerView {

    private TextView tvContent;

    public CustomMarkerView (Context context, int layoutResource) {
        super(context, layoutResource);
        // this markerview only displays a textview
        tvContent = (TextView) findViewById(R.id.tv_markerView_content);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if(highlight.getDataSetIndex()==0){
            tvContent.setText("角度"+((int)e.getY())+"°"); // set the entry-value as the display text
        }else if(highlight.getDataSetIndex()==1){
            tvContent.setText("速度"+((int)e.getY())+"°"); // set the entry-value as the display text
        }else if(highlight.getDataSetIndex()==2){
            tvContent.setText("交互力"+((int)e.getY())+"°"); // set the entry-value as the display text
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}