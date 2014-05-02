package frame.preferences;

import javax.swing.*;
import javax.swing.event.*;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceConstants.ImageWatermarkKind;
import org.pushingpixels.substance.api.skin.RavenSkin;
import org.pushingpixels.substance.api.watermark.SubstanceImageWatermark;

import control.WindowControl;

import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;
import frame.preferences.treeList.*;
import control.*;
public class PreferencesFrame extends JFrame implements TreeSelectionListener {
	private JScrollPane sliderBar;
	private JPanel container;
	private JTree tree;
	private JButton defaultButton;
	private JButton okButton;
	private JButton cancelButton;

	public PreferencesFrame() {
		this.setTitle("Preferences");
		this.setLayout(new BorderLayout());
		MTreeNode rootNode = TreeBuilder.build();
		MTreeModel model = new MTreeModel(rootNode);
		this.tree = new JTree(model);
		this.tree.setCellRenderer(new NodeRenderer());
		this.tree.addTreeSelectionListener(this);
		this.sliderBar = new JScrollPane(new JPanel());
		((JPanel) sliderBar.getViewport().getView()).add(tree);
		this.container = new JPanel();
		this.sliderBar.setOpaque(false);
		JPanel southPanel = new JPanel();
		this.defaultButton = new JButton("Default");
		southPanel.add(defaultButton);
		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
		southPanel.add(okButton);
		southPanel.add(cancelButton);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PreferencesControl.writeProperties();
				PreferencesFrame.this.dispose();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PreferencesControl.recoverProperties();
				PreferencesFrame.this.dispose();
			}
		});
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				PreferencesControl.recoverProperties();
			}
		});
		this.container.add(new SkinChooser());
		this.add(sliderBar, BorderLayout.WEST);
		this.add(container, BorderLayout.CENTER);
		this.add(southPanel, BorderLayout.SOUTH);
		this.setSize(700, 600);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	

	

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		Object node = tree.getLastSelectedPathComponent();
		if (node instanceof MTreeNode) {
			if (node.toString().equals("Chat Room")) {
				container.removeAll();
				container.add(new ChatRoomChooser());
			}
			if (node.toString().equals("Skin")) {
				container.removeAll();
				container.add(new SkinChooser());
			}
			if (node.toString().equals("General Skin")) {
				container.removeAll();
				container.add(new SkinChooser());
			}
			if (node.toString().equals("Special Skin")) {
				container.removeAll();
				container.add(new SpecialSkinChooser());
			}
			this.validate();
			this.repaint();
			return;
		}
	}
}
