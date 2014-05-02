package gameMaterial.UI;

import gameMaterial.GameCharacter;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class UIChatBalloon {
	String text;
	protected Hashtable<String, ImageIcon> imgTable;
	int lineChar = 14;
	int TTL = 0;

	public UIChatBalloon(String s) {
		text = s;
		TTL = 50;
	}

	public void loadImage() {
		imgTable = new Hashtable<String, ImageIcon>();
		BufferedImage img = null;
		String fileName[] = { "0.c.png", "0.nw.png", "0.n.png", "0.ne.png",
				"0.e.png", "0.se.png", "0.s.png", "0.sw.png", "0.w.png",
				"0.arrow.png" };
		String fileIdx[] = { "0.c", "0.nw", "0.n", "0.ne", "0.e", "0.se",
				"0.s", "0.sw", "0.w", "0.arrow" };
		try {
			for (int i = 0; i < fileName.length; i++) {
				// img = ImageIO.read(new File(fileName[i]));
				img = ImageIO.read(this.getClass().getResource(
						"UI.wz_ChatBalloon.img/" + fileName[i]));
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
		if (TTL < 0 || text.length() == 0) {
			if (Math.random() < 0.2) {
				text = StringUtil.generateRandomString();
				TTL = 50;
			}
			return;
		}
		TTL--;
		image = imgTable.get("0.nw");
		if (image != null) {
			int lines = StringUtil.countByteString(text) / lineChar;
			if (StringUtil.countByteString(text) % lineChar != 0)
				lines++;
			BufferedImage offScreen = new BufferedImage(12 + 12 * lineChar / 2,
					lines * 14 + 12 + 20, BufferedImage.TYPE_4BYTE_ABGR);
			offScreen.getGraphics().drawImage(image.getImage(), 0, 0,
					image.getIconWidth(), image.getIconHeight(), null);
			int baseX = image.getIconWidth(), baseY = image.getIconHeight();
			image = imgTable.get("0.n");
			for (int i = 0; i < lineChar / 2; i++) {
				offScreen.getGraphics().drawImage(image.getImage(), baseX, 0,
						image.getIconWidth(), image.getIconHeight(), null);
				baseX += image.getIconWidth();
			}
			image = imgTable.get("0.ne");
			offScreen.getGraphics().drawImage(image.getImage(), baseX, 0,
					image.getIconWidth(), image.getIconHeight(), null);
			for (int i = 0; i < lines; i++) {
				image = imgTable.get("0.w");
				offScreen.getGraphics().drawImage(image.getImage(), 0, baseY,
						image.getIconWidth(), image.getIconHeight(), null);
				int shiftX = image.getIconWidth();
				image = imgTable.get("0.c");
				for (int j = 0; j < lineChar / 2; j++) {
					offScreen.getGraphics().drawImage(image.getImage(), shiftX,
							baseY, image.getIconWidth(), image.getIconHeight(),
							null);
					shiftX += image.getIconWidth();
				}
				image = imgTable.get("0.e");
				offScreen.getGraphics().drawImage(image.getImage(), shiftX,
						baseY, image.getIconWidth(), image.getIconHeight(),
						null);
				baseY += image.getIconHeight();
			}
			image = imgTable.get("0.sw");
			offScreen.getGraphics().drawImage(image.getImage(), 0, baseY,
					image.getIconWidth(), image.getIconHeight(), null);
			baseX = image.getIconWidth();
			image = imgTable.get("0.s");
			for (int i = 0; i < lineChar/2; i++) {
				offScreen.getGraphics().drawImage(image.getImage(), baseX,
						baseY, image.getIconWidth(), image.getIconHeight(),
						null);
				baseX += image.getIconWidth();
			}
			image = imgTable.get("0.se");
			offScreen.getGraphics().drawImage(image.getImage(), baseX, baseY,
					image.getIconWidth(), image.getIconHeight(), null);
			image = imgTable.get("0.arrow");
			if (GameCharacter.getInstance().roleDir == 0)
				offScreen.getGraphics().drawImage(image.getImage(), 15, baseY,
						image.getIconWidth(), image.getIconHeight(), null);
			else
				offScreen.getGraphics().drawImage(image.getImage(), baseX - 30,
						baseY, image.getIconWidth(), image.getIconHeight(),
						null);
			Graphics2D g2d = offScreen.createGraphics();
			g2d.setPaint(Color.BLACK);
			g2d.setFont(new Font("²Ó©úÅé", Font.PLAIN, 12));
			baseY = 6;
			int startTextIdx = 0;
			for (int i = 0; i < lines; i++) {
				String lineStr = StringUtil.subString(text, startTextIdx,
						lineChar);
				baseY += 14;
				g2d.drawString(lineStr, 6, baseY);
				startTextIdx += lineStr.length() - 1;
			}
			image = new ImageIcon(offScreen);
			g.drawImage(image.getImage(), x - image.getIconWidth() / 2, y - 60
					- image.getIconHeight(), image.getIconWidth(),
					image.getIconHeight(), null);
		}
	}
}
