package net.endarium.api.minecraft.commands.system;

import net.endarium.api.minecraft.EndariumBukkit;
import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.login.LoginManager;
import net.endarium.api.players.report.Report;
import net.endarium.api.players.report.ReportManager;
import net.endarium.api.utils.builders.inventories.InventoryBuilder;
import net.endarium.api.utils.builders.items.ItemFactory;
import net.endarium.api.utils.builders.items.heads.HeadBuilder;
import net.endarium.api.utils.mojang.UUIDEndaFetcher;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.commands.Command;
import net.endarium.api.utils.commands.Command.SenderType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReportCommand {

	private String PREFIX = ChatColor.GOLD + "[Report] ";


	@Command(name = { "report", "reports" }, minimumRank = Rank.DEFAULT, senderType = SenderType.ONLY_PLAYER)
	public void onCommand(Player player, String[] args) {

		EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());
		if (!(endaPlayer.isLogged())) {
			return;
		}

		if (args == null) {
			this.sendHelp(player);
		}

		if (args.length != 1) {
			this.sendHelp(player);
			return;
		}

		// Vérifier si le joueur existe sur Mojang
		String targetName = args[0];
		UUID targetUUID = UUIDEndaFetcher.getPlayerUUID(targetName);
		if (targetUUID == null) {
			player.sendMessage(
					PREFIX + ChatColor.RED + "Le compte '" + targetName + "' n'existe pas.");
			return;
		}
		targetName = UUIDEndaFetcher.getPlayerName(targetUUID);

		// Ouvrir le Menu de Bannissement
		player.sendMessage(PREFIX + ChatColor.WHITE + "Vous allez " + ChatColor.RED + "report " + ChatColor.WHITE
				+ "le joueur : " + ChatColor.YELLOW + targetName + ChatColor.WHITE + ".");
		new ReportInventory(player, targetName);
	}

	public class ReportInventory implements Listener {

		private InventoryBuilder inventoryBuilder;

		private String inventoryName = "Report";

		private Map<Player, String> targetReportMap = new HashMap<Player, String>();

		/**
		 * Inventaire : Ban
		 *
		 * @param player
		 * @param target
		 */
		public ReportInventory(Player player, String target) {

			this.inventoryName = "Report - " + target;
			this.inventoryBuilder = new InventoryBuilder(inventoryName);
			this.inventoryBuilder.inventory.remove(player);
			this.targetReportMap.remove(player);
			this.targetReportMap.put(player, target);
			Bukkit.getPluginManager().registerEvents(this, EndariumBukkit.getPlugin());

			this.inventoryBuilder.addLine(new String[]{"", "", "", "", "", "", "", "", ""})
					.addLine(new String[]{"", "", "anti_jeu", "",
							"triche", "", "mauvais_langage", "", ""})
					.addLine(new String[]{"", "", "", "", "", "", "", "", ""})
					.addLine(new String[]{"x", "x", "x", "x", "report_target", "x", "x", "x", "report_cancel"});

			// Footer du Menu de Ban
			this.inventoryBuilder.setItem("x",
					new ItemFactory(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14)).withName("§0").done());
			this.inventoryBuilder.setItem("report_cancel",
					new ItemFactory(Material.REDSTONE).withName(ChatColor.RED + "" + ChatColor.UNDERLINE + "Annuler")
							.withLore(ChatColor.WHITE + "Cliquez pour " + ChatColor.RED + "annuler",
									ChatColor.WHITE + "le report.")
							.done());
			this.inventoryBuilder.setItem("report_target", new HeadBuilder().setHead(target)
					.setName(ChatColor.GOLD + "" + ChatColor.UNDERLINE + target).build());

			// Items des différentes Sanctions
			this.inventoryBuilder.setItem("triche",
					this.getReportItem(Material.DIAMOND_SWORD, "Triche"));
			this.inventoryBuilder.setItem("anti_jeu",
					this.getReportItem(Material.EYE_OF_ENDER, "Anti-Jeu"));
			this.inventoryBuilder.setItem("mauvais_langage",
					this.getReportItem(Material.BARRIER, "Mauvais Langage"));

			player.openInventory(this.inventoryBuilder.build(player));
		}

		/**
		 * Générer un Item Icon d'une Sanction.
		 *
		 * @param material
		 * @param report
		 * @return
		 */
		private ItemStack getReportItem(Material material, String report) {
			return new ItemFactory(material)
					.withName(ChatColor.GRAY + "Motif : " + ChatColor.AQUA + "" + ChatColor.UNDERLINE + report)
					.withLore("", ChatColor.WHITE + "Vous êtes sur le point",
							ChatColor.WHITE + "de " + ChatColor.RED + "report " + ChatColor.WHITE + "un joueur.", "",
							ChatColor.DARK_GREEN + "● " + ChatColor.GREEN + "Cliquez pour le report")
					.addFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_DESTROYS).done();
		}

		@EventHandler
		public void onInventoryClick(InventoryClickEvent event) {
			Player player = (Player) event.getWhoClicked();
			String targetPlayer = this.targetReportMap.get(player);
			if (targetPlayer == null)
				return;
			if (event.getInventory() == null)
				return;
			if (event.getCurrentItem() == null)
				return;
			if (event.getCurrentItem().getType().equals(Material.AIR))
				return;
			if (!(event.getInventory().equals(this.inventoryBuilder.inventory.get(player))))
				return;
			if (event.getClickedInventory().getType().equals(InventoryType.PLAYER))
				return;
			event.setCancelled(true);
			switch (event.getCurrentItem().getType()) {

				// Footer : Ban
				case DIAMOND_SWORD:
					player.closeInventory();
					this.applyReport(player, targetPlayer, "Triche");
					break;

				case REDSTONE:
					player.closeInventory();
					player.sendMessage(PREFIX + ChatColor.WHITE + "Vous avez " + ChatColor.RED + "annulé " + ChatColor.WHITE
							+ "le report de : " + ChatColor.YELLOW + targetPlayer + ChatColor.WHITE + ".");
					break;

				// Ban | Raison : Triche
				case EYE_OF_ENDER:
					player.closeInventory();
					this.applyReport(player, targetPlayer, "Anti-jeu");
					break;

				// Ban | Raison : StreamHack
				case BARRIER:
					player.closeInventory();
					this.applyReport(player, targetPlayer, "Mauvais Langage");
					break;


				default:
					break;
			}
		}

		/**
		 * Appliquer un Report sur un Joueur.
		 *
		 * @param player
		 * @param target
		 * @param reason
		 */
		private void applyReport(Player player, String target, String reason) {
			// Recherche du joueur signalé
			Player reportedPlayer = Bukkit.getPlayer(target);
			if (reportedPlayer == null) {
				player.sendMessage(PREFIX + ChatColor.WHITE + "Le joueur n'a pas été trouvé!");
				return;
			}

			// Création du rapport
			Report report = new Report(reportedPlayer.getUniqueId(), player.getUniqueId(), reason);


			// Enregistrement du rapport dans le gestionnaire de rapports
			ReportManager reportManager = new ReportManager();
			reportManager.addReport(report);
			if (reportManager.addReport(report)) {
				player.sendMessage(PREFIX + ChatColor.WHITE + "Le report a été soumis avec succès pour le joueur " + target + " pour la raison : " + reason);
			} else {
				player.sendMessage(PREFIX + ChatColor.WHITE + "Une erreur s'est produite lors de la soumission du repport.");
			}
		}

	}


	/**
	 * Message d'Aide de la Commande.
	 */
	private void sendHelp(Player player) {
		player.sendMessage("");
		player.sendMessage(" " + ChatColor.GRAY + "» " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Aide"
				+ ChatColor.WHITE + "│ " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Signalement d'un Joueur");
		player.sendMessage("");
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/report [pseudo] "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Signaler un Joueur à la modération.");
		player.sendMessage("");
	}
}