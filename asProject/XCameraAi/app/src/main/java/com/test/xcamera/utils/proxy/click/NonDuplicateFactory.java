package com.test.xcamera.utils.proxy.click;


import com.test.xcamera.utils.proxy.NonDuplicateClickUtils;
import com.test.xcamera.utils.proxy.Perform;


public class NonDuplicateFactory {
    public static void proxy(Perform perform) {
        if(!NonDuplicateClickUtils.isDuplicateClick()){
            perform.perform();
        }
//        perform.perform();
    }
}
