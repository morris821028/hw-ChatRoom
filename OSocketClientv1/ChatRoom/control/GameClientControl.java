package control;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import game.*;

public class GameClientControl {
	private static GameClientControl singleton = null;

	public static GameClientControl getInstance() {
		if (singleton == null)
			singleton = new GameClientControl();
		return singleton;
	}

	private Socket gameClientSocket;
	private PrintWriter gameClientWriter;
	private BufferedReader gameClientReader;
	private GameClientThread gameClientThread;
	private boolean gameClientRunning;

	private Timer gameTimer;
	Point Gpos;
	Point Rpos;
	int GTeamSpeed;
	int GTeamDir, GnextDir;
	int inverseDir[] = { 2, 3, 0, 1 };

	private GameClientControl() {

	}


	public void clientStartGame(String role) {
		GamePanel.getInstance().initGame(role);
	}

	public void clientExitGame() {
		if (this.gameClientSocket != null) {
			try {
				closeGameSocket();
			} catch (Exception e) {

			}
		}
		// gameServer.gameServerCmdTalk("CLOSE");
		GamePanel.getInstance().startTimer();
	}

	public boolean openGameSocket(int port, String hostIP) {
		try {
			gameClientSocket = new Socket(hostIP, port);
			gameClientWriter = new PrintWriter(new OutputStreamWriter(
					gameClientSocket.getOutputStream(), "UTF-8"));
			gameClientReader = new BufferedReader(new InputStreamReader(
					gameClientSocket.getInputStream(), "UTF-8"));
			gameClientWriter.println("inputName.getText()");
			gameClientWriter.flush();
			gameClientThread = new GameClientThread();
			gameClientThread.start();
			gameClientRunning = true;
			gameTimer = new Timer();
			gameTimer.schedule(new GameProcess(), 5000, 50);
		} catch (Exception ee) {
			System.out.println(ee.getMessage());
		}
		return false;
	}

	public boolean closeGameSocket() {
		try {
			gameClientCmdTalk("CLOSE");
			gameClientRunning = false;
			gameClientSocket.close();
			if (gameTimer != null)
				gameTimer.cancel();
			// this.controlPanel.closeGameAction();
			return true;
		} catch (Exception ee) {
			System.out.println(ee.getMessage());
		}
		return false;
	}

	private void gameClientCmdTalk(String str) {
		if (gameClientWriter != null) {
			gameClientWriter.println(str);
			gameClientWriter.flush();
		}
	}

	public void processSnakeDir(int dir, int team) {
		if (team == 1) {
			if (dir != inverseDir[GTeamDir])
				GnextDir = dir;
		}
	}

	class GameClientThread extends Thread {// listen from gameserver
		public GameClientThread() {
			// System.out.println("listen from gameserver");
		}

		public void run() {
			String msg;
			while (gameClientRunning) {
				try {
					msg = gameClientReader.readLine();
					if (msg == null || msg.length() == 0)
						continue;
					// System.out
					// .println("gameclient receive from gameserver, "
					// + msg);
					StringTokenizer st = new StringTokenizer(msg, "@");
					msg = st.nextToken();
					if (msg.equals("CLOSE")) {
						// System.out.println("do closing");
						clientExitGame();
					} else if (msg.equals("GOK")) {
						clientStartGame("Client");
						int width = GamePanel.getInstance().board.length;
						int height = GamePanel.getInstance().board[0].length;
						Gpos = new Point(10, height / 2);
						Rpos = new Point(width - 11, height / 2);
						GTeamSpeed = 3;
						GTeamDir = 1;
						GnextDir = 1;
						GamePanel.getInstance().moveSnake(10, height / 2, 1);
						GamePanel.getInstance().moveSnake(width - 11,
								height / 2, 2);
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
				} catch (Exception e) {
					// showServerErr("GameClientThread" + e.getMessage());
				}
			}
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
				gameClientCmdTalk(cmd);
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
				gameClientCmdTalk("M@2@" + Rpos.x + "@" + Rpos.y);
			}
			if (Gprops < 0) {
				if (Gprops == -1) {
					String cmd = GamePanel.getInstance().getFireCmd(Gpos.x,
							Gpos.y, GTeamDir);
					// System.out.println("Fire" + cmd);
					gameClientCmdTalk(cmd);
				}
				if (Gprops == -2) {
					String cmd = GamePanel.getInstance().getSunCmd(Gpos.x,
							Gpos.y, GTeamDir);
					gameClientCmdTalk(cmd);
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
			// System.out.println("GameProcess");
		}
	}
}
