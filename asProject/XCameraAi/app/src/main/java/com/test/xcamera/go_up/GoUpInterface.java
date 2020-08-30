package com.test.xcamera.go_up;

import com.test.xcamera.bean.CommunityBean;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/7/22
 * e-mail zhouxuecheng1991@163.com
 * <p>
 * 统一的接口管理类,把goup页面相关的接口都放在这个里面定义
 */

public class GoUpInterface {
    /**
     * 这个是从M层给P层回调的接口
     */
    public interface ModelToPresenter {

        /**
         * 社区帮助
         *
         * @param communityBean
         */
        public void communityHelp(CommunityBean communityBean);

        /**
         * 统一接口出错回调
         *
         * @param errorInfo
         */
        public void interfaceError(String errorInfo);
    }

    /**
     * 这个是P层给view层回调的接口
     */
    public interface PresenterToView {
        /**
         * 社区入口icon是否显示
         *
         * @param isHide
         * @param communityBean
         */
        public void communityIconIsHide(boolean isHide, CommunityBean communityBean);

        /**
         * 展示错误页面或者是空页面
         *
         * @param errorInfo
         */
        public void showErrorView(String errorInfo);
    }
}
