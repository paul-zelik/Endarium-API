package net.endarium.api.utils.mojang;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

/**
 * UUID Fetcher avec un Cache.
 */
public class UUIDFetcher {

	public final static Map<String, UUID> uuidCache = new HashMap<>();
	public final static Map<UUID, String> nameCache = new HashMap<>();

	/**
	 * Récupérer l'UUID d'un Joueur via son Pseudo.
	 */
	public static UUID getUUID(String playerName) {
		return uuidCache.computeIfAbsent(playerName, UUIDFetcher::generateUUID);
	}

	private static UUID generateUUID(String playerName) {
		try {
			JSONObject json = HTTP.getJson("https://api.mojang.com/users/profiles/minecraft/" + playerName);
			String uuidName = json.getString("id");
			return new UUID(new BigInteger(uuidName.substring(0, 16), 16).longValue(),
					new BigInteger(uuidName.substring(16), 16).longValue());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Récupérer le Pseudo d'un Joueur via son UUID.
	 */
	public static String getName(UUID uuid) {
		return nameCache.computeIfAbsent(uuid, (k) -> {
			try {
				String url = "https://sessionserver.mojang.com/session/minecraft/profile/"
						+ uuid.toString().replace("-", "");
				JSONObject json = HTTP.getJson(url);
				String pseudoName = json.getString("name");
				nameCache.put(uuid, pseudoName);
				return pseudoName;
			} catch (Exception e) {
				return null;
			}
		});
	}
}
