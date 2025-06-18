package net.endarium.api.players.moderation.mute;

import java.util.Date;
import java.util.UUID;

public class MuteInfos {

	private UUID uuid, modUUID;
	private String reason;
	private int delay;
	private Date date, expiryDate;

	private String muteID;

	/**
	 * Récupérer le MuteInfos d'un Joueur.
	 * 
	 * @param uuid
	 * @param modUUID
	 * @param reason
	 * @param date
	 * @param expiryDate
	 * @param muteID
	 */
	public MuteInfos(UUID uuid, UUID modUUID, String reason, int delay, Date date, Date expiryDate, String muteID) {
		this.uuid = uuid;
		this.modUUID = modUUID;
		this.reason = reason;
		this.delay = delay;
		this.date = date;
		this.expiryDate = expiryDate;
		this.muteID = muteID;
	}

	public UUID getUUID() {
		return uuid;
	}

	public UUID getModUUID() {
		return modUUID;
	}

	public String getReason() {
		return reason;
	}

	public int getDelay() {
		return delay;
	}

	public Date getDate() {
		return date;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public String getMuteID() {
		return muteID;
	}
}