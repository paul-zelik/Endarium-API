package net.endarium.api.players;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.endarium.api.EndariumCommons;
import net.endarium.api.EndariumEntities;
import net.endarium.api.players.friends.Friends;
import net.endarium.api.players.language.Languages;
import net.endarium.api.players.moderation.mute.MuteInfos;
import net.endarium.api.players.party.Party;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.players.rank.permissions.Permission;
import net.endarium.api.players.wallets.Currency;
import net.endarium.api.players.wallets.WalletAccount;
import net.endarium.api.utils.EndariumAPI;
import net.endarium.api.utils.GSONUtils;
import redis.clients.jedis.Jedis;

public class EndaPlayer {

	private static Map<UUID, EndaPlayer> endaPlayers = new HashMap<UUID, EndaPlayer>();
	private EndariumCommons endariumCommons = EndariumCommons.getInstance();
	private EndariumEntities endariumEntities = endariumCommons.getEndariumEntities();

	private UUID uuid;
	private Rank rank;
	private List<String> permissions;

	private Friends friend;
	private Languages languages;

	private boolean spectator = false;

	private MuteInfos muteInfos;
	private boolean staffChat = true, modFly = false;

	private EndaPlayerCache endaPlayerCache;

	/**
	 * Génération d'un compte joueur Endarium.
	 * 
	 * @param uuid
	 */
	public EndaPlayer(UUID uuid) {
		this.uuid = uuid;
		try {

			this.rank = this.endariumEntities.getRankManager().getRank(uuid);
			this.permissions = this.endariumEntities.getPermissionManager().getPermissions(uuid);
			this.languages = this.endariumEntities.getLangManager().getLanguages(uuid);

			// Gestion du Cache du Joueur
			if (this.endaPlayerCache == null)
				this.endaPlayerCache = this.getEndaPlayerCache();

			System.out.println(EndariumAPI.getPrefixAPI() + "Le compte : " + uuid.toString() + ", est bien connecté.");
		} catch (Exception exception) {
			System.err.println(
					EndariumAPI.getPrefixAPI() + "Impossible de charger le EndaPlayer de " + uuid.toString() + ".");
		}
	}

	public Rank getRank() {
		return rank;
	}

	public void setRank(Rank rank) {
		this.rank = rank;
	}

	public void addPermission(String permission) {
		if (!(permissions.contains(permission))) {
			permissions.add(permission);
			this.endariumEntities.getPermissionManager().addPermission(uuid, permission);
		}
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public void addPermissionTemporary(String permission, int hoursDelay) {
		if (!(permissions.contains(permission))) {
			permissions.add(permission);
			this.endariumEntities.getPermissionManager().addPermissionTemporary(uuid, permission, hoursDelay);
		}
	}

	public void removePermission(String permission) {
		if (!(permissions.contains(permission))) {
			permissions.remove(permission);
			this.endariumEntities.getPermissionManager().removePermission(uuid, permission);
		}
	}

	public boolean hasPermission(String permission) {
		return permissions.contains(permission);
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public boolean isSpectator() {
		return spectator;
	}

	public void setSpectator(boolean spectator) {
		this.spectator = spectator;
	}

	public boolean isStaffChat() {
		return staffChat;
	}

	public void setStaffChat(boolean staffChat) {
		this.staffChat = staffChat;
	}

	public boolean isModFly() {
		return modFly;
	}

	public void setModFly(boolean modFly) {
		this.modFly = modFly;
	}

	public MuteInfos getMuteInfos() {
		return muteInfos;
	}

	public void setMuteInfos(MuteInfos muteInfos) {
		this.muteInfos = muteInfos;
	}

	/**
	 * Récupérer le Mode de Modération d'un Joueur.
	 */
	public boolean isModeModeration() {
		return this.getEndaPlayerCache().isModerationMode();
	}

	/**
	 * Définir le Mode de Modération d'un Joueur.
	 * 
	 * @param moderation
	 */
	public void setModeModeration(boolean moderation) {
		this.setEndaPlayerCache(this.getEndaPlayerCache().setModerationMode(moderation));
	}

	/**
	 * Vérifier si un Joueur est Administrateur.
	 */
	public boolean isAdministrator() {
		return this.rank.getPower() >= Rank.ADMINISTRATOR.getPower();
	}

	/**
	 * Vérifier si un Joueur est dans le Staff.
	 */
	public boolean isStaff() {
		return this.rank.getPower() >= Rank.STAFF.getPower();
	}

	/**
	 * Vérifier si un Joueur peut faire des Host.
	 */
	public boolean isHostPlayer() {
		return this.permissions.contains(Permission.HOST_START.getPermission())
				|| rank.getPower() >= Rank.ADMINISTRATOR.getPower();
	}

	/**
	 * Ajouter de la monnaie dans un Wallet.
	 * 
	 * @param currency
	 * @param value
	 */
	public void addCurrency(Currency currency, int value) {
		this.endariumEntities.getWalletsManager()
				.saveRedisWallet(this.getWalletAccount().addCurrencyWallet(currency, value));
	}



	/**
	 * Retirer de la monnaie d'un Wallet.
	 * 
	 * @param currency
	 * @param value
	 */
	public void removeCurrency(Currency currency, int value) {
		this.endariumEntities.getWalletsManager()
				.saveRedisWallet(this.getWalletAccount().removeCurrencyWallet(currency, value));
	}

	/**
	 * Rédéfinir un Wallet au Joueur.
	 * 
	 * @param currency
	 * @param value
	 */
	public void setCurrency(Currency currency, int value) {
		this.endariumEntities.getWalletsManager()
				.saveRedisWallet(this.getWalletAccount().setCurrencyWallet(currency, value));
	}

	/**
	 * Récupérer un Wallet du Joueur.
	 * 
	 * @param currency
	 * @return
	 */
	public int getCurrency(Currency currency) {
		return this.getWalletAccount().getCurrencyWallet(currency);
	}

	/**
	 * Récupérer les Wallets du Joueur.
	 */
	public WalletAccount getWalletAccount() {
		return this.endariumEntities.getWalletsManager().getRedisWallet(uuid);
	}

	/**
	 * Récupérer les Amis d'un Joueur.
	 */
	public Friends getFriends() {
		if (friend == null)
			this.friend = this.endariumEntities.getFriendsManager().getRedisFriends(uuid);
		return this.friend;
	}

	/**
	 * Définir les Amis d'un Joueur.
	 * 
	 * @param friends
	 */
	public void setFriends(Friends friends) {
		this.friend = friends;
		this.endariumEntities.getFriendsManager().saveRedisFriends(friends);
	}

	/**
	 * Récupérer la Langue d'un Joueur.
	 * 
	 * @return Languages
	 */
	public Languages getLanguages() {
		if (this.languages == null)
			return Languages.FRENCH;
		return this.languages;
	}

	/**
	 * Changer la Langue d'un Joueur.
	 * 
	 * @param languages
	 */
	public void setLanguages(Languages languages) {
		this.languages = languages;
		this.endariumEntities.getLangManager().setLanguages(uuid, languages);
	}

	/**
	 * Récupérer le Cache d'un Joueur.
	 */
	public EndaPlayerCache getEndaPlayerCache() {
		if (this.endaPlayerCache == null) {
			try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
				String pathJedis = "player_connected:" + uuid.toString();
				if (jedis.exists(pathJedis)) {
					EndaPlayerCache endaPlayerCacheValue = GSONUtils.getGson().fromJson(jedis.get(pathJedis),
							EndaPlayerCache.class);
					this.endaPlayerCache = endaPlayerCacheValue;
					return endaPlayerCacheValue;
				}
			} catch (Exception exception) {
				System.err.println(EndariumAPI.getPrefixAPI() + "Impossible de récupérer le Cache en Redis du joueur : "
						+ uuid.toString());
			}
			return null;
		} else {
			return this.endaPlayerCache;
		}
	}

	/**
	 * Sauvergarder le Cache d'un Joueur.
	 * 
	 * @param endaPlayerCache
	 */
	public void setEndaPlayerCache(EndaPlayerCache endaPlayerCache) {
		this.endaPlayerCache = endaPlayerCache;
		this.setConnectedCache(endaPlayerCache);
	}

	/**
	 * Vérifier si un Joueur est connecté sur Endarium.
	 * 
	 * @param uuid
	 * @return
	 */
	private void setConnectedCache(EndaPlayerCache endaPlayerCacheValue) {
		EndariumCommons.getInstance().getExecutorService().submit(() -> {
			try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
				String jsonEndaPlayerCache = GSONUtils.getGson().toJson(endaPlayerCacheValue);
				if (jsonEndaPlayerCache != null) {
					jedis.set("player_connected:" + uuid.toString(), jsonEndaPlayerCache);
				}
			} catch (Exception exception) {
				System.err.println(EndariumAPI.getPrefixAPI() + "Impossible de connecté le Cache en Redis du joueur : "
						+ uuid.toString());
			}
		});
	}

	/**
	 * Définir si un Joueur est connecté sur Endarium.
	 * 
	 * @param uuid
	 * @return
	 */
	public void setDisconnected() {
		EndariumCommons.getInstance().getExecutorService().submit(() -> {
			try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
				if (jedis.exists("player_connected:" + uuid.toString()))
					jedis.del("player_connected:" + uuid.toString());
			} catch (Exception exception) {
				System.err.println(EndariumAPI.getPrefixAPI()
						+ "Impossible de déconnecté le Cache en Redis le joueur : " + uuid.toString());
			}
		});
	}

	/**
	 * Vérifier si un Joueur est connecté sur Endarium.
	 * 
	 * @param uuid
	 * @return
	 */
	public static boolean isConnected(UUID uuid) {
		try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
			return jedis.exists("player_connected:" + uuid.toString());
		} catch (Exception exception) {
			return false;
		}
	}

	/**
	 * Effectuer la deconnexion d'un Joueur.
	 */
	public void logout() {

		// Appliquer le Cache de Redis des Wallets dans MySQL
		WalletAccount walletAccount = this.getWalletAccount();
		if (walletAccount != null) {
			for (Currency currency : Currency.values()) {
				int amount = walletAccount.getCurrencyWallet(currency);
				this.endariumEntities.getWalletsManager().setCurrency(uuid, currency, amount);
			}
		}

		this.logoutEndaPlayerCache();
	}

	// Déconnecter le Cache du Joueur.
	public void logoutEndaPlayerCache() {
		// Déconnecter un Joueur
		if (endaPlayers.get(uuid) != null)
			endaPlayers.remove(uuid);
	}

	/**
	 * Récupérer un EndaPlayer/Compte Endarium.
	 * 
	 * @param uuid
	 * @return
	 */
	public static EndaPlayer get(UUID uuid) {
		if (endaPlayers.get(uuid) == null)
			endaPlayers.put(uuid, new EndaPlayer(uuid));
		return endaPlayers.get(uuid);
	}
}