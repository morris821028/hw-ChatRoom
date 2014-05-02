package gameMaterial.characterPaint;

import gameMaterial.GameCharacter;
import gameMaterial.GameImageTool;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

public class Body {
	protected Hashtable<String, ImageIcon> imgTable;
	protected boolean reversion = true;
	public void setReversionRole(boolean f) {
		reversion = f;
	}
	public void loadImage() {
		imgTable = new Hashtable<String, ImageIcon>();
		BufferedImage img = null;
		String fileName[] = { "stand1.0.arm.png", "stand1.1.arm.png",
				"stand1.2.arm.png", "stand1.0.body.png", "stand1.1.body.png",
				"stand1.2.body.png", "ladder.0.body.png", "ladder.1.body.png",
				"jump.0.arm.png", "jump.0.body.png", "jump.0.lHand.png",
				"walk1.0.arm.png", "walk1.1.arm.png", "walk1.2.arm.png",
				"walk1.3.arm.png", "walk1.0.body.png", "walk1.1.body.png",
				"walk1.2.body.png", "walk1.3.body.png" };
		String fileIdx[] = { "stand1.0.arm", "stand1.1.arm", "stand1.2.arm",
				"stand1.0.body", "stand1.1.body", "stand1.2.body",
				"ladder.0.body", "ladder.1.body", "jump.0.arm", "jump.0.body",
				"jump.0.lHand", "walk1.0.arm", "walk1.1.arm", "walk1.2.arm",
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

	public void paintArm(Graphics g, int x, int y, String type) {
		ImageIcon image;
		if (imgTable == null)
			loadImage();
		image = imgTable.get(type);
		if (image != null) {
			g.drawImage(image.getImage(), x, y, image.getIconWidth(),
					image.getIconHeight(), null);
		}
	}
	public void paintNoBody(Graphics g, int x, int y, String type) {
		ImageIcon image;
		if (imgTable == null)
			loadImage();
		image = imgTable.get(type + ".body");
		if (image != null) {
			BufferedImage offScreen = new BufferedImage(
					image.getIconWidth() + 80, image.getIconHeight() + 80,
					BufferedImage.TYPE_4BYTE_ABGR);
			if (type.indexOf("ladder") == -1)
				GameCharacter.getInstance().headPaint.paint(
						offScreen.getGraphics(), image.getIconWidth() / 2 + 35,
						80 + 2, "front");
			else
				GameCharacter.getInstance().headPaint.paint(
						offScreen.getGraphics(), image.getIconWidth() / 2 + 35,
						80 + 15, "back");
			if (type.equals("walk1.1"))
				paintArm(offScreen.getGraphics(),
						image.getIconWidth() / 2 + 35, 81, type + ".arm");
			else
				paintArm(offScreen.getGraphics(),
						image.getIconWidth() / 2 + 40, 81, type + ".arm");
			paintArm(offScreen.getGraphics(), image.getIconWidth() / 2 + 25,
					85, type + ".lHand");
			if (GameCharacter.getInstance().roleDir == 0 && reversion) {
				image = new ImageIcon(GameImageTool.flipImage(offScreen));
			} else {
				image = new ImageIcon(offScreen);
			}
			g.drawImage(image.getImage(), x - image.getIconWidth() / 2, y
					- image.getIconHeight(), image.getIconWidth(),
					image.getIconHeight(), null);
		}
	}
	public void paint(Graphics g, int x, int y, String type) {
		ImageIcon image;
		if (imgTable == null)
			loadImage();
		image = imgTable.get(type + ".body");
		if (image != null) {
			BufferedImage offScreen = new BufferedImage(
					image.getIconWidth() + 80, image.getIconHeight() + 80,
					BufferedImage.TYPE_4BYTE_ABGR);
			offScreen.getGraphics().drawImage(image.getImage(), 35, 80,
					image.getIconWidth(), image.getIconHeight(), null);
			if (type.indexOf("ladder") == -1)
				GameCharacter.getInstance().headPaint.paint(
						offScreen.getGraphics(), image.getIconWidth() / 2 + 35,
						80 + 2, "front");
			else
				GameCharacter.getInstance().headPaint.paint(
						offScreen.getGraphics(), image.getIconWidth() / 2 + 35,
						80 + 15, "back");
			if (type.equals("walk1.1"))
				paintArm(offScreen.getGraphics(),
						image.getIconWidth() / 2 + 35, 81, type + ".arm");
			else
				paintArm(offScreen.getGraphics(),
						image.getIconWidth() / 2 + 40, 81, type + ".arm");
			paintArm(offScreen.getGraphics(), image.getIconWidth() / 2 + 25,
					85, type + ".lHand");
			if (GameCharacter.getInstance().roleDir == 0 && reversion) {
				image = new ImageIcon(GameImageTool.flipImage(offScreen));
			} else {
				image = new ImageIcon(offScreen);
			}
			g.drawImage(image.getImage(), x - image.getIconWidth() / 2, y
					- image.getIconHeight(), image.getIconWidth(),
					image.getIconHeight(), null);
		}
	}
}
