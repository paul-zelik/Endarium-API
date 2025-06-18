package net.endarium.api.minecraft.listeners.customs;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.endarium.api.players.rank.Rank;

public class RankChangeEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private Player player;
	private Rank rank;

	/**
	 * EVENT : Changement de Rank d'un Joueur.
	 * 
	 * @param player
	 * @param rank
	 */
	public RankChangeEvent(Player player, Rank rank) {
		this.player = player;
		this.rank = rank;
	}

	public Player getPlayer() {
		return player;
	}

	public Rank getRank() {
		return rank;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}