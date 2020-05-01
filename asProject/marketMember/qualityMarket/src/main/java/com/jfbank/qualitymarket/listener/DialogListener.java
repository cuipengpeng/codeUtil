package com.jfbank.qualitymarket.listener;

/**
 * 功能：<br>
 * 作者：赵海<br>
 * 时间： 2017/4/24 0024<br>.
 * 版本：1.2.0
 */

public class DialogListener {
    /**
     * 功能：<br>
     * 作者：赵海<br>
     * 时间： 2017/4/21 0021<br>.
     * 版本：1.2.0
     */

    public  interface DialogClickLisenter {
        int CLICK_CANCEL = 0;
        int CLICK_SURE = 1;

        void onDialogClick(int type);
    }
    public  interface DialogItemLisenter {
        void onDialogClick(int position);
    }
}
