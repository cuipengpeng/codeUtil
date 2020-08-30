package com.test.xcamera.phonealbum.player.mvp;

import android.widget.RelativeLayout;

import com.test.xcamera.phonealbum.widget.subtitle.bean.Mark;

import java.util.List;


public interface VideoPlayContract {
    interface View  {
        void doLiveWindowRatio(RelativeLayout.LayoutParams layoutParams);
        void showRunes(List<? extends Mark> properties,long time);

        /** 显示贴纸 */
        void showEffectView(Mark info, long time, boolean locked);
    }

    abstract class Presenter {
        public abstract void setLiveWindowRatio(RelativeLayout.LayoutParams layoutParams,int ratio, int width, int height);
        /**处理贴纸数据*/
        public abstract void setSticker( long time,boolean locked);


    }

}
