package com.test.xcamera.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Created by zll on 2019/5/13.
 */

public class ExceptionUtil {
    public static String getExceptionDetail(Exception ex) {
        String ret = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintStream pout = new PrintStream(out);
            ex.printStackTrace(pout);
            ret = new String(out.toByteArray());
            pout.close();
            out.close();
        } catch (Exception e) {
        }
        return ret;
    }
}
