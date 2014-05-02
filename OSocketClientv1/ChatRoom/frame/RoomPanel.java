package frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

public class RoomPanel extends JPanel {
	private static RoomPanel singleton = null;

	public static RoomPanel getInstance() {
		if (singleton == null)
			singleton = new RoomPanel();
		return singleton;
	}

	private MTextArea textArea;
	private JList userList;
	private DefaultListModel userListModel;

	private RoomPanel() {
		this.setBorder(new TitledBorder("Room"));
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.userListModel = new DefaultListModel();
		this.userList = new JList(userListModel);
		this.textArea = new MTextArea();

		this.userList.setOpaque(false);
		this.userList.setForeground(Color.GREEN);

		JScrollPane leftPane = new JScrollPane(userList);
		JScrollPane rightPane = new JScrollPane(textArea);
		JSplitPane splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				leftPane, rightPane);
		splitPane1.setDividerLocation(100);
		splitPane1.setOpaque(false);
		leftPane.setBorder(new TitledBorder("Online"));
		leftPane.setOpaque(false);
		rightPane.setBorder(new TitledBorder("Room"));
		rightPane.setOpaque(false);
		rightPane.getVerticalScrollBar().addAdjustmentListener(
				new AdjustmentListener() {
					boolean locked = true;
					JScrollBar sb;

					private AdjustmentListener init(JScrollBar sb) {
						this.sb = sb;
						return this;
					}

					public void adjustmentValueChanged(AdjustmentEvent e) {
						if (sb.getMaximum() - sb.getValue() - sb.getHeight() > 200)
							locked = false;
						else
							locked = true;

						if (locked == true) {
							sb.setValue(sb.getMaximum());
						}
					}
				}.init(rightPane.getVerticalScrollBar()));
		this.setLayout(new BorderLayout());
		this.setOpaque(false);
		this.add(splitPane1, BorderLayout.CENTER);
	}

	public DefaultListModel getUserListModel() {
		return this.userListModel;
	}

	public MTextArea getTextArea() {
		return this.textArea;
	}
}
