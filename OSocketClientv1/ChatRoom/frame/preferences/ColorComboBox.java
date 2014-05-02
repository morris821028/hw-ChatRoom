package frame.preferences;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;


public class ColorComboBox extends JPanel {

	JComboBox<ColorInfo> colorSelector;
	String prop;
	public ColorComboBox(String title, Color initColor, String prop) {
		this.setLayout(new GridLayout(1, 2));
		this.add(new JLabel(title));
		this.add(getFontColorComboBox());
		this.prop = prop;
	}

	private JComboBox<?> getFontColorComboBox() {
		colorSelector = new JComboBox<ColorInfo>();
		colorSelector.addItem(new ColorInfo("Black", Color.BLACK));
		colorSelector.addItem(new ColorInfo("Blue", Color.BLUE));
		colorSelector.addItem(new ColorInfo("Cyan", Color.CYAN));
		colorSelector.addItem(new ColorInfo("Red", Color.RED));
		colorSelector.addItem(new ColorInfo("Yellow", Color.YELLOW));
		colorSelector.addItem(new ColorInfo("White", Color.WHITE));
		colorSelector.addItem(new ColorInfo("Green", Color.GREEN));
		colorSelector.addItem(new ColorInfo("Magenta", Color.MAGENTA));
		colorSelector.addItem(new ColorInfo("Pink", Color.PINK));
		colorSelector.addItem(new ColorInfo("Orange", Color.ORANGE));
		colorSelector.addItem(new ColorInfo("Gray", Color.GRAY));
		colorSelector.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					ColorInfo item = (ColorInfo) ie.getItem();
					SwingUtilities.invokeLater(new Runnable() {

						public void run() {
						}
					});
				}
			}
		});
		colorSelector.setRenderer(new ListCellRenderer() {
			protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				JLabel renderer = (JLabel) defaultRenderer
						.getListCellRendererComponent(list, value, index,
								isSelected, cellHasFocus);
				if (value instanceof ColorInfo) {
					renderer.setIcon(new MIcon(((ColorInfo) value).color));
				}
				return renderer;
			}
		});
		return colorSelector;
	}

	class ColorInfo {
		Color color;
		String name;

		ColorInfo(String name, Color color) {
			this.color = color;
			this.name = name;
		}

		public Color getColor() {
			return color;
		}

		@Override
		public String toString() {
			return "";
		}
	}

	class MIcon implements Icon {
		Color fillColor;

		public MIcon(Color fillColor) {
			this.fillColor = fillColor;
		}

		public int getIconHeight() {
			return 20;
		}

		public int getIconWidth() {
			return 20;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(fillColor);
			g.fillRect(0, 0, 25, 25);
		}
	}
}
