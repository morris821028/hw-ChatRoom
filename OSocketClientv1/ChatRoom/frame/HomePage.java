package frame;

import game.GamePanel;

import java.awt.*;
import javax.swing.*;

import control.ChatSocketControl;
import control.GameClientControl;
import java.util.*;
import include.*;

public class HomePage extends JPanel {
	private static HomePage singleton = null;

	public static HomePage getInstance() {
		if (singleton == null)
			singleton = new HomePage();
		return singleton;
	}

	private UserInputPanel userInput;
	private JTabbedPane tabbedPane;
	private JPanel chatPanel;

	private HomePage() {
		userInput = new UserInputPanel();
		tabbedPane = new JTabbedPane();
		this.setLayout(new BorderLayout());

		chatPanel = new JPanel();
		chatPanel.setLayout(new BorderLayout());
		chatPanel.add(LoginPanel.getInstance(), BorderLayout.NORTH);
		chatPanel.add(RoomPanel.getInstance(), BorderLayout.CENTER);
		chatPanel.add(userInput, BorderLayout.SOUTH);

		tabbedPane.addTab("Chat", chatPanel);
		tabbedPane.addTab("Game", GamePanel.getInstance());

		this.add(tabbedPane, BorderLayout.CENTER);
	}

	public void openSocketAction() {// 成功連線後的動作
		LoginPanel.getInstance().lock();
	}

	public void closeSocketAction() {
		LoginPanel.getInstance().unlock();
		RoomPanel.getInstance().getUserListModel().clear();
	}

	public void clientQueryGame(String player) {
		ChatSocketControl.getInstance().clientQueryGame(player);
		tabbedPane.setSelectedIndex(1);
	}

	public void chatMessageAction(String type, String msg) {
		if (type.equals("E") || type.equals("S")) {
			RoomPanel.getInstance().getTextArea().append(msg, 2);
		} else if (type.equals("M")) {
			RoomPanel.getInstance().getTextArea().append(msg, 0);
		} else if (type.equals("MH")) {
			RoomPanel.getInstance().getTextArea().appendHTML(msg);
		} else {
			StringTokenizer st = new StringTokenizer(type, "@");
			st.nextToken();
			String label = st.nextToken();
			RoomPanel.getInstance().getTextArea().append(label + msg, 1);
			for (int i = 2; i < this.tabbedPane.getTabCount(); i++) {
				if (this.tabbedPane.getTitleAt(i).equals(label)) {
					Component c = this.tabbedPane.getComponentAt(i);
					if (c instanceof SecretPanel) {
						((SecretPanel) c).appendMessage(label + msg);
					}
					return;
				}
			}
			SwingUtilities.invokeLater(new Runnable() {
				private String label, msg;

				public Runnable init(String label, String msg) {
					this.label = label;
					this.msg = msg;
					return this;
				}

				public void run() {
					SecretPanel sp = new SecretPanel(label);
					sp.appendMessage(msg);
					tabbedPane.addTab(label, sp);
					tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1,
							new ButtonTabComponent(tabbedPane));

				}
			}.init(label, label + msg));

		}
	}

	public void clientStartGame(int port, String ip) {
		GameClientControl.getInstance().openGameSocket(port, ip);
	}
}
