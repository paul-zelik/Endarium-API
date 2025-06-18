package net.endarium.api.minecraft.commands;

import java.util.ArrayList;
import java.util.List;

import net.endarium.api.players.login.LoginManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import net.endarium.api.utils.Messages;
import net.endarium.api.utils.builders.JSONMessageBuilder;
import net.md_5.bungee.api.ChatColor;

public class PlayerProcessCommandListener implements Listener {

	private List<String> commands = new ArrayList<String>();

	/**
	 * Ajout des commmandes à bloquer.
	 */
	public PlayerProcessCommandListener() {
		commands.add("/minecraft");
		commands.add("/bukkit");
		commands.add("/me");
		commands.add("/pl");
		commands.add("/plugins");
		commands.add("/?");
		commands.add("/ver");
		commands.add("/version");
		commands.add("/plugin");
		commands.add("/ban-ip");
		commands.add("/banlist");
		commands.add("/op");
		commands.add("/scoreboard");
		commands.add("/seed");
		commands.add("/restart");
		commands.add("/viaver");
		commands.add("/viaversion");
		commands.add("exploitfixer ");
		commands.add("/bungee");
		commands.add("/icanhasbukkit");
		commands.add("/me");
		commands.add("/a");
		commands.add("/gc");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

		Player player = event.getPlayer();
		String message = event.getMessage();
		String[] args = message.split(" ");

		LoginManager loginManager = new LoginManager();

		if (loginManager.isLogged(player.getUniqueId())) {

			// Sécurité sur les Commandes V1.0
			if (args[0].contains(":")) {
				player.sendMessage(Messages.UNKNOW_COMMAND);
				event.setCancelled(true);
				return;
			}

			// Sécurité sur les Commandes V2.0
			for (String commandLock : commands) {
				if (args[0].equalsIgnoreCase(commandLock)) {
					event.setCancelled(true);
					player.sendMessage(Messages.UNKNOW_COMMAND);
					return;
				}
			}

			// Gestion des Commandes Existantes
			switch (args[0]) {
				case "/help":
					player.sendMessage("");
					player.sendMessage(" " + ChatColor.GRAY + "» " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Aide"
							+ ChatColor.WHITE + "│ " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Endarium");
					player.sendMessage("");
					player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/hub "
							+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Retourner au hub.");
					player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/spawn "
							+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Se téléporter au spawn.");
					player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/replay "
							+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Rejouer une partie à votre élimination.");
					player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/msg "
							+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Discuter en messages privés.");
					player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/friend "
							+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Gérer ses amis.");
					player.sendMessage("");
					JSONMessageBuilder jmDiscord = new JSONMessageBuilder();
					jmDiscord
							.newJComp(ChatColor.DARK_AQUA + " [" + ChatColor.AQUA + "" + ChatColor.BOLD + "Discord"
									+ ChatColor.DARK_AQUA + "] " + ChatColor.YELLOW + "Rendez-vous sur notre Discord : "
									+ ChatColor.AQUA + "" + ChatColor.BOLD + "gg/Endarium")
							.addHoverText(ChatColor.GRAY + "Rejoignez-nous sur notre Discord.")
							.addURL("https://discord.endarium.net").build(jmDiscord);
					jmDiscord.send(player);
					player.sendMessage("");
					event.setCancelled(true);
					break;

				default:
					break;
			}
		} else {
			player.sendMessage(Messages.ENDARIUM_PREFIX + org.bukkit.ChatColor.YELLOW + "Tu dois te login.");
		}
	}
}