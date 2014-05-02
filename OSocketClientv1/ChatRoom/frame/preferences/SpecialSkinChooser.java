package frame.preferences;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceConstants.ImageWatermarkKind;
import org.pushingpixels.substance.api.skin.RavenSkin;
import org.pushingpixels.substance.api.watermark.SubstanceImageWatermark;

import control.*;

public class SpecialSkinChooser extends JPanel {
	JTextField filepath;
	JButton uploadButton;

	public SpecialSkinChooser() {
		this.setLayout(new BorderLayout());
		this.add(new JLabel("Special Skin Theme"), BorderLayout.NORTH);
		JPanel leftPanel, rightPanel;
		leftPanel = new JPanel(new GridLayout(2, 1));
		leftPanel.setBorder(new TitledBorder("Default-Skin-Theme"));
		leftPanel.add(this.getCustomComboBox());
		uploadButton = new JButton("Upload Image");
		uploadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"所有圖片檔案 (*.jpg, *.gif, *.png)", "jpg", "gif", "png");
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					String filepath = chooser.getSelectedFile()
							.getAbsolutePath();
					InputStream imageInputStream = null;
					try {
						imageInputStream = new FileInputStream(filepath);
						SpecialSkinChooser.this.filepath.setText(filepath);
						addImageSkin(imageInputStream);
						PreferencesControl.writeSpecialSkin(filepath);
					} catch (Exception ee) {

					}
				}
			}
		});
		leftPanel.add(uploadButton);
		this.add(leftPanel, BorderLayout.CENTER);
		rightPanel = new JPanel();
		filepath = new JTextField("Nick");
		filepath.setText(PreferencesControl.propMap.get("SpecialSkin"));
		filepath.setEditable(false);
		filepath.setColumns(30);
		rightPanel.add(filepath);
		this.add(rightPanel, BorderLayout.SOUTH);
		this.setVisible(true);
	}

	private JComboBox<?> getCustomComboBox() {
		final JComboBox<String> customSelector = new JComboBox<String>();

		String customList[] = { "Nick", "Morris" };
		for (String custom : customList)
			customSelector.addItem(custom);
		customSelector.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					String item = (String) ie.getItem();
					InputStream imageInputStream = null;
					if (item.equals("Nick")) {
						filepath.setText("Nick");
						PreferencesControl.writeSpecialSkin("Nick");
						try {
							imageInputStream = WindowControl.getInstance()
									.getImageInputStream("watermark2.jpg");
						} catch (Exception e) {
						}
					} else if (item.equals("Morris")) {
						filepath.setText("Morris");
						PreferencesControl.writeSpecialSkin("Morris");
						try {
							imageInputStream = WindowControl.getInstance()
									.getImageInputStream("watermark.jpg");
						} catch (Exception e) {

						}
					}
					addImageSkin(imageInputStream);
				}
			}
		});
		return customSelector;
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
