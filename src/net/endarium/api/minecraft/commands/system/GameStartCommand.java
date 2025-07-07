package net.endarium.api.minecraft.commands.system;

import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.login.LoginManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.endarium.api.games.GameLobbyTask;
import net.endarium.api.games.GameStatus;
import net.endarium.api.games.servers.CrystaliserAPI;
import net.endarium.api.minecraft.EndariumBukkit;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.EndariumAPI;
import net.endarium.api.utils.Messages;
import net.endarium.api.utils.commands.Command;
import net.endarium.api.utils.commands.Command.SenderType;
import net.endarium.crystaliser.servers.EndaServer;
import net.endarium.crystaliser.servers.ServerType;
import net.md_5.bungee.api.ChatColor;

public class GameStartCommand {

	@Command(name = { "zstart", "zgamestart" }, permission = {
			"endarium.command.gamestart" }, minimumRank = Rank.STAFF, senderType = SenderType.ONLY_PLAYER)
	public void onCommandGameStart(Player player, String[] args) {

		EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());
		if (!(endaPlayer.isLogged())) {
			return;
		}

		// Vérifier si le Joueur à la Permission
		EndaServer endaServer = CrystaliserAPI.getEndaServer();
		if (endaServer == null)
			return;
		if ((endaServer.getServerType().equals(ServerType.HOST))
				&& (!(endaServer.getHostUUID().equals(player.getUniqueId())))) {
			player.sendMessage(Messages.UNKNOW_COMMAND);
			return;
		} else if (!(endaServer.isGameServer())) {
			player.sendMessage(Messages.UNKNOW_COMMAND);
			return;
		}

		// Vérifier si la partie à déjà Commencé
		if (!(GameStatus.isStatus(GameStatus.LOBBY))) {
			player.sendMessage(Messages.ENDARIUM_PREFIX + ChatColor.RED + "La partie est déjà en cours...");
			return;
		}

		// Manque de Joueur pour Démarrer
		if (Bukkit.getOnlinePlayers().size() <= 1) {
			player.sendMessage(Messages.ENDARIUM_PREFIX + ChatColor.RED + "Impossible de démarrer la partie.");
			return;
		}

		// Gestion du démarrage dans le Host
		if ((endaServer.getServerType().equals(ServerType.HOST)) && (!(GameLobbyTask.isStarted))) {
			this.startGameAction(player);
			return;
		}

		// Vérifier si le Timer à déjà été lancé
		if (GameLobbyTask.isStarted) {
			if (GameLobbyTask.isForced) {
				player.sendMessage(Messages.ENDARIUM_PREFIX + ChatColor.RED + "La démarrage à déjà été forcé.");
				return;
			} else {
				Bukkit.broadcastMessage(EndariumAPI.getGameSetting().getGamePrefix() + ChatColor.GOLD + player.getName()
						+ ChatColor.WHITE + " vient d'accélerer le démarrage de la partie.");
				player.sendMessage(Messages.ENDARIUM_PREFIX + ChatColor.WHITE + "Vous venez d'" + ChatColor.RED
						+ "accélérer" + ChatColor.WHITE + " le démarrage de la partie...");
				GameLobbyTask.setLobbyTimer(0);
				GameLobbyTask.setForced(true);
				return;
			}
		}

		// Forcer le démarrage de la Partie
		this.startGameAction(player);
		return;
	}

	/**
	 * Lancement de la Partie.
	 * 
	 * @param player
	 */
	private void startGameAction(Player player) {
		Bukkit.broadcastMessage(EndariumAPI.getGameSetting().getGamePrefix() + ChatColor.GOLD + player.getName()
				+ ChatColor.WHITE + " vient d'accélerer le démarrage de la partie.");
		player.sendMessage(Messages.ENDARIUM_PREFIX + ChatColor.WHITE + "Vous venez de " + ChatColor.RED + "lancer"
				+ ChatColor.WHITE + " le démarrage de la partie...");
		new GameLobbyTask(EndariumAPI.getGameSetting().getGameInterface()).runTaskTimer(EndariumBukkit.getPlugin(), 0L,
				20L);
		GameLobbyTask.setLobbyTimer(10);
		GameLobbyTask.setStarted(true);
		GameLobbyTask.setForced(true);
	}
}