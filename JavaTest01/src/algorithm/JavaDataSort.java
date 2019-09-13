package algorithm;

import java.util.Arrays;


/**
 * java 8 �������㷨
 * https://www.jianshu.com/p/8c915179fd02
 *
 */
public class JavaDataSort {
	 /*
	 * ð������
	 * �Ƚ����ڵ�Ԫ�ء������һ���ȵڶ����󣬾ͽ�������������  
	 * ��ÿһ������Ԫ����ͬ���Ĺ������ӿ�ʼ��һ�Ե���β�����һ�ԡ�����һ�㣬����Ԫ��Ӧ�û�����������  
	 * ������е�Ԫ���ظ����ϵĲ��裬�������һ����
	 * ����ÿ�ζ�Խ��Խ�ٵ�Ԫ���ظ�����Ĳ��裬ֱ��û���κ�һ��������Ҫ�Ƚϡ� 
	 * @param numbers ��Ҫ�������������
	 */
	public static void bubbleSort(int[] numbers)
	{
	    int temp = 0;
	    int size = numbers.length;
	    for(int i = 0 ; i < size-1; i ++)
	    {
	    for(int j = 0 ;j < size-1-i ; j++)
	    {
	        if(numbers[j] > numbers[j+1])  //��������λ��
	        {
	        temp = numbers[j];
	        numbers[j] = numbers[j+1];
	        numbers[j+1] = temp;
	        }
	    }
	    }
	}
	
	
	
	
	/*
	 * 
	 * ��������
	 * 
	 */
	public static void quick(int[] numbers)
	{
	    if(numbers.length > 0)   //�鿴�����Ƿ�Ϊ��
	    {
	    quickSort(numbers, 0, numbers.length-1);
	    }
	}
	 
	/**
	 * 
	 * @param numbers ����������
	 * @param low  ��ʼλ��
	 * @param high ����λ��
	 */
	public static void quickSort(int[] numbers,int low,int high)
	{
	    if(low < high)
	    {
		    int middle = getMiddle(numbers,low,high); //��numbers�������һ��Ϊ��
		    quickSort(numbers, low, middle-1);   //�Ե��ֶα���еݹ�����
		    quickSort(numbers, middle+1, high); //�Ը��ֶα���еݹ�����
	    }

	}
	
	 /**
		 * ���ҳ����ᣨĬ�������λlow������numbers�������������λ��
		 * 
		 * @param numbers ����������
		 * @param low   ��ʼλ��
		 * @param high  ����λ��
		 * @return  ��������λ��
		 */
		public static int getMiddle(int[] numbers, int low,int high)
		{
		    int temp = numbers[low]; //����ĵ�һ����Ϊ����
		    while(low < high)
		    {
		    while(low < high && numbers[high] >= temp)
		    {
		        high--;
		    }
		    numbers[low] = numbers[high];//������С�ļ�¼�Ƶ��Ͷ�
		    while(low < high && numbers[low] < temp)
		    {
		        low++;
		    }
		    numbers[high] = numbers[low] ; //�������ļ�¼�Ƶ��߶�
		    }
		    numbers[low] = temp ; //�����¼��β
		    return low ; // ���������λ��
		}

/**
 * ѡ�������㷨
 * ��δ�����������ҵ���СԪ�أ���ŵ��������е���ʼλ��  
 * �ٴ�ʣ��δ����Ԫ���м���Ѱ����СԪ�أ�Ȼ��ŵ���������ĩβ�� 
 * �Դ����ƣ�ֱ������Ԫ�ؾ�������ϡ� 
 * @param numbers
 */
public static void selectSort(int[] numbers)
{
int size = numbers.length; //���鳤��
int temp = 0 ; //�м����

for(int i = 0 ; i < size ; i++)
{
    int k = i;   //��ȷ����λ��
    //ѡ���Ӧ���ڵ�i��λ�õ���
    for(int j = size -1 ; j > i ; j--)
    {
    if(numbers[j] < numbers[k])
    {
        k = j;
    }
    }
    //����������
    temp = numbers[i];
    numbers[i] = numbers[k];
    numbers[k] = temp;
}
}


/**  
 * ��������
 * 
 * �ӵ�һ��Ԫ�ؿ�ʼ����Ԫ�ؿ�����Ϊ�Ѿ�������
 * ȡ����һ��Ԫ�أ����Ѿ������Ԫ�������дӺ���ǰɨ�� 
 * �����Ԫ�أ������򣩴�����Ԫ�أ�����Ԫ���Ƶ���һλ��  
 * �ظ�����3��ֱ���ҵ��������Ԫ��С�ڻ��ߵ�����Ԫ�ص�λ��  
 * ����Ԫ�ز��뵽��λ����  
 * �ظ�����2  
 * @param numbers  ����������
 */  
public static void insertSort(int[] numbers)
{
int size = numbers.length;
int temp = 0 ;
int j =  0;

for(int i = 0 ; i < size ; i++)
{
    temp = numbers[i];
    //����temp��ǰ���ֵС����ǰ���ֵ����
    for(j = i ; j > 0 && temp < numbers[j-1] ; j --)
    {
    numbers[j] = numbers[j-1];
    }
    numbers[j] = temp;
}
}


/**ϣ�������ԭ��:���������������Ҫ����Ӵ�С���У��������Ƚ�������з��飬Ȼ�󽫽ϴ�ֵ�Ƶ�ǰ�棬��Сֵ
 * �Ƶ����棬�������������в���������������һ��ʼ���ò���������������ݽ������ƶ��Ĵ���������˵ϣ�������Ǽ�ǿ
 * ��Ĳ�������
 * ������5, 2, 8, 9, 1, 3��4��˵�����鳤��Ϊ7����incrementΪ3ʱ�������Ϊ��������
 * 5��2��8��9��1��3��4����һ������9��5�Ƚϣ�1��2�Ƚϣ�3��8�Ƚϣ�4�ͱ����±�ֵСincrement������ֵ��Ƚ�
 * �������ǰ��մӴ�С���У����Դ�Ļ�����ǰ�棬��һ�����������Ϊ9, 2, 8, 5, 1, 3��4
 * ��һ�κ�increment��ֵ��Ϊ3/2=1,��ʱ��������в�������
 *ʵ������Ӵ�С��
 */

    public static void shellSort(int[] data) 
    {
        int j = 0;
        int temp = 0;
        //ÿ�ν���������Ϊԭ����һ��
        for (int increment = data.length / 2; increment > 0; increment /= 2)
        {
        for (int i = increment; i < data.length; i++) 
        {
            temp = data[i];
            for (j = i; j >= increment; j -= increment) 
            {
            if(temp > data[j - increment])//�����С������ֻ���޸�����
            {   
                data[j] = data[j - increment];
            }
            else
            {
                break;
            }
            
            } 
            data[j] = temp;
        }
    
        }
    }

    /**
     * �鲢����
     * ���:�����������������ϣ������ϲ���һ���µ������ ���Ѵ��������з�Ϊ���ɸ������У�ÿ��������������ġ�Ȼ���ٰ����������кϲ�Ϊ������������
     * ʱ�临�Ӷ�ΪO(nlogn)
     * �ȶ�����ʽ
     * 
     * �ϲ�������
��r[i��n]�����������ӱ�r[i��m]��r[m+1��n]��ɣ������ӱ��ȷֱ�Ϊn-i +1��n-m��
1��j=m+1��k=i��i=i; //�������ӱ����ʼ�±꼰�����������ʼ�±�
2����i>m ��j>n��ת�� //����һ���ӱ��Ѻϲ��꣬�Ƚ�ѡȡ����
3��//ѡȡr[i]��r[j]��С�Ĵ��븨������rf
        ���r[i]<r[j]��rf[k]=r[i]�� i++�� k++�� ת��
        ����rf[k]=r[j]�� j++�� k++�� ת��
4��//����δ��������ӱ���Ԫ�ش���rf
        ���i<=m����r[i��m]����rf[k��n] //ǰһ�ӱ�ǿ�
        ���j<=n ,  ��r[j��n] ����rf[k��n] //��һ�ӱ�ǿ�
5���ϲ�������

     * 
     * @param nums ����������
     * @return �����������
     */
    public static int[] sort(int[] nums, int low, int high) {
        int mid = (low + high) / 2;
        if (low < high) {
            // ���
            sort(nums, low, mid);
            // �ұ�
            sort(nums, mid + 1, high);
            // ���ҹ鲢
            merge(nums, low, mid, high);
        }
        return nums;
    }

    /**
     * ��������low��highλ�õ�����������
     * @param nums ����������
     * @param low ���ŵĿ�ʼλ��
     * @param mid �����м�λ��
     * @param high ���Ž���λ��
     */
    public static void merge(int[] nums, int low, int mid, int high) {
        int[] temp = new int[high - low + 1];
        int i = low;// ��ָ��
        int j = mid + 1;// ��ָ��
        int k = 0;

        // �ѽ�С�������Ƶ���������
        while (i <= mid && j <= high) {
            if (nums[i] < nums[j]) {
                temp[k++] = nums[i++];
            } else {
                temp[k++] = nums[j++];
            }
        }

        // �����ʣ�������������
        while (i <= mid) {
            temp[k++] = nums[i++];
        }

        // ���ұ߱�ʣ�������������
        while (j <= high) {
            temp[k++] = nums[j++];
        }

        // ���������е�������nums����
        for (int k2 = 0; k2 < temp.length; k2++) {
            nums[k2 + low] = temp[k2];
        }
    }


    //��data�����0��lastIndex���󶥶�
    public static void buildMaxHeap(int[] data, int lastIndex){
         //��lastIndex���ڵ㣨���һ���ڵ㣩�ĸ��ڵ㿪ʼ 
        for(int i=(lastIndex-1)/2;i>=0;i--){
            //k���������жϵĽڵ� 
            int k=i;
            //�����ǰk�ڵ���ӽڵ����  
            while(k*2+1<=lastIndex){
                //k�ڵ�����ӽڵ������ 
                int biggerIndex=2*k+1;
                //���biggerIndexС��lastIndex����biggerIndex+1�����k�ڵ�����ӽڵ����
                if(biggerIndex<lastIndex){  
                    //�������ӽڵ��ֵ�ϴ�  
                    if(data[biggerIndex]<data[biggerIndex+1]){  
                        //biggerIndex���Ǽ�¼�ϴ��ӽڵ������  
                        biggerIndex++;  
                    }  
                }  
                //���k�ڵ��ֵС����ϴ���ӽڵ��ֵ  
                if(data[k]<data[biggerIndex]){  
                    //��������  
                    swap(data,k,biggerIndex);  
                    //��biggerIndex����k����ʼwhileѭ������һ��ѭ�������±�֤k�ڵ��ֵ�����������ӽڵ��ֵ  
                    k=biggerIndex;  
                }else{  
                    break;  
                }  
            }
        }
    }
    //����
    private static void swap(int[] data, int i, int j) {  
        int tmp=data[i];  
        data[i]=data[j];  
        data[j]=tmp;  
    } 

	public static void printArr(int[] numbers)
	{
	    for(int i = 0 ; i < numbers.length ; i ++ )
	    {
	    System.out.print(numbers[i] + ",");
	    }
	    System.out.println("");
	}
	public static void main(String[] args) 
	{
	    int[] numbers = {10,20,15,0,6,7,2,1,-5,55};
	    System.out.print("����ǰ��");
	    printArr(numbers);
	    
	    bubbleSort(numbers);
	    System.out.print("ð�������");
	    printArr(numbers);
	    
	    
	    quick(numbers);
	    System.out.print("���������");
	    printArr(numbers);
	    
	    
	    //������
	     int[] a={49,38,65,97,76,13,27,49,78,34,12,64};
	        int arrayLength=a.length;  
	        //ѭ������  
	        System.out.println("������: ");  
	        for(int i=0;i<arrayLength-1;i++){  
	            //����  
	            buildMaxHeap(a,arrayLength-1-i);  
	            //�����Ѷ������һ��Ԫ��  
	            swap(a,0,arrayLength-1-i);  
	            System.out.println(Arrays.toString(a));  
	        } 
	}
	
}