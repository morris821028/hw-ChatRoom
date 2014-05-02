package control;

import game.GamePanel;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import socket.*;

public class GameServerControl {
	private static GameServerControl singleton = null;

	public static GameServerControl getInstance() {
		if (singleton == null)
			singleton = new GameServerControl();
		return singleton;
	}

	private GameServerControl() {
		while (!openServer())
			// 嘗試開啟一個沒有使用的 port number 當作 client game server.
			;
	}

	private boolean gameServerRunning = false;
	private ServerSocket gameServerSocket;
	private GameServerThread gameServerThread;
	private GameClientThread gameClientThread;
	private int gameServerPort;
	/* <Game Part Information> */
	private Timer gameTimer;
	Point Gpos;
	Point Rpos;
	int GTeamSpeed;
	int GTeamDir, GnextDir;
	int inverseDir[] = { 2, 3, 0, 1 };

	/* </Game Part Information> */

	private boolean openServer() {
		if (gameServerRunning)
			return false;
		try {
			int port, limitcap;
			port = (int) (Math.random() * 60000) + 1023;
			limitcap = 1;
			gameServerSocket = new ServerSocket(port);
			gameServerThread = new GameServerThread(gameServerSocket, limitcap);
			gameServerThread.start();
			gameServerRunning = true;
			gameServerPort = port;
			return true;
		} catch (java.net.BindException be) {
			System.out.println(be.getMessage());
		} catch (Exception ee) {
			System.out.println(ee.getMessage());
		}
		return false;
	}

	public boolean closeServer() {
		try {
			gameServerCmdTalk("CLOSE");
			gameServerThread.stop();
			gameServerSocket.close();
			gameServerRunning = false;
			return true;
		} catch (Exception ee) {
			System.out.print(ee.getMessage());
		}
		return false;
	}

	public void gameServerCmdTalk(String str) {
		if (gameClientThread != null) {
			// System.out.println("gameServerCmd, " + str);
			gameClientThread.getWriter().println(str);
			gameClientThread.getWriter().flush();
		}
	}

	/**
	 * 固定時間內可以進行改變方向
	 * 
	 * @param dir
	 * @param team
	 */
	public void processSnakeDir(int dir, int team) {
		if (team == 1) {
			if (dir != inverseDir[GTeamDir])
				GnextDir = dir;
		}
	}

	public int getGameServerPort() {
		return this.gameServerPort;
	}

	public void clientServerStartGame(String role) {
		GamePanel.getInstance().initGame(role);
	}

	public void clientServerExitGame() {
		GamePanel.getInstance().startTimer();
		gameServerCmdTalk("CLOSE");
	}

	class GameServerThread extends Thread {
		private ServerSocket serverSocket;
		private int limitCap;

		public GameServerThread(ServerSocket serverSocket, int limitCap) {
			this.serverSocket = serverSocket;
			this.limitCap = limitCap;
		}

		public void run() {
			Socket socket;
			BufferedReader reader;
			PrintWriter writer;
			User user;
			while (true) {
				try {
					socket = gameServerSocket.accept();
					reader = new BufferedReader(new InputStreamReader(
							socket.getInputStream()));
					writer = new PrintWriter(socket.getOutputStream());
					String userName = reader.readLine();
					user = new User(userName, socket.getInetAddress()
							.getHostAddress().toString());
					// System.out.println("GameServerThread, " + userName);
					clientServerStartGame("Server");
					gameClientThread = new GameClientThread(socket, user);
					gameClientThread.start();
					gameTimer = new Timer();
					gameTimer.schedule(new GameProcess(), 5000, 50);
				} catch (Exception ee) {
					// System.out.println("GameServerThread Err "
					// + ee.getMessage());
				}
			}
		}
	}

	class GameClientThread extends Thread {// listen from gameclient
		private Socket gameSocket;
		private User user;
		private BufferedReader reader;
		private PrintWriter writer;

		public GameClientThread(Socket socket, User user) {
			try {
				this.gameSocket = socket;
				this.user = user;
				this.reader = new BufferedReader(new InputStreamReader(
						gameSocket.getInputStream(), "UTF-8"));
				this.writer = new PrintWriter(new OutputStreamWriter(
						gameSocket.getOutputStream(), "UTF-8"));
				// System.out.println("send GOK command");
				writer.println("GOK");
				writer.flush();
				int width = GamePanel.getInstance().board.length;
				int height = GamePanel.getInstance().board[0].length;
				Gpos = new Point(10, height / 2);
				Rpos = new Point(width - 11, height / 2);
				GTeamSpeed = 3;
				GTeamDir = 1;
				GnextDir = 1;
				GamePanel.getInstance().moveSnake(10, height / 2, 1);
				GamePanel.getInstance().moveSnake(width - 11, height / 2, 2);
				writer.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void closeGameSocket() {
			try {
				clientServerExitGame();
				gameTimer.cancel();
				// System.out.println("game socket close");
				gameSocket.close();
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
			/*
			 * System.out.println(inputName.getText() +
			 * "receive game client send, " + msg);
			 */
			StringTokenizer st = new StringTokenizer(msg, "@");
			msg = st.nextToken();
			if (msg.equals("CLOSE")) {
				closeGameSocket();
			} else if (msg.equals("D")) {
				int team = Integer.parseInt(st.nextToken());
				int dir = Integer.parseInt(st.nextToken());
				GameServerControl.this.processSnakeDir(dir, team);
			} else if (msg.equals("M")) {
				int team = Integer.parseInt(st.nextToken());
				int x = Integer.parseInt(st.nextToken());
				int y = Integer.parseInt(st.nextToken());
				GamePanel.getInstance().moveSnake(x, y, team);
			} else if (msg.equals("E")) {
				int x = Integer.parseInt(st.nextToken());
				int y = Integer.parseInt(st.nextToken());
				GamePanel.getInstance().eraseBoard(x, y);
			} else if (msg.equals("P")) {
				int x = Integer.parseInt(st.nextToken());
				int y = Integer.parseInt(st.nextToken());
				int p = Integer.parseInt(st.nextToken());
				GamePanel.getInstance().setProps(x, y, (byte) p);
			} else if (msg.equals("EF")) {
				int x = Integer.parseInt(st.nextToken());
				int y = Integer.parseInt(st.nextToken());
				int dir = Integer.parseInt(st.nextToken());
				GamePanel.getInstance().getFireCmd(x, y, dir);
			} else if (msg.equals("ES")) {
				int x = Integer.parseInt(st.nextToken());
				int y = Integer.parseInt(st.nextToken());
				int dir = Integer.parseInt(st.nextToken());
				GamePanel.getInstance().getSunCmd(x, y, dir);
			} else if (msg.equals("ER")) {
				int x = Integer.parseInt(st.nextToken());
				int y = Integer.parseInt(st.nextToken());
				int dir = Integer.parseInt(st.nextToken());
				int cnt = Integer.parseInt(st.nextToken());
				GamePanel.getInstance().eraseRandom(x, y, dir, cnt);
			}
		}

		public BufferedReader getReader() {
			return reader;
		}

		public PrintWriter getWriter() {
			return writer;
		}
	}

	class GameProcess extends TimerTask {
		private int timeCounter = 0;
		int dx[] = { 0, 1, 0, -1 };
		int dy[] = { -1, 0, 1, 0 };
		int mdx[] = { 0, -1, 0, 1 };
		int mdy[] = { -1, 0, 1, 0 };

		public void run() {
			timeCounter++;
			if (timeCounter % 15 == 0) {
				String cmd = GamePanel.getInstance().eraseRandom();
				gameServerCmdTalk(cmd);
			}
			int Gprops = 0, Rprops = 0;
			if (timeCounter % (6 - GTeamSpeed) == 0) {
				GnextDir = GamePanel.getInstance().autoGuided(1, GTeamDir,
						GnextDir);
				GTeamDir = GnextDir;
				Gpos.x = Gpos.x + dx[GTeamDir];
				Gpos.y = Gpos.y + dy[GTeamDir];
				Rpos.x = Rpos.x + mdx[GTeamDir];
				Rpos.y = Rpos.y + mdy[GTeamDir];
				Gprops = GamePanel.getInstance().moveSnake(Gpos.x, Gpos.y, 1);
				gameServerCmdTalk("M@2@" + Rpos.x + "@" + Rpos.y);
			}
			if (Gprops < 0) {
				if (Gprops == -1) {
					String cmd = GamePanel.getInstance().getFireCmd(Gpos.x,
							Gpos.y, GTeamDir);
					gameServerCmdTalk(cmd);
				}
				if (Gprops == -2) {
					String cmd = GamePanel.getInstance().getSunCmd(Gpos.x,
							Gpos.y, GTeamDir);
					gameServerCmdTalk(cmd);
				}
				if (Gprops == -3) {
					if (GTeamSpeed > 1)
						GTeamSpeed--;
				}
				if (Gprops == -4) {
					if (GTeamSpeed < 5)
						GTeamSpeed++;
				}
			}
			if (timeCounter % 150 == 0) {
				String cmd = GamePanel.getInstance().addProps();
				gameServerCmdTalk(cmd);
			}
		}
	}
}
