package gameMaterial.characterPaint;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Face extends Head {
	public Head head;
	public int counter = 0;
	private Hashtable<String, ImageIcon> imgTable;

	public Face(Head head) {
		this.head = head;
	}

	public void loadImage() {
		imgTable = new Hashtable<String, ImageIcon>();
		BufferedImage img = null;
		String fileName[] = { "blink.0.face.png", "blink.1.face.png",
				"blink.2.face.png" };
		String fileIdx[] = { "blink.0.face", "blink.1.face", "blink.2.face" };
		try {
			for (int i = 0; i < fileName.length; i++) {
				// img = ImageIO.read(new File(fileName[i]));
				img = ImageIO.read(this.getClass().getResource(
						"Character.wz_00021299.img/" + fileName[i]));
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
		counter++;
		counter %= 21;
		if (counter < 15)
			image = imgTable.get("blink.0.face");
		else if(counter < 17)
			image = imgTable.get("blink.1.face");
		else if(counter < 19)
			image = imgTable.get("blink.2.face");
		else
			image = imgTable.get("blink.1.face");
		if (image != null) {
			g.drawImage(image.getImage(), x - image.getIconWidth() / 2 -3 ,
					y - 20, image.getIconWidth(), image.getIconHeight(), null);
		}
	}
}
