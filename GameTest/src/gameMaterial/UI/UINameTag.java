package gameMaterial.UI;

import gameMaterial.GameCharacter;
import gameMaterial.GameImageTool;

import java.awt.Color;
import java.awt.Font;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class UINameTag {
	String name;
	protected Hashtable<String, ImageIcon> imgTable;

	public UINameTag(String s) {
		name = s;
	}

	public void loadImage() {
		imgTable = new Hashtable<String, ImageIcon>();
		BufferedImage img = null;
		String fileName[] = { "17.c.png", "17.e.png", "17.w.png" };
		String fileIdx[] = { "17.c", "17.e", "17.w" };
		try {
			for (int i = 0; i < fileName.length; i++) {
				// img = ImageIO.read(new File(fileName[i]));
				img = ImageIO.read(this.getClass().getResource(
						"UI.wz_NameTag.img/" + fileName[i]));
				imgTable.put(fileIdx[i], new ImageIcon(img));
			}
		} catch (Exception e) {
			System.out.println("Err" + e.getMessage());
		}
	}

	public void paint(Graphics g, int x, int y) {
		ImageIcon image;
		if (imgTable == null)
			loadImage();
		image = imgTable.get("17.c");
		if (image != null) {
			BufferedImage offScreen = new BufferedImage(name.length() * 14, 21,
					BufferedImage.TYPE_4BYTE_ABGR);
			image = imgTable.get("17.w");
			offScreen.getGraphics().drawImage(image.getImage(), 0, 0,
					image.getIconWidth(), image.getIconHeight(), null);
			int baseX = image.getIconWidth();
			int len = name.length();
			for (int i = 0; i < len; i++) {
				image = imgTable.get("17.c");
				offScreen.getGraphics().drawImage(image.getImage(), baseX, 0,
						image.getIconWidth(), image.getIconHeight(), null);
				baseX += image.getIconWidth();
				offScreen.getGraphics().drawImage(image.getImage(), baseX, 0,
						image.getIconWidth(), image.getIconHeight(), null);
				baseX += image.getIconWidth();
			}
			image = imgTable.get("17.e");
			offScreen.getGraphics().drawImage(image.getImage(), baseX, 0,
					image.getIconWidth(), image.getIconHeight(), null);
			Graphics2D g2d = offScreen.createGraphics();
			g2d.setPaint(Color.BLACK);
	        g2d.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
	        g2d.drawString(name, 11, 15);
			image = new ImageIcon(offScreen);
			g.drawImage(image.getImage(), x - baseX / 2, y + 5,
					image.getIconWidth(), image.getIconHeight(), null);
		}
	}
}
