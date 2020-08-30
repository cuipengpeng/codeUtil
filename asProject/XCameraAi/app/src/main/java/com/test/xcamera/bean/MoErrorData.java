package com.test.xcamera.bean;

/**
 * Created by ms on 2020/2/10.
 *
 * 固件上报 异常状态
 */

public class MoErrorData {
    public int event;
    public int status;
    public int value;
    public int battery_mode;
    public int ptz_mode;
    public int ptz_sensitivity;
    public int ptz_action;
    public int track_status;
    public int temperature;
    //TODO 测试用
    public int result;
    /**
     * 已經轉換過了
     * */
    public String size;

    @Override
    public String toString() {
        return "MoErrorData{" +
                "event=" + event +
                ", status=" + status +
                ", value=" + value +
                ", battery_mode=" + battery_mode +
                ", ptz_mode=" + ptz_mode +
                ", ptz_sensitivity=" + ptz_sensitivity +
                ", ptz_action=" + ptz_action +
                ", track_status=" + track_status +
                ", temperature=" + temperature +
                ", result=" + result +
                ", size='" + size + '\'' +
                '}';
    }
}
