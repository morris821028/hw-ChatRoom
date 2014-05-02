package frame;

import javax.imageio.ImageIO;
import java.io.*;

import javax.swing.*;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceConstants.ImageWatermarkKind;
import org.pushingpixels.substance.api.skin.RavenSkin;
import org.pushingpixels.substance.api.watermark.SubstanceImageWatermark;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import control.*;

public class MClient extends JFrame {

	public MClient() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("MClient");
		this.setSize(800, 650);
		this.setResizable(false);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {

				ChatSocketControl.getInstance().closeSocket();

			}
		});
		this.setLocationRelativeTo(null);
		this.add(HomePage.getInstance());
		try {
			Image frameIcon = ImageIO.read(this.getClass().getResource(
					"images/frameicon.png"));
			this.setIconImage(frameIcon);
		} catch (Exception e) {

		}
		PreferencesControl.loadProperties();
	}
}
