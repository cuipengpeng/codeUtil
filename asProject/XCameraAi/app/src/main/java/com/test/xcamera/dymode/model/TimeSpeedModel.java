package com.test.xcamera.dymode.model;

import java.util.List;

public class TimeSpeedModel {
    long duration;
    double speed;

    public TimeSpeedModel(long duration, double speed) {
        this.duration = duration;
        this.speed = speed;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public static long calculateRealTime(List<TimeSpeedModel> mDurings) {
        if (mDurings == null || mDurings.size() <= 0) return 0;
        int total = 0;
        for (TimeSpeedModel timeSpeedModel : mDurings) {
            total += (calculateRealTime(timeSpeedModel.duration, timeSpeedModel.speed));
        }
        return total;
    }

    public static long calculateRealTime(long duration, double speed) {
        return (long) (1.0 * duration / speed);
    }

    public static int calculateRealTime(int duration, double speed) {
        return (int) (1.0 * duration / speed);
    }
}
