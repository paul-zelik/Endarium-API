package net.endarium.api.players;

import java.util.UUID;

import net.endarium.api.EndariumCommons;
import net.endarium.api.utils.EndariumAPI;
import net.endarium.api.utils.GSONUtils;
import redis.clients.jedis.Jedis;

public class EndaPlayerCache {

	private String serverName;
	private boolean moderationMode;

	private boolean useEndariumProxy = false;

	/**
	 * Gestion d'un Cache du Joueur.
	 * 
	 * @param serverName
	 * @param moderationMode
	 * @param userEndariumProxy
	 */
	public EndaPlayerCache(String serverName, boolean moderationMode, boolean useEndariumProxy) {
		this.serverName = serverName;
		this.moderationMode = moderationMode;
		this.useEndariumProxy = useEndariumProxy;
	}

	public String getServerName() {
		return serverName;
	}

	public EndaPlayerCache setServerName(String serverName) {
		this.serverName = serverName;
		return this;
	}

	public boolean isModerationMode() {
		return moderationMode;
	}

	public EndaPlayerCache setModerationMode(boolean moderationMode) {
		this.moderationMode = moderationMode;
		return this;
	}

	public boolean isUseEndariumProxy() {
		return useEndariumProxy;
	}

	public void setUseEndariumProxy(boolean useEndariumProxy) {
		this.useEndariumProxy = useEndariumProxy;
	}

	/**
	 * Récupérer un EndaPlayerCache depuis l'Exterieur
	 * 
	 * @param uuid
	 */
	public static EndaPlayerCache get(UUID uuid) {
		if (EndaPlayer.isConnected(uuid)) {
			try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
				String pathJedis = "player_connected:" + uuid.toString();
				if (jedis.exists(pathJedis)) {
					EndaPlayerCache endaPlayerCacheValue = GSONUtils.getGson().fromJson(jedis.get(pathJedis),
							EndaPlayerCache.class);
					return endaPlayerCacheValue;
				}
			} catch (Exception exception) {
				System.err.println(EndariumAPI.getPrefixAPI()
						+ "Impossible de récupérer le Cache Externe en Redis du joueur : " + uuid.toString());
			}
			return null;
		}
		return null;
	}
}