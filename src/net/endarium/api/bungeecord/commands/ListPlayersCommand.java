package net.endarium.api.bungeecord.commands;

import net.endarium.api.bungeecord.EndariumBungeeCord;
import net.endarium.api.utils.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ListPlayersCommand extends Command {

	public ListPlayersCommand() {
		super("list", null, new String[] { "liste", "playercount" });
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender sender, String[] args) {
		sender.sendMessage(Messages.ENDARIUM_PREFIX + ChatColor.YELLOW + "Il y a actuellement " + ChatColor.GREEN
				+ EndariumBungeeCord.getRedisBungeeAPI().getPlayerCount() + " joueur(s)" + ChatColor.YELLOW
				+ " en ligne.");
	}
}