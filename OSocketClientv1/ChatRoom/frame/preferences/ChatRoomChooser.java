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
import java.util.*;
import java.io.*;
import control.*;

public class ChatRoomChooser extends JPanel {
	JList<String> fontList;
	DefaultListModel listModel;
	JTextField chooseFont;
	JComboBox<String> fontSelector;

	public ChatRoomChooser() {
		this.setLayout(new BorderLayout());
		this.add(new JLabel("Chat Room Setting"), BorderLayout.NORTH);
		JPanel leftPanel, centerPanel, rightPanel, underPanel;
		leftPanel = new JPanel();
		leftPanel.setBorder(new TitledBorder("Font"));
		leftPanel.setLayout(new GridLayout(1, 1));
		leftPanel.add(new JScrollPane(this.getFontList()));
		this.add(leftPanel, BorderLayout.CENTER);
		centerPanel = new JPanel();
		centerPanel.setBorder(new TitledBorder("Font-Size"));
		centerPanel.add(this.getFontSizeComboBox());
		// fontSelector.setSelectedIndex(fontSelector.get)
		this.add(centerPanel, BorderLayout.EAST);
		underPanel = new JPanel();
		underPanel.setBorder(new TitledBorder("Font-Color"));
		underPanel.setLayout(new GridLayout(1, 6));
		underPanel.add(new ColorComboBox("超連結", Color.BLACK, ""));
		underPanel.add(new ColorComboBox("一般", Color.BLACK, ""));
		underPanel.add(new ColorComboBox("密語", Color.BLACK, ""));
		underPanel.add(new ColorComboBox("系統", Color.BLACK, ""));
		rightPanel = new JPanel();
		this.chooseFont = new JTextField("Comic Sans MS");
		this.chooseFont.setColumns(30);
		this.chooseFont.setEditable(false);
		this.chooseFont.setText(PreferencesControl.propMap.get("Font-family"));
		rightPanel.setLayout(new GridLayout(2, 1));
		rightPanel.add(underPanel);
		rightPanel.add(this.chooseFont);
		this.add(rightPanel, BorderLayout.SOUTH);
		this.setVisible(true);
	}

	public JList<?> getFontList() {
		listModel = new DefaultListModel();
		fontList = new JList<String>(listModel);
		fontList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		GraphicsEnvironment ge;

		ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] fontNames = ge.getAvailableFontFamilyNames();

		FontPolicy fontPolicy = SubstanceLookAndFeel.getFontPolicy();
		FontSet fontSet = fontPolicy.getFontSet("Substance", null);
		String currentFont = fontSet.getWindowTitleFont().getFontName();
		listModel.addElement(currentFont);
		for (String fontName : fontNames)
			listModel.addElement(fontName);
		fontList.setCellRenderer(new ListCellRenderer() {
			protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				JLabel renderer = (JLabel) defaultRenderer
						.getListCellRendererComponent(list, value, index,
								isSelected, cellHasFocus);
				renderer.setFont(new Font(renderer.getText(), Font.PLAIN, 18));
				return renderer;
			}
		});
		fontList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 0) {
					int index = fontList.locationToIndex(e.getPoint());
					String item = listModel.getElementAt(index).toString();
					chooseFont.setText(item);
					SwingUtilities.invokeLater(new Runnable() {
						String fontName = "";

						public Runnable init(String fontName) {
							this.fontName = fontName;
							return this;
						}

						public void run() {
							PreferencesControl.writeFont(fontName, "");
							WindowControl.setChatFontFamily(fontName);
						}
					}.init(item));
				}
			}
		});
		return fontList;
	}

	private JComboBox<?> getFontSizeComboBox() {
		fontSelector = new JComboBox<String>();

		String fontSize[] = { "8", "9", "10", "11", "12", "14", "16", "18",
				"20", "22", "24", "26", "28", "36", "48", "72" };
		for (String size : fontSize)
			fontSelector.addItem(size);
		fontSelector.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					String item = (String) ie.getItem();
					SwingUtilities.invokeLater(new Runnable() {
						int fontSize = 12;

						public Runnable init(String fontSize) {
							this.fontSize = Integer.parseInt(fontSize);
							return this;
						}

						public void run() {
							PreferencesControl.writeFont("", fontSize + "");
							WindowControl.setChatFontSize(fontSize);
						}
					}.init(item));
				}
			}
		});
		return fontSelector;
	}
}
