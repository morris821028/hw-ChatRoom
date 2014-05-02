package game;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import javax.swing.*;
import java.awt.*;

/**
 * 進度條，自動 5 秒倒數，再確認連線後才會觸發
 * @author owner
 * 
 */
public class AnimationProgress extends JPanel implements ActionListener {
	private Timer timer = new Timer(250, this);
	private int count = 0;
	JProgressBar bar = new JProgressBar(0, 4000);

	public AnimationProgress() {
		this.setLayout(new GridLayout(1, 1));
		bar.setStringPainted(true);
		bar.setString("Waiting.");
		bar.setFont(new Font("Serif", Font.BOLD, 28));
		add(bar);
		this.setVisible(false);
	}

	public void start() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				AnimationProgress.this.bar.setValue(0);
				AnimationProgress.this.setVisible(true);
				AnimationProgress.this.actionPerformed(null);
				AnimationProgress.this.bar.setVisible(true);
				AnimationProgress.this.timer.start();
			}
		});
	}

	public void actionPerformed(ActionEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				bar.setValue(bar.getValue() + 250);
				count++;
				if (bar.getValue() >= bar.getMaximum()) {
					bar.setString("Start");
					bar.setForeground(Color.RED);
					if (count >= 4000 / 250 + 3) {
						AnimationProgress.this.setVisible(false);
						AnimationProgress.this.bar.setVisible(false);
						AnimationProgress.this.timer.stop();
					}
				} else {
					String barMsg = "Waiting";
					for (int i = count % 3; i >= 0; i--)
						barMsg += ".";
					bar.setString(barMsg);
				}
				AnimationProgress.this.repaint();
			}
		});
	}
}