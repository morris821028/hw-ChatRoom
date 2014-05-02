package gameMaterial;

import gameMap.GameMap;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class DropMode extends StateMode {
	int time, g;
	int jump_oY;
	int command[] = { 0, 1, 2, 2, 3, 3, 3, 4, 4, 4, 4, 4, 4, 3, 3, 3, 2, 2, 1,
			0, 1, 3, 3, 3, 4, 4 };
	int command2[] = { 0, 1, 2, 3, 4, 5, 6, 6, 7, 7, 7, 7, 8, 8, 8, 8, 9, 9,
			10, 11, 12, 13, 14, 15, 0, 0 };
	boolean isComplete;
	Hashtable<String, ImageIcon> imgTable;

	public void loadImage() {
		imgTable = new Hashtable<String, ImageIcon>();
		BufferedImage img = null;
		String fileName[] = { "jump.0.arm.png", "jump.0.body.png",
				"jump.0.lHand.png" };
		String fileIdx[] = { "jump.0.arm", "jump.0.body", "jump.0.lHand" };
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

	public void on() {
		isComplete = false;
		time = 0;
		g = -9;
		GameCharacter peter = GameCharacter.getInstance();
		jump_oY = peter.Y;
		peter.speedY = 0;
		if (peter.speedX < 0)
			peter.speedX++;
		if (peter.speedX > 0)
			peter.speedX--;
	}

	public void off() {

	}

	public void ack() {
		GameCharacter peter = GameCharacter.getInstance();
		int vy, vx;
		vy = jump_oY - (peter.speedY * time + g * time * time / 2);
		peter.Y = vy;
		vx = peter.speedX;
		if (GameMap.getInstance().isCollided(peter.X + 1, peter.Y) && time > 0) {
			peter.X += 1;
		} else
			peter.X += vx;
		if (peter.X < 0) {
			peter.X -= peter.speedX;
		}
		time++;
		if (GameMap.getInstance().isCollided(peter.X, peter.Y) && time > 0) {
			isComplete = true;
			Point p = GameMap.getInstance().getCollidedPoint(peter.X, peter.Y);
			peter.X = p.x;
			peter.Y = p.y;
			if (time + 1 < command.length)
				time++;
			GameCharacter.getInstance().nextState("released");
			return;
		}
	}

	public boolean isComplete(Object argv) {
		return isComplete;
	}

	public String getCommand() {
		GameCharacter peter = GameCharacter.getInstance();
		if (peter.speedX == 0) {
			if (peter.roleDir == 0)
				return "mjump" + command[time % command.length];
			else
				return "mljump" + command[time % command.length];
		} else {
			if (peter.roleDir == 0)
				return "rjump" + command2[time % command2.length];
			else
				return "rljump" + command2[time % command2.length];
		}
	}

	public void timerAction() {
		ack();
	}

	public void paint(Graphics g) {
		GameCharacter peter = GameCharacter.getInstance();
		int x = peter.X, y = peter.Y;
		ImageIcon image;
		if (imgTable == null)
			loadImage();
		image = imgTable.get("jump.0.body");
		BufferedImage offScreen = new BufferedImage(39, 66,
				BufferedImage.TYPE_4BYTE_ABGR);
		if (image != null) {
			peter.bodyPaint.paint(g, x, y, "jump.0");
			/*
			 * offScreen.getGraphics().drawImage(image.getImage(), 5, 35,
			 * image.getIconWidth(), image.getIconHeight(), null); image =
			 * imgTable.get("front.head"); if (image != null) {
			 * offScreen.getGraphics().drawImage(image.getImage(), 5, 2,
			 * image.getIconWidth(), image.getIconHeight(), null); } image =
			 * imgTable.get("jump.0.arm"); if (image != null) {
			 * offScreen.getGraphics().drawImage(image.getImage(), 23, 30,
			 * image.getIconWidth(), image.getIconHeight(), null); } image =
			 * imgTable.get("jump.0.lHand"); if (image != null) {
			 * offScreen.getGraphics().drawImage(image.getImage(), 9, 40,
			 * image.getIconWidth(), image.getIconHeight(), null); }
			 * 
			 * y -= 66; x -= 15; image = new ImageIcon(offScreen); if
			 * (peter.roleDir == 0) image = new
			 * ImageIcon(GameImageTool.flipImage(offScreen));
			 * g.drawImage(image.getImage(), x, y, 39, 66, null);
			 */
		}
	}
}
