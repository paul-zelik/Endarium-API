package net.endarium.api.bungeecord.channels;

public enum RedisBungeeChannel {

	CHANNEL_PROXY("CHANNEL_PROXY_ENDARIUM"), CHANNEL_FRIEND("CHANNEL_FRIENDS_ENDARIUM"),
	CHANNEL_STAFFCHAT("CHANNEL_STAFFCHAT");

	private String name;

	/**
	 * Enumération des Channels RedisBungee.
	 */
	private RedisBungeeChannel(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}