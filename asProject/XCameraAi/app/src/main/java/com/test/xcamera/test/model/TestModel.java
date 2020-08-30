package com.test.xcamera.test.model;
import android.database.Observable;

import com.test.xcamera.test.contract.TestContract;

import com.test.xcamera.test.entity.TestBean;
import com.moxiang.common.mvp.BaseModel;

import java.util.List;


/**
 * Created by admin on 2019/11/6.
 */

public class TestModel extends BaseModel implements TestContract.ITestModel {


    @Override
    public void onDestroy() {

    }

    @Override
    public Observable<List<TestBean>> getData(String param1, String param2) {
        return null;
    }
}
