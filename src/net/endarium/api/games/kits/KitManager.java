package net.endarium.api.games.kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.endarium.api.players.stats.IStats;
import org.bukkit.Bukkit;

import net.endarium.api.players.EndaPlayer;
import net.md_5.bungee.api.ChatColor;

public class KitManager {

	private List<KitAbstract> kitList;
	private Map<UUID, KitAbstract> playerKits;

	/**
	 * Manager du système de Kits.
	 */
	public KitManager() {
		this.kitList = new ArrayList<KitAbstract>();
		this.playerKits = new HashMap<UUID, KitAbstract>();
	}

	/**
	 * Enregister un Kit d'une Liste de la Game.
	 * 
	 * @param kitAbstract
	 */
	public void registerKit(KitAbstract kitAbstract) {
		if (this.kitList.contains(kitAbstract))
			return;
		this.kitList.add(kitAbstract);
	}

	/**
	 * Supprimer un Kit d'une Liste de la Game.
	 * 
	 * @param kitAbstract
	 */
	public void unregisterKit(KitAbstract kitAbstract) {
		this.kitList.remove(kitAbstract);
	}

	/**
	 * Définir un Kit à un Joueur.
	 * 
	 * @param uuid
	 * @param kitAbstract
	 */
	public void setPlayerKit(UUID uuid, KitAbstract kitAbstract) {
		this.playerKits.remove(uuid);
		this.playerKits.put(uuid, kitAbstract);
	}

	/**
	 * Supprimer un Kit à un Joueur.
	 * 
	 * @param uuid
	 */
	public void removePlayerKit(UUID uuid) {
		this.playerKits.remove(uuid);
	}

	/**
	 * Vérifier si un Joueur possède un Kit.
	 * 
	 * @param uuid
	 * @param kitsInfos
	 */
	public boolean hasKit(UUID uuid, KitsInfos kitsInfos) {
		if (this.playerKits.get(uuid) != null)
			return this.playerKits.get(uuid).getKitsInfos().equals(kitsInfos);
		return false;
	}

	/**
	 * Vérifier si le Joueur possède le Kit.
	 * 
	 * @param uuid
	 * @param kitsInfos
	 */
	public boolean hasPermissionKit(UUID uuid, KitsInfos kitsInfos) {
		EndaPlayer endaPlayer = EndaPlayer.get(uuid);
		if (kitsInfos.isFree()) {
			return true;
		}
		return endaPlayer.hasPermission(kitsInfos.getPermission());
	}

	/**
	 * Récupérer le nom du Kit d'un Joueur.
	 * 
	 * @param uuid
	 * @return
	 */
	public String getPlayerKitName(UUID uuid) {
		if (this.playerKits.get(uuid) != null)
			return this.playerKits.get(uuid).getKitsInfos().getName();
		return ChatColor.RED + "Aucun";
	}

	/**
	 * Récupérer un KitAbstract par un KitInfos.
	 * 
	 * @param kitsInfos
	 * @return
	 */
	public KitAbstract getKitAbstractByInfos(KitsInfos kitsInfos) {
		for (KitAbstract kits : kitList)
			if (kits.getKitsInfos().equals(kitsInfos))
				return kits;
		return null;
	}

	/**
	 * Envoyer les Kits aux Joueurs.
	 */
	public void sendPlayerKit() {
		Bukkit.getOnlinePlayers().forEach(playerOnline -> {
			if (this.playerKits.containsKey(playerOnline.getUniqueId()))
				this.getPlayerKit(playerOnline.getUniqueId()).sendKit(playerOnline);
		});
	}

	/**
	 * Appliquer le dernier Kit d'un Joueur.
	 *
	 * @param uuid
	 * @param iStats
	 */
	public void applyLastKit(UUID uuid, IStats iStats) {
		if ((iStats != null) && (iStats.getLastKit() != null))
			this.setPlayerKit(uuid, this.getKitAbstractByInfos(iStats.getLastKit()));
	}

	/**
	 * Récupérer le Kit d'un Joueur.
	 * 
	 * @param uuid
	 * @return
	 */
	public KitAbstract getPlayerKit(UUID uuid) {
		if (this.playerKits.containsKey(uuid))
			return this.playerKits.get(uuid);
		return null;
	}

	/**
	 * Vider les caches des Kits.
	 */
	public void clearKit() {
		this.kitList.clear();
		this.playerKits.clear();
	}

	public List<KitAbstract> getKitList() {
		return kitList;
	}
}
