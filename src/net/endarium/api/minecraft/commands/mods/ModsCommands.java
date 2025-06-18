package net.endarium.api.minecraft.commands.mods;

import java.util.UUID;

import net.endarium.api.players.login.LoginManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.endarium.api.games.GameStatus;
import net.endarium.api.games.servers.CrystaliserAPI;
import net.endarium.api.games.servers.CrystaliserServerManager;
import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.EndaPlayerCache;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.players.rank.permissions.Permission;
import net.endarium.api.players.wallets.Currency;
import net.endarium.api.utils.EndariumAPI;
import net.endarium.api.utils.Messages;
import net.endarium.api.utils.builders.scoreboards.TeamTagsSign;
import net.endarium.api.utils.builders.titles.ActionBarBuilder;
import net.endarium.api.utils.commands.Command;
import net.endarium.api.utils.commands.Command.SenderType;
import net.endarium.api.utils.mojang.UUIDEndaFetcher;
import net.endarium.api.utils.tools.SoundUtils;
import net.md_5.bungee.api.ChatColor;

public class ModsCommands {

	private String PREFIX = Messages.ZMODS_PREFIX;

	@Command(name = { "zmod" }, minimumRank = Rank.MODERATOR, senderType = SenderType.ONLY_PLAYER)
	public void onCommandRankList(Player player, String[] args) {
		LoginManager loginManager = new LoginManager();
		if (!(loginManager.isLogged(player.getUniqueId()))) {
			return;
		}

		EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());

		// Bloquer le Gamemode en pleine Partie
		if ((EndariumAPI.getGameSetting().isGameServer()) && (!(endaPlayer.isSpectator()))) {
			player.sendMessage(PREFIX + ChatColor.RED
					+ "Vous ne pouvez pas activer votre Mode de Modération en tant que joueur dans une partie.");
			SoundUtils.sendSound(player, Sound.VILLAGER_NO);
			return;
		}

		// Mise en place du Mode de Modération
		if (!(endaPlayer.isModeModeration())) {

			// Activer le Mode de Modération
			player.sendMessage(PREFIX + ChatColor.WHITE + "Votre Mode Modération est maintenant " + ChatColor.GREEN
					+ "activé" + ChatColor.WHITE + ".");
			endaPlayer.setModeModeration(true);

			// Parametrer le Compte en Mode de Modération
			Bukkit.getOnlinePlayers().forEach(players -> {

				EndaPlayer endaPlayerOnline = EndaPlayer.get(players.getUniqueId());

				// Cacher le Modérateur des Autres Joueurs
				if ((players != player) && (!(endaPlayerOnline.isModeModeration()))) {
					players.hidePlayer(player);
				}

				// Afficher au Joueur les autres Modérateurs
				if ((players != player) && (endaPlayerOnline.isModeModeration()))
					player.showPlayer(players);

				// Annoncer aux Modérateur qu'il rejoint le ZMOD
				if (endaPlayerOnline.isModeModeration())
					players.sendMessage(ChatColor.BLUE + "[ZMOD][" + ChatColor.GREEN + "+" + ChatColor.BLUE + "] "
							+ player.getName());

			});

			// Appliquer le Tag de Modération
			TeamTagsSign.setNameTag(player, "§0", ChatColor.BLUE + "" + ChatColor.BOLD + "[ZMOD] " + ChatColor.BLUE);

		} else {

			// Désactiver le Mode de Modération
			Bukkit.getOnlinePlayers().forEach(players -> {

				EndaPlayer endaPlayerOnline = EndaPlayer.get(players.getUniqueId());

				if (players != player)
					players.showPlayer(player);

				// Cacher au Joueur les autres Modérateurs
				if ((players != player) && (endaPlayerOnline.isModeModeration()))
					player.hidePlayer(players);

				// Annoncer aux Modérateur qu'il quitte le ZMOD
				if (endaPlayerOnline.isModeModeration())
					players.sendMessage(ChatColor.BLUE + "[ZMOD][" + ChatColor.RED + "-" + ChatColor.BLUE + "] "
							+ player.getName());
			});

			// Parametrer le Compte en fonction de son Emplacement
			if ((EndariumAPI.getGameSetting().isGameServer()) && (endaPlayer.isSpectator())) {
				EndariumAPI.getGameSetting().getTeamManager().setSpectator(player);
				player.setMaxHealth(20.0d);
				player.setHealth(20.0d);
				player.setGameMode(GameMode.SPECTATOR);
				player.getInventory().clear();
			} else {
				TeamTagsSign.setNameTag(player, endaPlayer.getRank().getOrderCode() + endaPlayer.getRank().getName(),
						endaPlayer.getRank().getChatColor() + endaPlayer.getRank().getPrefix()
								+ Messages.getRankSpaceConvention(endaPlayer.getRank()));
			}

			player.sendMessage(PREFIX + ChatColor.WHITE + "Votre Mode Modération est maintenant " + ChatColor.RED
					+ "désactivé" + ChatColor.WHITE + ".");
			endaPlayer.setModeModeration(false);
		}
	}

	@Command(name = { "zfly" }, minimumRank = Rank.MODERATOR, senderType = SenderType.ONLY_PLAYER)
	public void onCommandFly(Player player, String[] args) {

		EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());

		// Mise en place du Mode de Fly
		if (endaPlayer.isModFly()) {
			endaPlayer.setModFly(false);
			player.setAllowFlight(false);
			player.setFlying(false);
			player.sendMessage(PREFIX + ChatColor.WHITE + "Votre Mode de Fly est maintenant " + ChatColor.RED
					+ "désactivé" + ChatColor.WHITE + ".");
			new ActionBarBuilder(ChatColor.WHITE + "Mode Fly : " + ChatColor.RED + "" + ChatColor.BOLD + "Désactivé")
					.sendTo(player);
		} else {
			endaPlayer.setModFly(true);
			player.setAllowFlight(true);
			player.setFlying(true);
			player.sendMessage(PREFIX + ChatColor.WHITE + "Votre Mode de Fly est maintenant " + ChatColor.GREEN
					+ "activé" + ChatColor.WHITE + ".");
			new ActionBarBuilder(ChatColor.WHITE + "Mode Fly : " + ChatColor.GREEN + "" + ChatColor.BOLD + "Activé")
					.sendTo(player);
		}
	}

	@Command(name = { "zgm", "zgamemode" }, minimumRank = Rank.MODERATOR, senderType = SenderType.ONLY_PLAYER)
	public void onCommandGamemode(Player player, String[] args) {
		EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());

		// Bloquer le Gamemode en pleine Partie
		if ((GameStatus.isStatus(GameStatus.GAME)) && (!(endaPlayer.isSpectator()))) {
			player.sendMessage(PREFIX + ChatColor.RED
					+ "Vous ne pouvez pas changer de Gamemode en pleine partie. C'est pas bien d'essayer de tricher ! :')");
			SoundUtils.sendSound(player, Sound.VILLAGER_NO);
			return;
		}

		// Bloquer le Gamemode si le joueur n'est pas en ZMOD
		if ((GameStatus.isStatus(GameStatus.GAME)) && (!(endaPlayer.isModeModeration()))) {
			player.sendMessage(PREFIX + ChatColor.RED
					+ "Vous ne pouvez pas vous mettre en Gamemode si vous n'êtes pas en Mode de Modération.");
			SoundUtils.sendSound(player, Sound.VILLAGER_NO);
			return;
		}

		// Vérifier si la validité des Arguments
		if (args.length != 1) {
			player.sendMessage("");
			player.sendMessage(" " + ChatColor.GRAY + "» " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Aide"
					+ ChatColor.WHITE + "│ " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Commande de Gamemode");
			player.sendMessage("");
			player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/zgm [0|1|2|3] "
					+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Changer de Gamemode.");
			player.sendMessage(ChatColor.GRAY + "0 = SURVIE | 1 = CREATIF | 2 = ADVENTURE | 3 = SPECTATEUR");
			player.sendMessage("");
			return;
		}

		String gamemode = args[0];

		// Mise en place du Gamemode
		switch (gamemode) {
		case "0":
			player.setGameMode(GameMode.SURVIVAL);
			new ActionBarBuilder(ChatColor.WHITE + "Gamemode : " + ChatColor.GOLD + "" + ChatColor.BOLD + "Survie")
					.sendTo(player);
			break;
		case "1":
			player.setGameMode(GameMode.CREATIVE);
			new ActionBarBuilder(ChatColor.WHITE + "Gamemode : " + ChatColor.GOLD + "" + ChatColor.BOLD + "Créatif")
					.sendTo(player);
			break;
		case "2":
			player.setGameMode(GameMode.ADVENTURE);
			new ActionBarBuilder(ChatColor.WHITE + "Gamemode : " + ChatColor.GOLD + "" + ChatColor.BOLD + "Adventure")
					.sendTo(player);
			break;
		case "3":
			player.setGameMode(GameMode.SPECTATOR);
			new ActionBarBuilder(ChatColor.WHITE + "Gamemode : " + ChatColor.GOLD + "" + ChatColor.BOLD + "Spectateur")
					.sendTo(player);
			break;
		default:
			player.sendMessage(PREFIX + ChatColor.RED + "Erreur : Le gamemode est incorrect, /zgm [0|1|2|3].");
			player.sendMessage(ChatColor.GRAY + "0 = SURVIE | 1 = CREATIF | 2 = ADVENTURE | 3 = SPECTATEUR");
			break;
		}
	}

	@Command(name = { "zreport", "zreports" }, minimumRank = Rank.MODERATOR, senderType = SenderType.ONLY_PLAYER)
	public void onCommandZReport(Player player, String[] args) {
		player.sendMessage(PREFIX + ChatColor.RED + "La commande est en cours de développement.");
	}

	@Command(name = { "ztp", "zteleport" }, minimumRank = Rank.MODERATOR, senderType = SenderType.ONLY_PLAYER)
	public void onCommandTeleport(Player player, String[] args) {

		// Vérifier si la validité des Arguments
		if ((args.length != 1) && (args.length != 2)) {
			player.sendMessage("");
			player.sendMessage(" " + ChatColor.GRAY + "» " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Aide"
					+ ChatColor.WHITE + "│ " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Commande de Téléportation");
			player.sendMessage("");
			player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/ztp [pseudo] "
					+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Se téléporter sur un Joueur.");
			player.sendMessage(
					" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/ztp [pseudo] [target] "
							+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Téléporter un Joueur sur un Joueur.");
			player.sendMessage(ChatColor.GRAY + "Information : La détection du serveur est automatique.");
			player.sendMessage("");
			return;
		}

		// Envoyer le Modérateur sur un autre Joueur
		if (args.length == 1) {

			// Détecter le Joueur
			String pseudoTarget = args[0];

			// Détecter si le Joueur veut se téléporter à Lui-Même
			if (pseudoTarget.equalsIgnoreCase(player.getName())) {
				player.sendMessage(PREFIX + ChatColor.RED
						+ "Tu veux te téléporter à toi-même ? Pas bête pour se retrouver soi-même dans ce vaste monde !");
				SoundUtils.sendSound(player, Sound.VILLAGER_NO);
				return;
			}

			// Détecter si le Joueur est sur le Serveur du Modérateur
			Player target = Bukkit.getPlayer(pseudoTarget);
			if (target != null) {
				player.teleport(target);
				SoundUtils.sendSound(player, Sound.ENDERMAN_TELEPORT);
				new ActionBarBuilder(ChatColor.WHITE + "Téléportation vers : " + ChatColor.GOLD + "" + ChatColor.BOLD
						+ target.getName()).sendTo(player);
				return;
			}

			// Détecter si le Joueur est sur un autre Serveur
			UUID uuidTarget = UUIDEndaFetcher.getPlayerUUID(pseudoTarget);
			if (!(EndaPlayer.isConnected(uuidTarget))) {
				player.sendMessage(PREFIX + ChatColor.RED + "Le joueur '" + pseudoTarget + "' n'est pas en ligne.");
				return;
			}

			// Effectuer la Téléportation à un Joueur
			EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());
			String serverName = EndaPlayerCache.get(uuidTarget).getServerName();
			player.sendMessage(PREFIX + ChatColor.WHITE + "Téléportation vers le serveur : " + ChatColor.YELLOW
					+ serverName + ChatColor.WHITE + ".");
			if (!(endaPlayer.isModeModeration())) {
				player.sendMessage(ChatColor.RED
						+ "Attention, vous n'êtes pas en Mode Modération, nous venons de vous l'activer automatiquement !");
				player.performCommand("zmod");
			}
			CrystaliserServerManager.sendToServer(player, serverName);
			return;
		}

		// Teleporter un Joueur sur un autre Joueur
		if (args.length == 2) {

			// Détecter les Joueurs
			String pseudo = args[0];
			String pseudoTarget = args[1];

			Player fromPlayer = Bukkit.getPlayer(pseudo);
			Player toPlayer = Bukkit.getPlayer(pseudoTarget);

			// Vérifier si les Joueurs sont en Lignes
			if ((fromPlayer == null) || (toPlayer == null)) {
				player.sendMessage(
						PREFIX + ChatColor.RED + "Erreur : Un des deux joueurs n'est pas sur votre serveur.");
				SoundUtils.sendSound(player, Sound.VILLAGER_NO);
				return;
			}

			// Vérifier si il veut pas Téléporter un Joueur sur Lui-Même
			if (pseudo.equalsIgnoreCase(pseudoTarget)) {
				player.sendMessage(PREFIX + ChatColor.RED
						+ "Tu veux téléporter un joueur sur lui-même ? Il ne risque pas de voir la différence, enfin je pense...");
				SoundUtils.sendSound(player, Sound.VILLAGER_NO);
				return;
			}

			// Effectuer la Téléportation à un Joueur
			fromPlayer.teleport(toPlayer);
			new ActionBarBuilder(ChatColor.WHITE + "Téléportation de : " + ChatColor.YELLOW + "" + ChatColor.BOLD
					+ fromPlayer.getName() + ChatColor.WHITE + "," + " vers : " + ChatColor.GOLD + "" + ChatColor.BOLD
					+ toPlayer.getName()).sendTo(player);
			return;
		}
	}

	@Command(name = { "zseen" }, minimumRank = Rank.MODERATOR, senderType = SenderType.ONLY_PLAYER)
	public void onCommandSeen(Player player, String[] args) {

		// Vérifier les Argumets
		if (args.length != 1) {
			player.sendMessage("");
			player.sendMessage(" " + ChatColor.GRAY + "» " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Aide"
					+ ChatColor.WHITE + "│ " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Inspection d'un Joueur");
			player.sendMessage("");
			player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/zseen [pseudo] "
					+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Voir les données du Joueur.");
			player.sendMessage("");
			return;
		}

		// Vérifier si le Target est Connecté
		String target = args[0];
		Player targetPlayer = Bukkit.getPlayer(target);
		if (targetPlayer == null) {
			player.sendMessage(PREFIX + ChatColor.RED + "Le joueur n'est pas en ligne.");
			return;
		}

		EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());
		EndaPlayer targetEndaPlayer = EndaPlayer.get(targetPlayer.getUniqueId());

		// Afficher les Informations
		player.sendMessage("");
		player.sendMessage(
				" " + ChatColor.GRAY + "» " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Aide" + ChatColor.WHITE + "│ "
						+ ChatColor.YELLOW + "" + ChatColor.BOLD + "Informations : " + targetPlayer.getName());
		player.sendMessage("");
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "Connexion (MS) "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + ((CraftPlayer) player).getHandle().ping + "ms");
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "Serveur "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + CrystaliserAPI.getEndaServer().getServerName());
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "Grade "
				+ ChatColor.WHITE + "» " + targetEndaPlayer.getRank().getChatColor() + "" + ChatColor.BOLD
				+ targetEndaPlayer.getRank().getName());
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "Coins "
				+ ChatColor.WHITE + "» " + Currency.COINS.getColor() + targetEndaPlayer.getCurrency(Currency.COINS)
				+ " " + Currency.COINS.getIcon());
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "Tokens "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + targetEndaPlayer.getCurrency(Currency.TOKENS) + " "
				+ Currency.TOKENS.getIcon());
		if ((endaPlayer.getRank().getPower() >= Rank.ADMINISTRATOR.getPower())
				|| (endaPlayer.hasPermission(Permission.SUPER_MODO.getPermission()))) {
			player.sendMessage(
					" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "Adresse IP " + ChatColor.WHITE
							+ "» " + ChatColor.AQUA + targetPlayer.getAddress().getAddress().getHostAddress());
			player.sendMessage(" " + ChatColor.DARK_RED + "" + ChatColor.BOLD + "■ " + ChatColor.RED
					+ "Vous êtes sur un compte de Super-Modérateur.");
		}
		player.sendMessage("");
	}

	@Command(name = { "zhelp" }, minimumRank = Rank.STAFF, senderType = SenderType.ONLY_PLAYER)
	public void onCommandZHelp(Player player, String[] args) {
		player.sendMessage("");
		player.sendMessage(" " + ChatColor.GRAY + "» " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Aide"
				+ ChatColor.WHITE + "│ " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Commande de Staff");
		player.sendMessage("");
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/zmod "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Activer/Désactiver le Mode Modération.");
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/zstart "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Forcer le démarrage d'une Partie.");
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/zfly "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Activer/Désactiver le Fly.");
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/zplay "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Afficher la liste des Serveurs.");
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/zseen [pseudo] "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Informations sur le Joueur.");
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/ztp [pseudo] "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Se téléporter à un Joueur.");
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/zgm [0|1|2|3] "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Changer de Gamemode.");
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/zmute [pseudo] "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Gestion du Mute d'un Joueur.");
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/zban [pseudo] "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Gestion du Bannissement d'un Joueur.");
		player.sendMessage(
				" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/zkick [player] [raison] "
						+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Exclure un joueur du serveur.");
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/slowchat "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Gérer le flood dans le chat.");
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/staffchat "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Discuter dans un chat réservé.");
		player.sendMessage("");
	}
}