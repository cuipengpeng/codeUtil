package com.jfbank.qualitymarket.callback;

/**
 * 功能：IBillPendingCallBack<br>
 * 作者：赵海<br>
 * 时间： 2016/12/30 0030<br>.
 * 版本：1.2.0
 */

public class IBillPendingCallBack {
    /**
     * 支付回调选择判断
     */
    public interface IAdapterCallBack {
        /**
         * 提前还款支付回调显示
         * @param isshowPay 是否显示支付按钮
         * @param isChecked 是否全选
         * @param money  支付金额
         */
        void onShowPay(boolean isshowPay, boolean isChecked, float money);
    }

    /**
     * 支付成功后回调
     */
    public interface IPayCallBack {
        /**
         * 还款支付结果判断
         * @param payResult 支付结果状态码
         * @param msg 支付结果信息描述
         */
        void onPayResult( int payResult, String msg);
    }
}
