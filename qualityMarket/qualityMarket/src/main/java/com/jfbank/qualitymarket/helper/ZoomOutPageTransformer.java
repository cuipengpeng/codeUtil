package com.jfbank.qualitymarket.helper;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

public class ZoomOutPageTransformer implements PageTransformer {
	private static float MIN_SCALE = 0.85f;

	private static float MIN_ALPHA = 0.5f;

	@Override
	public void transformPage(View view, float position) {
//		int pageWidth = view.getWidth();
//		int pageHeight = view.getHeight();

		if (position < -1) { // [-Infinity,-1)
								// This page is way off-screen to the left.
			view.setAlpha(0);
			view.setTranslationX(0);
		} else if (position <= 1) { // [-1,1]
//									// Modify the default slide transition to
//									// shrink the page as well
			float scaleFactor = Math.max(MIN_ALPHA, 1 - Math.abs(position));
//			float vertMargin = pageHeight * (1 - scaleFactor) / 2;
//			float horzMargin = pageWidth * (1 - scaleFactor) / 2;
//			if (position < 0) {
//				view.setTranslationX(horzMargin - vertMargin / 2);
//			} else {
//				view.setTranslationX(-horzMargin + vertMargin / 2);
//			}
			// Scale the page down (between MIN_SCALE and 1)
			// Fade the page relative to its size.
			view.setAlpha(scaleFactor);
		} else { // (1,+Infinity]
					// This page is way off-screen to the right.
			view.setAlpha(0);
			view.setTranslationX(0);
		}
	}
}