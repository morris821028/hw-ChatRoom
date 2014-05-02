package frame.preferences.treeList;

import java.awt.*;

public class TreeBuilder {
	/*public static MTreeNode getMTreeNode(Component c) {
		if (c instanceof PaintPanel)
			return new MTreeNode("Root", MTreeNode.NODE_ROOT, c);
		if(c instanceof ClassDiagram) 
			return new MTreeNode(c.toString(), MTreeNode.NODE_CLASS, c);
		if(c instanceof UseCase) 
			return new MTreeNode(c.toString(), MTreeNode.NODE_CASE, c);
		if(c instanceof CompositePanel) 
			return new MTreeNode("Group", MTreeNode.NODE_GROUP, c);
		return null;
	}*/

	public static MTreeNode build() {
		MTreeNode rootNode = new MTreeNode("", MTreeNode.NODE_CASE, null);
		MTreeNode skinNode = new MTreeNode("Skin", MTreeNode.NODE_CASE, null);
		MTreeNode generalNode = new MTreeNode("General Skin", MTreeNode.NODE_CASE, null);
		MTreeNode specialNode = new MTreeNode("Special Skin", MTreeNode.NODE_CASE, null);
		MTreeNode chatNode = new MTreeNode("Chat Room", MTreeNode.NODE_CASE, null);
		MTreeNode windowNode = new MTreeNode("Environment", MTreeNode.NODE_CASE, null);
		rootNode.addChild(skinNode);
		skinNode.addChild(generalNode);
		skinNode.addChild(specialNode);
		rootNode.addChild(chatNode);
		rootNode.addChild(windowNode);
		return rootNode;
	}
}

