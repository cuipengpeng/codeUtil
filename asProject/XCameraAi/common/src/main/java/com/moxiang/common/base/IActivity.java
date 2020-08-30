package com.moxiang.common.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by admin on 2019/10/15.
 */

public interface IActivity {
    int bindActivityLayout();
    void initData(@Nullable Bundle savedInstanceState);
    void initView(@Nullable Bundle savedInstanceState);
}
