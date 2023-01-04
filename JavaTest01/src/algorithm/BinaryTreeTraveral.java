package algorithm;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * ������������ȱ���(Depth First Search)��Ϊ��ǰ��(������)������(�����)������(���Ҹ�)
�����������Ǹ��ݸ��ڵ��˳�������ģ������ֱ�����ʽ����������ҡ���Ϊ���֣�ǰ��(������)������(�����)������(���Ҹ�)�����������Ϊ��Ҫ��
������ǿ������ö�ջ���Ƚ�������ص㣬�ֽ�������ѹջ���ٽ�������ѹջ��������������λ��ջ�������Ա�֤����������������������������

�����������(Breadth First Search),�ֽп��������������������������ǴӸ���㿪ʼ�������Ŀ��һ��һ��������������������ö���ʵ�ֹ������������
 *
 *
 *	https://leetcode-cn.com/
 *	https://paperswithcode.com/ 
 *	https://www.nowcoder.com/
 */
class BinaryTreeTraveral {
 
    // �������ڵ�
    public static class BinaryTreeNode {
        int value;
        BinaryTreeNode left;
        BinaryTreeNode right;
 
        public BinaryTreeNode(int value) {
            this.value = value;
        }
 
        public BinaryTreeNode(int value, BinaryTreeNode left,
                BinaryTreeNode right) {
            super();
            this.value = value;
            this.left = left;
            this.right = right;
        }
 
    }
 
    // �������Ľڵ�
    public static void visit(BinaryTreeNode node) {
        System.out.println(node.value);
    }
 
    /** �ݹ�ʵ�ֶ�������������� */
    public static void preOrder(BinaryTreeNode node) {
        if (node != null) {
            visit(node);
            preOrder(node.left);
            preOrder(node.right);
        }
    }
 
    /** �ݹ�ʵ�ֶ�������������� */
    public static void inOrder(BinaryTreeNode node) {
        if (node != null) {
            inOrder(node.left);
            visit(node);
            inOrder(node.right);
        }
    }
 
    /** �ݹ�ʵ�ֶ������ĺ������ */
    public static void postOrder(BinaryTreeNode node) {
        if (node != null) {
            postOrder(node.left);
            postOrder(node.right);
            visit(node);
        }
    }
 
    /** �ǵݹ�ʵ�ֶ�������������� */
    public static void iterativePreorder(BinaryTreeNode node) {
        Stack<BinaryTreeNode> stack = new Stack<>();
        if (node != null) {
            stack.push(node);
            while (!stack.empty()) {
                node = stack.pop();
                // �ȷ��ʽڵ�
                visit(node);
                // �����ӽ��ѹ��ջ
                if (node.right != null) {
                    stack.push(node.right);
                }
                // �����ӽ��ѹ��ջ
                if (node.left != null) {
                    stack.push(node.left);
                }
            }
        }
    }
 
    /** �ǵݹ�ʵ�ֶ�������������� */
    public static void iterativeInOrder(BinaryTreeNode root) {
        Stack<BinaryTreeNode> stack = new Stack<>();
        BinaryTreeNode currentNode = root;
        while (currentNode != null || stack.size() > 0) {
            // �ѵ�ǰ�ڵ����������ӽ��ѹ��ջ
            while (currentNode != null) {
                stack.push(currentNode);
                currentNode = currentNode.left;
            }
            // ���ʽڵ㣬����ýڵ��������
            if (stack.size() > 0) {
                currentNode = stack.pop();
                visit(currentNode);
                currentNode = currentNode.right;
            }
        }
    }
 
    /** �ǵݹ�ʹ�õ�ջʵ�ֶ������������ */
    public static void iterativePostOrder(BinaryTreeNode root) {
        Stack<BinaryTreeNode> stack = new Stack<>();
        BinaryTreeNode currentNode = root;
        // ���ʸ��ڵ�ʱ�ж����������ǹ������ʹ�
        BinaryTreeNode hasPrintedNode = null;
        while (currentNode != null || stack.size() > 0) {
            // �ѵ�ǰ�ڵ�����ڵ�ȫ����ջ
            while (currentNode != null) {
                stack.push(currentNode);
                currentNode = currentNode.left;
            }
            if (stack.size() > 0) {
                BinaryTreeNode tempRight = stack.peek().right;
                // һ�����ڵ㱻���ʵ�ǰ���ǣ������������������ѱ����ʹ�
                if (tempRight == null || tempRight == hasPrintedNode) {
                    currentNode = stack.pop();
                    visit(currentNode);
                    hasPrintedNode = currentNode;// ��¼�ձ����ʹ��Ľڵ�
                    currentNode = null;
                } else {
                    // ����������
                    currentNode = tempRight;
                }
            }
        }
    }
 
    /** �ǵݹ�ʹ��˫ջʵ�ֶ������������ */
    public static void iterativePostOrderByTwoStacks(BinaryTreeNode root) {
        Stack<BinaryTreeNode> stack = new Stack<>();
        Stack<BinaryTreeNode> temp = new Stack<>();
        BinaryTreeNode node = root;
        while (node != null || stack.size() > 0) {
            // �ѵ�ǰ�ڵ�����Ҳ��ӽ������ջ
            while (node != null) {
                stack.push(node);
                temp.push(node);
                node = node.right;
            }
            // ����ջ���ڵ��������
            if (stack.size() > 0) {
                node = stack.pop();
                node = node.left;
            }
        }
        while (temp.size() > 0) {
            node = temp.pop();
            visit(node);
        }
    }
 
    /** ������������ȱ�������������� */
    public static void layerTraversal(BinaryTreeNode root) {
        Queue<BinaryTreeNode> queue = new LinkedList<>();
 
        if (root != null) {
            queue.add(root);
            while (!queue.isEmpty()) {
                BinaryTreeNode currentNode = queue.poll();
                visit(currentNode);
                if (currentNode.left != null) {
                    queue.add(currentNode.left);
                }
 
                if (currentNode.right != null) {
                    queue.add(currentNode.right);
                }
 
            }
        }
    }
 
    public static void main(String[] args) {
 
        // ���������
        // 1
        // / \
        // 2 3
        // / / \
        // 4 5 7
        // 	 \ /
        // 	 6 8
        BinaryTreeNode root = new BinaryTreeNode(1);
        BinaryTreeNode node2 = new BinaryTreeNode(2);
        BinaryTreeNode node3 = new BinaryTreeNode(3);
        BinaryTreeNode node4 = new BinaryTreeNode(4);
        BinaryTreeNode node5 = new BinaryTreeNode(5);
        BinaryTreeNode node6 = new BinaryTreeNode(6);
        BinaryTreeNode node7 = new BinaryTreeNode(7);
        BinaryTreeNode node8 = new BinaryTreeNode(8);
 
        root.left = node2;
        root.right = node3;
        node2.left = node4;
        node3.left = node5;
        node3.right = node7;
        node5.right = node6;
        node7.left = node8;
        System.out.println("�������������");
        preOrder(root);
        System.out.println("��������������ǵݹ�");
        iterativePreorder(root);
        System.out.println("�������������");
        inOrder(root);
        System.out.println("��������������ǵݹ�");
        iterativeInOrder(root);
        System.out.println("�������������");
        postOrder(root);
        System.out.println("��������ջ�ǵݹ�������");
        iterativePostOrder(root);
        System.out.println("������˫ջ�ǵݹ�������");
        iterativePostOrderByTwoStacks(root);
        System.out.println("������������ȱ���-���������");
        layerTraversal(root);
    }
}