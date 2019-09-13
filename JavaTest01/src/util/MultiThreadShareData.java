package util;

public class MultiThreadShareData {
	
	public static void main(String[] args) {
	        final ShareData1 shareData1 = new ShareData1(0, "qqqqqq");
	        new Thread(new MyRunnable1(shareData1)).start();
	        new Thread(new MyRunnable2(shareData1)).start();
	}
	
	static class MyRunnable1 implements Runnable{
		public static final String TAG="MyRunnable-1";
	        private ShareData1 shareData1;
	        public void run() {
	        	System.out.println(TAG+"--mCount="+shareData1.mCount+"--"+shareData1.mName);
	        	shareData1.mCount = shareData1.mCount+1;
	        	shareData1.mName = "name-1";
	        	System.out.println(TAG+"--mCount="+shareData1.mCount+"--"+shareData1.mName);

	        }
	        public MyRunnable1(ShareData1 shareData1){
	            this.shareData1 = shareData1;
	        }
	    }
	
	static class MyRunnable2 implements Runnable{
		public static final String TAG="MyRunnable-2";
	        private ShareData1 shareData1;
	        public void run() {
	        	System.out.println(TAG+"--mCount="+shareData1.mCount+"--"+shareData1.mName);
	        	shareData1.mCount = shareData1.mCount+1;
	        	shareData1.mName = "name-2";
	        	System.out.println(TAG+"--mCount="+shareData1.mCount+"--"+shareData1.mName);

	        }
	        public MyRunnable2(ShareData1 shareData1){
	            this.shareData1 = shareData1;
	        }
	    }
	
	static class ShareData1 {
		int mCount=0;
		String mName="abc";

		public ShareData1(int count, String name) {
			this.mCount = count;
			this.mName = name;
		}
		
		public void get() {
			synchronized (this) {
				
			}
		}
		public void set() {
			synchronized (this) {
				
			}
		}
	}
	
//	从线程中获取返回数据的三种方法
//	1、通过回调函数获取返回数据
//	2、thread.join()
//	3、实现 Callable<V>接口，通过Future.get()获取返回数据。其中 V 代表 返回值类型
//	https://blog.csdn.net/qq_18505715/article/details/78726164

}
