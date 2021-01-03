package com.hospital.checkup.utils;

import com.hospital.checkup.base.BaseApplication;

public class RunningDataSingleInstance {

    private static volatile RunningDataSingleInstance mRunningDataSingleInstance;
    private RunningDataSingleInstance(){
    }

    public static synchronized RunningDataSingleInstance getInstance() {
        if (mRunningDataSingleInstance == null) {
            synchronized (RunningDataSingleInstance.class){
                if(mRunningDataSingleInstance==null){
                    mRunningDataSingleInstance = new RunningDataSingleInstance();
                }
            }
        }
        return mRunningDataSingleInstance;
    }

    private String testID;
    private String modelID;
    private String operatorID = BaseApplication.userInfo.getUserId();
    private String leftOrRight;
    private String jointAngle;
    private String nearAngle;
    private String distanceAngle;

    public String getTestID() {
        return testID;
    }

    public void setTestID(String testID) {
        this.testID = testID;
    }

    public String getModelID() {
        return modelID;
    }

    public void setModelID(String modelID) {
        this.modelID = modelID;
    }

    public String getOperatorID() {
        return operatorID;
    }

    public void setOperatorID(String operatorID) {
        this.operatorID = operatorID;
    }

    public String getLeftOrRight() {
        return leftOrRight;
    }

    public void setLeftOrRight(String leftOrRight) {
        this.leftOrRight = leftOrRight;
    }

    public String getJointAngle() {
        return jointAngle;
    }

    public void setJointAngle(String jointAngle) {
        this.jointAngle = jointAngle;
    }

    public String getNearAngle() {
        return nearAngle;
    }

    public void setNearAngle(String nearAngle) {
        this.nearAngle = nearAngle;
    }

    public String getDistanceAngle() {
        return distanceAngle;
    }

    public void setDistanceAngle(String distanceAngle) {
        this.distanceAngle = distanceAngle;
    }
}
