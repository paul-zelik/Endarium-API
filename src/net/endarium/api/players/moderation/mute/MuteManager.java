package net.endarium.api.players.moderation.mute;

import java.util.UUID;

import net.endarium.api.EndariumCommons;
import net.endarium.api.utils.GSONUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

public class MuteManager {

	private final String TABLE = "player_mutes";

	/**
	 * Appliquer un Mute à un Joueur.
	 * 
	 * @param uuid
	 * @param muteInfos
	 */
	public void createMute(UUID uuid, MuteInfos muteInfos) {
		EndariumCommons.getInstance().getExecutorService().submit(() -> {
			try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
				String jsonMute = GSONUtils.getGson().toJson(muteInfos);
				if (jsonMute != null) {
					String pathMuteRedis = TABLE + ":" + uuid.toString();
					jedis.set(pathMuteRedis, jsonMute);
					jedis.expire(pathMuteRedis, 60 * muteInfos.getDelay());
				}
			} catch (JedisException exception) {
				exception.printStackTrace();
			}
		});
	}

	/**
	 * Appliquer un UnMute à un Joueur.
	 * 
	 * @param uuid
	 */
	public void applyUnMute(UUID uuid) {
		EndariumCommons.getInstance().getExecutorService().submit(() -> {
			try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
				String pathMuteRedis = TABLE + ":" + uuid.toString();
				if (jedis.exists(pathMuteRedis))
					jedis.del(pathMuteRedis);
			} catch (JedisException exception) {
				exception.printStackTrace();
			}
		});
	}

	/**
	 * Vérifier si un Joueur est Mute.
	 * 
	 * @param uuid
	 * @return
	 */
	public MuteInfos isMute(UUID uuid) {
		try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
			String pathMuteRedis = TABLE + ":" + uuid.toString();
			if (!(jedis.exists(pathMuteRedis)))
				return null;
			return GSONUtils.getGson().fromJson(jedis.get(pathMuteRedis), MuteInfos.class);
		} catch (JedisException exception) {
			exception.printStackTrace();
		}
		return null;
	}
}