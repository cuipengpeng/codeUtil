package algorithm;

public class SingleChainRevert {
	
	public static void main(String[] args) {
//		������ת: ���θ�ֵ����ָ������ݽ���
		Node first;//ָ��firstʼ�ղ���
		Node head = null;//ָ������ͷ�������ϱ仯
		Node tmp;
		first = head;
		while(first.next!=null){
		        tmp=first.next;
		        first.next = tmp.next;
		        tmp.next=head;
		        head=tmp;
		}
		
//		���������������������: ���ݹ����
//		��α�����ʵ�֣�
	}
	
	class Node{
		Node next;
	}

}
