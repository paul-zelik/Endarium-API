package net.endarium.api.players.friends;

import java.util.UUID;

public enum FriendChannelType {

	INVITE_FRIEND("INVITE_FRIEND"), REMOVE_FRIEND("REMOVE_FRIEND"), ACCEPT_FRIEND("ACCEPT_FRIEND"),
	REFUSE_FRIEND("REFUSE_FRIEND"),

	CONNECT_NOTIFICATION("CONNECT_NOTIFICATION"), DISCONNECT_NOTIFICATION("DISCONNECT_NOTIFICATION");

	private String name;

	private FriendChannelType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * Class de gestion des requêtes d'amis.
	 */
	public static class FriendChannelRequest {

		private FriendChannelType friendChannelType;
		private UUID uuid, targetUUID;
		private String name, targetName;

		/**
		 * Gestion des requêtes d'Amis.
		 * 
		 * @param uuid
		 * @param targetUUID
		 */
		public FriendChannelRequest(UUID uuid, UUID targetUUID) {
			this.uuid = uuid;
			this.targetUUID = targetUUID;
		}

		/**
		 * Gestion des requêtes d'Ami avec un ChannelType.
		 * 
		 * @param friendChannelType
		 * @param uuid
		 * @param targetUUID
		 * @param name
		 * @param targetName
		 */
		public FriendChannelRequest(FriendChannelType friendChannelType, UUID uuid, UUID targetUUID, String name,
				String targetName) {
			this.friendChannelType = friendChannelType;
			this.uuid = uuid;
			this.targetUUID = targetUUID;
			this.name = name;
			this.targetName = targetName;
		}

		public FriendChannelType getFriendChannelType() {
			return friendChannelType;
		}

		public UUID getUUID() {
			return uuid;
		}

		public UUID getTargetUUID() {
			return targetUUID;
		}

		public String getName() {
			return name;
		}

		public String getTargetName() {
			return targetName;
		}
	}
}