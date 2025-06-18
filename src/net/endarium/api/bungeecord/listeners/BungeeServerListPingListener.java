package net.endarium.api.bungeecord.listeners;

import net.endarium.api.EndariumCommons;
import net.endarium.api.bungeecord.EndariumBungeeCord;
import net.endarium.api.utils.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class BungeeServerListPingListener implements Listener {

	private String line1, line2;

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	public void onProxyPing(final ProxyPingEvent event) {

		final ServerPing serverPing = event.getResponse();
		if (serverPing == null)
			return;

		int slots = EndariumBungeeCord.getInstance().getSlots();

		// Définir les lignes du MOTD
		line1 = Messages.centerText(ChatColor.GOLD + "" + ChatColor.BOLD + "Endarium" + ChatColor.WHITE + "│ "
				+ ChatColor.AQUA + "Serveur Mini-Jeux " + ChatColor.GREEN + "1.8 " + ChatColor.WHITE + "➡ "
				+ ChatColor.GREEN + "1.20");

		line2 = "";
		if (EndariumCommons.getInstance().isDeveloper())
			line2 = Messages.centerText(ChatColor.DARK_RED + "" + ChatColor.BOLD + "● " + ChatColor.RED + "Ouverture du Royaume le 24/02/2024 à 16h"
					+ ChatColor.DARK_RED + "" + ChatColor.BOLD + " ●");
		else
			line2 = Messages.centerText(ChatColor.GOLD + "" + ChatColor.BOLD + "● " + ChatColor.YELLOW
					+ "Ouvert en version 1.0" + ChatColor.GOLD + "" + ChatColor.BOLD + " ●");

		// Appliquer les informations sur le Ping
		serverPing.setDescription(line1 + "\n" + line2);
		serverPing.setPlayers(new ServerPing.Players(slots, EndariumBungeeCord.getRedisBungeeAPI().getPlayerCount(),
				event.getResponse().getPlayers().getSample()));

		// Serveur est en Maintenance
		if (EndariumBungeeCord.getInstance().getEndariumCommons().isDeveloper())
			serverPing.setVersion(new ServerPing.Protocol(ChatColor.DARK_RED + "Fermé" + ChatColor.DARK_GRAY + " - "
					+ ChatColor.GRAY + EndariumBungeeCord.getRedisBungeeAPI().getPlayerCount() + ChatColor.DARK_GRAY
					+ "/" + ChatColor.GRAY + slots, 4));

		event.setResponse(serverPing);
	}
}