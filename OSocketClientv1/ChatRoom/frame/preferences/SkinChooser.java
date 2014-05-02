package frame.preferences;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceSkin;
import org.pushingpixels.substance.api.skin.RavenSkin;
import org.pushingpixels.substance.api.skin.SkinInfo;
import org.pushingpixels.substance.api.watermark.SubstanceImageWatermark;
import org.pushingpixels.substance.api.SubstanceConstants.ImageWatermarkKind;
import org.pushingpixels.substance.api.fonts.FontPolicy;
import org.pushingpixels.substance.api.fonts.FontSet;

import frame.MClient;

import java.util.*;
import java.io.*;
import control.*;

public class SkinChooser extends JPanel {
	DefaultListModel listModel;
	JTextField chooseTheme;
	JList<SkinLoader> skinList;

	public SkinChooser() {
		this.setLayout(new BorderLayout());
		this.add(new JLabel("Skin Theme"), BorderLayout.NORTH);

		JPanel leftPanel, rightPanel;
		leftPanel = new JPanel();
		leftPanel.setBorder(new TitledBorder("Skin Theme List"));
		BorderLayout layout = new BorderLayout();
		leftPanel.setLayout(layout);
		leftPanel.add(new JScrollPane(this.getSkinList()), BorderLayout.CENTER);
		this.add(leftPanel, BorderLayout.CENTER);
		rightPanel = new JPanel();
		this.chooseTheme = new JTextField(SubstanceLookAndFeel.getCurrentSkin()
				.getDisplayName());
		this.chooseTheme.setColumns(30);
		this.chooseTheme.setEditable(false);
		this.chooseTheme.setText(PreferencesControl.propMap.get("GeneralSkin"));
		rightPanel.add(this.chooseTheme);
		this.add(rightPanel, BorderLayout.SOUTH);
		this.setVisible(true);
	}

	public JList<?> getSkinList() {
		Map<String, SkinInfo> skins = SubstanceLookAndFeel.getAllSkins();
		SubstanceSkin currentSkin = SubstanceLookAndFeel.getCurrentSkin();
		listModel = new DefaultListModel();
		skinList = new JList<SkinLoader>(listModel);
		skinList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		for (SkinInfo skin : skins.values()) {
			SkinLoader item = new SkinLoader(skin);
			listModel.addElement(item);
			if (currentSkin != null) {
				if (currentSkin.getClass().getName()
						.equals(skin.getClassName())) {
					skinList.setSelectedIndex(listModel.getSize() - 1);
				}
			}
		}
		skinList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 0) {
					int index = skinList.locationToIndex(e.getPoint());
					Object item = listModel.getElementAt(index);

					if (item instanceof SkinLoader) {
						chooseTheme.setText(((SkinLoader) item)
								.getSkinClassName());
						((SkinLoader) item).loadSkin();
					}
				}
			}
		});
		return skinList;
	}

	public JComboBox<?> getSkinComboBox() {
		JComboBox<SkinLoader> skinSelector = new JComboBox<SkinLoader>();
		Map<String, SkinInfo> skins = SubstanceLookAndFeel.getAllSkins();
		SubstanceSkin currentSkin = SubstanceLookAndFeel.getCurrentSkin();
		for (SkinInfo skin : skins.values()) {
			SkinLoader item = new SkinLoader(skin);
			skinSelector.addItem(item);
			if (currentSkin != null) {
				if (currentSkin.getClass().getName()
						.equals(skin.getClassName())) {
					skinSelector.setSelectedItem(item);
				}
			}
		}
		skinSelector.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					if (ie.getItem() instanceof SkinLoader)
						((SkinLoader) ie.getItem()).loadSkin();
				}
			}
		});
		return skinSelector;
	}

	public class SkinLoader {
		SkinInfo skin;

		public SkinLoader(SkinInfo skin) {
			this.skin = skin;
		}

		public void loadSkin() {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					PreferencesControl.writeGeneralSkin(skin.getClassName());
					SubstanceLookAndFeel.setSkin(skin.getClassName());
				}
			});
		}

		public String getSkinClassName() {
			return skin.getClassName();
		}

		@Override
		public String toString() {
			return skin.getDisplayName();
		}
	}
}
