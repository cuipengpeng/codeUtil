package algorithm;

public class Combination{
	private static String allStr = "0123456789";
	private static int sortLength=4;
	
	private static int allKindCount=1;
	private static int[] columnValueForStrPosition = new int[sortLength];
	
	public static void main(String[] args) {
		for(int i = 0; i<columnValueForStrPosition.length; i++){
			columnValueForStrPosition[i] = 0;
		}
		StringBuilder stringBuilder= new StringBuilder();
		for(int j = 0; j<columnValueForStrPosition.length; j++){
			stringBuilder.append(allStr.charAt(columnValueForStrPosition[j]));
		}				
		System.out.println("count="+allKindCount+"---------value="+stringBuilder.toString());
		
		while (columnValueForStrPosition[sortLength-1]<=allStr.length()){
			for(int i=0; i<allStr.length(); i++){
				int columnPosition = 0;
				columnValueForStrPosition[columnPosition]++;///数数
				allKindCount++;
				boolean  cycleAdd = true;
				while(columnPosition<columnValueForStrPosition.length && cycleAdd){
					if(columnValueForStrPosition[columnPosition]<allStr.length()){
						cycleAdd = false;
					}
					
//					System.out.println("ttttttt");
					if(columnValueForStrPosition[columnPosition]>=allStr.length() && columnPosition< columnValueForStrPosition.length){
						columnValueForStrPosition[columnPosition] = 0;
						columnPosition++;
						System.out.println("basePosition---------"+columnPosition);
						if(columnPosition>= columnValueForStrPosition.length){
							return;
						}
						
						columnValueForStrPosition[columnPosition]++;
						
						if(columnValueForStrPosition[columnPosition]<allStr.length()){
							cycleAdd = false;
						}
					}
				}
				
				StringBuilder builder01= new StringBuilder();
				for(int j = 0; j<columnValueForStrPosition.length; j++){
					builder01.append(allStr.charAt(columnValueForStrPosition[j]));
				}				
				System.out.println("count="+allKindCount+"---------value="+builder01.toString());
			}
		}
	}
	
}
