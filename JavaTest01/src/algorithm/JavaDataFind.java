package algorithm;


public class JavaDataFind {

	/**˳�����ƽ��ʱ�临�Ӷ� O��n�� 
	 * @param searchKey Ҫ���ҵ�ֵ 
	 * @param array ���飨����������в��ң� 
	 * @return  ���ҽ����������±�λ�ã� 
	 */  
	public static int orderSearch(int searchKey,int[] array){  
	    if(array==null||array.length<1)  
	        return -1;  
	    for(int i=0;i<array.length;i++){  
	        if(array[i]==searchKey){  
	            return i;  
	        }  
	    }  
	    return -1;  
	      
	}  
	
	/** 
	 * ���ֲ����ֳ��۰���ң�����һ��Ч�ʽϸߵĲ��ҷ����� �����ֲ���Ҫ�󡿣�1.�������˳��洢�ṹ 2.���밴�ؼ��ִ�С�������С� 
	 *  
	 * @param array 
	 *            �������� * 
	 * @param searchKey 
	 *            ����Ԫ�� * 
	 * @return searchKey�������±꣬û�ҵ�����-1 
	 */  
	public static int binarySearch(int[] array, int searchKey) {  
	  
	    int low = 0;  
	    int high = array.length - 1;  
	    while (low <= high) {  
	        int middle = (low + high) / 2;  
	        if (searchKey == array[middle]) {  
	            return middle;  
	        } else if (searchKey < array[middle]) {  
	            high = middle - 1;  
	        } else {  
	            low = middle + 1;  
	        }  
	    }  
	    return -1;  
	} 
	
	
	
	/** 
	 * �ֿ���� 
	 *  
	 *  a. ���Ƚ����ұ�ֳ����ɿ飬��ÿһ��������Ԫ�صĴ��������ģ��������֮�����������ģ��������������ǰ��ؼ���ֵ�����ģ�Ҳ����˵�ڵ�һ��������һ������Ԫ�صĹؼ��ֶ�С�ڵڶ�������������Ԫ�صĹؼ��֣��ڶ���������һ������Ԫ�صĹؼ��ֶ�С�ڵ���������������Ԫ�صĹؼ��֣��������ƣ��� 
b. ����һ����������ÿ�������Ĺؼ���ֵ�����˳������һ�����������У����������Ҳ���������У� 
c. ����ʱ���ø����Ĺؼ���ֵ���������в��ң�ȷ����������������Ԫ�ش�����ĸ����У����ҷ����ȿ������۰뷽����Ҳ������˳����ҡ� 
d. �ٵ���Ӧ�Ŀ���˳����ң�����Եõ����ҵĽ���� 
	 *  
	 * @param index 
	 *            ���������зŵ��Ǹ�������ֵ 
	 * @param st 
	 *            ˳��� 
	 * @param key 
	 *            Ҫ���ҵ�ֵ 
	 * @param m 
	 *            ˳����и���ĳ�����ȣ�Ϊm 
	 * @return 
	 */  
	public static int blockSearch(int[] index, int[] st, int key, int m) {  
	    // ������st�����У��÷ֿ���ҷ������ҹؼ���Ϊkey�ļ�¼  
	    // 1.��index[ ] ���۰���ң�ȷ��Ҫ���ҵ�key�����ĸ�����  
	    int i = binarySearch(index, key);  
	    if (i >= 0) {  
	        int j = i > 0 ? i * m : i;  
	        int len = (i + 1) * m;  
	        // ��ȷ���Ŀ�����˳����ҷ�������key  
	        for (int k = j; k < len; k++) {  
	            if (key == st[k]) {  
	                System.out.println("��ѯ�ɹ�");  
	                return k;  
	            }  
	        }  
	    }  
	    System.out.println("����ʧ��");  
	    return -1;  
	} 
	
	/**** 
	 * Hash���� 
	 *  
	 *  	 *  ��ϣ�������ͨ���Լ�¼�Ĺؼ���ֵ�������㣬ֱ��������ĵ�ַ���ǹؼ��ֵ���ַ��ֱ��ת�����������÷����Ƚϡ�����f����n����㣬RiΪ����ĳ����㣨1��i��n����keyi����ؼ���ֵ����keyi��Ri�ĵ�ַ֮�佨��ĳ�ֺ�����ϵ������ͨ����������ѹؼ���ֵת������Ӧ���ĵ�ַ���У�addr(Ri)=H(keyi)��addr(Ri)Ϊ��ϣ������ 
�����ͻ�ķ������������֣����� 
(1)���ŵ�ַ������ 
�����������Ԫ�صĹ�ϣֵ��ͬ�����ڹ�ϣ����Ϊ����������Ԫ������ѡ��һ�������������ҹ�ϣ��ʱ�����û���ڵ�һ����Ӧ�Ĺ�ϣ�������ҵ����ϲ���Ҫ�������Ԫ�أ�����ͻ����������ң�ֱ���ҵ�һ�����ϲ���Ҫ�������Ԫ�أ���������һ���յı������ 
(2)����ַ�� 
����ϣֵ��ͬ������Ԫ�ش����һ�������У��ڲ��ҹ�ϣ��Ĺ����У������ҵ��������ʱ������������Բ��ҷ����� 

	 *  
	 * @param hash 
	 * @param hashLength 
	 * @param key 
	 * @return 
	 */  
	public static int searchHash(int[] hash, int hashLength, int key) {  
	    // ��ϣ����  
	    int hashAddress = key % hashLength;  
	  
	    // ָ��hashAdrress��Ӧֵ���ڵ����ǹؼ�ֵ�����ÿ���Ѱַ�����  
	    while (hash[hashAddress] != 0 && hash[hashAddress] != key) {  
	        hashAddress = (++hashAddress) % hashLength;  
	    }  
	  
	    // ���ҵ��˿��ŵ�Ԫ����ʾ����ʧ��  
	    if (hash[hashAddress] == 0)  
	        return -1;  
	    return hashAddress;  
	  
	}  
	  
	
	/*** 
	 * ���ݲ���Hash�� 
	 *  
	 * @param hash 
	 *            ��ϣ�� 
	 * @param hashLength 
	 * @param data 
	 */  
	public static void insertHash(int[] hash, int hashLength, int data) {  
	    // ��ϣ����  
	    int hashAddress = data % hashLength;  
	  
	    // ���key���ڣ���˵���Ѿ�������ռ�ã���ʱ��������ͻ  
	    while (hash[hashAddress] != 0) {  
	        // �ÿ���Ѱַ���ҵ�  
	        hashAddress = (++hashAddress) % hashLength;  
	    }  
	  
	    // ��data�����ֵ���  
	    hash[hashAddress] = data;  
	}  
}



