import java.io.InputStream;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;

import java.util.*;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceConstants.ImageWatermarkKind;
import org.pushingpixels.substance.api.skin.RavenSkin;
import org.pushingpixels.substance.api.watermark.SubstanceImageWatermark;

import frame.*;

public class Demo {
	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				UIManager.put("TabbedPane.contentOpaque", Boolean.TRUE);
				UIManager.put("Tree.rendererFillBackground", false);
				try {
					InputStream inputstream = MClient.class.getResource(
							"images/watermark2.jpg").openStream();

					SubstanceImageWatermark watermark = new SubstanceImageWatermark(
							inputstream);
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
		});
		MClient f = new MClient();
		f.setVisible(true);
	}
}
