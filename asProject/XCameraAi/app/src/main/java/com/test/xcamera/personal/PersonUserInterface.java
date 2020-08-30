package com.test.xcamera.personal;


import com.framwork.base.view.BaseViewInterface;
import com.test.xcamera.bean.User;

public interface PersonUserInterface extends BaseViewInterface {
    /**
     * 获取修改信息
     * @return
     */
     User.UserDetail getUserMassage();

    /**
     * 编辑请求回调
     * @param msg
     */
     void editUserCallBack(String msg);

    /**
     * token 过期
     * @param msg
     */
     void unTokenCallBack(String msg);

}
