package frame.preferences.treeList;

import java.util.Vector;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * Defines the data model used by the JTree class
 * 
 * @author Ha Minh Nam
 * 
 */
public class MTreeModel implements TreeModel {

	private TreeNode rootNode;
	private Vector<TreeModelListener> listeners = new Vector<TreeModelListener>();

	public MTreeModel(TreeNode rootNode) {
		this.rootNode = rootNode;
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
	}

	@Override
	public Object getChild(Object parent, int index) {
		TreeNode parentNode = (TreeNode) parent;
		return parentNode.getChildAt(index);
	}

	@Override
	public int getChildCount(Object parent) {
		TreeNode parentNode = (TreeNode) parent;
		return parentNode.getChildCount();
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		TreeNode parentNode = (TreeNode) parent;
		TreeNode childNode = (TreeNode) child;
		return parentNode.getIndex(childNode);
	}

	@Override
	public Object getRoot() {
		return rootNode;
	}

	@Override
	public boolean isLeaf(Object node) {
		TreeNode treeNode = (TreeNode) node;
		return treeNode.isLeaf();
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
	}

}

