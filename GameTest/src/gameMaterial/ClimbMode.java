package gameMaterial;

import gameMap.GameMap;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ClimbMode extends StateMode {
	int climbCounter = 0;
	boolean isComplete;
	Hashtable<String, ImageIcon> imgTable;
	
	public void loadImage() {
		imgTable = new Hashtable<String, ImageIcon>();
		BufferedImage img = null;
		String fileName[] = { "ladder.0.body.png", "ladder.1.body.png" };
		String fileIdx[] = { "ladder.0.body", "ladder.1.body" };
		try {
			for (int i = 0; i < fileName.length; i++) {
				// img = ImageIO.read(new File(fileName[i]));
				img = ImageIO.read(this.getClass().getResource(
						"Character.wz_00002000.img/" + fileName[i]));
				imgTable.put(fileIdx[i], new ImageIcon(img));
			}
			img = ImageIO.read(this.getClass().getResource(
					"Character.wz_00012000.img/back.head.png"));
			imgTable.put("back.head", new ImageIcon(img));
		} catch (Exception e) {
			System.out.println("Err" + e.getMessage());
		}
	}

	public void on() {
		if (imgTable == null)
			loadImage();
		climbCounter = 0;isComplete = false;
		GameCharacter.getInstance().speedX = 0;
	}

	public void off() {

	}

	public void ack() {
		GameCharacter peter = GameCharacter.getInstance();
		if (peter.getPrevKey().equals("up")
				|| peter.getPrevKey().equals("down")) {
			if (peter.roleDir == 2) {
				peter.Y -= 5;
				climbCounter++;
			}
			if (peter.roleDir == 3) {
				peter.Y += 5;
				climbCounter--;
			}
		}
		if (!GameMap.getInstance().isClimbable(peter.X, peter.Y)) {
			isComplete = true;
			GameCharacter.getInstance().nextState("drop");
		}
	}

	public boolean isComplete(Object argv) {
		if(isComplete)
			return true;
		if (argv instanceof Integer) {
			int v = (Integer) argv;
			if (v == 3)
				return true;
			else if (v == 1)
				GameCharacter.getInstance().speedX = 20;
			else if (v == 2)
				GameCharacter.getInstance().speedX = -20;
			else
				GameCharacter.getInstance().speedX = 0;
		}
		return false;
	}

	public String getCommand() {
		return "";
	}

	public void paint(Graphics g) {
		GameCharacter peter = GameCharacter.getInstance();
		int x = peter.X, y = peter.Y;
		ImageIcon image;
		if (imgTable == null)
			loadImage();
		int idx = (climbCounter % 2 + 2) % 2;
		image = imgTable.get("ladder." + idx + ".body");
		BufferedImage offScreen = new BufferedImage(39, 66,
				BufferedImage.TYPE_4BYTE_ABGR);
		/* System.out.println(idx); */
		peter.bodyPaint.paint(g, x, y, "ladder." + idx);
		/*
		 * if (image != null) { image = imgTable.get("back.head"); if (image !=
		 * null) { offScreen.getGraphics().drawImage(image.getImage(), 0, 2,
		 * image.getIconWidth(), image.getIconHeight(), null); } y -= 66; x -=
		 * 15; image = new ImageIcon(offScreen); if (peter.roleDir == 0) image =
		 * new ImageIcon(GameImageTool.flipImage(offScreen));
		 * g.drawImage(image.getImage(), x, y, 39, 66, null); }
		 */
	}
}
