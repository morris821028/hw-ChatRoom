package gameMaterial;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import gameMap.GameMap;

public class RunMode extends StateMode {
	Hashtable<String, ImageIcon> imgTable;

	public void loadImage() {
		imgTable = new Hashtable<String, ImageIcon>();
		BufferedImage img = null;
		String fileName[] = { "walk1.0.arm.png", "walk1.1.arm.png",
				"walk1.2.arm.png", "walk1.3.arm.png", "walk1.0.body.png",
				"walk1.1.body.png", "walk1.2.body.png", "walk1.3.body.png" };
		String fileIdx[] = { "walk1.0.arm", "walk1.1.arm", "walk1.2.arm",
				"walk1.3.arm", "walk1.0.body", "walk1.1.body", "walk1.2.body",
				"walk1.3.body" };
		try {
			for (int i = 0; i < fileName.length; i++) {
				// img = ImageIO.read(new File(fileName[i]));
				img = ImageIO.read(this.getClass().getResource(
						"Character.wz_00002000.img/" + fileName[i]));
				imgTable.put(fileIdx[i], new ImageIcon(img));
			}
			img = ImageIO.read(this.getClass().getResource(
					"Character.wz_00012000.img/front.head.png"));
			imgTable.put("front.head", new ImageIcon(img));
		} catch (Exception e) {
			System.out.println("Err" + e.getMessage());
		}
	}

	int counter;

	public void on() {
		if (imgTable == null)
			loadImage();
		GameCharacter peter = GameCharacter.getInstance();
		if (peter.roleDir == 0)
			peter.speedX = 7;
		else
			peter.speedX = -7;
	}

	public void off() {

	}

	public void ack() {
		GameCharacter peter = GameCharacter.getInstance();
		peter.X += peter.speedX;
		counter++;
		if (GameMap.getInstance().isCollided(peter.X, peter.Y)) {
			Point p = GameMap.getInstance().getCollidedPoint(peter.X, peter.Y);
			peter.X = p.x;
			peter.Y = p.y;
		}
		if (!GameMap.getInstance().isCollided(peter.X, peter.Y + 5)) {
			Point p = GameMap.getInstance().getCollidedPoint(peter.X,
					peter.Y + 20);
			peter.X = p.x;
			peter.Y = p.y;
		}
		if (!GameMap.getInstance().isCollided(peter.X, peter.Y + 20)) {
			GameCharacter.getInstance().nextState("drop");
		}
	}

	public boolean isComplete(Object argv) {
		return true;
	}

	public String getCommand() {
		GameCharacter peter = GameCharacter.getInstance();
		if (peter.roleDir == 0) {
			return "srun" + peter.runCounter;
		} else {
			return "trun" + peter.runCounter;
		}
	}

	public void paint(Graphics g) {
		GameCharacter peter = GameCharacter.getInstance();
		int idx = (counter / 10) % 4;
		int x = peter.X, y = peter.Y;
		ImageIcon image;
		if (imgTable == null)
			loadImage();
		image = imgTable.get("walk1." + idx + ".body");
		BufferedImage offScreen = new BufferedImage(39, 66,
				BufferedImage.TYPE_4BYTE_ABGR);
		peter.bodyPaint.paint(g, x, y, "walk1." + idx);
		/*
		 * if (image != null) {
		 * 
		 * offScreen.getGraphics().drawImage(image.getImage(), 5, 35,
		 * image.getIconWidth(), image.getIconHeight(), null); image =
		 * imgTable.get("walk1." + idx + ".arm"); if (image != null) { int vx =
		 * 20 + 5; if (idx == 1) vx -= 10;
		 * offScreen.getGraphics().drawImage(image.getImage(), vx, 36,
		 * image.getIconWidth(), image.getIconHeight(), null); }
		 */
		/*
		 * image = imgTable.get("front.head"); if (image != null) {
		 * offScreen.getGraphics().drawImage(image.getImage(), 0, 2,
		 * image.getIconWidth(), image.getIconHeight(), null); } y -= 66; x -=
		 * 15; image = new ImageIcon(offScreen); if (peter.roleDir == 0) image =
		 * new ImageIcon(GameImageTool.flipImage(offScreen));
		 * g.drawImage(image.getImage(), x, y, 39, 66, null); }
		 */
	}

	public void timerAction() {
		ack();
	}
}
