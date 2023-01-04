package algorithm;
import java.util.LinkedList;
import java.util.Queue;

/**
* ���������ͼ�������������������͹�����ȱ��������ַ�ʽ
* ͨ������ʹ�������ͼ�ı��� 
*/
public class GraphTraveral{
// �ڽӾ���洢ͼ 
// --A B C D E F G H I 
// A 0 1 0 0 0 1 1 0 0 
// B 1 0 1 0 0 0 1 0 1 
// C 0 1 0 1 0 0 0 0 1 
// D 0 0 1 0 1 0 1 1 1 
// E 0 0 0 1 0 1 0 1 0 
// F 1 0 0 0 1 0 1 0 0 
// G 0 1 0 1 0 1 0 1 0 
// H 0 0 0 1 1 0 1 0 0 
// I 0 1 1 1 0 0 0 0 0 

// ������ 
private int number = 9; 
// ��¼�����Ƿ񱻷��� 
private boolean[] flag; 
// ���� 
private String[] vertexs = { "A", "B", "C", "D", "E", "F", "G", "H", "I" }; 
// �� 
private int[][] edges = { 
{ 0, 1, 0, 0, 0, 1, 1, 0, 0 }, { 1, 0, 1, 0, 0, 0, 1, 0, 1 }, { 0, 1, 0, 1, 0, 0, 0, 0, 1 }, 
{ 0, 0, 1, 0, 1, 0, 1, 1, 1 }, { 0, 0, 0, 1, 0, 1, 0, 1, 0 }, { 1, 0, 0, 0, 1, 0, 1, 0, 0 }, 
{ 0, 1, 0, 1, 0, 1, 0, 1, 0 }, { 0, 0, 0, 1, 1, 0, 1, 0, 0 }, { 0, 1, 1, 1, 0, 0, 0, 0, 0 } 
}; 

// ͼ����ȱ�������(�ݹ�) 
void DFSTraverse() { 
	flag = new boolean[number]; 
	for (int i = 0; i < number; i++) { 
		if (flag[i] == false) {// ��ǰ����û�б����� 
			DFS(i); 
		} 
	} 
} 

// ͼ��������ȵݹ��㷨 
void DFS(int i) { 
	flag[i] = true;// ��i�����㱻���� 
	System.out.print(vertexs[i] + " "); 
	for (int j = 0; j < number; j++) { 
		if (flag[j] == false && edges[i][j] == 1) { 
			DFS(j); 
		} 
	} 
} 

// ͼ�Ĺ�ȱ������� 
void BFSTraverse() { 
	flag = new boolean[number]; 
	Queue<Integer> queue = new LinkedList<Integer>(); 
	for (int i = 0; i < number; i++) { 
		if (flag[i] == false) { 
			flag[i] = true; 
			System.out.print(vertexs[i] + " "); 
			queue.add(i); 
			while (!queue.isEmpty()) { 
				int j = queue.poll(); 
				for (int k = 0; k < number; k++) { 
					if (edges[j][k] == 1 && flag[k] == false) { 
						flag[k] = true; 
						System.out.print(vertexs[k] + " "); 
						queue.add(k); 
					} 
				} 
			} 
		} 
	} 
} 

// ���� 
	public static void main(String[] args) { 
		GraphTraveral graph = new GraphTraveral(); 
		System.out.println("ͼ����ȱ�������(�ݹ�):"); 
		graph.DFSTraverse(); 
		System.out.println("\n-------------"); 
		System.out.println("ͼ�Ĺ�ȱ�������:"); 
		graph.BFSTraverse(); 
	}
}