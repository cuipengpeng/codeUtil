package com.caishi.chaoge.ui.widget.dialog.manager;


import com.caishi.chaoge.ui.widget.dialog.SYDialog;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 支持多个Dialog依次弹出
 */

public class DialogsManager {

    private volatile boolean showing = false;//是否有dialog在展示
    private ConcurrentLinkedQueue<DialogWrapper> dialogQueue = new ConcurrentLinkedQueue<>();

    private DialogsManager() {
    }

    public static DialogsManager getInstance() {
        return DialogHolder.instance;
    }

    private static class DialogHolder {
        private static DialogsManager instance = new DialogsManager();
    }

    /**
     * 请求加入队列并展示
     *
     * @param dialogWrapper DialogWrapper
     * @return 加入队列是否成功
     */
    public synchronized boolean requestShow(DialogWrapper dialogWrapper) {
        boolean b = dialogQueue.offer(dialogWrapper);
        checkAndDispatch();
        return b;
    }

    /**
     * 结束一次展示 并且检查下一个弹窗
     */
    public synchronized void over() {
        showing = false;
        next();
    }

    private synchronized void checkAndDispatch() {
        if (!showing) {
            next();
        }
    }

    /**
     * 弹出下一个弹窗
     */
    private synchronized void next() {
        DialogWrapper poll = dialogQueue.poll();
        if (poll == null) return;
        SYDialog.Builder dialog = poll.getDialog();
        if (dialog != null) {
            showing = true;
            dialog.show();
        }
    }


}
