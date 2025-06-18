package net.endarium.api.games;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.endarium.api.games.kits.KitManager;
import net.endarium.api.games.teams.TeamManager;
import net.endarium.api.players.EndaPlayer;
import net.md_5.bungee.api.ChatColor;

/**
 * Class de gestion des paramètres de Game.
 */
public class GameSetting {

	private String gameName, worldName;
	private Location lobbyLocation;
	private int minPlayers, teamNumber;
	private boolean gameServer, teamEnable, kitsEnable, spectatorEnable;

	private TeamManager teamManager;
	private KitManager kitManager;

	private GameInterface gameInterface;

	/**
	 * Gestion des paramètres principaux d'un Jeu.
	 * 
	 * @param gameName
	 * @param worldName
	 * @param minPlayers
	 * @param teamNumber
	 * @param gameServer
	 * @param teamEnable
	 * @param kitsEnable
	 * @param spectatorEnable
	 */
	public GameSetting(String gameName, String worldName, int minPlayers, int teamNumber, boolean gameServer,
			boolean teamEnable, boolean kitsEnable, boolean spectatorEnable) {
		this.gameName = gameName;
		this.worldName = worldName;
		this.lobbyLocation = null;
		this.minPlayers = minPlayers;
		this.teamNumber = teamNumber;
		this.gameServer = gameServer;
		this.teamEnable = teamEnable;
		this.kitsEnable = kitsEnable;
		this.spectatorEnable = spectatorEnable;
		this.kitManager = new KitManager();

		// Initialisation des Teams.
		if (gameServer)
			this.teamManager = new TeamManager(teamNumber, Bukkit.getMaxPlayers() / teamNumber, teamEnable);
	}

	/**
	 * Récupérer le nom du Jeu.
	 */
	public String getGameName() {
		return gameName;
	}

	/**
	 * Récupérer le prefix d'un jeu pour le Chat.
	 */
	public String getGamePrefix() {
		return ChatColor.BLUE + gameName + ChatColor.DARK_GRAY + "│ ";
	}

	public String getWorldName() {
		return worldName;
	}

	/**
	 * Récupérer le point du Spawn du Jeu.
	 */
	public Location getLobbyLocation() {
		if (lobbyLocation == null)
			return new Location(Bukkit.getWorld(worldName), -7.5, 51.0, 0.5, 0.0f, 0.0f);
		return lobbyLocation;
	}

	/**
	 * Récupérer les option de démarrage d'un Joueur.
	 */
	public GameInterface getGameInterface() {
		return gameInterface;
	}

	/**
	 * Récupérer l'Interface du Jeu.
	 * 
	 * @param gameInterface
	 */
	public void setGameInterface(GameInterface gameInterface) {
		this.gameInterface = gameInterface;
	}

	public int getMinPlayers() {
		return minPlayers;
	}

	public int getTeamNumber() {
		return teamNumber;
	}

	public boolean isGameServer() {
		return gameServer;
	}

	public boolean isTeamEnable() {
		return teamEnable;
	}

	public boolean isKitsEnable() {
		return kitsEnable;
	}
	
	public boolean isSpectatorEnable() {
		return spectatorEnable;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}

	public void setLobbyLocation(Location lobbyLocation) {
		this.lobbyLocation = lobbyLocation;
	}

	public void setMinPlayers(int minPlayers) {
		this.minPlayers = minPlayers;
	}

	public void setTeamNumber(int teamNumber) {
		this.teamNumber = teamNumber;
	}
	
	public void setKitsEnable(boolean kitsEnable) {
		this.kitsEnable = kitsEnable;
	}

	public void setGameServer(boolean gameServer) {
		this.gameServer = gameServer;
	}

	public void setTeamEnable(boolean teamEnable) {
		this.teamEnable = teamEnable;
	}

	public void setSpectatorEnable(boolean spectatorEnable) {
		this.spectatorEnable = spectatorEnable;
	}

	/**
	 * Téléporter un Spectateur à un Joueur en vie Aléatoire.
	 * 
	 * @param player
	 */
	public void teleportPlayerRandomAlivePlayer(Player player) {
		Bukkit.getOnlinePlayers().forEach(playerOnline -> {
			EndaPlayer endaPlayer = EndaPlayer.get(playerOnline.getUniqueId());
			if ((playerOnline != player) && (!(endaPlayer.isSpectator()))) {
				player.teleport(playerOnline);
				return;
			}
		});
	}

	/**
	 * RECUPERER LE SYSTEME DES TEAMS.
	 * 
	 * @return {@link TeamManager}
	 */
	public TeamManager getTeamManager() {
		return teamManager;
	}
	
	/**
	 * RECUPERER LE SYSTEME DES KITS.
	 * 
	 * @return {@link KitManager}
	 */
	public KitManager getKitManager() {
		return kitManager;
	}
}