package net.endarium.api.minecraft.commands.mods;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import net.endarium.api.players.moderation.ban.BanInfos;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.players.rank.permissions.Permission;
import net.endarium.api.utils.EndariumAPI;
import net.endarium.api.utils.Messages;
import net.endarium.api.utils.builders.inventories.InventoryBuilder;
import net.endarium.api.utils.builders.items.ItemFactory;
import net.endarium.api.utils.builders.items.heads.HeadBuilder;
import net.endarium.api.utils.commands.Command;
import net.endarium.api.utils.commands.Command.SenderType;
import net.endarium.api.utils.mojang.UUIDEndaFetcher;
import net.endarium.api.utils.tools.SoundUtils;
import net.md_5.bungee.api.ChatColor;

public class BanCommand {

	private String PREFIX = ChatColor.GOLD + "[ZBan] ";

	@Command(name = { "zban" }, minimumRank = Rank.MODERATOR, senderType = SenderType.ONLY_PLAYER)
	public void onCommandBan(Player player, String[] args) {

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
		if ((EndaPlayer.isConnected(targetUUID)) && (Bukkit.getPlayer(targetUUID) == null)) {
			player.sendMessage(PREFIX + ChatColor.RED
					+ "Ce joueur est sur Endarium, vous devez être sur son serveur pour effectuer un bannissement.");
			return;
		}

		// Vérifier si le Joueur est déjà Banni
		BanInfos banInfos = EndariumBukkit.getPlugin().getEndariumCommons().getEndariumEntities().getBanManager()
				.isBan(targetUUID);
		if (banInfos != null) {
			player.sendMessage(
					PREFIX + ChatColor.RED + "Ce joueur est déjà banni pour : " + banInfos.getReason() + ".");
			return;
		}

		// Ouvrir le Menu de Bannissement
		player.sendMessage(PREFIX + ChatColor.WHITE + "Vous allez " + ChatColor.RED + "sanctionner " + ChatColor.WHITE
				+ "le joueur : " + ChatColor.YELLOW + targetName + ChatColor.WHITE + ".");
		new BanInventory(player, targetName);
	}

	@Command(name = { "zunban" }, minimumRank = Rank.ADMINISTRATOR, permission = { "endarium.mods.*",
			"endarium.mods.unban" }, senderType = SenderType.ONLY_PLAYER)
	public void onCommandUnBan(Player player, String[] args) {

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

		// Vérifier si le Joueur n'est pas déjà Banni
		BanInfos banInfos = EndariumBukkit.getPlugin().getEndariumCommons().getEndariumEntities().getBanManager()
				.isBan(targetUUID);
		if (banInfos == null) {
			player.sendMessage(PREFIX + ChatColor.RED + "Ce joueur n'est pas banni.");
			return;
		}

		// Appliquer le UnBan
		EndariumBukkit.getPlugin().getEndariumCommons().getEndariumEntities().getBanManager().applyUnBan(targetUUID);
		player.sendMessage(PREFIX + ChatColor.WHITE + "Vous venez de " + ChatColor.RED + "dé-bannir " + ChatColor.WHITE
				+ "le joueur : " + ChatColor.YELLOW + targetName + ChatColor.WHITE + ".");
		SoundUtils.sendSound(player, Sound.LEVEL_UP);

		// Vérifier si le Modérateur est responsable du Ban
		if (!(banInfos.getModUUID().equals(player.getUniqueId()))) {
			player.sendMessage(PREFIX + ChatColor.RED + "Attention : Ce joueur n'était pas banni par vous.");
		}
	}

	@Command(name = { "zbaninfos", "zbaninfo" }, minimumRank = Rank.MODERATOR, senderType = SenderType.ONLY_PLAYER)
	public void onCommandBanInfos(Player player, String[] args) {

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

		// Vérifier si le Joueur est déjà Banni
		BanInfos banInfos = EndariumBukkit.getPlugin().getEndariumCommons().getEndariumEntities().getBanManager()
				.isBan(targetUUID);
		if (banInfos == null) {
			player.sendMessage(PREFIX + ChatColor.RED + "Ce joueur n'est pas banni.");
			return;
		}

		EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());

		// Envoyer les Informations du Ban
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, new Locale("fr"));
		String modName = UUIDEndaFetcher.getPlayerName(banInfos.getModUUID());

		player.sendMessage("");
		player.sendMessage(" " + ChatColor.GRAY + "» " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Ban Infos"
				+ ChatColor.WHITE + "│ " + ChatColor.YELLOW + "" + ChatColor.BOLD + targetName);
		player.sendMessage("");
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD
				+ "Modérateur Responsable " + ChatColor.WHITE + "» " + ChatColor.AQUA + modName);
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "Raison "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + banInfos.getReason());
		if (banInfos.isPermanent())
			player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD
					+ "Durée de la Sanction " + ChatColor.WHITE + "» " + ChatColor.AQUA + "PERMANENT");
		else
			player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "Banni jusqu'au "
					+ ChatColor.WHITE + "» " + ChatColor.AQUA + dateFormat.format(banInfos.getExpiryDate()));
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "Date de la Sanction "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + dateFormat.format(banInfos.getDate()));
		if ((endaPlayer.getRank().getPower() >= Rank.ADMINISTRATOR.getPower())
				|| (endaPlayer.hasPermission(Permission.SUPER_MODO.getPermission()))) {
			player.sendMessage("");
			player.sendMessage(
					" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "Modérateur était en Jeu "
							+ ChatColor.WHITE + "» " + ChatColor.AQUA + (banInfos.isModPlayed() ? "Oui" : "Non"));
			player.sendMessage(
					" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "Le Ban est-il Hors-Ligne "
							+ ChatColor.WHITE + "» " + ChatColor.AQUA + (banInfos.isPlayerOnline() ? "Non" : "Oui"));
			player.sendMessage(" " + ChatColor.DARK_RED + "" + ChatColor.BOLD + "■ " + ChatColor.RED
					+ "Vous êtes sur un compte de Super-Modérateur.");
		}
		player.sendMessage("");
	}

	/**
	 * Message d'Aide de la Commande.
	 */
	private void sendHelp(CommandSender sender) {
		sender.sendMessage("");
		sender.sendMessage(" " + ChatColor.GRAY + "» " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Aide"
				+ ChatColor.WHITE + "│ " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Commande de Ban");
		sender.sendMessage("");
		sender.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/zban [player] "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Bannir un joueur du serveur.");
		sender.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/zunban [player] "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Dé-Bannir un joueur du serveur.");
		sender.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/zbaninfos [player] "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Voir les informations d'un ban.");
		sender.sendMessage("");
	}

	public class BanInventory implements Listener {

		private InventoryBuilder inventoryBuilder;

		private String inventoryName = "Ban";

		private Map<Player, String> targetBanMap = new HashMap<Player, String>();

		/**
		 * Inventaire : Ban
		 * 
		 * @param player
		 * @param target
		 */
		public BanInventory(Player player, String target) {

			this.inventoryName = "Ban - " + target;
			this.inventoryBuilder = new InventoryBuilder(inventoryName);
			this.inventoryBuilder.inventory.remove(player);
			this.targetBanMap.remove(player);
			this.targetBanMap.put(player, target);
			Bukkit.getPluginManager().registerEvents(this, EndariumBukkit.getPlugin());

			this.inventoryBuilder.addLine(new String[] { "", "", "", "", "", "", "", "", "" })
					.addLine(new String[] { "", "sanction_triche", "sanction_streamhack", "sanction_antijeu",
							"sanction_build", "sanction_troll", "", "sanction_penal", "" })
					.addLine(new String[] { "", "", "sanction_concours", "sanction_skinincorrect",
							"sanction_pseudoincorrect", "", "", "", "" })
					.addLine(new String[] { "", "", "", "", "", "", "", "", "" })
					.addLine(new String[] { "x", "x", "x", "x", "ban_target", "x", "x", "x", "ban_cancel" });

			// Footer du Menu de Ban
			this.inventoryBuilder.setItem("x",
					new ItemFactory(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14)).withName("§0").done());
			this.inventoryBuilder.setItem("ban_cancel",
					new ItemFactory(Material.REDSTONE).withName(ChatColor.RED + "" + ChatColor.UNDERLINE + "Annuler")
							.withLore(ChatColor.WHITE + "Cliquez pour " + ChatColor.RED + "annuler",
									ChatColor.WHITE + "la sanction.")
							.done());
			this.inventoryBuilder.setItem("ban_target", new HeadBuilder().setHead(target)
					.setName(ChatColor.GOLD + "" + ChatColor.UNDERLINE + target).build());

			// Items des différentes Sanctions
			this.inventoryBuilder.setItem("sanction_triche",
					this.getSanctionItem(Material.DIAMOND_SWORD, "Triche", "Permanent"));
			this.inventoryBuilder.setItem("sanction_streamhack",
					this.getSanctionItem(Material.EYE_OF_ENDER, "StreamHack", "15 Jours"));
			this.inventoryBuilder.setItem("sanction_antijeu",
					this.getSanctionItem(Material.BARRIER, "Anti-Jeux", "3 Jours"));
			this.inventoryBuilder.setItem("sanction_build",
					this.getSanctionItem(Material.WORKBENCH, "Contruction Incorrect", "3 Jours"));
			this.inventoryBuilder.setItem("sanction_troll",
					this.getSanctionItem(Material.ANVIL, "Troll/Comportement", "3 Jours"));

			this.inventoryBuilder.setItem("sanction_concours",
					this.getSanctionItem(Material.GOLD_INGOT, "Jeux Concours", "1 Jour"));
			this.inventoryBuilder.setItem("sanction_skinincorrect",
					this.getSanctionItem(Material.ARMOR_STAND, "Skin Incorrect", "7 Jours"));
			this.inventoryBuilder.setItem("sanction_pseudoincorrect",
					this.getSanctionItem(Material.PAPER, "Pseudo Incorrect", "7 Jours"));

			this.inventoryBuilder.setItem("sanction_penal",
					this.getSanctionItem(Material.NETHER_STAR, "Sanction Warn", "1 Jour"));

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
							ChatColor.WHITE + "de " + ChatColor.RED + "bannir " + ChatColor.WHITE + "un joueur.", "",
							ChatColor.GRAY + "Raison : " + ChatColor.AQUA + sanction,
							ChatColor.GRAY + "Durée : " + ChatColor.DARK_AQUA + delay, "",
							ChatColor.DARK_GREEN + "● " + ChatColor.GREEN + "Cliquez pour Sanctionner")
					.addFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_DESTROYS).done();
		}

		@EventHandler
		public void onInventoryClick(InventoryClickEvent event) {
			Player player = (Player) event.getWhoClicked();
			String targetPlayer = this.targetBanMap.get(player);
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
			case REDSTONE:
				player.closeInventory();
				player.sendMessage(PREFIX + ChatColor.WHITE + "Vous avez " + ChatColor.RED + "annulé " + ChatColor.WHITE
						+ "la sanction de : " + ChatColor.YELLOW + targetPlayer + ChatColor.WHITE + ".");
				break;

			// Ban | Raison : Triche
			case DIAMOND_SWORD:
				player.closeInventory();
				this.applyBan(player, targetPlayer, "Triche (HackedClient)", "(Permanent)", -1);
				break;

			// Ban | Raison : StreamHack
			case EYE_OF_ENDER:
				player.closeInventory();
				this.applyBan(player, targetPlayer, "StreamHack", "(15 Jours)", 15);
				break;

			// Ban | Raison : Anti-Jeux
			case BARRIER:
				player.closeInventory();
				this.applyBan(player, targetPlayer, "Anti-Jeux", "(3 Jours)", 3);
				break;

			// Ban | Raison : Construction Incorrect
			case WORKBENCH:
				player.closeInventory();
				this.applyBan(player, targetPlayer, "Construction Incorrect", "(3 Jours)", 3);
				break;

			// Ban | Raison : Troll/Comportement
			case ANVIL:
				player.closeInventory();
				this.applyBan(player, targetPlayer, "Troll/Comportement", "(3 Jours)", 3);
				break;

			// Ban | Raison : Jeux Concours
			case GOLD_INGOT:
				player.closeInventory();
				this.applyBan(player, targetPlayer, "Jeux Concours", "(1 Jour)", 1);
				break;

			// Ban | Raison : Skin Incorrect
			case ARMOR_STAND:
				player.closeInventory();
				this.applyBan(player, targetPlayer, "Skin Incorrect", "(7 Jours)", 7);
				break;

			// Ban | Raison : Pseudo Incorrect
			case PAPER:
				player.closeInventory();
				this.applyBan(player, targetPlayer, "Pseudo Incorrect", "(7 Jours)", 7);
				break;

			// Ban | Raison : Sanction Warn
			case NETHER_STAR:
				player.closeInventory();
				this.applyBan(player, targetPlayer, "Avertissement (discord.endarium.net)", "(1 Jour)", 1);
				break;

			default:
				break;
			}
		}

		/**
		 * Appliquer un Ban sur un Joueur.
		 * 
		 * @param player
		 * @param target
		 * @param reason
		 * @param delay
		 */
		private void applyBan(Player player, String target, String reason, String delay, int day) {

			// Appliquer le Ban sur la Base de Données
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy/ HH:mm:ss");
			LocalDateTime localDateTime = LocalDateTime.now();

			BanInfos banInfos;
			UUID uuidTarget = UUIDEndaFetcher.getPlayerUUID(target);
			String banId = (UUID.randomUUID().toString().replaceAll("-", "")).substring(1, 12);

			EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());
			boolean modPlayed = false;
			if ((EndariumAPI.getGameSetting().isGameServer()) && (!(endaPlayer.isSpectator())))
				modPlayed = true;

			if (day <= -1) {
				/* BAN : PERMANENT */
				banInfos = new BanInfos(uuidTarget, player.getUniqueId(), banId, reason, null, null, true, true,
						modPlayed, EndaPlayer.isConnected(uuidTarget));
				EndariumBukkit.getPlugin().getEndariumCommons().getEndariumEntities().getBanManager()
						.createBanPermanent(uuidTarget, banInfos);
			} else {
				/* BAN : TEMPORAIRE */
				banInfos = new BanInfos(uuidTarget, player.getUniqueId(), banId, reason, null, null, true, true,
						modPlayed, EndaPlayer.isConnected(uuidTarget));
				EndariumBukkit.getPlugin().getEndariumCommons().getEndariumEntities().getBanManager()
						.createBanTemporaire(uuidTarget, banInfos, day);
			}

			// Effectuer un Kick du Joueur
			String kickMessage = ChatColor.DARK_RED + "● " + ChatColor.RED + "Vous avez été banni d'Endarium"
					+ ChatColor.DARK_RED + " ●\n" + ChatColor.GRAY + "Raison : " + ChatColor.WHITE + reason + "\n"
					+ ChatColor.GRAY + "Durée : " + ChatColor.DARK_AQUA + delay + "\n§f\n" + ChatColor.DARK_GRAY
					+ "ID : #" + banInfos.getBanID().toString() + " - " + dateTimeFormatter.format(localDateTime);
			Player playerTarget = Bukkit.getPlayer(uuidTarget);
			if (playerTarget != null) {
				playerTarget.kickPlayer(kickMessage);
				Bukkit.getWorld(EndariumAPI.getGameSetting().getWorldName())
						.strikeLightningEffect(playerTarget.getLocation());
				Bukkit.broadcastMessage(Messages.ENDARIUM_PREFIX + ChatColor.RED + target + ChatColor.WHITE
						+ " vient d'être banni de votre serveur.");
			}

			// Envoyer un Message aux Modérateurs
			player.sendMessage(PREFIX + ChatColor.WHITE + "Vous venez de " + ChatColor.RED + "sanctionner"
					+ ChatColor.WHITE + " : " + ChatColor.YELLOW + target + ChatColor.WHITE + ", raison : "
					+ ChatColor.AQUA + reason + " " + ChatColor.DARK_AQUA + delay);
		}
	}
}