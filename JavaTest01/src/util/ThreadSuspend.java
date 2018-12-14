package util;

import java.util.concurrent.locks.LockSupport;

public class ThreadSuspend {

    public static void main(String[] args){
        ThreadExt threadExt = new ThreadExt();

        try {
            Thread.sleep(3000); // let thread work about 3 sec
            System.out.println("Ready to suspend the Thread "+ threadExt.toString());
            threadExt.suspendWorkflow(); // suspend the thread
            Thread.sleep(7000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Ready to resume the Thread " + threadExt.toString());
        threadExt.resumeWorkFlow(); // at last, resume the thread.
    }

    static class ThreadExt extends Thread {
        private volatile boolean isPark = false;
        private volatile boolean isWorkingAgain = false;

        public ThreadExt() {
            this.setName("Thread-ext");
            this.start();
        }

        public void suspendWorkflow() {
//            this.interrupt();
            this.isPark = true;
        }

        public void resumeWorkFlow() {
            LockSupport.unpark(this);
            this.isPark = false;
            this.isWorkingAgain = true;
        }

        @Override
        public void run() {

            final Thread currentThread = Thread.currentThread();
            while (!currentThread.isInterrupted()) {
                try {
//                	System.out.println("currentThread.isInterrupted()="+currentThread.isInterrupted());
                    while (isPark) {
                        LockSupport.park();
                    }
                    work();
                } catch (InterruptedException e) {
                    System.out.println("Exception--oh no, i was been Interrupted..");
                    continue;
                }
            }
            System.out.println("oh no, -- currentThread was been Interrupted..");
        }

        private void work() throws InterruptedException {
        	System.out.println(isWorkingAgain ? "yeh, i am working again..": "yeh, i am working..");
            Thread.sleep(1000);
        }
    }

}

