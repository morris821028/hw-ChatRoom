package frame;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

import control.ChatSocketControl;
import control.WindowControl;
import frame.fileupload.FileUploadFrame;
import frame.preferences.*;
import java.util.*;
public class UserInputPanel extends JPanel {
	private JComboBox inputOption;
	private JTextField inputMsg;
	private JButton sendBtn;
	private JButton emotionBtn;
	private JPanel emotionPanel;
	private JDialog emotionDialog;
	public UserInputPanel() {
		Font displayFont = new Font("Serif", Font.BOLD, 18);
		String option[] = { "To All", "Whisper" };

		this.setBorder(new TitledBorder("Input Message"));
		inputOption = new JComboBox(option);
		inputMsg = new JTextField();
		sendBtn = new JButton("SEND");
		inputOption.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (e.getItem().toString().equals("Whisper")) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								if (inputOption.getItemCount() >= 5)
									inputOption.removeItemAt(4);
								DefaultListModel userListModel = RoomPanel
										.getInstance().getUserListModel();
								String choices[] = new String[userListModel
										.getSize()];
								if (choices.length == 0) {
									inputOption.setSelectedIndex(0);
									return;
								}
								for (int i = 0; i < userListModel.size(); i++)
									choices[i] = userListModel.get(i)
											.toString();
								String input = (String) JOptionPane
										.showInputDialog(null, "選擇密語對象",
												"請在清單中選擇",
												JOptionPane.QUESTION_MESSAGE,
												null, choices, choices[0]);
								if (input == null) {
									inputOption.setSelectedIndex(0);
									return;
								}
								inputOption.insertItemAt(input,
										inputOption.getItemCount());
								inputOption.setSelectedIndex(inputOption
										.getItemCount() - 1);
							}
						});
					}
				}
			}
		});
		inputMsg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String who;
				if (inputOption.getSelectedIndex() == 0)
					who = "";
				else
					who = inputOption.getSelectedItem().toString();
				ChatSocketControl.getInstance().clientTalk(inputMsg.getText(),
						who);
				inputMsg.setText(""); // touch [Enter] key
			}
		});
		sendBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String who;
				if (inputOption.getSelectedIndex() == 0)
					who = "";
				else
					who = inputOption.getSelectedItem().toString();
				ChatSocketControl.getInstance().clientTalk(inputMsg.getText(),
						who);
				inputMsg.setText("");

			}
		});

		JButton htmlBtn = new JButton();
		htmlBtn.setContentAreaFilled(false);
		htmlBtn.setIcon(WindowControl.getInstance().getImageIcon("HTML"));
		htmlBtn.setBorderPainted(false);
		htmlBtn.setToolTipText("超連結");
		htmlBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String urlstr = JOptionPane.showInputDialog("上傳網址 URL");
				if (urlstr == null || urlstr.trim().length() == 0)
					return;
				urlstr = MTextArea.transferHyperlink(urlstr);
				if (urlstr.length() > 0) {
					String attachInfo = "<div class=\"normal\"><font>"
							+ LoginPanel.getInstance().getUserName()
							+ "&nbsp;&gt;&nbsp Upload Hyperlink</font></div>";
					ChatSocketControl.getInstance().clientCmdTalk(
							"TH@" + attachInfo + urlstr);
				}

			}
		});

		JButton imgBtn = new JButton();
		imgBtn.setContentAreaFilled(false);
		imgBtn.setBorder(BorderFactory.createEmptyBorder());
		imgBtn.setIcon(WindowControl.getInstance().getImageIcon("IMG"));
		imgBtn.setBorderPainted(false);
		imgBtn.setToolTipText("上傳圖片網址");
		imgBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String urlstr = JOptionPane
						.showInputDialog("圖片網址 URL, (*.png, *.gif, *.jpeg)");
				if (urlstr == null || urlstr.trim().length() == 0)
					return;
				urlstr = MTextArea.transferImageHyperlink(urlstr);
				String attachInfo = "<div class=\"normal\"><font>"
						+ LoginPanel.getInstance().getUserName()
						+ "&nbsp;&gt;&nbsp Upload Image</font></div>";
				ChatSocketControl.getInstance().clientCmdTalk(
						"TH@" + attachInfo + urlstr);

			}
		});

		JButton gameBtn = new JButton();
		gameBtn.setContentAreaFilled(false);
		gameBtn.setBorder(BorderFactory.createEmptyBorder());
		gameBtn.setIcon(WindowControl.getInstance().getImageIcon("GAME"));
		gameBtn.setBorderPainted(false);
		gameBtn.setToolTipText("遊戲說明");
		gameBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					try {
						JDialog dialog;
						JPanel panel = new JPanel();
						JLabel imglabel = new JLabel(new ImageIcon(
								WindowControl.class
										.getResource("images/gameinfo.gif")));
						panel.setLayout(new BorderLayout());
						panel.add(imglabel, BorderLayout.CENTER);
						dialog = new JDialog();
						dialog.setContentPane(imglabel);
						dialog.pack();
						dialog.setLocationRelativeTo(null);
						dialog.setVisible(true);
					} catch (Exception err) {
						System.out.println("Err 419" + err);
					}
				} catch (Exception ee) {
					System.out.println("Err 422" + ee);
				}
			}
		});

		JButton settingBtn = new JButton();
		settingBtn.setContentAreaFilled(false);
		settingBtn.setBorder(BorderFactory.createEmptyBorder());
		settingBtn.setIcon(WindowControl.getInstance().getImageIcon("SETTING"));
		settingBtn.setBorderPainted(false);
		settingBtn.setToolTipText("設定 - 建造中");
		settingBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new PreferencesFrame();
			}
		});

		JButton infoBtn = new JButton();
		infoBtn.setContentAreaFilled(false);
		infoBtn.setBorder(BorderFactory.createEmptyBorder());
		infoBtn.setIcon(WindowControl.getInstance().getImageIcon("INFO"));
		infoBtn.setBorderPainted(false);
		infoBtn.setToolTipText("關於");
		infoBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String showText = WindowControl.getInstance().getDocumentText(
						"aboutinfo.txt");
				JOptionPane.showMessageDialog(null, showText);
			}
		});
		JButton uploadBtn = new JButton();
		uploadBtn.setContentAreaFilled(false);
		uploadBtn.setBorder(BorderFactory.createEmptyBorder());
		uploadBtn.setIcon(WindowControl.getInstance().getImageIcon("UPLOAD"));
		uploadBtn.setBorderPainted(false);
		uploadBtn.setToolTipText("上傳圖片");
		uploadBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					FileUploadFrame.getInstance().setVisible(true);
				} catch (Exception ee) {
					System.out.println(ee.getMessage());
				}
			}
		});
		emotionBtn = new JButton();
		try {
			emotionBtn.setText(new String("(・_・)".getBytes(), "UTF-8"));
		} catch (Exception e) {

		}
		emotionPanel = this.createEmoticonPanel();
		emotionBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				emotionDialog = new JDialog();
				emotionDialog.getContentPane().add(emotionPanel);
				emotionDialog.setResizable(false);
				emotionDialog.pack();
				emotionDialog.setSize(350, emotionDialog.getHeight());
				emotionDialog.setLocationRelativeTo(RoomPanel.getInstance());
				//dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
				emotionDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				emotionDialog.setVisible(true);
			}
		});

		JPanel upPanel = new JPanel();
		JPanel downPanel = new JPanel();
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.LEFT);
		upPanel.setLayout(layout);
		upPanel.add(htmlBtn);
		upPanel.add(imgBtn);
		upPanel.add(uploadBtn);
		upPanel.add(gameBtn);
		upPanel.add(settingBtn);
		upPanel.add(infoBtn);
		upPanel.add(emotionBtn);

		downPanel.setLayout(new BorderLayout());
		downPanel.add(inputOption, BorderLayout.WEST);
		downPanel.add(inputMsg, BorderLayout.CENTER);
		downPanel.add(sendBtn, BorderLayout.EAST);
		this.setLayout(new BorderLayout());
		this.add(upPanel, BorderLayout.NORTH);
		this.add(downPanel, BorderLayout.SOUTH);
	}

	public void setWhisper(String name) {
		inputOption.setEnabled(false);
		inputOption.getModel().setSelectedItem(name);
	}

	public JPanel createEmoticonPanel() {
		JPanel p = new JPanel();
		String s = WindowControl.getInstance().getDocumentText("emoticon.txt");
		StringTokenizer st = new StringTokenizer(s, "\n");
		p.setLayout(new GridLayout(20, 3));
		p.setOpaque(false);
		while(st.hasMoreTokens()) {
			String token = st.nextToken();
			JButton btn = new JButton(token);
			btn.setOpaque(false);
			btn.setForeground(Color.WHITE);
			btn.setActionCommand(token);
			btn.setContentAreaFilled(false);
			btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					inputMsg.setText(inputMsg.getText() + " " + e.getActionCommand());
					emotionDialog.dispose();
				}
			});
			p.add(btn);
		}
		return p;
	}
}
