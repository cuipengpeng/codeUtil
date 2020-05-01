package com.android.player.utils.algorithm;

public class QuickSort {
	
	public static void main(String[] args) {
//		int[] arr = new int[]{4,1,5,12,7,13,2,11,6,3,8,50,9};
		int[] arr = new int[]{5,4,3,2,1,6,4,3,2,1,12,7,13,2,11,6,3,8,50,9};
		for(int i=0;i<=arr.length-1;i++){
			System.out.println("--"+arr[i]);
		}	
		System.out.println("###############");
	    quikSort(arr,0,arr.length-1);    
//	    quikSort(arr,3, 9);    
		
		for(int i=0;i<=arr.length-1;i++){
			System.out.println("--"+arr[i]);
		}
	}

	//low:�������߽�ֵ����ʼΪ0
	//high:������ұ߽�ֵ����ʼΪlength-1
	public static void quikSort(int arr[], int lowPosition, int highPosition){
	    if(lowPosition>=highPosition){    //�ݹ��˳�������ֻ��һ��Ԫ��ʱ
	        return;
	    }

	    int lowBaseValue = arr[lowPosition];
	    int tmpLowPosition=lowPosition;
	    for(int j=lowPosition+1;j<=highPosition;j++){
	        if(arr[j]<=lowBaseValue){        //a[j] is smaller than pivot
	            tmpLowPosition++;    //a[i] is bigger than pivot
	            System.out.println("tmpLowPosition="+tmpLowPosition+"--j="+j);
	            System.out.println("tmpLowPositionValue="+arr[tmpLowPosition]+"--jValue="+arr[j]);
	            if(tmpLowPosition!=j){
	     	       //Swap pivot to middle position
	         	   int temp = arr[tmpLowPosition];
	         	   arr[tmpLowPosition] = arr[j];
	         	   arr[j] = temp;
	            }

	        }
	    }
	       //Swap pivot to middle position
		   int temp2 = arr[lowPosition];
		   arr[lowPosition] = arr[tmpLowPosition];
		   arr[tmpLowPosition] = temp2;
		   
	    //���зֻ�(partition),�ݹ�
	    quikSort(arr,lowPosition,tmpLowPosition-1);        //a[i] is the pivot now
	    quikSort(arr,tmpLowPosition+1,highPosition);
	}
}
