package gameMaterial;

import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import gameMap.GameMap;
import java.awt.*;

public class StopMode extends StateMode {
	Hashtable<String, ImageIcon> imgTable;

	public void loadImage() {
		imgTable = new Hashtable<String, ImageIcon>();
		BufferedImage img = null;
		String fileName[] = { "stand1.0.arm.png", "stand1.1.arm.png",
				"stand1.2.arm.png", "stand1.0.body.png", "stand1.1.body.png",
				"stand1.2.body.png"};
		String fileIdx[] = { "stand1.0.arm", "stand1.1.arm", "stand1.2.arm",
				"stand1.0.body", "stand1.1.body", "stand1.2.body"};
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

	int counter = 0;

	public void on() {
		if (imgTable == null) {
			loadImage();
		}
		GameCharacter peter = GameCharacter.getInstance();
		peter.speedX = 0;
		peter.speedY = 0;
		counter = 0;
	}

	public void off() {

	}

	public void ack() {
		GameCharacter peter = GameCharacter.getInstance();
		if (!GameMap.getInstance().isCollided(peter.X, peter.Y + 10)) {
			GameCharacter.getInstance().nextState("drop");
		}
	}

	public boolean isComplete(Object argv) {
		return true;
	}

	public String getCommand() {
		GameCharacter peter = GameCharacter.getInstance();
		if (peter.roleDir == 0) {
			return "rstop";
		} else {
			return "lstop";
		}
	}

	public void paint(Graphics g) {
		counter++;
		GameCharacter peter = GameCharacter.getInstance();
		int idx = (counter / 50) % 3;
		int x = peter.X, y = peter.Y;
		ImageIcon image;
		if (imgTable == null)
			loadImage();
		image = imgTable.get("stand1." + idx + ".body");
		BufferedImage offScreen = new BufferedImage(39, 66,
				BufferedImage.TYPE_4BYTE_ABGR);
		peter.bodyPaint.paint(g, x, y, "stand1." + idx);
		/*if (image != null) {
			image = imgTable.get("stand1." + idx + ".arm");
			if (image != null) {
				offScreen.getGraphics().drawImage(image.getImage(), 24, 36,
						image.getIconWidth(), image.getIconHeight(), null);
			}
			image = imgTable.get("front.head");
			if (image != null) {
				offScreen.getGraphics().drawImage(image.getImage(), 0, 2,
						image.getIconWidth(), image.getIconHeight(), null);
			}
			y -= 66;
			x -= 15;
			image = new ImageIcon(offScreen);
			if (peter.roleDir == 0)
				image = new ImageIcon(GameImageTool.flipImage(offScreen));
			g.drawImage(image.getImage(), x, y, 39, 66, null);
		}*/
	}
}
