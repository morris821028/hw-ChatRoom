package Server;

public class User {
	private String name;
	private String ip;

	public User(String name, String ip) {
		this.name = name;
		this.ip = ip;
	}

	public String getUserName() {
		return name;
	}

	public String getIP() {
		return ip;
	}

	public void setUserName(String name) {
		this.name = name;
	}

	public void setIP(String ip) {
		this.ip = ip;
	}
}
