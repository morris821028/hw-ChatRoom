package Server;

import java.net.ServerSocket;
import java.net.Socket;

import java.io.*;

import java.util.*;

import java.lang.Thread;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;

public class ControlPanel extends JPanel {
	private JTextField inputPort;
	private JTextField inputLimitCap;
	private JToggleButton inputSwitch;
	private JPanel northPanel;

	private JTextArea contentArea;
	private JList userList;
	private DefaultListModel userListModel;
	private JList gameList;
	private DefaultListModel gameListModel;
	private JPanel centerPanel;

	private JTextField inputMsg;
	private JButton sendBtn;
	private JPanel southPanel;

	private boolean serverRunning = false;
	private ServerSocket serverSocket;
	private ServerThread serverThread;
	private ArrayList<ClientThread> clientList;
	private FileServer fileServer;
	
	public ControlPanel() {
		createNorthPanel();
		createCenterPanel();
		createSouthPanel();
		fileServer = new FileServer(this);
		this.setLayout(new BorderLayout());
		this.add(northPanel, BorderLayout.NORTH);
		this.add(centerPanel, BorderLayout.CENTER);
		this.add(southPanel, BorderLayout.SOUTH);
	}
	
	private void createNorthPanel() {
		Font displayFont = new Font("Serif", Font.BOLD, 18);
		northPanel = new JPanel();
		northPanel.setBorder(new TitledBorder("Setting"));
		northPanel.setLayout(new GridLayout(1, 5));

		inputLimitCap = new JTextField("10");
		inputPort = new JTextField("4096");
		inputSwitch = new JToggleButton("OFF");

		inputLimitCap.setFont(displayFont);
		inputPort.setFont(displayFont);
		inputSwitch.setFont(displayFont);
		inputSwitch.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (openServer()) {
						inputSwitch.setText("ON");
						inputPort.setEnabled(false);
						inputLimitCap.setEnabled(false);
					} else if (!serverRunning) {
						inputSwitch.setSelected(false);
					}
				} else if (e.getStateChange() == ItemEvent.DESELECTED) {
					if (closeServer()) {
						inputSwitch.setText("OFF");
						inputPort.setEnabled(true);
						inputLimitCap.setEnabled(true);
					} else if (serverRunning) {
						inputSwitch.setSelected(true);
					}
				}
			}
		});

		JLabel showlabel;
		showlabel = new JLabel("Limit Capacity");
		showlabel.setFont(displayFont);
		northPanel.add(showlabel);
		northPanel.add(inputLimitCap);
		showlabel = new JLabel("Port Number");
		showlabel.setFont(displayFont);
		northPanel.add(showlabel);
		northPanel.add(inputPort);
		northPanel.add(inputSwitch);
	}

	private void createCenterPanel() {
		Font displayFont = new Font("Serif", Font.BOLD, 18);
		Font displayFontII = new Font("Serif", Font.BOLD, 24);
		centerPanel = new JPanel();
		centerPanel.setBorder(new TitledBorder("Server Information"));
		userListModel = new DefaultListModel();
		gameListModel = new DefaultListModel();

		userList = new JList(userListModel);
		gameList = new JList(gameListModel);
		contentArea = new JTextArea();

		userList.setBackground(Color.BLACK);
		userList.setForeground(Color.GREEN);
		userList.setFont(displayFont);
		gameList.setBackground(Color.BLACK);
		gameList.setForeground(Color.GREEN);
		gameList.setFont(displayFont);
		contentArea.setBackground(Color.BLACK);
		contentArea.setForeground(Color.ORANGE);
		contentArea.setFont(displayFontII);

		JScrollPane leftPane = new JScrollPane(userList);
		JScrollPane centerPane = new JScrollPane(gameList);
		JScrollPane rightPane = new JScrollPane(contentArea);
		JSplitPane splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				leftPane, centerPane);
		splitPane1.setDividerLocation(100);
		JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				splitPane1, rightPane);
		splitPane2.setDividerLocation(250);

		leftPane.setBorder(new TitledBorder("User List"));
		centerPane.setBorder(new TitledBorder("Game List"));
		rightPane.setBorder(new TitledBorder("Room"));

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
						/*System.out.println(sb.getValue() + sb.getHeight()
								+ ", " + sb.getMaximum() + locked);*/
						if (locked == true) {
							sb.setValue(sb.getMaximum());
						}
					}
				}.init(rightPane.getVerticalScrollBar()));
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(splitPane2, BorderLayout.CENTER);
	}

	private void createSouthPanel() {
		Font displayFont = new Font("Serif", Font.BOLD, 18);
		Font displayFontII = new Font("Serif", Font.BOLD, 24);
		southPanel = new JPanel();
		southPanel.setBorder(new TitledBorder("Server Broadcasting"));

		inputMsg = new JTextField();
		sendBtn = new JButton("SEND");

		inputMsg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				serverBroadcasting();// touch [Enter] key.
			}
		});
		sendBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				serverBroadcasting();
			}
		});

		southPanel.setLayout(new BorderLayout());
		southPanel.add(inputMsg, BorderLayout.CENTER);
		southPanel.add(sendBtn, BorderLayout.EAST);
	}

	private boolean openServer() {
		if (serverRunning)
			return false;
		try {
			int port, limitcap;
			port = Integer.parseInt(this.inputPort.getText());
			limitcap = Integer.parseInt(this.inputLimitCap.getText());
			serverSocket = new ServerSocket(port);
			serverThread = new ServerThread(serverSocket, limitcap);
			serverThread.start();
			clientList = new ArrayList<ClientThread>();
			serverRunning = true;
			return true;
		} catch (java.net.BindException be) {
			showServerErr(be.getMessage());
		} catch (Exception ee) {
			showServerErr(ee.getMessage());
		}
		return false;
	}

	private boolean closeServer() {
		try {
			fileServer.closeServer();
			serverCmdBroadcasting("CLOSE@" + "\r\n");
			serverThread.stop();
			serverSocket.close();
			serverRunning = false;
			userListModel.clear();
			gameListModel.clear();
			for (int i = 0; i < clientList.size(); i++)
				clientList.get(i).stop();
			clientList.clear();
			return true;
		} catch (Exception ee) {
			showServerErr(ee.getMessage());
		}
		return false;
	}

	private void showServerErr(String err) {
		contentArea.append("ServerErr[" + err + "]\r\n");
	}

	private void serverCmdBroadcasting(String str) {
		for (int i = clientList.size() - 1; i >= 0; i--) {
			clientList.get(i).getWriter().println(str);
			clientList.get(i).getWriter().flush();
		}
		contentArea.append("Server Command> " + str);
		inputMsg.setText("");
	}

	private void serverCmdSpecialLine(String name, String str) {
		for (int i = clientList.size() - 1; i >= 0; i--) {
			if (clientList.get(i).user.getUserName().equals(name)) {
				clientList.get(i).getWriter().println(str);
				clientList.get(i).getWriter().flush();
				return;
			}
		}
	}

	private void serverBroadcasting() {
		if (clientList != null) {
			for (int i = clientList.size() - 1; i >= 0; i--) {
				clientList.get(i).getWriter()
						.println("S@" + inputMsg.getText() + "\r\n");
				clientList.get(i).getWriter().flush();
			}
		}
		contentArea.append("Server> " + inputMsg.getText() + "\r\n");
		inputMsg.setText("");
	}

	private void checkGameQueue() {
		if (gameListModel.size() > 1) {
			int playeri = -1, playerj = -1;
			String player1 = (String) gameListModel.get(0);
			gameListModel.remove(0);
			String player2 = (String) gameListModel.get(0);
			gameListModel.remove(0);
			for (int i = 0; i < clientList.size(); i++) {
				if (clientList.get(i).user.getUserName().equals(player1)) {
					playeri = i;
				}
				if (clientList.get(i).user.getUserName().equals(player2)) {
					playerj = i;
				}
			}
			if (playeri < 0 || playerj < 0) {
				/*System.out.println("checkGameQueue Err 254");*/
				return;
			}
			/*System.out.println("GLS@" + clientList.get(playeri).gameServerPort
					+ "@" + clientList.get(playerj).user.getIP());
			System.out.println("GameServer, "
					+ clientList.get(playeri).user.getUserName());
			System.out.println("GameClient, "
					+ clientList.get(playerj).user.getUserName());*/
			clientList
					.get(playerj)
					.getWriter()
					.println(
							"GLS@" + clientList.get(playeri).gameServerPort
									+ "@"
									+ clientList.get(playeri).user.getIP());
			clientList.get(playerj).getWriter().flush();
		}
	}

	class ServerThread extends Thread {
		private ServerSocket serverSocket;
		private int limitCap;

		public ServerThread(ServerSocket serverSocket, int limitCap) {
			this.serverSocket = serverSocket;
			this.limitCap = limitCap;
		}

		public void run() {
			Socket socket;
			BufferedReader reader;
			PrintWriter writer;
			ClientThread clientThread;
			User user;
			while (true) {
				try {
					socket = serverSocket.accept();
					reader = new BufferedReader(new InputStreamReader(
							socket.getInputStream()));
					writer = new PrintWriter(socket.getOutputStream());
					String msg = reader.readLine();
					StringTokenizer st = new StringTokenizer(msg, "@");
					String userName = st.nextToken();
					int gameServerPort = Integer.parseInt(st.nextToken());
					if (userListModel.size() == limitCap) {
						writer.println("E@Server busy.");
						writer.flush();
						socket.close();
						continue;
					}
					boolean sameNameFlag = false;
					for (int i = 0; i < userListModel.size(); i++) {
						if (userListModel.get(i).toString().equals(userName)) {
							sameNameFlag = true;
						}
					}
					if (sameNameFlag) {
						writer.println("E@Same Name User");
						writer.flush();
						socket.close();
						continue;
					}
					userListModel.addElement(userName);
					user = new User(userName, socket.getInetAddress()
							.getHostAddress().toString());
					clientThread = new ClientThread(socket, user,
							gameServerPort);
					clientThread.start();
					clientList.add(clientThread);
					inputMsg.setText(userName + " has entered the room.");
					serverBroadcasting();
					serverCmdBroadcasting("A@" + user.getUserName() + "\r\n");
					/*System.out.println(userName + gameServerPort);
					System.out.println("ServerThread socket accept");
					System.out.println(socket.getPort());
					System.out
							.println(socket.getInetAddress().getHostAddress());*/
				} catch (Exception ee) {
					System.out.println("ServerThread Err " + ee.getMessage());
				}
			}
		}
	}

	class ClientThread extends Thread {// receive message to client
		private Socket socket;
		private User user;
		private BufferedReader reader;
		private PrintWriter writer;
		private int gameServerPort;

		public ClientThread(Socket socket, User user, int gameServerPort) {
			try {
				this.socket = socket;
				this.user = user;
				this.gameServerPort = gameServerPort;
				this.reader = new BufferedReader(new InputStreamReader(
						socket.getInputStream(), "UTF-8"));
				this.writer = new PrintWriter(new OutputStreamWriter(
						socket.getOutputStream(), "UTF-8"));
				for (int i = clientList.size() - 1; i >= 0; i--)
					writer.println("A@" + clientList.get(i).user.getUserName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void closeSocket() {
			try {
				String msg = user.getUserName() + " has left the room.\r\n";
				inputMsg.setText(msg);
				serverBroadcasting();
				serverCmdBroadcasting("D@" + user.getUserName());

				userListModel.removeElement(user.getUserName());
				for (int i = 0; i < clientList.size(); i++) {
					if (clientList.get(i).user.getUserName().equals(
							user.getUserName())) {
						clientList.remove(i);
						break;
					}

				}
				socket.close();
				this.stop();
			} catch (Exception e) {
			}
		}

		public void run() {
			String msg;
			while (true) {
				try {
					msg = reader.readLine();
					dispatcherMessage(msg);
				} catch (Exception e) {

				}
			}
		}

		public void dispatcherMessage(String msg) {
			if (msg == null || msg.length() == 0)
				return;
			StringTokenizer st = new StringTokenizer(msg, "@");
			msg = st.nextToken();
			if (msg.equals("T")) {
				String sentence = st.nextToken();
				while (st.hasMoreTokens()) {
					sentence += "@" + st.nextToken();
				}
				sentence = this.user.getUserName() + "> " + sentence + "\r\n";
				contentArea.append(sentence);
				serverCmdBroadcasting("M@" + sentence);
			} else if (msg.equals("TS")) {
				String to = st.nextToken();
				String sentence = st.nextToken();
				while (st.hasMoreTokens()) {
					sentence += "@" + st.nextToken();
				}
				//contentArea.append(sentence);
				serverCmdSpecialLine(to, "MS@" + this.user.getUserName() + "@ >> " + sentence + "\r\n");
				serverCmdSpecialLine(this.user.getUserName(), "MS@" + to + "@ << " + sentence + "\r\n");
			} else if (msg.equals("TH")) {
				String sentence = st.nextToken();
				while (st.hasMoreTokens())
					sentence += "@" + st.nextToken();
				//contentArea.append(sentence);
				serverCmdBroadcasting("MH@" + sentence);
			}else if (msg.equals("GR")) {// 請求隨機對手
				gameListModel.addElement(this.user.getUserName());
				checkGameQueue();
			} else if (msg.equals("GE")) {// 放棄遊戲
				gameListModel.removeElement(this.user.getUserName());
			} else if (msg.equals("CLOSE")) {
				closeSocket();
			}
		}

		public BufferedReader getReader() {
			return reader;
		}

		public PrintWriter getWriter() {
			return writer;
		}
	}
}
