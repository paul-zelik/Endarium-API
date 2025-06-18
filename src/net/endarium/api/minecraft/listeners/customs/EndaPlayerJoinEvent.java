package net.endarium.api.minecraft.listeners.customs;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.endarium.api.players.EndaPlayer;

public class EndaPlayerJoinEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private Player player;
	private EndaPlayer endaPlayer;

	private boolean cancel = false;

	/**
	 * EVENT : Gestion du Join d'un Joueur.
	 * 
	 * @param player
	 * @param endaPlayer
	 */
	public EndaPlayerJoinEvent(Player player, EndaPlayer endaPlayer) {
		this.player = player;
		this.endaPlayer = endaPlayer;
	}

	public Player getPlayer() {
		return player;
	}

	public EndaPlayer getEndaPlayer() {
		return endaPlayer;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean value) {
		this.cancel = value;
	}
}