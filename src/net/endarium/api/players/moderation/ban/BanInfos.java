package net.endarium.api.players.moderation.ban;

import java.util.Date;
import java.util.UUID;

public class BanInfos {

	private UUID uuid, modUUID;
	private String reason;
	private Date date, expiryDate;

	private boolean permanent, banned, modPlayed, playerOnline;

	private String banID;

	/**
	 * Récupérer le BanInfos d'un Joueur.
	 * 
	 * @param uuid
	 * @param modUUID
	 * @param banID
	 * @param reason
	 * @param date
	 * @param expiryDate
	 * @param permanent
	 * @param banned
	 */
	public BanInfos(UUID uuid, UUID modUUID, String banID, String reason, Date date, Date expiryDate, boolean permanent,
			boolean banned, boolean modPlayed, boolean playerOnline) {
		this.uuid = uuid;
		this.modUUID = modUUID;
		this.banID = banID;
		this.reason = reason;
		this.date = date;
		this.expiryDate = expiryDate;
		this.permanent = permanent;
		this.banned = banned;
		this.modPlayed = modPlayed;
		this.playerOnline = playerOnline;
	}

	public UUID getUUID() {
		return uuid;
	}

	public UUID getModUUID() {
		return modUUID;
	}

	public String getBanID() {
		return banID;
	}

	public String getReason() {
		return reason;
	}

	public Date getDate() {
		return date;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public boolean isPermanent() {
		return permanent;
	}

	public boolean isBanned() {
		return banned;
	}

	public boolean isModPlayed() {
		return modPlayed;
	}

	public boolean isPlayerOnline() {
		return playerOnline;
	}
}