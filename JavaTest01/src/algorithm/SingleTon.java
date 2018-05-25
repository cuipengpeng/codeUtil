package algorithm;

public class SingleTon {
	private static volatile SingleTon s;
	private SingleTon(){};
	
	public static SingleTon getInstance(){
		if(s==null){
			synchronized(SingleTon.class){
				if(s==null){
					s=new SingleTon();
				}
			}
		}
		
		return s;
	}
	
}
