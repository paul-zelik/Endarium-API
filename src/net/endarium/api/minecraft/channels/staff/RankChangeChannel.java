package net.endarium.api.minecraft.channels.staff;

import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;

import net.endarium.api.EndariumCommons;
import net.endarium.api.minecraft.listeners.customs.RankChangeEvent;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.GSONUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RankChangeChannel implements Closeable {

	private static final String CHANNEL = "RankChangeChannel";
	private final RankChangeListener rankChangeListener;

	/**
	 * Constructeur du Channel de changement de Rank.
	 */
	public RankChangeChannel() {
		this.rankChangeListener = new RankChangeListener();
		EndariumCommons.getInstance().getExecutorService().submit(() -> {
			try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
				jedis.subscribe(rankChangeListener, CHANNEL);
			}
		});
	}

	@Override
	public void close() throws IOException {
		try {
			this.rankChangeListener.unsubscribe();
		} catch (JedisConnectionException jedisConnectionException) {
			System.err.println(jedisConnectionException.getMessage());
		}
	}

	/**
	 * Appeler à un Changement de Rank d'un Joueur.
	 * 
	 * @param uuid
	 * @param rank
	 */
	public void applyChangeRank(UUID uuid, Rank rank) {
		RankChangeObject rankChangeObject = new RankChangeObject(uuid, rank);
		EndariumCommons.getInstance().getExecutorService().execute(() -> {
			try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
				jedis.publish(CHANNEL, GSONUtils.getGson().toJson(rankChangeObject));
			}
		});
	}

	/**
	 * Class d'écoute du Listener de changement de Rank.
	 */
	protected class RankChangeListener extends JedisPubSub {
		@Override
		public void onMessage(String channel, String message) {
			if (channel == null || message == null)
				return;
			switch (channel) {
			case CHANNEL:
				RankChangeObject rankChangeObject = GSONUtils.getGson().fromJson(message, RankChangeObject.class);
				if (rankChangeObject == null)
					return;
				Bukkit.getOnlinePlayers().forEach(players -> {
					if (players.getUniqueId().equals(rankChangeObject.getUUID()))
						Bukkit.getPluginManager().callEvent(new RankChangeEvent(players, rankChangeObject.getRank()));
				});
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Build un protected RankChangeObject.
	 */
	protected class RankChangeObject {

		private UUID uuid;
		private Rank rank;

		private RankChangeObject(UUID uuid, Rank rank) {
			this.uuid = uuid;
			this.rank = rank;
		}

		public UUID getUUID() {
			return uuid;
		}

		public Rank getRank() {
			return rank;
		}
	}
}