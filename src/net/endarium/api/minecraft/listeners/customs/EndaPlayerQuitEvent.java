package net.endarium.api.minecraft.listeners.customs;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EndaPlayerQuitEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private Player player;

	/**
	 * EVENT : Gestion du Quit d'un Joueur.
	 * 
	 * @param player
	 */
	public EndaPlayerQuitEvent(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}