package net.endarium.api.minecraft.commands.mods;

import net.endarium.api.players.login.LoginManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import net.endarium.api.minecraft.EndariumBukkit;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.commands.Command;
import net.endarium.api.utils.commands.Command.SenderType;
import net.md_5.bungee.api.ChatColor;

public class SlowChatCommand {

	public static String PREFIX = ChatColor.GOLD + "[SlowChat] ";

	public static int currentSlow = 0;
	private static BukkitTask bukkitTask;

	@Command(name = { "slowchat" }, minimumRank = Rank.HELPER, senderType = SenderType.ONLY_PLAYER)
	public void onCommand(Player player, String[] args) {

		LoginManager loginManager = new LoginManager();
		if (!(loginManager.isLogged(player.getUniqueId()))) {
			return;
		}

		// Vérifier si les arguments sont valides
		if ((args.length == 0) || (args.length > 1)) {
			this.sendHelp(player);
			return;
		}

		// Désactiver le SlowChat
		if (args[0].equalsIgnoreCase("off")) {
			if (currentSlow == 0) {
				player.sendMessage(PREFIX + ChatColor.RED + "Le mode ralenti n'est pas activé.");
				return;
			}
			if (bukkitTask != null)
				bukkitTask.cancel();
			Bukkit.broadcastMessage(PREFIX + ChatColor.GREEN + "Le (t)chat n'est plus ralenti.");
			currentSlow = 0;
			return;
		}

		// Activer le SlowChat
		try {
			int time = Integer.parseInt(args[0]);
			if ((time < 0) || (time > 15)) {
				player.sendMessage(
						PREFIX + ChatColor.RED + "Erreur : La valeur doit être comprise entre 0 et 15 seconde(s).");
				return;
			}
			currentSlow = time;
			Bukkit.broadcastMessage(PREFIX + ChatColor.GREEN + "Le (t)chat a été ralenti." + ChatColor.WHITE
					+ " Vous pouvez envoyer un message toutes les " + ChatColor.RED + currentSlow + " seconde(s)"
					+ ChatColor.WHITE + ".");
			runSlowChatOffTask();
		} catch (Exception e) {
			player.sendMessage(PREFIX + ChatColor.RED
					+ "Erreur : Vous devez utiliser une valeur numérique. En cas de problèmes veuillez contacter un Owner.");
			return;
		}
	}

	/**
	 * Sécurité pour couper automatiquement le SlowChat!
	 */
	public void runSlowChatOffTask() {
		if (bukkitTask != null)
			bukkitTask.cancel();
		bukkitTask = Bukkit.getScheduler().runTaskLater(EndariumBukkit.getPlugin(), new Runnable() {
			@Override
			public void run() {
				Bukkit.broadcastMessage(PREFIX + ChatColor.GREEN + "Le (t)chat n'est plus ralenti.");
				currentSlow = 0;
				return;
			}
		}, 20 * 60 * 10);
	}

	/**
	 * Message d'Aide de la Commande.
	 */
	private void sendHelp(CommandSender sender) {
		sender.sendMessage("");
		sender.sendMessage(" " + ChatColor.GRAY + "» " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Aide"
				+ ChatColor.WHITE + "│ " + ChatColor.YELLOW + "" + ChatColor.BOLD + "SlowChat Command");
		sender.sendMessage("");
		sender.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/slowchat [duree] "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Ralentir le (t)chat du serveur.");
		sender.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/slowchat off "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Rétablir le (t)chat.");
		sender.sendMessage("");
	}
}