package net.endarium.api.minecraft.listeners.customs;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.endarium.api.games.kits.KitsInfos;

public class PlayerKitChangeEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private Player player;
	private KitsInfos kitsInfos;

	/**
	 * EVENT : Changement de Kit d'un Joueur.
	 * 
	 * @param player
	 * @param kitsInfos
	 */
	public PlayerKitChangeEvent(Player player, KitsInfos kitsInfos) {
		this.player = player;
		this.kitsInfos = kitsInfos;
	}

	public Player getPlayer() {
		return player;
	}

	public KitsInfos getKitsInfos() {
		return kitsInfos;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}