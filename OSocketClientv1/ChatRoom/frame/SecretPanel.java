package frame;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SecretPanel extends JPanel {
	private MTextArea textArea;
	private String who;
	private UserInputPanel userInput;
	/**
	 * 
	 * @param who
	 *            告知密語對象
	 */
	public SecretPanel(String who) {
		this.who = who;
		this.textArea = new MTextArea();
		this.setLayout(new BorderLayout());
		this.add(textArea, BorderLayout.CENTER);
		userInput = new UserInputPanel();
		userInput.setWhisper(who);
		this.add(userInput, BorderLayout.SOUTH);
	}

	public void appendMessage(String msg) {
		this.textArea.append(msg, 1);
	}
}
