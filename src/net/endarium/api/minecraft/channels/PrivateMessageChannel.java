package net.endarium.api.minecraft.channels;

import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.endarium.api.EndariumCommons;
import net.endarium.api.players.EndaPlayer;
import net.endarium.api.utils.GSONUtils;
import net.endarium.api.utils.builders.titles.ActionBarBuilder;
import net.endarium.api.utils.mojang.UUIDEndaFetcher;
import net.endarium.api.utils.tools.SoundUtils;
import net.md_5.bungee.api.ChatColor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class PrivateMessageChannel implements Closeable {

	private static final String CHANNEL = "CHANNEL_MP_ENDARIUM";
	private final PrivateMessageListener privateMessageListener;

	/**
	 * Constructure du Channel des Messages Privés.
	 */
	public PrivateMessageChannel() {
		this.privateMessageListener = new PrivateMessageListener();
		EndariumCommons.getInstance().getExecutorService().submit(() -> {
			try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
				jedis.subscribe(privateMessageListener, CHANNEL);
			}
		});
	}

	@Override
	public void close() throws IOException {
		try {
			this.privateMessageListener.unsubscribe();
		} catch (JedisConnectionException jedisConnectionException) {
			System.err.println(jedisConnectionException.getMessage());
		}
	}

	/**
	 * Envoyer un Message privé à un Joueur.
	 * 
	 * @param toUUID
	 * @param message
	 */
	public void sendMessage(UUID fromUUID, UUID toUUID, String message) {
		PrivateMessage privateMessage = new PrivateMessage(fromUUID, toUUID, message);
		EndariumCommons.getInstance().getExecutorService().submit(() -> {
			try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
				jedis.publish(CHANNEL, GSONUtils.getGson().toJson(privateMessage));
			}
		});
	}

	/**
	 * Class d'écoute du Listener des MPs.
	 */
	protected class PrivateMessageListener extends JedisPubSub {
		@Override
		public void onMessage(String channel, String message) {
			if (channel == null || message == null)
				return;
			if (channel.equals(CHANNEL)) {
				PrivateMessage privateMessage = GSONUtils.getGson().fromJson(message, PrivateMessage.class);
				Player player = Bukkit.getPlayer(privateMessage.getTo());
				if ((player != null) && (player.isOnline())) {
					player.sendMessage(privateMessage.getMessage());

					// Sauvegarder le Target + Notifications
					EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());
					if (endaPlayer == null)
						return;
					endaPlayer
							.setFriends(endaPlayer.getFriends().setPrivateMessageLastTarget(privateMessage.getFrom()));
					if (endaPlayer.getFriends().isPrivateMessageNotifications()) {
						new ActionBarBuilder(ChatColor.WHITE + "Nouveau message de : " + ChatColor.DARK_AQUA + ""
								+ ChatColor.BOLD + UUIDEndaFetcher.getPlayerName(privateMessage.getFrom()))
								.sendTo(player);
						SoundUtils.sendSound(player, Sound.NOTE_PIANO);
					}
				}
			}
			super.onMessage(channel, message);
		}
	}

	/**
	 * Build un Object d'envoi de Message Privé.
	 */
	protected class PrivateMessage {

		private UUID from, to;
		private String message;

		public PrivateMessage(UUID from, UUID to, String message) {
			this.from = from;
			this.to = to;
			this.message = message;
		}

		public UUID getFrom() {
			return from;
		}

		public UUID getTo() {
			return to;
		}

		public String getMessage() {
			return message;
		}
	}
}