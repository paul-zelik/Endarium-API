package net.endarium.api.games.kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.endarium.api.games.GameStatus;
import net.endarium.api.games.servers.CrystaliserAPI;
import net.endarium.api.minecraft.listeners.customs.PlayerKitChangeEvent;
import net.endarium.api.players.EndaPlayer;
import net.endarium.api.utils.EndariumAPI;
import net.endarium.api.utils.builders.inventories.VirtualMenu;
import net.endarium.api.utils.builders.items.ItemFactory;
import net.endarium.api.utils.tools.SoundUtils;
import net.endarium.crystaliser.servers.GameType;
import net.md_5.bungee.api.ChatColor;

public class KitsMenu extends VirtualMenu {

	/**
	 * Constructeur de Menu de Kit.
	 * 
	 * @param player
	 */
	public KitsMenu(Player player) {
		super(player, "Kits", 3);
		EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());

		// Implémenter la liste des Kits
		if (endaPlayer != null) {
			for (KitAbstract kitAbstract : EndariumAPI.getGameSetting().getKitManager().getKitList())
				this.menuInventory
						.addItem(new ItemFactory(kitAbstract.getItemIcon(player))
								.withAmount(EndariumAPI.getGameSetting().getKitManager()
										.hasPermissionKit(player.getUniqueId(), kitAbstract.getKitsInfos()) ? 1 : 0)
								.done());
		}

		for (int i : this.getPaneSlotList())
			this.menuInventory.setItem(i,
					new ItemFactory(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 8)).withName("§0").done());
		this.menuInventory.setItem(22, new ItemFactory(Material.ARROW).withName(ChatColor.WHITE + "Fermer").done());

		open();
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClick(InventoryClickEvent event) {

		if (!(event.getWhoClicked() instanceof Player))
			return;

		Player player = (Player) event.getWhoClicked();
		Inventory inventory = event.getInventory();

		if ((this.menuName.equals(inventory.getName())) && (this.player.equals(player))) {
			event.setCancelled(true);

			if (!(GameStatus.isStatus(GameStatus.LOBBY)))
				return;
			if (event.getInventory() == null)
				return;
			if (event.getClickedInventory() == null)
				return;
			if (event.getCurrentItem() == null)
				return;
			if (event.getCurrentItem().getType().equals(Material.AIR))
				return;
			if (!(event.getSlotType().equals(InventoryType.SlotType.CONTAINER)))
				return;
			if ((event.getCurrentItem() == null) || (!(event.getCurrentItem().hasItemMeta()))
					|| (event.getCurrentItem().getItemMeta() == null)) {
				return;
			}

			// Interaction avec un Item Random
			if (event.getCurrentItem().getType().equals(Material.STAINED_GLASS_PANE))
				return;

			switch (event.getCurrentItem().getType()) {
			case ARROW:
				player.closeInventory();
				return;

			default:

				// Management du selection de kits
				GameType gameType = GameType.getOriginGameType(CrystaliserAPI.getEndaServer().getGameType());
				Material material = event.getCurrentItem().getType();
				KitsInfos kitsInfos = KitsInfos.getKitByMaterial(material, gameType);
				KitAbstract kitAbstract = EndariumAPI.getGameSetting().getKitManager().getKitAbstractByInfos(kitsInfos);

				// Vérifier si le Kit est eronné
				if ((kitsInfos == null) || (kitAbstract == null)) {
					player.sendMessage(EndariumAPI.getGameSetting().getGamePrefix() + ChatColor.RED
							+ "Une erreur est survenue, impossible de sélectionner un Kit.");
					player.closeInventory();
					return;
				}

				// Vérifier si le Joueur possède le Kit
				if (!(EndariumAPI.getGameSetting().getKitManager().hasPermissionKit(player.getUniqueId(), kitsInfos))) {
					player.sendMessage(EndariumAPI.getGameSetting().getGamePrefix() + ChatColor.RED
							+ "Vous ne possédez pas ce kit, rendez-vous sur le Hub pour l'obtenir.");
					SoundUtils.sendSound(player, Sound.VILLAGER_NO);
					player.closeInventory();
					return;
				}

				// Mise en place du Kit au Joueur
				EndariumAPI.getGameSetting().getKitManager().setPlayerKit(player.getUniqueId(), kitAbstract);
				player.sendMessage(EndariumAPI.getGameSetting().getGamePrefix() + ChatColor.WHITE
						+ "Vous avez sélectionné le kit : " + ChatColor.YELLOW + kitsInfos.getName());
				SoundUtils.sendSound(player, Sound.LEVEL_UP);
				Bukkit.getPluginManager().callEvent(new PlayerKitChangeEvent(player, kitsInfos));
				player.closeInventory();
				break;
			}
		}
	}

	/**
	 * Récupérer la position des Glass.
	 */
	private List<Integer> getPaneSlotList() {
		List<Integer> slots = new ArrayList<Integer>();
		for (int i = 18; i < 27; i++)
			slots.add(i);
		return slots;
	}
}