package gameMaterial.characterPaint;

import gameMaterial.GameCharacter;
import gameMaterial.GameImageTool;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Head {
	private Hashtable<String, ImageIcon> imgTable;

	public void loadImage() {
		imgTable = new Hashtable<String, ImageIcon>();
		BufferedImage img = null;
		try {
			img = ImageIO.read(this.getClass().getResource(
					"Character.wz_00012000.img/front.head.png"));
			imgTable.put("front.head", new ImageIcon(img));
			img = ImageIO.read(this.getClass().getResource(
					"Character.wz_00012000.img/back.head.png"));
			imgTable.put("back.head", new ImageIcon(img));
		} catch (Exception e) {
			System.out.println("Err" + e.getMessage());
		}
	}

	public void paint(Graphics g, int x, int y, String type) {
		ImageIcon image;
		if (imgTable == null)
			loadImage();
		if(type.equals("front"))
			image = imgTable.get("front.head");
		else
			image = imgTable.get("back.head");
		if(image != null) {
			g.drawImage(image.getImage(), x - image.getIconWidth() / 2, y
					- image.getIconHeight(), image.getIconWidth(),
					image.getIconHeight(), null);
		}
	}
}
