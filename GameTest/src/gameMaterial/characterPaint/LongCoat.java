package gameMaterial.characterPaint;

import gameMaterial.GameCharacter;
import gameMaterial.GameImageTool;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class LongCoat extends Body {
	public Body body;
	private Hashtable<String, ImageIcon> imgTable;

	public LongCoat(Body body) {
		this.body = body;
	}

	public void loadImage() {
		imgTable = new Hashtable<String, ImageIcon>();
		BufferedImage img = null;
		String fileName[] = { "stand1.0.mailArm.png", "stand1.1.mailArm.png",
				"stand1.2.mailArm.png", "stand1.0.mail.png",
				"stand1.1.mail.png", "stand1.2.mail.png", "ladder.0.mail.png",
				"ladder.1.mail.png", "jump.0.mail.png", "walk1.0.mailArm.png",
				"walk1.1.mailArm.png", "walk1.0.mail.png", "walk1.1.mail.png",
				"walk1.2.mail.png", "walk1.3.mail.png" };
		String fileIdx[] = { "stand1.0.mailArm", "stand1.1.mailArm",
				"stand1.2.mailArm", "stand1.0.mail", "stand1.1.mail",
				"stand1.2.mail", "ladder.0.mail", "ladder.1.mail",
				"jump.0.mail", "walk1.0.mailArm", "walk1.1.mailArm",
				"walk1.0.mail", "walk1.1.mail", "walk1.2.mail", "walk1.3.mail" };
		try {
			for (int i = 0; i < fileName.length; i++) {
				// img = ImageIO.read(new File(fileName[i]));
				img = ImageIO.read(this.getClass().getResource(
						"Character.wz_01052619.img/" + fileName[i]));
				imgTable.put(fileIdx[i], new ImageIcon(img));
			}
		} catch (Exception e) {
			System.out.println("Err" + e.getMessage());
		}
	}

	public void paint(Graphics g, int x, int y, String type) {
		ImageIcon image = null;
		if (imgTable == null)
			loadImage();
		image = imgTable.get(type + ".mail");
		// System.out.println(type + ".mail");
		if (image != null) {
			BufferedImage offScreen = new BufferedImage(
					image.getIconWidth() + 80, image.getIconHeight() + 80,
					BufferedImage.TYPE_4BYTE_ABGR);
			body.setReversionRole(false);
			body.paint(offScreen.getGraphics(),
					(image.getIconWidth() + 80) / 2,
					image.getIconHeight() + 80, type);
			if (type.indexOf("ladder") == -1)
				offScreen.getGraphics().drawImage(image.getImage(), 38, 78,
						image.getIconWidth(), image.getIconHeight(), null);
			else
				offScreen.getGraphics().drawImage(image.getImage(), 36, 72,
						image.getIconWidth(), image.getIconHeight(), null);

			body.paintNoBody(offScreen.getGraphics(),
					(image.getIconWidth() + 80) / 2,
					image.getIconHeight() + 80, type);
			if (GameCharacter.getInstance().roleDir == 0 && reversion) {
				image = new ImageIcon(GameImageTool.flipImage(offScreen));
			} else {
				image = new ImageIcon(offScreen);
			}
			g.drawImage(image.getImage(), x - image.getIconWidth() / 2, y
					- image.getIconHeight(), image.getIconWidth(),
					image.getIconHeight(), null);
		} else {
			System.out.println(type + ".mail");
			body.setReversionRole(true);
			body.paint(g, x, y, type);
		}
	}
}
