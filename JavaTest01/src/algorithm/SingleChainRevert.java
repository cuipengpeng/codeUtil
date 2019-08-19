package algorithm;

public class SingleChainRevert {
	
	public static void main(String[] args) {
//		单链表反转: 两次赋值两次指向的数据交换
		Node first;//指向first始终不变
		Node head = null;//指向链表头部，不断变化
		Node tmp;
		first = head;
		while(first.next!=null){
		        tmp=first.next;
		        first.next = tmp.next;
		        tmp.next=head;
		        head=tmp;
		}
		
//		二叉树左序，右序，中序遍历: 即递归调用
//		层次遍历的实现？
	}
	
	class Node{
		Node next;
	}

}
