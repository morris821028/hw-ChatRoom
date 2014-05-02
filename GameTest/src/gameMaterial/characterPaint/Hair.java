package gameMaterial.characterPaint;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Hair extends Head {
	public Head head;
	private Hashtable<String, ImageIcon> imgTable;
	public Hair(Head head) {
		this.head = head;
	}
	public void loadImage() {
		imgTable = new Hashtable<String, ImageIcon>();
		BufferedImage img = null;
		String fileName[] = { "backDefault.backHair.png", "default.hairOverHead.png" };
		String fileIdx[] = { "backDefault.backHair", "default.hairOverHead" };
		try {
			for (int i = 0; i < fileName.length; i++) {
				// img = ImageIO.read(new File(fileName[i]));
				img = ImageIO.read(this.getClass().getResource(
						"Character.wz_00037402.img/" + fileName[i]));
				/*img = ImageIO.read(this.getClass().getResource(
						"Character.wz_00031680.img/" + fileName[i]));*/
				imgTable.put(fileIdx[i], new ImageIcon(img));
			}
		} catch (Exception e) {
			System.out.println("Err" + e.getMessage());
		}
	}

	public void paint(Graphics g, int x, int y, String type) {
		this.head.paint(g, x, y, type);
		ImageIcon image;
		if (imgTable == null)
			loadImage();
		if(type.equals("front"))
			image = imgTable.get("default.hairOverHead");
		else
			image = imgTable.get("backDefault.backHair");
		if(image != null) {
			g.drawImage(image.getImage(), x - image.getIconWidth() / 2 + 3, y
					- 48, image.getIconWidth(),
					image.getIconHeight(), null);
		}
	}
}
