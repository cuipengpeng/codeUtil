package algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Combination{
	private static int minSortLength=2;
	private static int maxSortLength=4;
	
	private static String allStr = "0123456";
	private static int currentSortLength=minSortLength;
	private static int[] columnValueForStrPosition = new int[currentSortLength];
	
	private static boolean singleSortLengthFinish=false;//单次的特定长度的所有可能性是否完成
	
	private static int allKindCount=1;
	public static void main(String[] args) throws Exception {
//		File file = new File("D:\\test01.txt");
//		FileInputStream input= new FileInputStream(file); 
//		InputStreamReader inputStreamReader = new InputStreamReader(input);
//		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//		String mima= bufferedReader.readLine();
		
		
		for(int k=minSortLength; k<=maxSortLength;k++){
			currentSortLength = k;
			columnValueForStrPosition = new int[currentSortLength];
			singleSortLengthFinish = false;
	
			//打印指定长度的所有可能性
			for(int i = 0; i<columnValueForStrPosition.length; i++){
				columnValueForStrPosition[i] = 0;
			}

			while (columnValueForStrPosition[currentSortLength-1]<=allStr.length() && !singleSortLengthFinish){
				
				//个位循环一遍并打印
				for(int i=0; i<allStr.length(); i++){
					
					StringBuilder stringBuilder= new StringBuilder();
					for(int j = 0; j<columnValueForStrPosition.length; j++){
						stringBuilder.append(allStr.charAt(columnValueForStrPosition[j]));
					}				
					System.out.println("count="+allKindCount+"---------value="+stringBuilder.toString());
					
					int columnPosition = 0;
					columnValueForStrPosition[columnPosition]++;///个位数数，from 1 to allStr.length()
					allKindCount++;
					boolean  cycleAdd = true;
					
					while(columnPosition<currentSortLength && cycleAdd && !singleSortLengthFinish){
//						if(columnValueForStrPosition[columnPosition]<allStr.length()){
//							cycleAdd = false;
//						}
						
	//					System.out.println("ttttttt");
						//满足进位
						if(columnValueForStrPosition[columnPosition]>=allStr.length() && columnPosition<currentSortLength){
							columnValueForStrPosition[columnPosition] = 0;//满足进位，低位清零，重新开始计数
							columnPosition++;//位置移动到高位
							System.out.println("columnPosition---------"+columnPosition);
							if(columnPosition>=currentSortLength){
								singleSortLengthFinish = true;
	//							return;
							}
							if(columnPosition<currentSortLength){
								columnValueForStrPosition[columnPosition]++;//低位满足进位，高位数+1
								//判断高位是否满足进位
								if(columnValueForStrPosition[columnPosition]<allStr.length()){
									cycleAdd = false;
								}
							}
						}else{
							cycleAdd = false;
						}
					}

					
//					StringBuilder builder01= new StringBuilder();
//					for(int j = 0; j<columnValueForStrPosition.length; j++){
//						builder01.append(allStr.charAt(columnValueForStrPosition[j]));
//					}				
//					System.out.println("count="+allKindCount+"---------value="+builder01.toString());
					
	//				if(builder01.toString().equals(mima)){
	//					System.out.println("密码是："+builder01.toString());
	//					return;
	//				}
					
					if(singleSortLengthFinish){
						break;
					}
					
				}
			}
		}
	}
	
}
