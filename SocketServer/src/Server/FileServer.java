package Server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.security.*;
import java.util.Calendar;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FileServer {
	private ServerSocket serverSocket = null;
	private final int serverPort = 1979;
	private final String savePath = "C:\\xampp\\htdocs\\TEST\\downloads\\";
	private final String apachePath = "http://140.115.205.194/test/downloads/";
	private ServerThread serverThread;
	private ArrayList<ClientThread> clientList;
	boolean serverRunning;
	
	private JToggleButton inputSwitch;// 開啟連線
	
	public void openSocketAction() {// 成功連線後的動作
		inputSwitch.setSelected(true);
		inputSwitch.setText("ON");
	}

	public void closeSocketAction() {// 成功關閉後的動作
		inputSwitch.setSelected(false);
		inputSwitch.setText("OFF");
	}
	FileServer(Component attach) {
		JFrame f = new JFrame("File Receive State");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(300, 650);
		f.setLayout(new BorderLayout());
		f.add(createNorthPanel(), BorderLayout.NORTH);
		f.setLocationRelativeTo(attach);    
		final Toolkit toolkit = Toolkit.getDefaultToolkit();		
	    final Dimension screenSize = toolkit.getScreenSize();
	    final int x = (screenSize.width - f.getWidth()) / 2;
	    final int y = (screenSize.height - f.getHeight()) / 2;
		f.setLocation(x-500, y);
		f.setVisible(true);
	}
	public JPanel createNorthPanel() {
		Font displayFont = new Font("Serif", Font.BOLD, 18);
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		inputSwitch = new JToggleButton("OFF");
		inputSwitch.setFont(displayFont);
		inputSwitch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (inputSwitch.getText().equals("OFF")) {
					openServer();
				} else {
					closeServer();
				}
			}
		});
		JLabel infolabel = new JLabel("PORT - " + serverPort);
		infolabel.setFont(displayFont);
		panel.add(infolabel);
		panel.add(inputSwitch);
		return panel;
	}
	public boolean openServer() {
		if (serverRunning)
			return false;
		try {
			int port, limitcap;
			port = serverPort;
			limitcap = 10;
			serverSocket = new ServerSocket(port);
			serverThread = new ServerThread(serverSocket, limitcap);
			serverThread.start();
			clientList = new ArrayList<ClientThread>();
			serverRunning = true;
			this.openSocketAction();
			System.out.println("File Server open, #port : " + serverPort);
			return true;
		} catch (java.net.BindException be) {
			be.getStackTrace();
		} catch (Exception ee) {
			ee.getStackTrace();
		}
		return false;
	}

	public boolean closeServer() {
		try {
			serverThread.stop();
			serverSocket.close();
			serverRunning = false;
			clientList.clear();
			this.closeSocketAction();
			return true;
		} catch (Exception ee) {
			ee.getMessage();
		}
		return false;
	}

	class ServerThread extends Thread {
		private ServerSocket serverSocket;
		private int limitCap;

		public ServerThread(ServerSocket serverSocket, int limitCap) {
			this.serverSocket = serverSocket;
			this.limitCap = limitCap;
		}

		public void run() {
			Socket socket;
			BufferedReader reader;
			PrintWriter writer;
			ClientThread clientThread;
			User user;
			while (true) {
				try {
					socket = serverSocket.accept();
					reader = new BufferedReader(new InputStreamReader(
							socket.getInputStream()));
					writer = new PrintWriter(socket.getOutputStream());
					user = new User("SSSS", socket.getInetAddress()
							.getHostAddress().toString());
					clientThread = new ClientThread(socket, user);
					clientThread.start();
					clientList.add(clientThread);
				} catch (Exception ee) {
					System.out.println("ServerThread Err " + ee.getMessage());
				}
			}
		}
	}

	public static String MD5encoding(String str) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md.update(str.getBytes());
		byte[] bs = md.digest();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bs.length; i++) {
			int v = bs[i] & 0xff;
			if (v < 16) {
				sb.append(0);
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString();
	}

	class ClientThread extends Thread {// receive message to client
		private Socket socket;
		private User user;
		private BufferedReader reader;
		private PrintWriter writer;

		public ClientThread(Socket socket, User user) {
			try {
				this.socket = socket;
				this.user = user;
				this.reader = new BufferedReader(new InputStreamReader(
						socket.getInputStream(), "UTF-8"));
				this.writer = new PrintWriter(new OutputStreamWriter(
						socket.getOutputStream(), "UTF-8"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			getData(savePath, serverPort);
		}

		public void getData(String savePath, int port) {
			int progress = 0;
			try {
				DataInputStream inputStream = new DataInputStream(
						new BufferedInputStream(socket.getInputStream()));
				int bufferSize = 8192;
				byte[] buf = new byte[bufferSize];
				long len = 0;
				long passedlen = 0;
				String fileName = inputStream.readUTF();
				String code = user.getIP() + Calendar.getInstance();
				int startIndex = fileName.lastIndexOf(46) + 1;
				int endIndex = fileName.length();
				fileName = MD5encoding(code) + "."
						+ fileName.substring(startIndex, endIndex);
				System.out.println(fileName);
				savePath += fileName;
				writer.write(apachePath + fileName + "\r\n");
				writer.flush();
				System.out.println(savePath);
				DataOutputStream fileOut = new DataOutputStream(
						new BufferedOutputStream(new FileOutputStream(savePath)));
				len = inputStream.readLong();
				System.out.printf("接收 %d KB\n", len);
				System.out.println("開始接收");
				while (true) {
					int read = 0;
					if (inputStream != null) {
						read = inputStream.read(buf);
					}
					passedlen += read;
					if (read == -1)
						break;
					if ((passedlen * 100.0 / len) > progress) {
						progress = (int) (passedlen * 100.0 / len);
						System.out.printf("%d%%..\n", progress);
					}
					fileOut.write(buf, 0, read);
				}
				System.out.println("save as " + savePath);
				fileOut.close();
				closeSocket();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void closeSocket() {
			try {
				socket.close();
				this.stop();
			} catch (Exception e) {
			}
		}

		public void run() {
			String msg;
			while (true) {
				try {
					msg = reader.readLine();
					dispatcherMessage(msg);
				} catch (Exception e) {

				}
			}
		}

		public void dispatcherMessage(String msg) {
			if (msg == null || msg.length() == 0)
				return;
			StringTokenizer st = new StringTokenizer(msg, "@");
			msg = st.nextToken();
		}

		public BufferedReader getReader() {
			return reader;
		}

		public PrintWriter getWriter() {
			return writer;
		}
	}
}