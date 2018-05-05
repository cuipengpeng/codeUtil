package com.test.bank.weight.holder;

import android.view.View;
import android.widget.ImageView;

import com.test.bank.R;

/**
 * Created by 55 on 2018/1/17.
 * 基金持仓页【认购期】
 */

public class FundPosCloseHolder extends BaseHolder<Integer> {

    View vStep1;
    View vStep2;
    View vStep3;
    View vStep4;

    ImageView ivStep1;
    ImageView ivStep2;
    ImageView ivStep3;
    ImageView ivStep4;

    @Override
    protected void initView(View rootView) {
        vStep1 = rootView.findViewById(R.id.v_fps_step1);
        vStep2 = rootView.findViewById(R.id.v_fps_step2);
        vStep3 = rootView.findViewById(R.id.v_fps_step3);
        vStep4 = rootView.findViewById(R.id.v_fps_step4);

        ivStep1 = rootView.findViewById(R.id.iv_fps_step1);
        ivStep2 = rootView.findViewById(R.id.iv_fps_step2);
        ivStep3 = rootView.findViewById(R.id.iv_fps_step3);
        ivStep4 = rootView.findViewById(R.id.iv_fps_step4);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.holder_fundpos_subscription;
    }

    @Override
    protected void updateView() {

        vStep1.setSelected(data >= 1);
        vStep2.setSelected(data >= 2);
        vStep3.setSelected(data >= 3);
        vStep4.setSelected(data >= 4);

        ivStep1.setSelected(data >= 1);
        ivStep2.setSelected(data >= 2);
        ivStep3.setSelected(data >= 3);
        ivStep4.setSelected(data >= 4);
    }
}
