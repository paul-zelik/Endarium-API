package net.endarium.api.games.kits;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.endarium.api.utils.EndariumAPI;

/**
 * Class d'Abstraction des Kits.
 */
public abstract class KitAbstract {

	public abstract KitsInfos getKitsInfos();

	public abstract void sendKit(Player player);

	public abstract ItemStack getItemIcon(Player player);

	public boolean enable = true;

	/**
	 * Enregistrer le Kit d'un Jeu.
	 * 
	 * @param kitAbstract
	 */
	public void register(KitAbstract kitAbstract) {
		EndariumAPI.getGameSetting().getKitManager().registerKit(kitAbstract);
	}
}
