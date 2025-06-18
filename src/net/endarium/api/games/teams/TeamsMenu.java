package net.endarium.api.games.teams;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.endarium.api.games.GameStatus;
import net.endarium.api.utils.EndariumAPI;
import net.endarium.api.utils.builders.inventories.VirtualMenu;
import net.endarium.api.utils.builders.items.ItemFactory;
import net.endarium.api.utils.tools.SoundUtils;
import net.md_5.bungee.api.ChatColor;

public class TeamsMenu extends VirtualMenu {

	private String PREFIX = ChatColor.GOLD + "[Teams] ";

	/**
	 * Constructeur du Menu de Teams.
	 * 
	 * @param player
	 */
	public TeamsMenu(Player player) {
		super(player, "Equipes", 3);

		// Implémenter la liste des Teams
		for (Teams teams : EndariumAPI.getGameSetting().getTeamManager().getTeamList()) {
			ItemStack itemStack = teams.getIconItem();
			itemStack.setAmount(EndariumAPI.getGameSetting().getTeamManager().countTeam(teams));
			ItemMeta itemMeta = itemStack.getItemMeta();
			itemMeta.setDisplayName(teams.getChatColor() + teams.getName());

			// Description pour l'icon de Team
			List<String> lores = new ArrayList<>();
			lores.add("");
			// Description des Joueurs dans la Team.
			int playerInTeam = 0;
			for (Player playerTeam : EndariumAPI.getGameSetting().getTeamManager().getTeamPlayerList(teams)) {
				lores.add(ChatColor.GRAY + "■ " + teams.getChatColor() + playerTeam.getName());
				playerInTeam++;
			}
			for (int x = playerInTeam; x != EndariumAPI.getGameSetting().getTeamManager().getTeamSize(); x++) {
				lores.add(ChatColor.GRAY + "[Emplacement Vide]");
			}
			lores.add("");
			if (EndariumAPI.getGameSetting().getTeamManager().getTeamPlayerList(teams).size() >= EndariumAPI
					.getGameSetting().getTeamManager().getTeamSize()) {
				lores.add(ChatColor.DARK_RED + "» " + ChatColor.RED + "Cette équipe est pleine");
			} else {
				lores.add(ChatColor.GOLD + "» " + ChatColor.YELLOW + "Cliquez pour rejoindre");
			}
			itemMeta.setLore(lores);

			itemStack.setItemMeta(itemMeta);
			this.menuInventory.addItem(itemStack);
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
				break;

			default:

				// Selection de Teams
				Teams teams = Teams.getTeam(event.getCurrentItem().getItemMeta().getDisplayName().substring(2));
				if (teams == null)
					return;

				// Vérifier si le Joueur est déà dans la Team
				if ((EndariumAPI.getGameSetting().getTeamManager().getPlayerTeam(player) != null)
						&& (EndariumAPI.getGameSetting().getTeamManager().getPlayerTeam(player).equals(teams))) {
					player.sendMessage(PREFIX + ChatColor.RED + "Vous êtes déjà dans cette équipe.");
					player.closeInventory();
					return;
				}

				// Vérifier si la team est Full
				if (EndariumAPI.getGameSetting().getTeamManager().getTeamPlayerList(teams).size() >= EndariumAPI
						.getGameSetting().getTeamManager().getTeamSize()) {
					player.sendMessage(PREFIX + ChatColor.RED + "Cette équipe est déjà pleine.");
					player.closeInventory();
					return;
				}

				// Vérifier l'équilibre est affecter la Team
				if (!(EndariumAPI.getGameSetting().getTeamManager().canAccesTeam(teams))) {
					player.sendMessage(PREFIX + ChatColor.RED + "Impossible de rejoindre cette équipe.");
					player.closeInventory();
					return;
				}

				// Gestion de l'arrivée d'un Joueur dans une Team
				EndariumAPI.getGameSetting().getTeamManager().removePlayerFromAll(player);
				EndariumAPI.getGameSetting().getTeamManager().addPlayerTeam(player, teams);
				player.sendMessage(PREFIX + ChatColor.WHITE + "Vous avez rejoint l'équipe : " + teams.getChatColor()
						+ teams.getName());
				for (Player playerInTeam : EndariumAPI.getGameSetting().getTeamManager().getTeamPlayerList(teams))
					if (!(playerInTeam.getName().equalsIgnoreCase(player.getName())))
						playerInTeam.sendMessage(PREFIX + ChatColor.YELLOW + player.getName() + ChatColor.WHITE
								+ " vient de rejoindre votre équipe.");
				SoundUtils.sendSound(player, Sound.LEVEL_UP);
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