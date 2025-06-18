package net.endarium.api.minecraft.commands.mods;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import net.endarium.api.players.login.LoginManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.commands.Command;
import net.endarium.api.utils.commands.Command.SenderType;
import net.md_5.bungee.api.ChatColor;

public class KickCommand {

	private String PREFIX = ChatColor.GOLD + "[ZKick] ";

	@Command(name = { "zkick" }, minimumRank = Rank.HELPER, senderType = SenderType.ONLY_PLAYER)
	public void onCommandRankList(Player player, String[] args) {
		LoginManager loginManager = new LoginManager();
		if (!(loginManager.isLogged(player.getUniqueId()))) {
			return;
		}

		// Vérifier si la validité des Arguments
		if (args.length < 2) {
			this.sendHelp(player);
			return;
		}

		String targetName = args[0];
		Player target = Bukkit.getPlayer(targetName);

		// Vérifier si le Joueur est en Ligne
		if (target == null) {
			player.sendMessage(PREFIX + ChatColor.WHITE + "Le joueur '" + ChatColor.RED + targetName + ChatColor.WHITE
					+ "' n'est pas en ligne.");
			return;
		}

		EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());
		EndaPlayer targetEndaPlayer = EndaPlayer.get(target.getUniqueId());

		// Vérifier si TARGET != MODO
		if ((targetName.equalsIgnoreCase(player.getName()))
				|| (targetEndaPlayer.getRank().getPower() >= endaPlayer.getRank().getPower())) {
			player.sendMessage(PREFIX + ChatColor.RED + "Vous ne pouvez pas exclure ce joueur.");
			return;
		}

		// Récupérer la raison de Kick & Executer la Sanction
		String kickMessage = "";
		for (int i = 1; i < args.length; i++)
			kickMessage = kickMessage + " " + args[i];
		kickMessage.trim();

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy/ HH:mm:ss");
		LocalDateTime localDateTime = LocalDateTime.now();
		String kickId = (UUID.randomUUID().toString().replaceAll("-", "")).substring(1, 12);

		player.sendMessage(PREFIX + ChatColor.WHITE + "Vous venez d'exclure le joueur : " + ChatColor.YELLOW
				+ target.getName() + ChatColor.WHITE + ".");
		target.kickPlayer(ChatColor.DARK_RED + "● " + ChatColor.RED + "Vous avez été exclu de Endarium"
				+ ChatColor.DARK_RED + " ●\n" + ChatColor.GRAY + "Raison : " + ChatColor.WHITE + kickMessage + ".\n§f\n"
				+ ChatColor.DARK_GRAY + "ID : #" + kickId + " - " + dateTimeFormatter.format(localDateTime));
	}

	/**
	 * Message d'Aide de la Commande.
	 */
	private void sendHelp(CommandSender sender) {
		sender.sendMessage("");
		sender.sendMessage(" " + ChatColor.GRAY + "» " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Aide"
				+ ChatColor.WHITE + "│ " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Commande de Kick");
		sender.sendMessage("");
		sender.sendMessage(
				" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/zkick [player] [raison] "
						+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Exclure un joueur du serveur.");
		sender.sendMessage("");
	}
}