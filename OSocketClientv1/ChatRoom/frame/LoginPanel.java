package frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

import control.ChatSocketControl;

public class LoginPanel extends JPanel {
	private static LoginPanel singleton = null;

	public static LoginPanel getInstance() {
		if (singleton == null)
			singleton = new LoginPanel();
		return singleton;
	}

	private JTextField inputPort;// 連向 server 的 port
	private JTextField inputHost;// 連向 server 的 ip
	private JTextField inputName;// 進入聊天室的使用者代稱
	private JToggleButton inputSwitch;// 開啟連線
	private JButton inputRandom; // 隨機對戰
	private JButton inputAssign; // 指定對戰

	private LoginPanel() {
		this.setBorder(new TitledBorder("Login Setting"));
		this.setLayout(new FlowLayout(FlowLayout.LEFT));

		inputHost = new JTextField("140.115.205.194");
		inputPort = new JTextField("4096");
		inputName = new JTextField("GUEST");
		inputSwitch = new JToggleButton("OFF");
		inputRandom = new JButton("GAME");
		inputAssign = new JButton("Invite");

		inputSwitch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (inputSwitch.getText().equals("OFF")) {
					try {

						ChatSocketControl.getInstance().openSocket(
								Integer.parseInt(inputPort.getText()),
								inputHost.getText(),
								new String(inputName.getText().getBytes(),
										"UTF-8"));

					} catch (Exception error) {
						System.out.println(this.getClass() + "252" + error);
					}
				} else {
					ChatSocketControl.getInstance().closeSocket();
				}
			}
		});
		inputRandom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 HomePage.getInstance().clientQueryGame("");
			}
		});
		inputAssign.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Building ...");
			}
		});

		JLabel showlabel;
		showlabel = new JLabel("Host");
		this.add(showlabel);
		this.add(inputHost);
		showlabel = new JLabel("Port");
		this.add(showlabel);
		this.add(inputPort);
		showlabel = new JLabel("Name");
		this.add(showlabel);
		this.add(inputName);
		this.add(inputSwitch);
		this.add(inputRandom);
		this.add(inputAssign);
	}

	public void lock() {
		inputPort.setEnabled(false);
		inputHost.setEnabled(false);
		inputName.setEnabled(false);
		inputSwitch.setSelected(true);
		inputSwitch.setText("ON");
	}

	public void unlock() {
		inputPort.setEnabled(true);
		inputHost.setEnabled(true);
		inputName.setEnabled(true);
		inputSwitch.setSelected(false);
		inputSwitch.setText("OFF");
	}
	
	public String getUserName() {
		return inputName.getText();
	}
}
