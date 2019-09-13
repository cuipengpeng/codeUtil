package util;

import java.util.concurrent.locks.LockSupport;

public class ThreadSuspend {

	//suspend() 和 resume() 方法：两个方法配套使用，suspend()使得线程进入阻塞状态，并且不会自动恢复，必须其对应的resume() 被调用，才能使得线程重新进入可执行状态。
    //suspend() 和 resume() 方法阻塞时都不会释放占用的锁（如果占用了的话），而wait() 和 notify() 方法这一对方法阻塞时会释放占用的锁
    //yield() 使得线程放弃当前分得的 CPU 时间，但是不使线程阻塞，即线程仍处于可执行状态，随时可能再次分得 CPU 时间
    
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
            currentThread.suspend();
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

