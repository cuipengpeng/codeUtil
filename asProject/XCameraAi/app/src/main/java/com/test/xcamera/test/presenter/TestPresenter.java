package com.test.xcamera.test.presenter;


import com.test.xcamera.test.contract.TestContract;
import com.test.xcamera.test.model.TestModel;
import com.moxiang.common.mvp.BasePresenter;

/**
 * Created by admin on 2019/11/6.
 */

public class TestPresenter extends BasePresenter<TestContract.ITestModel,TestContract.ITestView> {


    public void getTestData(){
        mModel.getData("a","a");
    }
    @Override
    protected TestContract.ITestModel createModel() {
        return new TestModel();
    }

}
