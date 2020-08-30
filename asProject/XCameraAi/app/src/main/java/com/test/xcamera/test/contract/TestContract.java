package com.test.xcamera.test.contract;


import android.database.Observable;

import com.test.xcamera.test.entity.TestBean;
import com.moxiang.common.mvp.IModel;
import com.moxiang.common.mvp.IView;

import java.util.List;


/**
 * Created by admin on 2019/11/6.
 */

public interface TestContract {
    interface ITestView extends IView{

    }
    interface ITestModel extends IModel{
        Observable<List<TestBean>> getData(String param1, String param2);
    }
}
