package com.test.xcamera.home;

import com.framwork.base.view.BaseViewInterface;
import com.test.xcamera.widget.UploadDialog;

/**
 * Created by smz on 2019/11/10.
 */

public interface HomeViewInterface extends BaseViewInterface {

    void IsVisbilityUploadHardWare();

    void setProgresss(int progress);

    void setTitleAndContent(String title, String Content);

    void setVisable(int Visable1, int Visable2);

    UploadDialog getDialog();

    void setContent(int size, long time, String description, String version);

    void StartAct(Class clazz);

    void isClickable();
}
