package frame.preferences.treeList;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.TreeNode;

/**
 * Defines a tree node
 * 
 * @author Ha Minh Nam
 * 
 */
public class MTreeNode implements TreeNode {
	/**
	 * The title will be displayed in the tree
	 */
	private String title;

	/*
	 * Type of this node, which is used by a renderer to set appropriate icon
	 * for the node
	 */
	private int type;

	private Vector<TreeNode> children = new Vector<TreeNode>();
	private TreeNode parent;
	public Object target;
	// Constants for types of node
	public static final int NODE_ROOT = 0;
	public static final int NODE_GROUP = 1;
	public static final int NODE_CLASS = 2;
	public static final int NODE_CASE = 4;

	public MTreeNode(String title, int type, Object c) {
		this.title = title;
		this.type = type;
		this.target = c;
	}

	public void addChild(TreeNode child) {
		children.add(child);
	}

	public void setParent(TreeNode parent) {
		this.parent = parent;
	}

	@Override
	public Enumeration<TreeNode> children() {
		return children.elements();
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public TreeNode getChildAt(int childIndex) {
		return children.elementAt(childIndex);
	}

	@Override
	public int getChildCount() {
		return children.size();
	}

	@Override
	public int getIndex(TreeNode node) {
		return children.indexOf(node);
	}

	@Override
	public TreeNode getParent() {
		return this.parent;
	}

	@Override
	public boolean isLeaf() {
		return (children.size() == 0);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	/**
	 * The node object should override this method to provide a text that will
	 * be displayed for the node in the tree.
	 */
	public String toString() {
		return title;
	}

	public int getType() {
		return type;
	}
}

