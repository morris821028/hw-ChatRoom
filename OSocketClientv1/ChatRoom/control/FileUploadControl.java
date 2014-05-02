package control;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.SwingUtilities;

import frame.LoginPanel;
import frame.MTextArea;
import frame.fileupload.FileUploadFrame;

public class FileUploadControl {
	static final int fileServerPort = 1979;
	static final String fileServerIP = "140.115.205.194";
	private static FileUploadControl singleton = null;
	public static FileUploadControl getInstance() {
		if(singleton == null)
			singleton = new FileUploadControl();
		return singleton;
	}
	private FileUploadControl() {
		
	}
	public void upload(String filepath) {
		new UploadThread(filepath);
	}

	class UploadThread extends Thread {
		String filepath;
		String URL;

		/**
		 * 宣告的同時就會進行傳送
		 * 
		 * @param filepath
		 */
		UploadThread(String filepath) {
			this.filepath = filepath;
			this.URL = "";
			this.start();
		}

		/**
		 * 讀檔，並且每 8192 bytes 分段傳輸。逾時設定為 1 秒，防止上傳伺服沒開。
		 */
		public void run() {
			int progress = 0;
			DataOutputStream dataOut = null;
			DataInputStream dataIn = null;
			BufferedReader clientReader = null;
			Socket socket = null;
			try {
				File file = new File(filepath);
				socket = new Socket(FileUploadControl.fileServerIP,
						FileUploadControl.fileServerPort);
				socket.setSoTimeout(1000);
				clientReader = new BufferedReader(new InputStreamReader(
						socket.getInputStream(), "UTF-8"));
				dataOut = new DataOutputStream(socket.getOutputStream());
				dataOut.writeUTF(file.getName()); // 本地的檔案名稱
				dataOut.flush();

				URL = clientReader.readLine();

				dataOut.writeLong((long) file.length()); // 本地的檔案大小
				dataOut.flush();
				dataIn = new DataInputStream(new BufferedInputStream(
						new FileInputStream(filepath)));
				int bufferSize = 8192;
				byte[] buf = new byte[bufferSize];
				while (true) {
					int read = 0;
					if (dataIn != null) {
						read = dataIn.read(buf);
					}
					progress += Math.abs(read);
					if (read == -1) // 讀檔結束
						break;
					dataOut.write(buf, 0, read);
					dataOut.flush();
					FileUploadFrame.getInstance().setProcessBar((int)(progress * 100.0 / file.length()));
					try {
						Thread.sleep(100); // 休息一下再傳送，以免傳送過快。
					} catch (Exception e) {

					}
				}
			} catch (Exception e) {

			} finally {
				try {
					FileUploadFrame.getInstance().endUpload();
					if(URL != null && URL.length() != 0) {
						String urlstr = MTextArea.transferImageHyperlink(URL);
						String attachInfo = "<div class=\"normal\"><font>"
								+ LoginPanel.getInstance().getUserName()
								+ "&nbsp;&gt;&nbsp Upload Image</font></div>";
						ChatSocketControl.getInstance().clientCmdTalk(
								"TH@" + attachInfo + urlstr);
					}
					dataOut.close();
					dataIn.close();
					socket.close();
				} catch (Exception ee) {

				}
			}
		}
	}

}
