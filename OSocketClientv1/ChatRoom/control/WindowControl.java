package control;

import java.awt.*;
import javax.swing.*;

import java.io.*;
import javax.imageio.ImageIO;

import frame.RoomPanel;

import java.util.*;

public class WindowControl {
	private static WindowControl singleton = null;

	public static WindowControl getInstance() {
		if (singleton == null)
			singleton = new WindowControl();
		return singleton;
	}

	private Hashtable<String, ImageIcon> imageTable;

	private WindowControl() {
		imageTable = new Hashtable<String, ImageIcon>();
		String fileIdx[] = { "HTML", "IMG", "GAME", "SETTING", "INFO",
				"UPLOAD", "GAMEDemo", "ImageUpload" };
		String fileName[] = { "HTML.png", "IMG.png", "GAME.png", "SETTING.png",
				"INFO.png", "UPLOAD.png", "gameinfo.gif", "image-up-icon.png" };
		Image img;
		try {
			for (int i = 0; i < fileName.length; i++) {
				img = ImageIO.read(WindowControl.class.getResource("images/"
						+ fileName[i]));
				imageTable.put(fileIdx[i], new ImageIcon(img));
			}
		} catch (Exception e) {
			System.out.println("Err !!!!!! " + e.getMessage());
		}
	}

	public ImageIcon getImageIcon(String key) {
		return imageTable.get(key);
	}

	public InputStream getImageInputStream(String key) {
		InputStream in = null;
		try {
			in = WindowControl.class.getResource("images/" + key).openStream();
		} catch(Exception e) {
			System.out.println(WindowControl.class + e.getMessage());
		}
		return in;
	}

	public String getDocumentText(String key) {
		String fileText = "";
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(WindowControl.class.getResource(
							"text/" + key).openStream(), "unicode"));
			String htmlText;
			while ((htmlText = reader.readLine()) != null) {
				// Keep in mind that readLine() strips the newline
				// characters
				fileText += htmlText + "\n";
			}
			reader.close();
		} catch (Exception ee) {
			System.out.println(ee.getMessage());
			ee.getStackTrace();
		}
		return fileText;
	}

	public static void setChatFontFamily(String fontName) {
		RoomPanel.getInstance().getTextArea().setTextFontFamily(fontName);
	}

	public static void setChatFontSize(int fontSize) {
		RoomPanel.getInstance().getTextArea().setTextFontSize(fontSize);
	}
}
