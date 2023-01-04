package algorithm;

public class SingleChainRevert {
	
	
	public static void main(String[] args) {

//		1-->2-->3-->4
//		4-->3-->2-->1
		
//		������ת: ���θ�ֵ����ָ������ݽ���
		
		Node root = new Node();
		
		Node first = root;//ָ��firstʼ�ղ���
		Node head = root;//ָ������ͷ�������ϱ仯
		Node tmp = null; //exchange data
		while(first.next!=null){
		        tmp=first.next;
		        first.next = tmp.next;
		        tmp.next=head;
		        head=tmp;// update head node
		}
//		���������������������: ���ݹ����
//		��α�����ʵ�֣�
	}
	
	static class Node{
		public Node() {}
		String value;
		Node next;
	}

}
