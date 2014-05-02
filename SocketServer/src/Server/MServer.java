package Server;

import java.io.*;

import javax.swing.*;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.RavenSkin;


import java.awt.*;
import java.awt.event.*;

public class MServer extends JFrame {
	public MServer() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("MServer");
		this.setSize(800, 650);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// practice closing all socket.
			}
		});
		this.setLocationRelativeTo(null);
		this.add(new ControlPanel());
		this.setVisible(true);
	}

	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SubstanceLookAndFeel.setSkin(new RavenSkin());
				UIManager.put("TabbedPane.contentOpaque", Boolean.TRUE);
			}
		});
		new MServer();
	}
}
