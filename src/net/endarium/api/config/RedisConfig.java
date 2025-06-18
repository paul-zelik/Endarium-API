package net.endarium.api.config;

public class RedisConfig {

	private String host;
	private int port;
	private int database;
	private String password;
	private String clientName;
	private boolean enable;

	public RedisConfig(String host, int port, int database, String password, boolean enable) {
		this.host = host;
		this.port = port;
		this.database = database;
		this.password = password;
		this.clientName = "default";
		this.enable = enable;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getDatabase() {
		return database;
	}

	public void setDatabase(int database) {
		this.database = database;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
}