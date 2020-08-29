package algorithm;

public class SingleChainRevert {
	
	
	public static void main(String[] args) {

//		1-->2-->3-->4
//		4-->3-->2-->1
		
//		������ת: ���θ�ֵ����ָ������ݽ���
		Node first;//ָ��firstʼ�ղ���
		Node head = null;//ָ������ͷ�������ϱ仯
		Node tmp;   //exchange data
		first = head;
		while(first.next!=null){
		        tmp=first.next;
		        first.next = tmp.next;
		        tmp.next=head;
		        head=tmp;//����head�ڵ���Ϊ������õ�����ľ��
		}
		
//		���������������������: ���ݹ����
//		��α�����ʵ�֣�
	}
	
	class Node{
		Node next;
	}

}
