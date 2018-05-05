package com.test.bank.utils;

import android.content.Context;
import android.content.Intent;

import com.test.bank.view.activity.SingleFundDetailActivity;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/12/26<br>
*/
public class CommonUtil {

    public static void startActivity(Context context, Class<?> cls, String fundCode){
        Intent intent = new Intent(context, cls);
        intent.putExtra(SingleFundDetailActivity.KEY_OF_FUND_CODE, fundCode);
        context.startActivity(intent);
    }

}
