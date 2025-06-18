package net.endarium.api.minecraft.commands.mods;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.endarium.api.players.login.LoginManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.endarium.api.games.servers.CrystaliserAPI;
import net.endarium.api.games.servers.CrystaliserServerManager;
import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.EndariumAPI;
import net.endarium.api.utils.Messages;
import net.endarium.api.utils.builders.inventories.VirtualMenu;
import net.endarium.api.utils.builders.items.ItemFactory;
import net.endarium.api.utils.commands.Command;
import net.endarium.api.utils.commands.Command.SenderType;
import net.endarium.api.utils.mojang.UUIDEndaFetcher;
import net.endarium.crystaliser.servers.EndaServer;
import net.endarium.crystaliser.servers.ServerStatus;
import net.md_5.bungee.api.ChatColor;

public class ZPlayCommand {

	private String PREFIX = Messages.ZMODS_PREFIX;

	@Command(name = { "zplay" }, minimumRank = Rank.MODERATOR, senderType = SenderType.ONLY_PLAYER)
	public void onCommandZPlay(Player player, String[] args) {
		LoginManager loginManager = new LoginManager();
		if (!(loginManager.isLogged(player.getUniqueId()))) {
			return;
		}

		EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());

		// Vérifier si le Joueur est en train de Jouer
		if ((EndariumAPI.getGameSetting().isGameServer()) && (!(endaPlayer.isSpectator()))) {
			player.sendMessage(
					PREFIX + ChatColor.RED + "Vous ne pouvez pas accéder à la liste des serveurs en étant en jeu.");
			return;
		}

		// Ouvrir le Menu avec la liste des Serveurs
		new ZPlayMenu(player);
		return;
	}

	/**
	 * Gestion du Menu d'Affichage des Serveurs.
	 */
	protected class ZPlayMenu extends VirtualMenu implements Listener {

		public ZPlayMenu(Player player) {
			super(player, "ZPlay » Serveurs", 6);

			// Gestion du contenu du Menu
			for (int i : this.getPaneSlotList())
				this.menuInventory.setItem(i,
						new ItemFactory(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 8)).withName("§0").done());
			this.menuInventory.setItem(49, new ItemFactory(Material.ARROW).withName(ChatColor.WHITE + "Fermer").done());

			// Afficher la liste des Serveurs
			for (EndaServer endaServer : CrystaliserAPI.getEndaServerList()) {
				if (endaServer != null) {

					ItemStack itemStack = new ItemStack(CrystaliserAPI.getEndaServer().getServerName().equals(endaServer.getServerName()) ? Material.BARRIER : Material.WOOL);

					// Gestion de la couleur descriptive
					if ((!(itemStack.getType().equals(Material.BARRIER)))) {
						if (endaServer.getServerStatus().equals(ServerStatus.PUBLIC))
							if (endaServer.isStartedSoon())
								itemStack.setDurability((short) 3);
							else
								itemStack.setDurability((short) 5);
						else if (endaServer.getServerStatus().equals(ServerStatus.INGAME))
							itemStack.setDurability((short) 1);
						else if (endaServer.getServerStatus().equals(ServerStatus.REBOOT))
							itemStack.setDurability((short) 14);
						else
							itemStack.setDurability((short) 0);
					}
					ItemMeta itemMeta = itemStack.getItemMeta();
					itemMeta.setDisplayName("§b" + endaServer.getServerName());
					itemStack.setAmount(endaServer.getCurrentPlayers());

					// Gestion de la Description
					List<String> lores = new ArrayList<String>();
					lores.add("");
					lores.add(ChatColor.WHITE + "Joueurs : " + ChatColor.YELLOW + endaServer.getCurrentPlayers()
							+ ChatColor.GRAY + "/" + ChatColor.YELLOW + endaServer.getMaxPlayers());
					lores.add("");
					lores.add(
							ChatColor.WHITE + "Mode de Jeu : " + ChatColor.YELLOW + endaServer.getGameType().getName());
					lores.add(ChatColor.WHITE + "Type de Serveur : " + ChatColor.YELLOW
							+ endaServer.getServerType().getName());
					lores.add(ChatColor.WHITE + "Statut : " + ChatColor.RED + endaServer.getServerStatus().getName());
					lores.add(ChatColor.WHITE + "Carte : " + ChatColor.AQUA + endaServer.getMapInfos().getName());
					lores.add("");
					List<UUID> onlinePlayersUUID = endaServer.getPlayersOnlineList();
					if (!(onlinePlayersUUID.isEmpty())) {
						lores.add(ChatColor.GRAY + "Joueurs en ligne :");
						for (UUID uuidOnline : endaServer.getPlayersOnlineList()) {
							lores.add(ChatColor.DARK_GRAY + "- " + ChatColor.WHITE
									+ UUIDEndaFetcher.getPlayerName(uuidOnline));
						}
						lores.add("");
					}
					lores.add(CrystaliserAPI.getEndaServer().equals(endaServer)
							? ChatColor.RED + "» Vous êtes déjà connecté"
							: ChatColor.GREEN + "» Cliquez pour rejoindre");
					itemMeta.setLore(lores);

					itemStack.setItemMeta(itemMeta);
					this.menuInventory.addItem(itemStack);
				}
			}
			this.open();
		}

		@EventHandler(priority = EventPriority.NORMAL)
		public void onInventoryClick(InventoryClickEvent event) {

			if (!(event.getWhoClicked() instanceof Player))
				return;

			Player player = (Player) event.getWhoClicked();
			EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());
			Inventory inventory = event.getInventory();

			if ((this.menuName.equals(inventory.getName())) && (this.player.equals(player))) {
				event.setCancelled(true);

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

				// Intéraction avec des Items Aléatoire
				if ((event.getCurrentItem().getType().equals(Material.STAINED_GLASS_PANE))
						|| (event.getCurrentItem().getType().equals(Material.EXPLOSIVE_MINECART)))
					return;

				// Fermer le Menu de ZPLAY
				if (event.getCurrentItem().getType().equals(Material.ARROW)) {
					player.closeInventory();
					return;
				}

				// Vérifier si le Joueur est déjà sur le Serveur
				if (event.getCurrentItem().getType().equals(Material.BARRIER)) {
					player.sendMessage(PREFIX + ChatColor.RED + "Vous êtes déjà connecté sur ce serveur.");
					player.closeInventory();
					return;
				}

				// Effectuer la Téléportation vers le Serveur
				if (event.getCurrentItem().getType().equals(Material.WOOL)) {

					// Mise en place du ZMOD
					if (!(endaPlayer.isModeModeration()))
						player.getServer().dispatchCommand(player, "zmod");

					// Effectuer la Téléportation du Joueur
					String serverName = event.getCurrentItem().getItemMeta().getDisplayName().replace("§b", "");
					CrystaliserServerManager.sendToServer(player, serverName);
					player.sendMessage(Messages.ENDARIUM_PREFIX + ChatColor.WHITE + ChatColor.WHITE
							+ "Téléportation en cours vers : " + ChatColor.AQUA + serverName);
					player.closeInventory();
				}
			}
		}

		/**
		 * Récupérer la position des Glass.
		 */
		private List<Integer> getPaneSlotList() {
			List<Integer> slots = new ArrayList<Integer>();
			for (int i = 45; i < 54; i++)
				slots.add(i);
			return slots;
		}
	}
}