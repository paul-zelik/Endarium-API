package net.endarium.api.utils.builders.titles;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.endarium.api.minecraft.EndariumBukkit;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PlayerConnection;

public class ActionBarBuilder {

	private String message;
	private int stay;

	/**
	 * Construire une ActionBar.
	 * 
	 * @param message
	 */
	public ActionBarBuilder(String message) {
		this.message = message;
	}

	/**
	 * Ajouter un délai à l'ActionBar.
	 * 
	 * @param stay
	 * @return
	 */
	public ActionBarBuilder withStay(int stay) {
		this.stay = stay;
		return this;
	}

	/**
	 * Envoyer l'ActionBar à une liste de Joueurs.
	 * 
	 * @param players
	 */
	public void sendTo(Collection<Player> players) {
		players.forEach(this::sendTo);
	}

	/**
	 * Envoyer l'ActionBar à un Joueur.
	 * 
	 * @param player
	 */
	public void sendTo(Player player) {
		PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
		PacketPlayOutChat packet = new PacketPlayOutChat(buildChatComponent(this.message), (byte) 2);
		connection.sendPacket(packet);
		if (this.stay != 0) {
			Bukkit.getScheduler().runTaskLater(EndariumBukkit.getPlugin(), () -> {
				PacketPlayOutChat clear = new PacketPlayOutChat(buildChatComponent(""), (byte) 2);
				connection.sendPacket(clear);
			}, this.stay * 20L);
		}
	}

	/**
	 * Envoyer l'ActionBar à tous les Joueurs.
	 */
	public void sendToServer() {
		Bukkit.getOnlinePlayers().forEach(players -> this.sendTo(players));
	}

	private IChatBaseComponent buildChatComponent(String msg) {
		return IChatBaseComponent.ChatSerializer.a("{'text':'" + msg + "'}");
	}

	public String getMessage() {
		return message;
	}

	public int getStay() {
		return stay;
	}
}