import java.awt.BorderLayout;

import javax.swing.JFrame;

import frame.CanvasDemo;


public class Main {
	public static void main(String[] args) {
		JFrame f = new JFrame("Game Test");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(CanvasDemo.getInstance().getView(), BorderLayout.CENTER);
		f.setSize(1024, 768);
		f.setResizable(false);
		f.setVisible(true);
	}
}
