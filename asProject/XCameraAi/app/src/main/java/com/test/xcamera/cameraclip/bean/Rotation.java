package com.test.xcamera.cameraclip.bean;

import java.io.Serializable;

public class Rotation implements Serializable {
    private float angle;
    private long timestamp;

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Rotation clone(){
        Rotation newRotation = new Rotation();
        newRotation.setAngle(this.getAngle());
        newRotation.setTimestamp(this.getTimestamp());
        return newRotation;
    }

}
