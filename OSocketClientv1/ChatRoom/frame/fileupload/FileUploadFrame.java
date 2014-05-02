package frame.fileupload;

import java.awt.BorderLayout;
import java.awt.Dialog;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import control.ChatSocketControl;
import control.FileUploadControl;
import control.PreferencesControl;
import control.WindowControl;
import frame.HomePage;
import frame.RoomPanel;
import frame.preferences.PreferencesFrame;

public class FileUploadFrame extends JFrame {
	private static FileUploadFrame singleton = null;

	public static FileUploadFrame getInstance() {
		if (singleton == null)
			singleton = new FileUploadFrame();
		return singleton;
	}

	static String prevFilePath = null;

	private JButton uploadButton;
	private JButton cancelButton;
	private JButton chooseButton;
	private JTextField choosePath;
	private JLabel imageView;

	private JLabel msgLabel;
	private JProgressBar progressBar;
	private JDialog dialog;
	private boolean uploading;

	private FileUploadFrame() {
		uploadButton = new JButton("Upload");
		cancelButton = new JButton("Cancel");
		chooseButton = new JButton("Choose");
		choosePath = new JTextField("", 20);
		imageView = new JLabel();
		progressBar = new JProgressBar(0, 100);
		msgLabel = new JLabel("Uploading ...");
		progressBar.setStringPainted(true);
		uploading = false;
		imageView.setIcon(WindowControl.getInstance().getImageIcon(
				"ImageUpload"));
		choosePath.setEditable(false);
		chooseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFileChooser();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PreferencesControl.recoverProperties();
				FileUploadFrame.getInstance().setVisible(false);
			}
		});
		uploadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileUploadFrame.getInstance().setVisible(false);
				FileUploadControl.getInstance().upload(choosePath.getText());
				FileUploadFrame.getInstance().startUpload();
			}
		});
		JPanel northPanel = new JPanel();
		northPanel.add(choosePath);
		northPanel.add(chooseButton);
		JPanel southPanel = new JPanel();
		southPanel.add(uploadButton);
		southPanel.add(cancelButton);
		JScrollPane centerPane = new JScrollPane(new JPanel());
		JPanel view = ((JPanel) centerPane.getViewport().getView());
		view.setLayout(new BorderLayout());
		view.add(imageView, BorderLayout.CENTER);

		this.add(northPanel, BorderLayout.NORTH);
		this.add(centerPane, BorderLayout.CENTER);
		this.add(southPanel, BorderLayout.SOUTH);
		this.setTitle("File Upload Frame");
		this.setSize(500, 600);
		this.setLocationRelativeTo(null);
		this.setVisible(false);
	}

	public void openFileChooser() {
		JFileChooser chooser;
		if (prevFilePath != null)
			chooser = new JFileChooser(prevFilePath);
		else
			chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"所有圖片檔案 (*.jpg, *.gif, *.png)", "jpg", "gif", "png");
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String filepath = chooser.getSelectedFile().getAbsolutePath();
			this.choosePath.setText(filepath);
			try {
				Image image = ImageIO.read(new File(filepath));
				imageView.setIcon(new ImageIcon(image));
			} catch (Exception e) {
				System.out.println(e);
			}
			prevFilePath = filepath;
			/*
			 * initProgressBar(); SendData sendData = new SendData(filepath,
			 * serverIP, serverPort); openProgressBar(); return sendData.URL;
			 */
		}
	}

	/**
	 * 讀檔進度條
	 */
	public void startUpload() {
		uploading = true;
		JPanel panel;
		panel = new JPanel(new BorderLayout(5, 5));
		panel.add(msgLabel, BorderLayout.PAGE_START);
		panel.add(progressBar, BorderLayout.CENTER);
		panel.setBorder(BorderFactory.createEmptyBorder(11, 11, 11, 11));
		dialog = new JDialog();
		dialog.getContentPane().add(panel);
		dialog.setResizable(false);
		dialog.pack();
		dialog.setSize(500, dialog.getHeight());
		dialog.setLocationRelativeTo(null);
		dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.setVisible(true);
	}
	public void endUpload() {
		dialog.dispose();
		if(this.progressBar.getValue() != 100) {
			HomePage.getInstance().chatMessageAction("S", "Upload fail !");
		} else {
			HomePage.getInstance().chatMessageAction("S", "Upload success !");
		}
		uploading = false;
	}
	public void setProcessBar(int percent) {
		SwingUtilities.invokeLater(new Runnable() {
			int val;

			public Runnable init(int val) {
				this.val = val;
				return this;
			}

			public void run() {
				progressBar.setValue(this.val);
			}
		}.init(percent));
	}
}
