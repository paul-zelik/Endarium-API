package net.endarium.api.minecraft.commands.administrator;

import java.util.UUID;

import net.endarium.api.players.login.LoginManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.endarium.api.EndariumCommons;
import net.endarium.api.minecraft.EndariumBukkit;
import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.Messages;
import net.endarium.api.utils.commands.Command;
import net.endarium.api.utils.commands.Command.SenderType;
import net.endarium.api.utils.mojang.UUIDEndaFetcher;
import net.md_5.bungee.api.ChatColor;

public class RankCommand {

	private String PREFIX = ChatColor.GOLD + "[Grade] ";

	@Command(name = { "rank", "grade" }, permission = {"endarium.rank"}, minimumRank = Rank.ADMINISTRATOR, senderType = SenderType.PLAYER_AND_CONSOLE)
	public void onCommand(CommandSender sender, String[] args) {
		LoginManager loginManager = new LoginManager();
		if (sender instanceof Player) {
			if (!(loginManager.isLogged(((Player) sender).getUniqueId()))) {
				return;
			}
		}
		// Vérifier si la validité des Arguments
		if (args.length != 2 && args.length != 3) {
			this.sendHelp(sender);
			return;
		}

		// Vérifier si le joueur existe sur Mojang
		String targetName = args[0];
		UUID targetUUID = UUIDEndaFetcher.getPlayerUUID(targetName);
		if (targetUUID == null) {
			sender.sendMessage(
					PREFIX + ChatColor.RED + "Le compte '" + targetName + "' n'est pas un compte Mojang valide.");
			return;
		}

		// Détecter le grade demandé pour l'Admin
		Rank targetRank = EndariumCommons.getInstance().getEndariumEntities().getRankManager().getRank(targetUUID,
				true);
		if (targetRank == null) {
			sender.sendMessage(
					PREFIX + ChatColor.RED + "Le joueur '" + targetName + "' ne possède pas de compte sur Endarium.");
			return;
		}

		// Verifier si le Grade est Existant
		Rank rank = Rank.getUserRank(args[1], true);
		if (rank == null) {
			sender.sendMessage(PREFIX + ChatColor.RED + "Le grade est introuvable, utilisez : /ranklist.");
			return;
		}

		// Verifier si le joueur possède le Grade
		if (targetRank.equals(rank)) {
			sender.sendMessage(PREFIX + ChatColor.RED + "Le joueur '" + targetName + "' possède déjà le grade : "
					+ targetRank.getChatColor() + targetRank.getName() + ChatColor.RED + ".");
			return;
		}

		// Vérifier que le Sender peut appliquer ce Grade
		if (sender instanceof Player) {
			Player player = (Player) sender;
			EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());
			if (endaPlayer.getRank().getPower() < rank.getPower()) {
				player.sendMessage(PREFIX + ChatColor.RED + "Vous n'êtes pas autorisé à appliquer ce grade.");
				return;
			}
		}

		// Vérifier si on veut appliquer un délai sur le Grade
		if (args.length == 3) {
			int minutesDelay = 0;
			try {
				String valueDelay = args[2];
				minutesDelay = Integer.parseInt(valueDelay);
			} catch (NumberFormatException exception) {
				sender.sendMessage(PREFIX + ChatColor.RED + "Vous devez entrer une valeur de délai numérique.");
				return;
			}

			// Vérifier la validité du délai du Grade
			if (minutesDelay <= 1) {
				sender.sendMessage(PREFIX + ChatColor.RED + "Le délai doit être de minimum 2 minutes.");
				return;
			}

			// Application d'un Grade (Temporaire)
			EndariumCommons.getInstance().getEndariumEntities().getRankManager().setRankTemporary(targetUUID, rank,
					minutesDelay);
			sender.sendMessage(PREFIX + ChatColor.YELLOW + targetName + ChatColor.WHITE
					+ " possède maintenant le grade " + rank.getChatColor() + rank.getName() + " " + targetName
					+ ChatColor.WHITE + ", pendant une durée de " + ChatColor.LIGHT_PURPLE + minutesDelay + " minutes"
					+ ChatColor.WHITE + ".");
			EndariumBukkit.getPlugin().getRankChangeChannel().applyChangeRank(targetUUID, rank);
			return;
		}

		// Application d'un Grade (Permanent)
		EndariumCommons.getInstance().getEndariumEntities().getRankManager().setRank(targetUUID, rank);
		sender.sendMessage(PREFIX + ChatColor.YELLOW + targetName + ChatColor.WHITE + " possède maintenant le grade "
				+ rank.getChatColor() + rank.getName() + " " + targetName + ChatColor.WHITE + ".");
		EndariumBukkit.getPlugin().getRankChangeChannel().applyChangeRank(targetUUID, rank);
	}

	@Command(name = { "ranklist" }, minimumRank = Rank.ADMINISTRATOR, senderType = SenderType.PLAYER_AND_CONSOLE)
	public void onCommandRankList(CommandSender sender, String[] args) {
		sender.sendMessage("");
		sender.sendMessage(Messages.centerText(
				ChatColor.DARK_RED + "▸ " + ChatColor.RED + "Listes des Grades" + ChatColor.DARK_RED + " ◂"));
		sender.sendMessage("");
		for (Rank rankList : Rank.values()) {
			sender.sendMessage(" " + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "◼ " + rankList.getChatColor()
					+ rankList.getName() + ChatColor.DARK_GRAY + " ➠ " + rankList.getChatColor() + rankList.getPrefix()
					+ ChatColor.DARK_GRAY + " ➠ " + ChatColor.GRAY + "" + ChatColor.ITALIC
					+ rankList.getIdentificatorName());

		}
		sender.sendMessage("");
	}

	/**
	 * Message d'Aide de la Commande.
	 */
	private void sendHelp(CommandSender sender) {
		sender.sendMessage("");
		sender.sendMessage(" " + ChatColor.GRAY + "» " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Aide"
				+ ChatColor.WHITE + "│ " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Commande des Grades");
		sender.sendMessage("");
		sender.sendMessage(
				" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/rank [player] [grade] "
						+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Changer le Grade d'un Joueur.");
		sender.sendMessage(
				" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/rank [player] [grade] [minutes] "
						+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Changer temporairement le Grade d'un Joueur.");
		sender.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/ranklist "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Afficher la liste des Grades.");
		sender.sendMessage("");
	}
}