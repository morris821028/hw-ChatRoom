package control;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import frame.HomePage;
import frame.RoomPanel;

public class ChatSocketControl {
	private static ChatSocketControl singleton = null;
	public static ChatSocketControl getInstance() {
		if(singleton == null)
			singleton = new ChatSocketControl();
		return singleton;
	}
	private boolean clientRunning = false;
	private Socket clientSocket;
	private PrintWriter clientWriter;
	private BufferedReader clientReader;
	private ClientThread clientThread;

	class ClientThread extends Thread {
		public void run() {
			String msg;
			while (clientRunning) {
				try {
					msg = clientReader.readLine();
					if (msg == null || msg.length() == 0)
						continue;
					// System.out.println("client receive from server, " + msg);
					StringTokenizer st = new StringTokenizer(msg, "@");
					msg = st.nextToken();
					if (msg.equals("CLOSE")) {
						HomePage.getInstance().chatMessageAction("S", "----- Server Close -----");
						HomePage.getInstance().closeSocketAction();
						closeSocket();
					} else if (msg.equals("S")) {
						msg = "System> " + st.nextToken() + "\r\n";
						HomePage.getInstance().chatMessageAction("S", msg);
					} else if (msg.equals("M")) {
						msg = st.nextToken();
						while (st.hasMoreTokens())
							msg += "@" + st.nextToken();
						HomePage.getInstance().chatMessageAction("M", msg + "\r\n");
					} else if (msg.equals("MS")) {
						String who = st.nextToken();
						msg = st.nextToken();
						while (st.hasMoreTokens())
							msg += "@" + st.nextToken();
						HomePage.getInstance().chatMessageAction("MS@" + who, msg
								+ "\r\n");
						// contentArea.append(msg + "\r\n", 1);
					} else if (msg.equals("MH")) {
						msg = st.nextToken();
						while (st.hasMoreTokens())
							msg += "@" + st.nextToken();
						HomePage.getInstance().chatMessageAction("MH", msg);
						// contentArea.appendHTML(msg);
					} else if (msg.equals("A")) {
						RoomPanel.getInstance().getUserListModel().addElement(st.nextToken());
					} else if (msg.equals("D")) {
						RoomPanel.getInstance().getUserListModel().removeElement(st.nextToken());
					} else if (msg.equals("E")) {
						msg = "System> " + st.nextToken() + "\r\n";
						HomePage.getInstance().chatMessageAction("E", msg);
						/*
						 * contentArea.append( "System> " + st.nextToken() +
						 * "\r\n", 2);
						 */
						// System.out.println("do closing");
						HomePage.getInstance().closeSocketAction();
						closeSocket();
					} else if (msg.equals("GLS")) {
						int port = Integer.parseInt(st.nextToken());
						String ip = st.nextToken();
						HomePage.getInstance().clientStartGame(port, ip);
					}
				} catch (Exception e) {
					e.getStackTrace();
					showServerErr("ClientThread 99" + e.getMessage());
				}
			}
		}
	}

	/**
	 * 嘗試連線至指定伺服器 ，成功回傳 true，反之 false。
	 * 
	 * @return true or false
	 */
	public boolean openSocket(int port, String host, String name) {
		if (clientRunning)
			return false;
		try {
			if (name.length() == 0)
				return false;
			clientSocket = new Socket(host, port);
			clientWriter = new PrintWriter(new OutputStreamWriter(
					clientSocket.getOutputStream(), "UTF-8"));
			clientReader = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream(), "UTF-8"));
			clientCmdTalk(name + "@" + GameServerControl.getInstance().getGameServerPort());
			clientThread = new ClientThread();
			clientThread.start();
			clientRunning = true;
			HomePage.getInstance().openSocketAction();
			return true;
		} catch (Exception ee) {
			showServerErr(ee.getMessage());
		}
		try {
			HomePage.getInstance().closeSocketAction();
		} catch (Exception ee) {
			showServerErr(ee.getMessage());
		}
		return false;
	}

	/**
	 * 關閉與伺服器的連線，成功回傳 true，反之 false。
	 * 
	 * @return true or false
	 */
	public boolean closeSocket() {
		if (!clientRunning)
			return true;
		try {
			clientCmdTalk("CLOSE");
			clientRunning = false;
			clientSocket.close();
			clientThread.stop();
			RoomPanel.getInstance().getUserListModel().clear();
			HomePage.getInstance().chatMessageAction("S", "You have left the room.\r\n");
			HomePage.getInstance().closeSocketAction();
			return true;
		} catch (Exception ee) {
			showServerErr(ee.getMessage());
		}
		try {
			HomePage.getInstance().openSocketAction();
		} catch (Exception ee) {
			showServerErr(ee.getMessage());
		}
		return false;
	}

	/**
	 * 在面板中顯示網路錯誤訊息
	 * 
	 * @param err
	 *            錯誤訊息的字串
	 */
	private void showServerErr(String err) {
		HomePage.getInstance().chatMessageAction("S", "Err[" + err + "]\r\n");
	}

	/**
	 * 使用者向伺服器請求遊戲連線
	 * 
	 * @param player
	 *            當 player 為空，即隨機對戰，反之為指定對戰。
	 */
	public void clientQueryGame(String player) {
		if (player.length() == 0)
			clientCmdTalk("GR");
	}

	public void clientExitGame() {
		clientCmdTalk("GE");
	}

	/**
	 * 直接傳送指令，例如離開指令、請求遊戲 ... 等。
	 * 
	 * @param str
	 *            指令碼
	 */
	public void clientCmdTalk(String str) {
		if (clientWriter != null) {
			clientWriter.println(str);
			clientWriter.flush();
		}
	}

	/**
	 * 如果 towho 為空或者為空串，即為傳送給所有人。反之送給指定對象。
	 * 
	 * @param str
	 *            傳送的訊息
	 * @param towho
	 *            傳送對象
	 */
	public void clientTalk(String str, String towho) {
		if (clientWriter != null) {
			if (towho == null || towho.length() == 0)
				clientWriter.println("T@" + str);
			else
				clientWriter.println("TS@" + towho + "@" + str);
			clientWriter.flush();
		}
	}
}
