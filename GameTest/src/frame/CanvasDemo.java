package frame;

import gameMap.GameMap;

import gameMaterial.GameCharacter;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.media.*;

import java.net.URL;
import java.io.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class CanvasDemo extends JPanel implements KeyEventDispatcher {
	private static CanvasDemo singleton = null;
	public int WIDTH, HEIGHT;
	public int showWidth, showHeight;
	private int bshiftX = 0, bshiftY = 0;
	private JScrollPane viewPane;

	public static CanvasDemo getInstance() {
		if (singleton == null)
			singleton = new CanvasDemo();
		return singleton;
	}

	Hashtable<String, ImageIcon> imgTable;
	private Player player;

	private CanvasDemo() {

		int v = ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER;
		int h = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;

		viewPane = new JScrollPane(this, v, h);
		WIDTH = GameMap.getInstance().WIDTH;
		HEIGHT = GameMap.getInstance().HEIGHT;
		this.setSize(WIDTH, HEIGHT);
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		loadImage();
		viewPane.getVerticalScrollBar().setValue(
				viewPane.getVerticalScrollBar().getMaximum());
		Thread paintThread = new Thread(new PaintThread());
		paintThread.start();

		Thread playThread = new Thread(new PlayThread());
		playThread.start();
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(this);
	}

	public JScrollPane getView() {
		return viewPane;
	}

	public void loadImage() {
		imgTable = new Hashtable<String, ImageIcon>();
		BufferedImage img = null;
		String fileName[] = { "background.jpg" };
		String fileIdx[] = { "background" };
		try {
			for (int i = 0; i < fileName.length; i++) {
				// img = ImageIO.read(new File(fileName[i]));
				img = ImageIO.read(this.getClass().getResource(
						"images/" + fileName[i]));
				imgTable.put(fileIdx[i], new ImageIcon(img));
			}
		} catch (Exception e) {
			System.out.println("Err" + e.getMessage());
		}
	}

	Image offScreenImage = null;

	public void paint(Graphics g) {
		// super.paint(g);
		if (offScreenImage == null) {
			offScreenImage = this.createImage(WIDTH, HEIGHT);
		}
		// System.out.println(viewPane.getHorizontalScrollBar().getValue() +
		// viewPane.getHorizontalScrollBar().getWidth());
		int viewLX = viewPane.getHorizontalScrollBar().getValue();
		int viewLY = viewPane.getVerticalScrollBar().getValue();
		int viewRX = Math.min(1024 + viewLX, WIDTH);
		int viewRY = Math.min(768 + viewLY, HEIGHT);
		Graphics gOffScreen = offScreenImage.getGraphics();

		Color c = gOffScreen.getColor();

		ImageIcon image;
		image = imgTable.get("background");
		if (image != null)
			gOffScreen.drawImage(image.getImage(), 0, 0, 1920, 1280, this);
		GameMap.getInstance().paint(gOffScreen);
		GameCharacter peter = GameCharacter.getInstance();
		image = imgTable.get(peter.getActionCommand());
		/*
		 * if (peter.X >= (viewLX + viewRX) / 2 - 5 || peter.X <= (viewLX +
		 * viewRX) / 2 - 5) { int dx = peter.X - (viewLX + viewRX) / 2; viewLX
		 * += dx; } if (peter.Y >= (viewLY + viewRY) / 2 - 5 || peter.Y <=
		 * (viewLY + viewRY) / 2 - 5) { int dy = peter.Y - (viewLY + viewRY) /
		 * 2; viewLY += dy; } SwingUtilities.invokeLater(new Runnable() { int
		 * viewLX; int viewLY;
		 * 
		 * public Runnable init(int x, int y) { viewLX = x; viewLY = y; return
		 * this; }
		 * 
		 * public void run() {
		 * viewPane.getHorizontalScrollBar().setValue(viewLX);
		 * viewPane.getVerticalScrollBar().setValue(viewLY); } }.init(viewLX,
		 * viewLY));
		 */
		// viewAdjust.setMove(viewLX, viewLY);
		peter.paint(gOffScreen);
		gOffScreen.setColor(c);
		g.drawImage(offScreenImage, 0, 0, null);
		// super.paint(g);
		// super.paintComponent(g);
	}

	public boolean dispatchKeyEvent(KeyEvent e) {
		GameCharacter peter = GameCharacter.getInstance();
		if (e.getID() == KeyEvent.KEY_RELEASED) {
			peter.nextState("released");
			// peter.ack();
			return false;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			peter.nextState("right");
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			peter.nextState("left");
		} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			peter.nextState("space");
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			peter.nextState("up");
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			peter.nextState("down");
		}
		peter.ack();
		return false;
	}

	private class PaintThread implements Runnable {
		int vx, vy;

		public void run() {
			while (true) {
				SwingUtilities.invokeLater(new Runnable() {
					int viewLX;
					int viewLY;

					public Runnable init(int x, int y) {
						viewLX = x;
						viewLY = y;
						return this;
					}

					public void run() {
						viewPane.getHorizontalScrollBar().setValue(viewLX);
						viewPane.getVerticalScrollBar().setValue(viewLY);
					}
				}.init(vx, vy));
				repaint();
				GameCharacter peter = GameCharacter.getInstance();
				int viewLX = viewPane.getHorizontalScrollBar().getValue();
				int viewLY = viewPane.getVerticalScrollBar().getValue();
				int viewRX = Math.min(1024 + viewLX, WIDTH);
				int viewRY = Math.min(768 + viewLY, HEIGHT);
				if (peter.X >= (viewLX + viewRX) / 2 - 5
						|| peter.X <= (viewLX + viewRX) / 2 - 5) {
					int dx = peter.X - (viewLX + viewRX) / 2;
					viewLX += dx;
				}
				if (peter.Y >= (viewLY + viewRY) / 2 - 5
						|| peter.Y <= (viewLY + viewRY) / 2 - 5) {
					int dy = peter.Y - (viewLY + viewRY) / 2;
					viewLY += dy;
				}
				vx = viewLX;
				vy = viewLY;
				try {
					Thread.sleep(40);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				peter.timerAction();
			}
		}
	}

	private class PlayThread implements Runnable {
		public void play(String fileName) {
			try {
				AudioInputStream audioInputStream;// 文件流
				AudioFormat audioFormat;// 文件格式
				SourceDataLine sourceDataLine;// 输出设备

				// 取得文件输入流
				audioInputStream = AudioSystem.getAudioInputStream(this
						.getClass().getResource("music/" + fileName));
				audioFormat = audioInputStream.getFormat();
				// 转换mp3文件编码
				if (audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
					audioFormat = new AudioFormat(
							AudioFormat.Encoding.PCM_SIGNED,
							audioFormat.getSampleRate(), 16,
							audioFormat.getChannels(),
							audioFormat.getChannels() * 2,
							audioFormat.getSampleRate(), false);
					audioInputStream = AudioSystem.getAudioInputStream(
							audioFormat, audioInputStream);
				}

				// 打开输出设备
				DataLine.Info dataLineInfo = new DataLine.Info(
						SourceDataLine.class, audioFormat,
						AudioSystem.NOT_SPECIFIED);
				sourceDataLine = (SourceDataLine) AudioSystem
						.getLine(dataLineInfo);
				sourceDataLine.open(audioFormat);
				sourceDataLine.start();

				byte tempBuffer[] = new byte[320];
				try {
					int cnt;
					// 读取数据到缓存数据
					while ((cnt = audioInputStream.read(tempBuffer, 0,
							tempBuffer.length)) != -1) {
						if (cnt > 0) {
							// 写入缓存数据
							sourceDataLine.write(tempBuffer, 0, cnt);
						}
					}
					// Block等待临时数据被输出为空
					sourceDataLine.drain();
					sourceDataLine.close();
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(0);
				}
			} catch (Exception e) {

			}
			/*
			 * try { player = Manager.createPlayer(this.getClass().getResource(
			 * "music/" + fileName)); player.addControllerListener(new
			 * ControllerListener() { public void
			 * controllerUpdate(ControllerEvent event) { if (event instanceof
			 * EndOfMediaEvent) { player.stop(); player.close(); } } });
			 * player.realize(); player.start(); } catch (NoPlayerException e) {
			 * e.printStackTrace(); } catch (Exception e) { e.printStackTrace();
			 * }
			 */
		}

		public void run() {
			play("FloralLife.mp3");
		}
	}
}
