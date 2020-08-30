package com.test.xcamera.view;

import android.content.Context;
import android.graphics.Color;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.test.xcamera.R;
import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * author zxc
 * createTime 2019/10/12  16:21
 * 展示照片和视频的详细信息
 */
public class InformationsPopWindow implements PopupWindow.OnDismissListener {
    private SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
    private SimpleDateFormat formatH = new SimpleDateFormat("HH:mm:ss");
    private final Context context;
    private final View animationView;
    private PopupWindow popupWindow;
    private final LinearLayout popWindowlayout;
    private String conStr;
    private final Animation animation_up;
    private final Animation animation_down;

    public InformationsPopWindow(Context context, View animationView) {
        this.context = context;
        this.animationView = animationView;
        View view = View.inflate(context, R.layout.information_pop, null);
        popupWindow = new PopupWindow(context);
        popupWindow.setContentView(view);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(this);
        popWindowlayout = view.findViewById(R.id.popWindowlayout);

        animation_up = AnimationUtils.loadAnimation(context, R.anim.information_animation_up);
        animation_down = AnimationUtils.loadAnimation(context, R.anim.information_animation_down);
    }

    /**
     * 点击大图的标题进行显示
     *
     * @param view
     * @param moAlbumItem
     */
    public void show(View view, MoAlbumItem moAlbumItem) {
        if (popupWindow.isShowing()) {
            return;
        }
        popupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        view.measure(0, 0);
        int measuredWidth = (view.getMeasuredWidth() / 2) + 90;
        popupWindow.showAsDropDown(view, -measuredWidth, 100);

        addPopView(moAlbumItem);

        animation();

    }


    private void animation() {
        if (animationView != null) {
            animationView.startAnimation(animation_up);
        }
    }

    /**
     * 向pop中添加子布局
     *
     * @param moAlbumItem
     */
    private void addPopView(MoAlbumItem moAlbumItem) {
        conStr = "";
        if (popWindowlayout.getChildCount() > 0) {
            popWindowlayout.removeAllViews();
        }
        TextView information = new TextView(context);
        information.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        information.setGravity(Gravity.CENTER);
        information.setTextColor(Color.WHITE);
        information.setHeight(100);
        information.setText("Informations");
        popWindowlayout.addView(information);

        TextView view = new TextView(context);
        view.setHeight(3);
        view.setBackgroundColor(Color.parseColor("#1E1E1E"));
        popWindowlayout.addView(view);

        conStr = conStr +
                "       Type:                   " + moAlbumItem.getmType() + "\n\n" +
                "       Date:                   " + format.format(new Date(moAlbumItem.getmCreateTime())) + "\n\n" +
                "       Time:                   " + formatH.format(new Date(moAlbumItem.getmCreateTime())) + "\n\n";

        if (moAlbumItem.getmType().equals("video") || moAlbumItem.getmType().equals("slowmotionvideo") || moAlbumItem.getmType().equals("lapsevideo")) {
            conStr = conStr +
                    "       Size:                      " + Formatter.formatFileSize(context, moAlbumItem.getmVideo().getmSize()) + "\n\n" +
                    "       Duration:              " + DateUtils.stringForTime(moAlbumItem.getmVideo().getmDuration()) + "\n\n" +
                    "       Resolution:          " + moAlbumItem.getmVideo().getmWidth() + " * " + moAlbumItem.getmVideo().getmHeight() + "\n\n" +
                    "       FPS:                       " + moAlbumItem.getmVideo().getFps() + "FPS" + "\n\n";
        } else {
            conStr = conStr +
                    "       Shutter:                " + moAlbumItem.getmImage().getShutter() + "\n\n" +
                    "       Resolution:          " + moAlbumItem.getmImage().getmWidth() + " * " + moAlbumItem.getmImage().getmHeight() + "\n\n" +
                    "       Iso:                        " + moAlbumItem.getmImage().getIso() + "\n\n" +
                    "       EV:                         " + moAlbumItem.getmImage().getEv() + "\n\n" +
                    "       AWB:                        " + moAlbumItem.getmImage().getAwb();

        }
        TextView content = new TextView(context);
        content.setPadding(50, 0, 50, 0);
        content.setTextColor(Color.WHITE);
        content.setText(conStr);

        popWindowlayout.addView(content);
    }

    /**
     * 在大图预览的时候进行页面切换
     *
     * @param titleLayout
     * @param moAlbumItem
     */
    public void showPageSelect(LinearLayout titleLayout, MoAlbumItem moAlbumItem) {
        if (!popupWindow.isShowing()) {
            return;
        }
        titleLayout.measure(0, 0);
        int measuredWidth = titleLayout.getMeasuredWidth();
        int x = (popupWindow.getWidth() - measuredWidth) / 2;
        popupWindow.showAsDropDown(titleLayout, -x, 100);

        addPopView(moAlbumItem);
    }

    @Override
    public void onDismiss() {
        if (animationView != null) {
            animationView.startAnimation(animation_down);
        }
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
}
