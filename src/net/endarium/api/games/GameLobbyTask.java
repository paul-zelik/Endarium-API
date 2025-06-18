package net.endarium.api.games;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import net.endarium.api.games.servers.CrystaliserAPI;
import net.endarium.api.players.language.Languages;
import net.endarium.api.utils.EndariumAPI;
import net.endarium.api.utils.LangMessages;
import net.endarium.api.utils.tools.SoundUtils;
import net.endarium.crystaliser.servers.ServerStatus;
import net.md_5.bungee.api.ChatColor;

public class GameLobbyTask extends BukkitRunnable {

	public static Integer lobbyTimer = 60;
	public static boolean isStarted = false;
	public static boolean isForced = false;

	private GameInterface gameInterface;

	/**
	 * Démarrer un Timer de Lobby pour les Jeux!
	 * 
	 * @param gameInterface
	 */
	public GameLobbyTask(GameInterface gameInterface) {
		this.gameInterface = gameInterface;
		CrystaliserAPI.getEndaServer().setStartedSoon(true);
	}

	@Override
	public void run() {

		// Sécurité en cas de deconnexion d'un Joueur
		if (((Bukkit.getOnlinePlayers().size() < EndariumAPI.getGameSetting().getMinPlayers()) && (!(isForced)))
				|| (Bukkit.getOnlinePlayers().size() <= 1)) {
			Bukkit.broadcastMessage(EndariumAPI.getGameSetting().getGamePrefix() + GameMessages.ENOUGHT_PLAYERS);
			GameLobbyTask.setLobbyTimer(60);
			GameLobbyTask.setStarted(false);
			Bukkit.getOnlinePlayers().forEach(players -> players.setLevel(lobbyTimer));
			CrystaliserAPI.getEndaServer().setStartedSoon(false);
			this.cancel();
			return;
		}

		// Accélérer le démarrage
		if ((Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) && (lobbyTimer >= 20) && (!(isForced)))
			lobbyTimer = 10;

		// Lancement de la partie
		if (lobbyTimer == 0) {

			// Gestion des GolemaServer
			GameStatus.setStatus(GameStatus.GAME);
			CrystaliserAPI.getEndaServer().setJoinable(false);
			CrystaliserAPI.getEndaServer().setServerStatus(ServerStatus.INGAME);

			Bukkit.getOnlinePlayers().forEach(playerOnline -> playerOnline.setLevel(0));
			gameInterface.initGame();

			this.cancel();
			return;
		}

		// Annonce du délai aux Joueurs
		if ((lobbyTimer == 90) || (lobbyTimer == 60) || (lobbyTimer == 30) || (lobbyTimer == 15) || (lobbyTimer == 10)
				|| ((lobbyTimer <= 5) && (lobbyTimer != 0))) {

			HashMap<String, String> startedMessage = new HashMap<String, String>();

			startedMessage.put(Languages.FRENCH.getZoneLangue(),
					EndariumAPI.getGameSetting().getGamePrefix() + ChatColor.YELLOW + "Début de la partie dans "
							+ ChatColor.GOLD + lobbyTimer + (lobbyTimer <= 1 ? " seconde" : " secondes")
							+ ChatColor.YELLOW + ".");

			startedMessage.put(Languages.ENGLISH.getZoneLangue(),
					EndariumAPI.getGameSetting().getGamePrefix() + ChatColor.YELLOW + "The game starts in "
							+ ChatColor.GOLD + lobbyTimer + (lobbyTimer <= 1 ? " second" : " seconds")
							+ ChatColor.YELLOW + ".");

			startedMessage.put(Languages.SPANISH.getZoneLangue(),
					EndariumAPI.getGameSetting().getGamePrefix() + ChatColor.YELLOW + "El juego comienza en "
							+ ChatColor.GOLD + lobbyTimer + (lobbyTimer <= 1 ? " segundo" : " segundos")
							+ ChatColor.YELLOW + ".");

			LangMessages.broadcastMessage(startedMessage);

			if (lobbyTimer < 5)
				SoundUtils.sendSoundForAll(Sound.NOTE_PLING, 2.0f, 1.0f);
			if (lobbyTimer >= 5) {
				float pitch = 1.0f;
				SoundUtils.sendSoundForAll(Sound.NOTE_PLING, 2.0f, pitch);
				pitch = pitch - 0.2f;
			}
		}

		// Evolution du Timer
		Bukkit.getOnlinePlayers().forEach(playerOnline -> playerOnline.setLevel(lobbyTimer));
		lobbyTimer--;
	}

	/**
	 * Définir le Timer du Lobby.
	 * 
	 * @param lobbyTimer
	 */
	public static void setLobbyTimer(Integer lobbyTimer) {
		GameLobbyTask.lobbyTimer = lobbyTimer;
	}

	/**
	 * Définir si le Timer à commencé.
	 * 
	 * @param isStarted
	 */
	public static void setStarted(boolean isStarted) {
		GameLobbyTask.isStarted = isStarted;
	}

	/**
	 * Définir si le Timer est forcé.
	 * 
	 * @param isForced
	 */
	public static void setForced(boolean isForced) {
		GameLobbyTask.isForced = isForced;
	}
}