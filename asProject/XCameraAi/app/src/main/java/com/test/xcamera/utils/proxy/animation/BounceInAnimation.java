package com.test.xcamera.utils.proxy.animation;

import android.view.View;


/**
 * Created by liutao on 10/19/16.
 */
public class BounceInAnimation {

    public interface OnAnimationEndListener {
        void onAnimationEnd();
    }

    public static void bounceIn(View view, final OnAnimationEndListener listener) {
//        ViewAnimator.animate(view)
//                .scale(1.2f, 1f)
//                .duration(200)
//                .interpolator(new AccelerateDecelerateInterpolator())
//                .onStop(new Animation.AnimationListener.Stop() {
//                    @Override
//                    public void onStop() {
//                        if (listener != null) {
//                            listener.onAnimationEnd();
//                        }
//                    }
//                })
//                .start();
    }
}
