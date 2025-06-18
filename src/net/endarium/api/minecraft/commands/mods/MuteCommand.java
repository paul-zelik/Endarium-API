package net.endarium.api.minecraft.commands.mods;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import net.endarium.api.players.login.LoginManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import net.endarium.api.EndariumCommons;
import net.endarium.api.minecraft.EndariumBukkit;
import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.moderation.mute.MuteInfos;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.builders.inventories.InventoryBuilder;
import net.endarium.api.utils.builders.items.ItemFactory;
import net.endarium.api.utils.builders.items.heads.HeadBuilder;
import net.endarium.api.utils.commands.Command;
import net.endarium.api.utils.commands.Command.SenderType;
import net.endarium.api.utils.mojang.UUIDEndaFetcher;
import net.endarium.api.utils.tools.SoundUtils;
import net.md_5.bungee.api.ChatColor;

public class MuteCommand {

	private String PREFIX = ChatColor.GOLD + "[ZMute] ";

	@Command(name = { "zmute" }, minimumRank = Rank.HELPER, permission = { "endarium.mods.*",
			"endarium.mods.mute" }, senderType = SenderType.ONLY_PLAYER)
	public void onCommandMute(Player player, String[] args) {
		LoginManager loginManager = new LoginManager();
		if (!(loginManager.isLogged(player.getUniqueId()))) {
			return;
		}

		// Vérifier la validité des Arguments
		if (args.length != 1) {
			this.sendHelp(player);
			return;
		}

		// Vérifier si le joueur existe sur Mojang
		String targetName = args[0];
		UUID targetUUID = UUIDEndaFetcher.getPlayerUUID(targetName);
		if (targetUUID == null) {
			player.sendMessage(
					PREFIX + ChatColor.RED + "Le compte '" + targetName + "' n'est pas un compte Mojang valide.");
			return;
		}
		targetName = UUIDEndaFetcher.getPlayerName(targetUUID);

		// Détecter le grade du Joueur Sanctionné
		Rank targetRank = EndariumCommons.getInstance().getEndariumEntities().getRankManager().getRank(targetUUID,
				true);
		if (targetRank == null) {
			player.sendMessage(
					PREFIX + ChatColor.RED + "Le joueur '" + targetName + "' ne possède pas de compte sur Endarium.");
			return;
		}

		// Vérifier que le Joueur peut appliquer cette Sanction à ce Target
		EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());
		if (endaPlayer.getRank().getPower() <= targetRank.getPower()) {
			player.sendMessage(PREFIX + ChatColor.RED + "Vous n'êtes pas autorisé à sanctionner ce joueur.");
			return;
		}

		// Vérifier si le Joueur est sur le Proxy mais différent du Modérateur
		if ((EndaPlayer.isConnected(targetUUID)) && (Bukkit.getPlayer(targetName) == null)) {
			player.sendMessage(PREFIX + ChatColor.RED
					+ "Ce joueur est sur Endarium, vous devez être sur son serveur pour effectuer un mute.");
			return;
		}

		// Vérifier si le Joueur est déjà Mute
		MuteInfos muteInfos = EndariumBukkit.getPlugin().getEndariumCommons().getEndariumEntities().getMuteManager()
				.isMute(targetUUID);
		if (muteInfos != null) {
			player.sendMessage(
					PREFIX + ChatColor.RED + "Ce joueur est déjà mute pour : " + muteInfos.getReason() + ".");
			return;
		}

		// Ouvrir le Menu de Mute
		player.sendMessage(PREFIX + ChatColor.WHITE + "Vous allez " + ChatColor.RED + "sanctionner " + ChatColor.WHITE
				+ "le joueur : " + ChatColor.YELLOW + targetName + ChatColor.WHITE + ".");
		new MuteInventory(player, targetName);
	}

	@Command(name = { "zunmute" }, minimumRank = Rank.MODERATOR, permission = { "endarium.mods.*",
			"endarium.mods.unmute" }, senderType = SenderType.ONLY_PLAYER)
	public void onCommandUnMute(Player player, String[] args) {

		// Vérifier la validité des Arguments
		if (args.length != 1) {
			this.sendHelp(player);
			return;
		}

		// Vérifier si le joueur existe sur Mojang
		String targetName = args[0];
		UUID targetUUID = UUIDEndaFetcher.getPlayerUUID(targetName);
		if (targetUUID == null) {
			player.sendMessage(
					PREFIX + ChatColor.RED + "Le compte '" + targetName + "' n'est pas un compte Mojang valide.");
			return;
		}

		// Vérifier si le Joueur n'est pas déjà Mute
		MuteInfos muteInfos = EndariumBukkit.getPlugin().getEndariumCommons().getEndariumEntities().getMuteManager()
				.isMute(targetUUID);
		if (muteInfos == null) {
			player.sendMessage(PREFIX + ChatColor.RED + "Ce joueur n'est pas mute.");
			return;
		}

		// Appliquer le UnMute
		EndariumBukkit.getPlugin().getEndariumCommons().getEndariumEntities().getMuteManager().applyUnMute(targetUUID);
		player.sendMessage(PREFIX + ChatColor.WHITE + "Vous venez de " + ChatColor.RED + "dé-mute " + ChatColor.WHITE
				+ "le joueur : " + ChatColor.YELLOW + targetName + ChatColor.WHITE + ".");
		SoundUtils.sendSound(player, Sound.LEVEL_UP);

		Player playerTarget = Bukkit.getPlayer(targetUUID);
		if (playerTarget != null) {
			EndaPlayer.get(targetUUID).setMuteInfos(null);
		}

		// Vérifier si le Modérateur est responsable du Mute
		if (!(muteInfos.getModUUID().equals(player.getUniqueId()))) {
			player.sendMessage(PREFIX + ChatColor.RED + "Attention : Ce joueur n'était pas mute par vous.");
		}
	}

	@Command(name = { "zmuteinfos", "zmuteinfo" }, minimumRank = Rank.HELPER, permission = { "endarium.mods.*",
			"endarium.mods.mute" }, senderType = SenderType.ONLY_PLAYER)
	public void onCommandMuteInfos(Player player, String[] args) {

		// Vérifier la validité des Arguments
		if (args.length != 1) {
			this.sendHelp(player);
			return;
		}

		// Vérifier si le joueur existe sur Mojang
		String targetName = args[0];
		UUID targetUUID = UUIDEndaFetcher.getPlayerUUID(targetName);
		if (targetUUID == null) {
			player.sendMessage(
					PREFIX + ChatColor.RED + "Le compte '" + targetName + "' n'est pas un compte Mojang valide.");
			return;
		}

		// Vérifier si le Joueur n'est pas déjà Mute
		MuteInfos muteInfos = EndariumBukkit.getPlugin().getEndariumCommons().getEndariumEntities().getMuteManager()
				.isMute(targetUUID);
		if (muteInfos == null) {
			player.sendMessage(PREFIX + ChatColor.RED + "Ce joueur n'est pas mute.");
			return;
		}

		// Envoyer les Informations du Mute
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, new Locale("fr"));
		String modName = UUIDEndaFetcher.getPlayerName(muteInfos.getModUUID());

		player.sendMessage("");
		player.sendMessage(" " + ChatColor.GRAY + "» " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Mute Infos"
				+ ChatColor.WHITE + "│ " + ChatColor.YELLOW + "" + ChatColor.BOLD + targetName);
		player.sendMessage("");
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD
				+ "Modérateur Responsable " + ChatColor.WHITE + "» " + ChatColor.AQUA + modName);
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "Raison "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + muteInfos.getReason());
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "Mute jusqu'au "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + dateFormat.format(muteInfos.getExpiryDate()));
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "Date de la Sanction "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + dateFormat.format(muteInfos.getDate()));
		player.sendMessage("");
	}

	@Command(name = { "mute", "mutes" }, minimumRank = Rank.DEFAULT, senderType = SenderType.ONLY_PLAYER)
	public void onCommandMutePlayer(Player player, String[] args) {
		EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());
		if (endaPlayer.getMuteInfos() != null) {
			player.sendMessage(ChatColor.GOLD + "[Mute] " + ChatColor.WHITE
					+ "Vous êtes mute de ce serveur pour la raison suivante : " + ChatColor.YELLOW
					+ endaPlayer.getMuteInfos().getReason() + ChatColor.WHITE + ".");
			DateFormat shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT,
					new Locale("fr"));
			player.sendMessage(ChatColor.GRAY + "Votre sanction sera levée le : " + ChatColor.AQUA
					+ shortDateFormat.format(endaPlayer.getMuteInfos().getExpiryDate()));
		} else {
			player.sendMessage(ChatColor.GOLD + "[Mute] " + ChatColor.WHITE + "Vous n'êtes pas mute de ce serveur.");
		}
	}

	/**
	 * Message d'Aide de la Commande.
	 */
	private void sendHelp(CommandSender sender) {
		sender.sendMessage("");
		sender.sendMessage(" " + ChatColor.GRAY + "» " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Aide"
				+ ChatColor.WHITE + "│ " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Commande de Mute");
		sender.sendMessage("");
		sender.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/zmute [player] "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Mute un joueur du serveur.");
		sender.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/zunmute [player] "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Dé-Mute un joueur du serveur.");
		sender.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/zmuteinfos [player] "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Voir les informations d'un mute.");
		sender.sendMessage("");
	}

	public class MuteInventory implements Listener {

		private InventoryBuilder inventoryBuilder;

		private String inventoryName = "Mute";

		private Map<Player, String> targetMuteMap = new HashMap<Player, String>();

		/**
		 * Inventaire : Mute
		 * 
		 * @param player
		 * @param target
		 */
		public MuteInventory(Player player, String target) {

			this.inventoryName = "Mute - " + target;
			this.inventoryBuilder = new InventoryBuilder(inventoryName);
			this.inventoryBuilder.inventory.remove(player);
			this.targetMuteMap.remove(player);
			this.targetMuteMap.put(player, target);
			Bukkit.getPluginManager().registerEvents(this, EndariumBukkit.getPlugin());

			this.inventoryBuilder.addLine(new String[] { "", "", "", "", "", "", "", "", "" })
					.addLine(new String[] { "", "mute_spam_flood", "mute_insultes", "mute_provocations",
							"mute_racismes", "mute_insolence", "", "mute_warn", "" })
					.addLine(new String[] { "", "", "", "", "", "", "", "", "" })
					.addLine(new String[] { "x", "x", "x", "x", "mute_target", "x", "x", "x", "mute_cancel" });

			// Footer du Menu de Mute
			this.inventoryBuilder.setItem("x",
					new ItemFactory(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 3)).withName("§0").done());
			this.inventoryBuilder.setItem("mute_cancel",
					new ItemFactory(Material.REDSTONE).withName(ChatColor.RED + "" + ChatColor.UNDERLINE + "Annuler")
							.withLore(ChatColor.WHITE + "Cliquez pour " + ChatColor.RED + "annuler",
									ChatColor.WHITE + "la sanction.")
							.done());
			this.inventoryBuilder.setItem("mute_target", new HeadBuilder().setHead(target)
					.setName(ChatColor.GOLD + "" + ChatColor.UNDERLINE + target).build());

			// Items des différentes Sanctions
			this.inventoryBuilder.setItem("mute_spam_flood",
					this.getSanctionItem(Material.BOOK, "Spam/Flood", "3 Heures"));
			this.inventoryBuilder.setItem("mute_insultes",
					this.getSanctionItem(Material.BARRIER, "Insultes", "1 Jour"));
			this.inventoryBuilder.setItem("mute_provocations",
					this.getSanctionItem(Material.FLINT_AND_STEEL, "Provocations", "3 Jours"));
			this.inventoryBuilder.setItem("mute_racismes", this.getSanctionItem(Material.COAL, "Racismes", "7 Jours"));
			this.inventoryBuilder.setItem("mute_pub",
					this.getSanctionItem(Material.PAPER, "Publicité Insistante", "6 Heures"));

			this.inventoryBuilder.setItem("mute_warn",
					this.getSanctionItem(Material.NETHER_STAR, "Avertissement", "1 Heure"));

			player.openInventory(this.inventoryBuilder.build(player));
		}

		/**
		 * Générer un Item Icon d'une Sanction.
		 * 
		 * @param material
		 * @param sanction
		 * @param delay
		 * @return
		 */
		private ItemStack getSanctionItem(Material material, String sanction, String delay) {
			return new ItemFactory(material)
					.withName(ChatColor.GRAY + "Sanction : " + ChatColor.AQUA + "" + ChatColor.UNDERLINE + sanction)
					.withLore("", ChatColor.WHITE + "Vous êtes sur le point",
							ChatColor.WHITE + "de " + ChatColor.RED + "mute " + ChatColor.WHITE + "un joueur.", "",
							ChatColor.GRAY + "Raison : " + ChatColor.AQUA + sanction,
							ChatColor.GRAY + "Durée : " + ChatColor.DARK_AQUA + delay, "",
							ChatColor.DARK_GREEN + "● " + ChatColor.GREEN + "Cliquez pour Sanctionner")
					.addFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_DESTROYS).done();
		}

		@EventHandler
		public void onInventoryClick(InventoryClickEvent event) {
			Player player = (Player) event.getWhoClicked();
			String targetPlayer = this.targetMuteMap.get(player);
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

			// Footer : Mute
			case REDSTONE:
				player.closeInventory();
				player.sendMessage(PREFIX + ChatColor.WHITE + "Vous avez " + ChatColor.RED + "annulé " + ChatColor.WHITE
						+ "la sanction de : " + ChatColor.YELLOW + targetPlayer + ChatColor.WHITE + ".");
				break;

			// Mute | Raison : Spam/Flood
			case BOOK:
				player.closeInventory();
				this.applyMute(player, targetPlayer, "Spam/Flood", "(3 Heures)", 3);
				break;

			// Mute | Raison : Insultes
			case BARRIER:
				player.closeInventory();
				this.applyMute(player, targetPlayer, "Insultes", "(1 Jour)", 24);
				break;

			// Mute | Raison : Provocations
			case FLINT_AND_STEEL:
				player.closeInventory();
				this.applyMute(player, targetPlayer, "Provocations", "(3 Jours)", 72);
				break;

			// Mute | Raison : Racismes
			case COAL:
				player.closeInventory();
				this.applyMute(player, targetPlayer, "Racismes", "(7 Jours)", 168);
				break;

			// Mute | Raison : Publicité Insistante
			case PAPER:
				player.closeInventory();
				this.applyMute(player, targetPlayer, "Publicité Insistante", "(6 Heures)", 6);
				break;

			// Mute | Raison : Avertissement
			case NETHER_STAR:
				player.closeInventory();
				this.applyMute(player, targetPlayer, "Avertissement", "(1 Heure)", 1);
				break;

			default:
				break;
			}
		}

		/**
		 * Appliquer un Mute sur un Joueur.
		 * 
		 * @param player
		 * @param target
		 * @param reason
		 * @param delay
		 * @param hours
		 */
		private void applyMute(Player player, String target, String reason, String delay, int hours) {

			// Appliquer le Mute sur la Base de Données
			UUID uuidTarget = UUIDEndaFetcher.getPlayerUUID(target);
			String muteId = (UUID.randomUUID().toString().replaceAll("-", "")).substring(1, 12);
			MuteInfos muteInfos = new MuteInfos(uuidTarget, player.getUniqueId(), reason, hours, new Date(),
					this.addHoursToJavaUtilDate(new Date(), hours), muteId);
			EndariumBukkit.getPlugin().getEndariumCommons().getEndariumEntities().getMuteManager()
					.createMute(uuidTarget, muteInfos);

			// Effectuer le Mute du Joueur
			Player playerTarget = Bukkit.getPlayer(uuidTarget);
			if (playerTarget != null) {
				EndaPlayer.get(uuidTarget).setMuteInfos(muteInfos);
				playerTarget.sendMessage(ChatColor.GOLD + "[Mute] " + ChatColor.WHITE + "Vous venez d'être Mute pour : "
						+ ChatColor.YELLOW + reason + ChatColor.WHITE
						+ ". Cette sanction est appliquée pour une durée de : " + ChatColor.DARK_AQUA + delay
						+ ChatColor.WHITE + ".");
				SoundUtils.sendSound(playerTarget, Sound.LEVEL_UP);
			}

			// Envoyer un Message aux Modérateurs
			player.sendMessage(PREFIX + ChatColor.WHITE + "Vous venez de " + ChatColor.RED + "sanctionner"
					+ ChatColor.WHITE + " : " + ChatColor.YELLOW + target + ChatColor.WHITE + ", raison : "
					+ ChatColor.AQUA + reason + " " + ChatColor.DARK_AQUA + delay);
		}

		/**
		 * Ajouter une Heure à une Date.
		 * 
		 * @param date
		 * @param hours
		 * @return
		 */
		private Date addHoursToJavaUtilDate(Date date, int hours) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.HOUR_OF_DAY, hours);
			return calendar.getTime();
		}
	}
}