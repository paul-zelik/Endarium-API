package net.endarium.api.players.party;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.entity.Player;

public class Party {
	private ProxiedPlayer playerOwner;
	private List<ProxiedPlayer> players;
	private Map<UUID, ProxiedPlayer> invitations; // Map pour suivre les invitations

	public Party(ProxiedPlayer playerOwner) {
		this.playerOwner = playerOwner;
		this.players = new ArrayList<>();
		this.invitations = new HashMap<>();
		this.players.add(playerOwner);
	}

	public List<ProxiedPlayer> getPlayers() {
		return players;
	}

	public ProxiedPlayer getPlayerOwner() {
		return playerOwner;
	}

	public void addPlayer(ProxiedPlayer player) {
		if (!players.contains(player)) {
			players.add(player);
		}
	}

	public void removePlayer(ProxiedPlayer player) {
		players.remove(player);
	}

	// Envoyer une invitation à un joueur
	public void invitePlayer(ProxiedPlayer player) {
		if (!players.contains(player) && !invitations.containsKey(player.getUniqueId())) {
			invitations.put(player.getUniqueId(), player);
		}
	}

	// Accepter une invitation
	public boolean acceptInvitation(ProxiedPlayer player) {
		if (invitations.containsKey(player.getUniqueId())) {
			players.add(player);
			invitations.remove(player.getUniqueId());
			return true;
		}
		return false;
	}

	// Refuser une invitation
	public void declineInvitation(ProxiedPlayer player) {
		invitations.remove(player.getUniqueId());
	}

	// Vérifier si un joueur a une invitation en attente
	public boolean hasInvitation(ProxiedPlayer player) {
		return invitations.containsKey(player.getUniqueId());
	}
}
