package net.endarium.api.bungeecord.listeners;

import net.endarium.api.utils.Messages;
import net.endarium.crystaliser.servers.MapInfos;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 * Gestion du balancing des Hubs au Login.
 */
public class BungeeBalancingHubListener implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	public void onServerConnect(ServerConnectEvent event) {

		// Vérifier si la connexion provient d'un BungeeCord
		if ((event.getTarget() == null) || (!(event.getTarget().getName().equalsIgnoreCase("minecraft")))
				|| (event.getPlayer() == null))
			return;

		// Téléportation vers un Hub
		ServerInfo randomHub = this.getRandomHubLogin();
		if (randomHub != null) {
			ProxiedPlayer proxiedPlayer = event.getPlayer();
			proxiedPlayer.sendMessage("");
			proxiedPlayer.sendMessage("");
			proxiedPlayer.sendMessage("");
			proxiedPlayer.sendMessage("");
			proxiedPlayer.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Accueil" + ChatColor.WHITE + "│ "
					+ ChatColor.GREEN + "Bienvenue sur Endarium, " + ChatColor.AQUA + "" + ChatColor.BOLD
					+ proxiedPlayer.getName() + ChatColor.GREEN + ".");
			proxiedPlayer.sendMessage("");
			proxiedPlayer.sendMessage(ChatColor.YELLOW + "Vous voilà connecté sur le serveur " + ChatColor.GOLD
					+ "Endarium" + ChatColor.YELLOW
					+ ". Tous les jeux disponibles sont accessibles via le menu principal, présent sous forme de "
					+ ChatColor.GOLD + "" + ChatColor.BOLD + "boussole dans votre inventaire" + ChatColor.YELLOW + ".");
			proxiedPlayer.sendMessage("");
			proxiedPlayer.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "" + ChatColor.BOLD + "!"
					+ ChatColor.WHITE + "] Le serveur est actuellement en version " + ChatColor.RED + ""
					+ ChatColor.BOLD + "Bêta PUBLIQUE" + ChatColor.WHITE + " !");
			proxiedPlayer.sendMessage("");
			event.setTarget(randomHub);

		} else {
			event.setCancelled(true);
			event.getPlayer().disconnect(Messages.ENDARIUM_PREFIX + ChatColor.RED
					+ "Impossible de trouver un Hub !\n§c\nPatienter pendant le démarrage d'un Hub...");
			return;
		}
	}

	/**
	 * Récupérer un Hub Aléatoire.
	 * 
	 * @return
	 */
	private ServerInfo getRandomHubLogin() {
		ServerInfo hubServer = null;
		for (ServerInfo servers : ProxyServer.getInstance().getServers().values()) {
			if (servers == null)
				continue;
			if (!(servers.getName().startsWith("hub")))
				continue;
			if (hubServer == null && servers.getPlayers().size() < MapInfos.HUB.getMaxPlayers()) {
				hubServer = servers;
				break;
			}
		}
		return hubServer;
	}
}