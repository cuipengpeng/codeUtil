package com.caishi.chaoge.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {

	public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
 @Override  
    protected int computeVerticalScrollExtent() {  
        return 2;
    }  
          
    @Override  
    protected int computeVerticalScrollOffset() {  
        int sRange = super.computeVerticalScrollRange();  
        int sExtent = super.computeVerticalScrollExtent();  
        int range = sRange - sExtent;  
        if(range == 0){  
            return 0;  
        }  
        return (int) (sRange * super.computeVerticalScrollOffset() * 1.0f / range);  
    }  

}
