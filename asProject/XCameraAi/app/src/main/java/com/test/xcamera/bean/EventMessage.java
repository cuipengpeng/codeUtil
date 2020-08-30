package com.test.xcamera.bean;

/**
 * Created by smz on 2019/12/23.
 * <p>
 * eventbus 事件类
 */

public class EventMessage {
    public final static int HIDE_PARAMS_VIEW = 0;
    //单击屏幕 处理一些弹框的隐藏
    public final static int SCREEN_CLICKED = 1;
    public final static int PROFESSION_SHOW = 10;
    //隐藏更多设置
    public final static int PROFESSION_HIDE = 11;

    //云台模式 控制是否可以跟踪
    public final static int ENABLE_TRACING = 12;

    /**
     * 重置zoom的UI
     * */
    public final static int RESET_ZOOM = 100;

    /**
     * 设置页面 显示总设置页面
     */
    public static final int SHOW_SETTING_VIEW = 12;

    public int code;
    public String msg;
    public Object data;
    public int extra;

    public EventMessage(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public EventMessage(int code) {
        this(code, null, null);
    }

    public EventMessage(int code, int extra) {
        this.code = code;
        this.extra = extra;
    }
}
