package algorithm;

public class QuickSort {
	
	public static void main(String[] args) {
//		int[] arr = new int[]{4,1,5,12,7,13,2,11,6,3,8,50,9};
		int[] arr = new int[]{5,4,3,2,1,6,4,3,2,1,12,7,13,2,11,6,3,8,50,9};
		for(int i=0;i<=arr.length-1;i++){
			System.out.print("--"+arr[i]);
		}	
		System.out.println("\n###############");
	    quikSort(arr,0,arr.length-1);    
//	    quikSort(arr,3, 9);    
		
		for(int i=0;i<=arr.length-1;i++){
			System.out.print("--"+arr[i]);
		}
	}

	//low:�������߽�ֵ����ʼΪ0
	//high:������ұ߽�ֵ����ʼΪlength-1
	//https://blog.csdn.net/troubleshooter/article/details/80421861
	//https://blog.csdn.net/china77536816/article/details/44966405
	public static void quikSort(int arr[], int lowPosition, int highPosition){
	    if(lowPosition>=highPosition){   
	        return;
	    }

	    int lowBaseValue = arr[lowPosition];
	    int swapMiddlePosition=lowPosition;
	    for(int j=lowPosition+1;j<=highPosition;j++){
	        if(arr[j]<lowBaseValue){        //a[j] is smaller than pivot
	            swapMiddlePosition++;    
	            //System.out.println("swapMiddlePosition="+swapMiddlePosition+"--j="+j);
	            //System.out.println("swapMiddlePositionValue="+arr[swapMiddlePosition]+"--jValue="+arr[j]);
	            if(swapMiddlePosition!=j){
	     	       //Swap pivot to middle position
	         	   int temp = arr[swapMiddlePosition];
	         	   arr[swapMiddlePosition] = arr[j];
	         	   arr[j] = temp;
	            }

	        }
	    }
	       //Swap pivot to middle position
		   int temp2 = arr[lowPosition];
		   arr[lowPosition] = arr[swapMiddlePosition];
		   arr[swapMiddlePosition] = temp2;
		   
	    quikSort(arr,lowPosition,swapMiddlePosition-1);       
	    quikSort(arr,swapMiddlePosition+1,highPosition);
	}
}
