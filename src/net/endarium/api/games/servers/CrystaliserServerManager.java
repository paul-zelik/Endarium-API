package net.endarium.api.games.servers;

import java.util.List;
import java.util.Random;

import net.endarium.api.players.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.endarium.api.games.GameStatus;
import net.endarium.api.minecraft.EndariumBukkit;
import net.endarium.api.utils.Messages;
import net.endarium.api.utils.tools.SoundUtils;
import net.endarium.crystaliser.servers.EndaServer;
import net.endarium.crystaliser.servers.GameType;
import net.endarium.crystaliser.servers.ServerStatus;
import net.md_5.bungee.api.ChatColor;

public class CrystaliserServerManager {

	/**
	 * Envoyer un Joueur sur un Hub.
	 * 
	 * @param player
	 * @param kick
	 */
	public static void sendPlayerToHub(Player player, boolean kick) {

		// Vérifier si le Joueur est déjà sur un Hub
		if (CrystaliserAPI.getEndaServer().getGameType().equals(GameType.HUB)) {
			player.sendMessage(Messages.ENDARIUM_PREFIX + ChatColor.RED + "Vous êtes déjà connecté sur un hub.");
			return;
		}

		// Vérification si il existe des Hubs
		List<EndaServer> hubServerList = CrystaliserAPI.getEndaServerGameTypeJoinableList(GameType.HUB);
		if ((hubServerList.isEmpty()) || (hubServerList.size() == 0)) {
			if (kick)
				player.kickPlayer(Messages.ENDARIUM_PREFIX + ChatColor.RED + "Impossible de trouver un Hub...");
			else
				player.sendMessage(Messages.ENDARIUM_PREFIX + ChatColor.RED
						+ "Nous ne parvenons pas à trouver un Hub disponible...");
			return;
		}

		// Téléporter le Joueur sur un Hub
		EndaServer endaServer = hubServerList.get(new Random().nextInt(hubServerList.size()));
		player.sendMessage(Messages.ENDARIUM_PREFIX + ChatColor.GREEN + "Téléportation en cours vers un Hub...");
		CrystaliserServerManager.sendToServer(player, endaServer.getServerName());
	}

	/**
	 * Envoyer un Joueur sur un serveur de Jeu.
	 * 
	 * @param player
	 * @param gameType
	 */
	public static void sendPlayerToGame(Player player, GameType gameType) {

		// Vérifier la disponibilité
		List<EndaServer> endaServersGame = CrystaliserAPI.getEndaServerGameTypeJoinableList(gameType);
		if ((endaServersGame == null) || (endaServersGame.isEmpty()) || (endaServersGame.size() <= 0)) {
			// TODO : File d'attente
			SoundUtils.sendSound(player, Sound.VILLAGER_NO);
			player.sendMessage(Messages.ENDARIUM_PREFIX + ChatColor.GRAY + "Impossible de trouver un serveur "
					+ gameType.getDisplayName() + ". " + ChatColor.AQUA
					+ "Le serveur est en cours de démarrage, veuillez patienter...");
			return;
		}

		// Téléportation d'un Joueur
		EndaServer endaServerRandom = endaServersGame.get(new Random().nextInt(endaServersGame.size()));
		for (EndaServer endaServer : endaServersGame)
			if ((endaServer != null) && (endaServerRandom != null) && (endaServer != endaServerRandom)
					&& (endaServer.getCurrentPlayers() > endaServerRandom.getCurrentPlayers()))
				endaServerRandom = endaServer;
		player.sendMessage(Messages.ENDARIUM_PREFIX + ChatColor.WHITE + ChatColor.WHITE
				+ "Téléportation en cours vers : " + ChatColor.AQUA + endaServerRandom.getServerName());
		CrystaliserServerManager.sendToServer(player, endaServerRandom.getServerName());

	}

	/**
	 * Gestion la fin de la Partie et télépartion des Joueurs.
	 * 
	 * @param teleportDelay
	 * @param commandStop
	 */
	public static void manageGameEndReboot(int teleportDelay) {
		try {

			GameStatus.setStatus(GameStatus.FINISH);
			CrystaliserAPI.setServerStatus(ServerStatus.REBOOT);
			CrystaliserAPI.getEndaServer().setJoinable(false);
			CrystaliserAPI.getEndaServer().setSpectator(false);
			CrystaliserAPI.getEndaServer().setWhitelist(true);
			Bukkit.getServer().setWhitelist(true);

			Bukkit.getScheduler().runTaskLaterAsynchronously(EndariumBukkit.getPlugin(), new Runnable() {
				@Override
				public void run() {
					Bukkit.getOnlinePlayers()
							.forEach(playerOnline -> CrystaliserServerManager.sendPlayerToHub(playerOnline, false));
					Bukkit.getScheduler().runTaskLater(EndariumBukkit.getPlugin(), new Runnable() {
						@Override
						public void run() {
							Bukkit.getServer().shutdown();
						}
					}, 15 * 20L);
				}
			}, teleportDelay * 20L);

		} catch (Exception exception) {
			Bukkit.getServer().shutdown();
		}
	}

	/**
	 * Téléporter un Joueur sur un Serveur via BungeeCord.
	 * 
	 * @param player
	 * @param serverName
	 */
	public static void sendToServer(Player player, String serverName) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(serverName);
		player.sendPluginMessage(EndariumBukkit.getPlugin(), "BungeeCord", out.toByteArray());
	}
}