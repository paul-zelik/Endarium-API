package net.endarium.api.players.friends;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Friends {

	private UUID uuid;

	// Gestion des Amis
	private List<UUID> friends;

	private boolean active = true;
	private boolean notifications = true;

	// Messages Prives
	private int privateMessageReception = 1;
	private boolean privateMessageNotifications = false;
	private UUID privateMessageLastTarget = null;

	public Friends(UUID uuid) {
		this.uuid = uuid;
		this.friends = new ArrayList<UUID>();
	}

	public UUID getUUID() {
		return uuid;
	}

	/**
	 * Créer une relation d'amitié.
	 * 
	 * @param uuid
	 */
	public Friends addFriend(UUID uuid) {
		if (!(this.isFriend(uuid)))
			this.friends.add(uuid);
		return this;
	}

	/**
	 * Supprimer une relation d'amitié.
	 * 
	 * @param uuid
	 */
	public Friends removeFriend(UUID uuid) {
		this.friends.remove(uuid);
		return this;
	}

	/**
	 * Vérifier l'amitié avec un Joueur.
	 * 
	 * @param uuid
	 * @return
	 */
	public boolean isFriend(UUID uuid) {
		return this.friends.contains(uuid);
	}

	public boolean isActive() {
		return active;
	}

	public Friends setActive(boolean active) {
		this.active = active;
		return this;
	}

	public boolean isNotifications() {
		return notifications;
	}

	public Friends setNotifications(boolean notifications) {
		this.notifications = notifications;
		return this;
	}

	/**
	 * Compter le nombre d'Amis du Joueur.
	 */
	public int countFriends() {
		return this.friends.size();
	}

	public List<UUID> getFriends() {
		return friends;
	}

	/**
	 * GESTION DES MESSAGES PRIVES
	 */
	public int getPrivateMessageReception() {
		return privateMessageReception;
	}

	public Friends setPrivateMessageReception(int privateMessageReception) {
		this.privateMessageReception = privateMessageReception;
		return this;
	}

	public boolean isPrivateMessageNotifications() {
		return privateMessageNotifications;
	}

	public Friends setPrivateMessageNotifications(boolean privateMessageNotifications) {
		this.privateMessageNotifications = privateMessageNotifications;
		return this;
	}

	public UUID getPrivateMessageLastTarget() {
		return privateMessageLastTarget;
	}

	public Friends setPrivateMessageLastTarget(UUID privateMessageLastTarget) {
		this.privateMessageLastTarget = privateMessageLastTarget;
		return this;
	}
}