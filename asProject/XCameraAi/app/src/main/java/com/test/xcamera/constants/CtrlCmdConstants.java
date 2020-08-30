package com.test.xcamera.constants;

/**
 * Created by zll on 2019/5/22.
 */

public class CtrlCmdConstants {
    public static final short STREAM_IO_CTRL_CMD_SEQ = 0;
    public static final short STREAM_IO_CTRL_RESP_RET = 0;
    public static final short STREAM_IO_CTRL_HEADER_SIZE = 0;
    public static final short STREAM_IO_CTRL_DATA_SIZE = 0;
    /* photo */
    public static final short STREAM_IO_CTRL_CMD_TAKE_PHOTO = 1;

    /* record */
    public static final short STREAM_IO_CTRL_CMD_START_RECORD = 4096;
    public static final short STREAM_IO_CTRL_CMD_END_RECORD = 4097;

    /* sys */
    public static final short STREAM_IO_CTRL_CMD_SET_LANGUAGE = 0x2000;
}
