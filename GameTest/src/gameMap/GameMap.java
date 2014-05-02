package gameMap;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import frame.*;
import gameMaterial.GameCharacter;

public class GameMap {
	private static GameMap singleton = null;

	public static GameMap getInstance() {
		if (singleton == null)
			singleton = new GameMap();
		return singleton;
	}

	int mapRow, mapColumn;
	public int WIDTH, HEIGHT;
	public int[][] tile;
	public int[][] tile_climb;
	BufferedImage image;
	Hashtable<String, ImageIcon> imgTable;

	private GameMap() {
		String fileText = "";
		mapRow = 0;
		mapColumn = 0;
		String mapString = "";
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					this.getClass().getResource("map/map1.txt").openStream()));
			String htmlText;
			while ((htmlText = reader.readLine()) != null) {
				fileText += htmlText;
				mapRow++;
				mapColumn = htmlText.length();
			}
			mapString = fileText;
			reader.close();
		} catch (Exception ee) {
			System.out.println(ee.getMessage());
			ee.getStackTrace();
		}
		loadImage();
		buildMapImage(mapString);
		int row = 0, col = 0;
		String mapClimbString = "";
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					this.getClass().getResource("map/mapclimb.txt")
							.openStream()));
			String htmlText;
			fileText = "";
			while ((htmlText = reader.readLine()) != null) {
				fileText += htmlText;
				row++;
				col = htmlText.length();
				System.out.println(htmlText);
			}
			mapClimbString = fileText;
			reader.close();
		} catch (Exception ee) {
			System.out.println(ee.getMessage());
			ee.getStackTrace();
		}
		buildClimbMapImage(mapClimbString, row, col);
	}

	public void loadImage() {
		imgTable = new Hashtable<String, ImageIcon>();
		BufferedImage img = null;
		try {
			img = ImageIO.read(this.getClass().getResource("map/bsc.0.png"));
			imgTable.put("bsc.0", new ImageIcon(img));
			img = ImageIO.read(this.getClass().getResource("map/enH0.3.png"));
			imgTable.put("enH0.3", new ImageIcon(img));
			img = ImageIO.read(this.getClass().getResource("map/slLU.0.png"));
			imgTable.put("slLU.0", new ImageIcon(img));
			img = ImageIO.read(this.getClass().getResource("map/slRU.0.png"));
			imgTable.put("slRU.0", new ImageIcon(img));
		} catch (Exception e) {
			System.out.println("Err" + e.getMessage());
		}
		String fileName[] = { "ladder.2.0.0.png", "ladder.2.1.0.png",
				"ladder.2.2.0.png" };
		String fileIdx[] = { "ladder.2.0.0", "ladder.2.1.0", "ladder.2.2.0" };
		try {
			for (int i = 0; i < fileName.length; i++) {
				// img = ImageIO.read(new File(fileName[i]));
				img = ImageIO.read(this.getClass().getResource(
						"Map.wz_connect.img/" + fileName[i]));
				imgTable.put(fileIdx[i], new ImageIcon(img));
			}
		} catch (Exception e) {
			System.out.println("Err" + e.getMessage());
		}
	}

	public void buildMapImage(String mapString) {
		WIDTH = mapColumn * 90;
		HEIGHT = mapRow * 90;
		tile = new int[mapRow][mapColumn];
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.createGraphics();
		ImageIcon image;
		for (int i = 0; i < mapRow; i++) {
			for (int j = 0; j < mapColumn; j++) {
				int p = i * mapColumn + j;
				int kind = mapString.charAt(p) - '0';
				tile[i][j] = kind;
				if (kind == 0) {
					continue;
				} else if (kind == 1) {
					image = imgTable.get("enH0.3");
					g.drawImage(image.getImage(), j * 90, i * 90, 90, 30, null);
					image = imgTable.get("bsc.0");
					g.drawImage(image.getImage(), j * 90, i * 90 + 30, 90, 60,
							null);
				} else if (kind == 2) {
					image = imgTable.get("slRU.0");
					g.drawImage(image.getImage(), j * 90, i * 90, 90, 90, null);
				} else if (kind == 3) {
					image = imgTable.get("slLU.0");
					g.drawImage(image.getImage(), j * 90, i * 90, 90, 90, null);
				}
			}
		}
		GameCharacter.getInstance().X = 0;
		GameCharacter.getInstance().Y = (mapRow - 1) * 90;
	}

	public void buildClimbMapImage(String mapClimbString, int mapRow,
			int mapColumn) {
		tile_climb = new int[mapRow][mapColumn];
		Graphics g = image.createGraphics();
		ImageIcon tempimage;
		for (int i = 0; i < mapRow; i++) {
			for (int j = 0; j < mapColumn; j++) {
				int p = i * mapColumn + j;
				int kind = mapClimbString.charAt(p) - '0';
				tile_climb[i][j] = kind;
			}
		}
		for (int i = 0; i < mapRow; i++) {
			for (int j = 0; j < mapColumn; j++) {
				int kind = tile_climb[i][j];
				if (kind == 0) {
					continue;
				} else if (kind == 1) {
					int baseY = 0;
					if (i - 1 < 0 || tile_climb[i - 1][j] == 0) {
						tempimage = imgTable.get("ladder.2.0.0");
						g.drawImage(tempimage.getImage(), j * 90, i * 90 - 30,
								52, 30, null);
					}
					tempimage = imgTable.get("ladder.2.1.0");
					g.drawImage(tempimage.getImage(), j * 90, i * 90 + baseY,
							52, tempimage.getIconHeight(), null);
					baseY += tempimage.getIconHeight();
					g.drawImage(tempimage.getImage(), j * 90, i * 90 + baseY,
							52, 90 - baseY, null);
					baseY += tempimage.getIconHeight();
					if (i + 1 < mapRow || tile_climb[i + 1][j] == 0) {
						tempimage = imgTable.get("ladder.2.2.0");
						g.drawImage(tempimage.getImage(), j * 90, i * 90, 52,
								30, null);
					}
				}
			}
		}
	}

	final int windowHeight = 50;

	public boolean isClimbable(int x, int y) {
		if (x < 0 || x >= WIDTH)
			return false;
		if (y < 0 || y >= HEIGHT)
			return false;
		int rx = x / 90;
		int ry = y / 90;
		try {
			if (tile_climb[ry][rx] != 0) {
				if (rx * 90 + 15 <= x && x <= rx * 90 + 40)
					return true;
			}
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return true;
		}
		return false;
	}

	public boolean isCollided(int x, int y) {
		if (x < 0 || x >= WIDTH)
			return true;
		if (y < 0 || y >= HEIGHT)
			return true;
		int rx = x / 90;
		int ry = y / 90;
		try {
			if (tile[ry][rx] == 1) {
				return true;
			}
			if (tile[ry][rx] == 2) {
				int sx = rx * 90, sy = ry * 90;
				int ex = sx + 90, ey = sy + 90;
				int nx, ny, nc;
				nx = -(sy - ey);
				ny = sx - ex;
				nc = nx * sx + ny * sy;
				if (nx * x + ny * y - nc < 0)
					return true;
			}
			if (tile[ry][rx] == 3) {
				int sx = rx * 90, sy = ry * 90 + 90;
				int ex = sx + 90, ey = sy - 90;
				int nx, ny, nc;
				nx = -(sy - ey);
				ny = sx - ex;
				nc = nx * sx + ny * sy;
				if (nx * x + ny * y - nc < 0)
					return true;
			}
			return false;
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
		return true;
	}

	public Point getCollidedPoint(int x, int y) {
		int rx, ry;
		if (x < 0)
			x = 0;
		if (x >= WIDTH)
			x = WIDTH - 1;
		if (y < 0)
			y = 0;
		if (y >= HEIGHT)
			y = HEIGHT - 1;
		rx = x / 90;
		ry = y / 90;
		try {
			if (tile[ry][rx] == 1) {
				return new Point(x, ry * 90);
			}
			if (tile[ry][rx] == 2) {
				int sx = rx * 90, sy = ry * 90;
				int ex = sx + 90, ey = sy + 90;
				int nx, ny, nc;
				nx = -(sy - ey);
				ny = sx - ex;
				nc = nx * sx + ny * sy;
				return new Point(x, (nc - nx * x) / ny);
			}
			if (tile[ry][rx] == 3) {
				int sx = rx * 90, sy = ry * 90 + 90;
				int ex = sx + 90, ey = sy - 90;
				int nx, ny, nc;
				nx = -(sy - ey);
				ny = sx - ex;
				nc = nx * sx + ny * sy;
				return new Point(x, (nc - nx * x) / ny);
			}
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
		System.out.println("x");
		return new Point(x, y);
	}

	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, WIDTH, HEIGHT, null);
	}
}
