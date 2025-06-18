package net.endarium.api.bungeecord.commands.staff;

import net.endarium.api.bungeecord.EndariumBungeeCord;
import net.endarium.api.bungeecord.channels.RedisBungeeChannel;
import net.endarium.api.players.EndaPlayer;
import net.endarium.api.utils.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class StaffChatCommand extends Command {

	private String PREFIX = ChatColor.GOLD + "[StaffChat] ";

	/**
	 * Commande de gestion du StaffChat.
	 */
	public StaffChatCommand() {
		super("staffchat", null, new String[] { "sc" });
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender sender, String[] args) {

		if (!(sender instanceof ProxiedPlayer))
			return;

		ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
		EndaPlayer endaPlayer = EndaPlayer.get(proxiedPlayer.getUniqueId());

		if (endaPlayer == null)
			return;

		if (!(endaPlayer.isStaff())) {
			proxiedPlayer.sendMessage(Messages.NO_PERMISSION);
			return;
		}

		if (args.length == 0) {
			this.sendHelp(proxiedPlayer);
			return;
		}

		// Gérer les Notifications du StaffChat
		if ((args.length == 1) && (args[0].equalsIgnoreCase("toggle"))) {
			boolean staffchatState = !endaPlayer.isStaffChat();
			endaPlayer.setStaffChat(staffchatState);
			proxiedPlayer.sendMessage(PREFIX + ChatColor.WHITE + "Votre StaffChat est maintenant : "
					+ (staffchatState ? (ChatColor.GREEN + "activé") : (ChatColor.RED + "désactivé")) + ChatColor.WHITE
					+ ".");
			return;
		}

		// Vérifier si son StaffChat est Actif
		if (!(endaPlayer.isStaffChat())) {
			proxiedPlayer.sendMessage(PREFIX + ChatColor.RED + "Vous devez activer votre StaffChat.");
			return;
		}

		// Envoyer le message au Staff
		String message = "";
		for (int i = 0; i < args.length; i++)
			message = message + " " + args[i];
		message = message.trim();
		String staffMessage = ChatColor.DARK_AQUA + "(Staff : " + endaPlayer.getRank().getChatColor()
				+ proxiedPlayer.getName() + ChatColor.DARK_AQUA + ") : " + ChatColor.WHITE + message;

		EndariumBungeeCord.getRedisBungeeAPI().sendChannelMessage(RedisBungeeChannel.CHANNEL_STAFFCHAT.getName(),
				staffMessage);
	}

	@SuppressWarnings("deprecation")
	private void sendHelp(ProxiedPlayer proxiedPlayer) {
		proxiedPlayer.sendMessage("");
		proxiedPlayer.sendMessage(" " + ChatColor.GRAY + "» " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Aide"
				+ ChatColor.WHITE + "│ " + ChatColor.YELLOW + "" + ChatColor.BOLD + "(t)Chat de Staff");
		proxiedPlayer.sendMessage("");
		proxiedPlayer.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD
				+ "/staffchat [message] " + ChatColor.WHITE + "» " + ChatColor.AQUA + "Envoyer un message au Staff.");
		proxiedPlayer.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD
				+ "/staffchat toggle " + ChatColor.WHITE + "» " + ChatColor.AQUA + "Gérer la réception du StaffChat.");
		proxiedPlayer.sendMessage("");
	}
}