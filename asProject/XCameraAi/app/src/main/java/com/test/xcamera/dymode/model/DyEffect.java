package com.test.xcamera.dymode.model;

import com.ss.android.ugc.effectmanager.effect.model.Effect;

import java.io.Serializable;

/**
 * Created by zll on 2020/2/25.
 */

public class DyEffect implements Serializable{
    private Effect effect;
    private boolean isSelected = false;

    public Effect getEffect() {
        return effect;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
