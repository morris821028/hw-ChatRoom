package control;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceConstants.ImageWatermarkKind;
import org.pushingpixels.substance.api.skin.RavenSkin;
import org.pushingpixels.substance.api.watermark.SubstanceImageWatermark;

public class PreferencesControl {
	static Properties prop = null;
	static FileInputStream fin = null;
	static FileOutputStream fout = null;
	public static Map<String, String> propMap = new HashMap<String, String>();

	public static void loadProperties() {
		try {
			prop = new Properties();
			fin = new FileInputStream("MClientSetting.properties");
			if (fin == null) {

			} else {
			}
		} catch (Exception e) {
		}
		if (fin == null) {
			prop.setProperty("SpecialSkin", "Morris");
			prop.setProperty("GeneralSkin", "NULL");
			prop.setProperty("Font-family", "Comic Sans MS");
			prop.setProperty("Font-size", "18");
			Set<String> pnames = prop.stringPropertyNames();
			for (Iterator it = pnames.iterator(); it.hasNext();) {
				String propertyKey = (String) it.next();
				propMap.put(propertyKey, prop.getProperty(propertyKey));
			}
			try {
				fout = new FileOutputStream("MClientSetting.properties");
				prop.store(fout, "Copyright (c) Morris");
				fout.close();
			} catch (Exception e) {

			}
			try {
				prop = new Properties();
				fin = new FileInputStream("MClientSetting.properties");
				if (fin == null) {

				} else {
				}
			} catch (Exception e) {
			}
		}
		try {
			prop.load(fin);
			Set<String> pnames = prop.stringPropertyNames();
			for (Iterator it = pnames.iterator(); it.hasNext();) {
				String propertyKey = (String) it.next();
				propMap.put(propertyKey, prop.getProperty(propertyKey));
			}
			String generalSkin = prop.getProperty("GeneralSkin");
			setGeneralSkin(generalSkin);
			String specialSkin = prop.getProperty("SpecialSkin");
			setSpecialSkin(specialSkin);
			String fontFamily = prop.getProperty("Font-family");
			setFontFamily(fontFamily);
			String fontSize = prop.getProperty("Font-size");
			setFontSize(fontSize);
			fin.close();
		} catch (Exception e) {

		}
	}

	public static void writeProperties() {
		Set<String> pnames = prop.stringPropertyNames();
		for (Iterator it = pnames.iterator(); it.hasNext();) {
			String propertyKey = (String) it.next();
			propMap.put(propertyKey, prop.getProperty(propertyKey));
		}
		try {
			fout = new FileOutputStream("MClientSetting.properties");
			prop.store(fout, "Copyright (c) Morris");
			fout.close();
		} catch (Exception e) {

		}
	}

	public static void recoverProperties() {
		for (Iterator it = propMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry m = (Map.Entry) it.next();
			String key = (String) m.getKey();
			String value = (String) m.getValue();
			prop.setProperty(key, value);
		}
		loadProperties();
	}

	public static void writeSpecialSkin(String skin) {
		try {
			prop.setProperty("SpecialSkin", skin);
			prop.setProperty("GeneralSkin", "NULL");
		} catch (Exception e) {
		}
	}

	public static void writeGeneralSkin(String skin) {
		try {
			prop.setProperty("SpecialSkin", "NULL");
			prop.setProperty("GeneralSkin", skin);
		} catch (Exception e) {
		}
	}

	public static void writeFont(String fontFamily, String fontSize) {
		try {
			if (fontFamily.length() > 0)
				prop.setProperty("Font-family", fontFamily);
			if (fontSize.length() > 0)
				prop.setProperty("Font-size", fontSize);
		} catch (Exception e) {
		}
	}

	public static void setGeneralSkin(String skin) {
		if (skin.equals("NULL"))
			return;
		SwingUtilities.invokeLater(new Runnable() {
			String skinName;

			public Runnable init(String skinName) {
				this.skinName = skinName;
				return this;
			}

			public void run() {
				SubstanceLookAndFeel.setSkin(skinName);
			}
		}.init(skin));
	}

	public static void setSpecialSkin(String skin) {
		if (skin.equals("NULL"))
			return;
		InputStream imageInputStream = null;
		if (skin.equals("Nick")) {
			try {
				imageInputStream = WindowControl.getInstance()
						.getImageInputStream("watermark2.jpg");
			} catch (Exception e) {

			}
		} else if (skin.equals("Morris")) {
			try {
				imageInputStream = WindowControl.getInstance()
						.getImageInputStream("watermark.jpg");
			} catch (Exception e) {

			}
		} else {
			try {
				imageInputStream = new FileInputStream(skin);
			} catch (Exception ee) {

			}
		}
		addImageSkin(imageInputStream);
	}

	public static void setFontFamily(String font) {
		if (font.equals("Default"))
			return;
		SwingUtilities.invokeLater(new Runnable() {
			String fontName = "";

			public Runnable init(String fontName) {
				this.fontName = fontName;
				return this;
			}

			public void run() {
				WindowControl.setChatFontFamily(fontName);
			}
		}.init(font));
	}

	public static void setFontSize(String font) {
		if (font.equals("Default"))
			return;
		SwingUtilities.invokeLater(new Runnable() {
			int fontSize = 12;

			public Runnable init(String fontSize) {
				this.fontSize = Integer.parseInt(fontSize);
				return this;
			}

			public void run() {
				WindowControl.setChatFontSize(fontSize);
			}
		}.init(font));
	}

	public static void addImageSkin(InputStream imageInputStream) {
		SwingUtilities.invokeLater(new Runnable() {
			InputStream image;

			public Runnable init(InputStream image) {
				this.image = image;
				return this;
			}

			public void run() {
				try {
					SubstanceImageWatermark watermark = new SubstanceImageWatermark(
							image);
					SubstanceLookAndFeel.setSkin(new RavenSkin() {
						public RavenSkin withWatermark(
								SubstanceImageWatermark watermark) {
							watermark.setOpacity(0.3f);
							this.watermark = watermark;
							watermark.setKind(ImageWatermarkKind.APP_CENTER);
							return this;
						}
					}.withWatermark(watermark));
				} catch (Exception e) {
					e.getStackTrace();
				}
			}
		}.init(imageInputStream));
	}
}
