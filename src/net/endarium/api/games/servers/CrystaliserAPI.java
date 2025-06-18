package net.endarium.api.games.servers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.endarium.crystaliser.minecraft.CrystaliserMinecraft;
import net.endarium.crystaliser.servers.EndaServer;
import net.endarium.crystaliser.servers.GameType;
import net.endarium.crystaliser.servers.MapInfos;
import net.endarium.crystaliser.servers.ServerStatus;
import net.endarium.crystaliser.servers.ServerType;

public class CrystaliserAPI {

	/**
	 * Récupérer le EndaServer.
	 */
	public static EndaServer getEndaServer() {
		return CrystaliserMinecraft.getEndaServer();
	}

	/**
	 * Définir le Mode de Jeu.
	 * 
	 * @param gameType
	 */
	public static void setGameType(GameType gameType) {
		CrystaliserMinecraft.getEndaServer().setGameType(gameType);
	}

	/**
	 * Définir le Type de Serveur.
	 * 
	 * @param serverType
	 */
	public static void setServerType(ServerType serverType) {
		CrystaliserMinecraft.getEndaServer().setServerType(serverType);
	}

	/**
	 * Définir le ServerStatus.
	 * 
	 * @param serverStatus
	 */
	public static void setServerStatus(ServerStatus serverStatus) {
		CrystaliserMinecraft.getEndaServer().setServerStatus(serverStatus);
	}

	/**
	 * Définir la MapInfos.
	 * 
	 * @param mapInfos
	 */
	public static void setMapInfos(MapInfos mapInfos) {
		CrystaliserMinecraft.getEndaServer().setMapInfos(mapInfos);
	}

	/**
	 * Vérifier si le serveur est un serveur Host.
	 */
	public static boolean isHostServer() {
		return CrystaliserAPI.getEndaServer().getServerType().equals(ServerType.HOST);
	}

	/**
	 * Effectuer une initialisation finale du Serveur.
	 * 
	 * @param gameType
	 * @param serverType
	 * @param mapInfos
	 */
	public static boolean enabledGameServer(GameType gameType, ServerType serverType, MapInfos mapInfos) {
		if (CrystaliserMinecraft.getEndaServer().getServerStatus().equals(ServerStatus.INSTALL)) {
			if ((gameType.isVIPGame()) || (serverType.equals(ServerType.VIP)) || mapInfos.isVIPMap())
				CrystaliserMinecraft.getEndaServer().setServerStatus(ServerStatus.VIP);
			else if ((gameType.isOnlyHostGame()) || (serverType.equals(ServerType.HOST)) || (mapInfos.isOnlyHostMap()))
				CrystaliserMinecraft.getEndaServer().setServerStatus(ServerStatus.HOST);
			else if ((serverType.equals(ServerType.GAMES) || ((serverType.equals(ServerType.HUB)))))
				CrystaliserMinecraft.getEndaServer().setServerStatus(ServerStatus.PUBLIC);
			else if ((serverType.equals(ServerType.EVENTS)))
				CrystaliserMinecraft.getEndaServer().setServerStatus(ServerStatus.PRIVATE);
			else if ((serverType.equals(ServerType.DEVELOPMENT)))
				CrystaliserMinecraft.getEndaServer().setServerStatus(ServerStatus.STAFF);
			else
				CrystaliserMinecraft.getEndaServer().setServerStatus(ServerStatus.ADMIN);
			return true;
		}
		return false;
	}

	/**
	 * Compter le nombre de Joueurs sur le Network.
	 */
	public static Integer countPlayerNetwork() {
		int playerCount = 0;
		for (EndaServer endaServer : CrystaliserAPI.getEndaServerList()) {
			if (endaServer != null)
				playerCount = playerCount + endaServer.getCurrentPlayers();
		}
		return playerCount;
	}

	/**
	 * Compter le nombre de Joueurs dans un mode de Jeu.
	 * 
	 * @param gameType
	 */
	public static int countPlayerGameType(GameType gameType) {
		int playerCount = 0;
		for (EndaServer endaServer : CrystaliserAPI.getEndaServerList()) {
			if ((endaServer != null) && (endaServer.getGameType().equals(gameType)))
				playerCount = playerCount + endaServer.getCurrentPlayers();
		}
		return playerCount;
	}

	/**
	 * Compter le nombre de Joueurs dans un Host.
	 */
	public static int countPlayerHost() {
		int playerCount = 0;
		for (EndaServer endaServer : CrystaliserAPI.getEndaServerList()) {
			if ((endaServer != null) && (endaServer.getServerType().equals(ServerType.HOST)))
				playerCount = playerCount + endaServer.getCurrentPlayers();
		}
		return playerCount;
	}

	/**
	 * Compter le nombre de Joueurs dans une certaine Carte.
	 * 
	 * @param mapInfos
	 */
	public static int countPlayerMapInfos(MapInfos mapInfos) {
		int playerCount = 0;
		for (EndaServer endaServer : CrystaliserAPI.getEndaServerList()) {
			if ((endaServer != null) && (endaServer.getMapInfos().equals(mapInfos)))
				playerCount = playerCount + endaServer.getCurrentPlayers();
		}
		return playerCount;
	}

	/**
	 * Récupérer une liste de serveurs par GameType.
	 * 
	 * @param gameType
	 */
	public static List<EndaServer> getEndaServerGameTypeList(GameType gameType) {
		List<EndaServer> endaServers = new ArrayList<>();
		for (EndaServer endaServer : CrystaliserAPI.getEndaServerList()) {
			if ((endaServer != null) && (endaServer.getGameType().equals(gameType)))
				endaServers.add(endaServer);
		}
		return endaServers;
	}

	/**
	 * Récupérer une liste de serveurs par MapInfos.
	 * 
	 * @param mapInfos
	 */
	public static List<EndaServer> getEndaServerMapInfosList(MapInfos mapInfos) {
		List<EndaServer> endaServers = new ArrayList<>();
		for (EndaServer endaServer : CrystaliserAPI.getEndaServerList()) {
			if ((endaServer != null) && (endaServer.getMapInfos().equals(mapInfos)))
				endaServers.add(endaServer);
		}
		return endaServers;
	}

	/**
	 * Récupérer une liste de serveurs par GameType qui sont disponibles.
	 * 
	 * @param gameType
	 */
	public static List<EndaServer> getEndaServerGameTypeJoinableList(GameType gameType) {
		List<EndaServer> endaServers = new ArrayList<>();
		for (EndaServer endaServer : CrystaliserAPI.getEndaServerGameTypeList(gameType)) {
			System.out.println(endaServer.isJoinable());
			if (endaServer.isJoinable())
				endaServers.add(endaServer);
		}
		return endaServers;
	}

	/**
	 * Récupérer une liste de serveurs par MapInfos qui sont disponibles.
	 * 
	 * @param mapInfos
	 */
	public static List<EndaServer> getEndaServerMapInfosJoinableList(MapInfos mapInfos) {
		List<EndaServer> endaServers = new ArrayList<>();
		for (EndaServer endaServer : CrystaliserAPI.getEndaServerMapInfosList(mapInfos)) {
			if (endaServer.isJoinable())
				endaServers.add(endaServer);
		}
		return endaServers;
	}

	/**
	 * Récupérer la liste de tout les EndaServer.
	 */
	public static List<EndaServer> getEndaServerList() {
		return CrystaliserMinecraft.getPlugin().getCrystaliserCommons().getAllEndaServers();
	}

	/**
	 * Faire une demande de démarrage de Serveur Host.
	 * 
	 * @param gameType
	 * @param serverType
	 * @param serverStatus
	 * @param mapInfos
	 * @param hostUUID
	 * @param hostName
	 */
	public static boolean startHostEndaServer(GameType gameType, ServerType serverType, ServerStatus serverStatus,
			MapInfos mapInfos, UUID hostUUID, String hostName) {
		try {
			CrystaliserMinecraft.getPlugin().getServerDemandChannel().sendRequestDemandServer(hostUUID, hostName,
					gameType, serverType, serverStatus, mapInfos);
			return true;
		} catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
	}

	/**
	 * Faire une demande de démarrage de Serveur.
	 * 
	 * @param gameType
	 * @param serverType
	 * @param serverStatus
	 * @param mapInfos
	 */
	public static boolean startEndaServer(GameType gameType, ServerType serverType, ServerStatus serverStatus,
			MapInfos mapInfos) {
		try {
			CrystaliserMinecraft.getPlugin().getServerDemandChannel().sendRequestDemandServerWithoutOwner(gameType,
					serverType, serverStatus, mapInfos);
			return true;
		} catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
	}
}