package com.test.xcamera.home.adapter;

import android.support.v4.view.ViewPager;
import android.view.View;

public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MAX_SCALE = 1f;
        private static final float MIN_SCALE = 0.8f;//0.85f
 
        @Override
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();
            float scaleFactor;
            if (position < -1) {
                scaleFactor = MIN_SCALE;
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
            } else if (position <= 1) {
                //a页滑动至b页 ； a页从 0.0 -1 ；b页从1 ~ 0.0
                // [-1,1]
//              Log.e("TAG", view + " , " + position + "");
                scaleFactor = MIN_SCALE + (1 - Math.abs(position)) * (MAX_SCALE - MIN_SCALE);
                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
            } else { // (1,+Infinity]
                scaleFactor = MIN_SCALE;
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
            }
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2);
            }
        }
 
    }