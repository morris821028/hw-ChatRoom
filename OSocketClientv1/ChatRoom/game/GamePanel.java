package game;

import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import control.*;
import socket.*;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * 遊戲的圖型面板，只由 Game Server 創建道具。要將狀態轉到 Game Server/Client 傳送給對方。
 * 
 * @author owner
 * 
 */
public class GamePanel extends JPanel implements
/* KeyAdapter */KeyEventDispatcher, ActionListener {
	private static GamePanel singleton = null;

	public static GamePanel getInstance() {
		if (singleton == null)
			singleton = new GamePanel();
		return singleton;
	}

	private Timer timer = new Timer(100, this);
	private static Hashtable<String, ImageIcon> imgTable = GamePanel
			.loadImage();
	private String role;
	private AnimationProgress beginAnimation;
	public byte[][] board;
	private int[][] boardstep;
	private int stepcnt;
	private int mnstepcnt, mxstepcnt, showtimeslot;
	private boolean gameover = true, gameresult;
	Point G;
	Point R;
	int dx[] = { 0, 1, 0, -1 };
	int dy[] = { -1, 0, 1, 0 };

	static ImageIcon background;
	static ImageIcon sbody;
	static ImageIcon sghead;
	static ImageIcon srhead;
	static ImageIcon efire;
	static ImageIcon esun;
	static ImageIcon eice;
	static ImageIcon ewater;
	static ImageIcon gameFail;
	static ImageIcon gameVictory;

	public static Hashtable<String, ImageIcon> loadImage() {
		Hashtable<String, ImageIcon> imgTable = new Hashtable<String, ImageIcon>();
		BufferedImage img = null;
		String fileName[] = { "background.jpg", "sbody.png", "srhead.png",
				"sghead.png", "Efire.png", "Esun.png", "Eice.png",
				"Ewater.png", "fail.png", "victory.png" };
		String fileIdx[] = { "background", "sbody", "srhead", "sghead",
				"Efire", "Esun", "Eice", "Ewater", "fail", "victory" };
		try {
			for (int i = 0; i < fileName.length; i++) {
				// img = ImageIO.read(new File(fileName[i]));
				img = ImageIO.read(GamePanel.class.getResource("images/"
						+ fileName[i]));
				imgTable.put(fileIdx[i], new ImageIcon(img));
			}
		} catch (Exception e) {
			System.out.println("Err" + e.getMessage());
		}
		background = imgTable.get("background");
		sbody = imgTable.get("sbody");
		sghead = imgTable.get("sghead");
		srhead = imgTable.get("srhead");
		efire = imgTable.get("Efire");
		esun = imgTable.get("Esun");
		eice = imgTable.get("Eice");
		ewater = imgTable.get("Ewater");
		gameFail = imgTable.get("fail");
		gameVictory = imgTable.get("victory");
		return imgTable;
	}

	private GamePanel() {
		this.beginAnimation = new AnimationProgress();
		this.setLayout(new BorderLayout());
		this.add(beginAnimation, BorderLayout.NORTH);
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(this);
	}

	public void initGame(String role) {
		board = new byte[774 / 20][569 / 20];
		boardstep = new int[774 / 20][569 / 20];
		stepcnt = 0;
		gameover = false;
		gameresult = true;
		this.role = role;
		beginAnimation.start();
		this.repaint();
	}

	public void startTimer() {// show animation for game over.
		gameover = true;
		mnstepcnt = Integer.MAX_VALUE;
		mxstepcnt = Integer.MIN_VALUE;
		if (boardstep == null)
			return;
		for (int i = 0; i < boardstep.length; i++) {
			for (int j = 0; j < boardstep[i].length; j++) {
				mnstepcnt = Math.min(mnstepcnt, boardstep[i][j]);
				mxstepcnt = Math.max(mxstepcnt, boardstep[i][j]);
			}
		}
		showtimeslot = mnstepcnt;
		this.setVisible(true);
		this.actionPerformed(null);
		this.timer.start();
	}

	public void stopTimer() {// show animation for game over.
		this.timer.stop();
	}

	public void actionPerformed(ActionEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GamePanel.this.repaint();
				showtimeslot++;
				if (showtimeslot >= mxstepcnt)
					showtimeslot = mnstepcnt;
			}
		});
	}

	private boolean hasCoverHead(int x, int y) {
		if (x == G.x && y == G.y)
			return true;
		if (x == R.x && y == R.y)
			return true;
		return false;
	}

	public boolean validMove(int x, int y) {
		if (x < 0 || y < 0 || x >= board.length || y >= board[0].length)
			return false;
		return board[x][y] <= 0;
	}

	public int autoGuided(int team, int nowDir, int nextDir) {
		int x, y, tx, ty;
		if (team == 1) {
			x = G.x;
			y = G.y;
		} else {
			x = R.x;
			y = R.y;
		}
		tx = x + dx[nextDir];
		ty = y + dy[nextDir];
		if (validMove(tx, ty))
			return nextDir;
		for (int i = nowDir, j = 0; j < 4; j++, i = (i + 1) % 4) {
			tx = x + dx[i];
			ty = y + dy[i];
			if (validMove(tx, ty))
				return i;
		}
		return nextDir;
	}

	public void eraseBoard(int x, int y) {
		board[x][y] = 0;
	}

	public void setProps(int x, int y, byte c) {
		board[x][y] = c;
	}

	public void eraseRandom(int x, int y, int dir, int cnt) {
		if (dir == 1) {
			while (x < board.length && cnt > 0) {
				board[x][y] = 0;
				x--;
				cnt--;
			}
		} else {
			while (y < board[0].length && cnt > 0) {
				board[x][y] = 0;
				y++;
				cnt--;
			}
		}
	}

	public String eraseRandom() {
		int x = (int) (Math.random() * board.length);
		int y = (int) (Math.random() * board[0].length);
		int cnt = 0;
		String ret;
		if (Math.random() > 0.5) {
			ret = "ER@" + (board.length - x - 1) + "@" + y + "@1@";
			while (x < board.length && !hasCoverHead(x, y) && cnt < 5) {
				// ret += "E@" + x + "@" + y + "\r\n";
				board[x][y] = 0;
				x++;
				cnt++;
			}
			if (cnt == 0)
				return "";
			else
				return ret + cnt + "\n";
		} else {
			ret = "ER@" + (board.length - x - 1) + "@" + y + "@2@";
			while (y < board[0].length && !hasCoverHead(x, y) && cnt < 5) {
				// ret += "E@" + x + "@" + y + "\r\n";
				board[x][y] = 0;
				y++;
				cnt++;
			}
			if (cnt == 0)
				return "";
			else
				return ret + cnt + "\n";
		}
	}

	public String addProps() {
		String ret = "";
		double p;
		for (int i = 0; i < 3; i++) {
			int tryTime = 30, x, y;
			while (tryTime >= 0) {
				x = (int) (Math.random() * board.length);
				y = (int) (Math.random() * board[0].length);
				tryTime--;
				if (board[x][y] == 0) {
					p = Math.random();
					if (p < 0.35) {// fire
						board[x][y] = -1;
						ret += "P@" + (board.length - x - 1) + "@" + y + "@-1"
								+ "\r\n";
					} else if (p < 0.70) {// sun
						board[x][y] = -2;
						ret += "P@" + (board.length - x - 1) + "@" + y + "@-2"
								+ "\r\n";
					} else if (p < 0.85) {// ice
						board[x][y] = -3;
						ret += "P@" + (board.length - x - 1) + "@" + y + "@-3"
								+ "\r\n";
					} else {// water
						board[x][y] = -4;
						ret += "P@" + (board.length - x - 1) + "@" + y + "@-4"
								+ "\r\n";
					}
					break;
				}
			}
		}
		return ret;
	}

	public int moveSnake(int x, int y, int team) {
		if (!validMove(x, y)) {
			if (team == 1)
				gameresult = false;
			if (role.equals("Server")) {
				GameServerControl.getInstance().clientServerExitGame();
			}
			gameover = true;
			return 1;
		}
		int ret = board[x][y];
		board[x][y] = 1;
		boardstep[x][y] = stepcnt++;
		if (team == 1) {
			G = new Point(x, y);
		} else {
			R = new Point(x, y);
		}
		repaint();
		return ret;
	}

	public String getFireCmd(int x, int y, int dir) {
		String ret = "EF@" + (board.length - x - 1) + "@" + y + "@" + dir
				+ "\n";
		for (int i = 0; i < 8; i++) {
			x += dx[dir];
			y += dy[dir];
			if (x < 0 || y < 0 || x >= board.length || y >= board[0].length)
				return ret;
			board[x][y] = 0;
		}
		return ret;
	}

	public String getSunCmd(int x, int y, int dir) {
		String ret = "ES@" + (board.length - x - 1) + "@" + y + "@" + dir
				+ "\n";
		for (int i = -2; i < 2; i++) {
			for (int j = -2; j < 2; j++) {
				int tx = x + i;
				int ty = y + j;
				if (tx < 0 || ty < 0 || tx >= board.length
						|| ty >= board[0].length)
					continue;
				board[tx][ty] = 0;
			}
		}
		return ret;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		g.drawImage(background.getImage(), 0, 0, this.getWidth(),
				this.getHeight(), this);
		if (gameover) {
			if (!gameresult)
				g.drawImage(gameFail.getImage(), 100, this.getHeight() / 2,
						600, 200, this);
			else
				g.drawImage(gameVictory.getImage(), 100, this.getHeight() / 2,
						600, 200, this);
		}

		if (board != null) {
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board[i].length; j++) {
					if (board[i][j] == 0)
						continue;
					if (board[i][j] == 1
							&& (!gameover || (gameover && boardstep[i][j] <= showtimeslot)))
						g.drawImage(sbody.getImage(), i * 20, j * 20, 20, 20,
								this);
					if (board[i][j] == -1
							&& (!gameover || (gameover && boardstep[i][j] <= showtimeslot)))
						g.drawImage(efire.getImage(), i * 20, j * 20, 20, 20,
								this);
					if (board[i][j] == -2
							&& (!gameover || (gameover && boardstep[i][j] <= showtimeslot)))
						g.drawImage(esun.getImage(), i * 20, j * 20, 20, 20,
								this);
					if (board[i][j] == -3
							&& (!gameover || (gameover && boardstep[i][j] <= showtimeslot)))
						g.drawImage(eice.getImage(), i * 20, j * 20, 20, 20,
								this);
					if (board[i][j] == -4
							&& (!gameover || (gameover && boardstep[i][j] <= showtimeslot)))
						g.drawImage(ewater.getImage(), i * 20, j * 20, 20, 20,
								this);
				}
			}
			if (G != null)
				g.drawImage(sghead.getImage(), G.x * 20 - 15, G.y * 20 - 15,
						50, 50, this);
			if (R != null)
				g.drawImage(srhead.getImage(), R.x * 20 - 15, R.y * 20 - 15,
						50, 50, this);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	public boolean dispatchKeyEvent(KeyEvent e) {
		if (gameover)
			return false;
		if (e.getID() == KeyEvent.KEY_RELEASED)
			return false;
		int dir;
		switch (e.getKeyCode()) {
		case KeyEvent.VK_DOWN:
			dir = 2;
			break;
		case KeyEvent.VK_UP:
			dir = 0;
			break;
		case KeyEvent.VK_RIGHT:
			dir = 1;
			break;
		case KeyEvent.VK_LEFT:
			dir = 3;
			break;
		default:
			return false;
		}
		if (role.equals("Server")) {
			GameServerControl.getInstance().processSnakeDir(dir, 1);
		} else {
			GameClientControl.getInstance().processSnakeDir(dir, 1);
		}
		return false;
	}
}
