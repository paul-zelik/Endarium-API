package net.endarium.api.games.teams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.endarium.api.utils.EndariumAPI;
import net.endarium.api.utils.builders.scoreboards.TeamTagsSign;
import net.md_5.bungee.api.ChatColor;

public class TeamManager {

	private List<Teams> teamList = new ArrayList<Teams>();
	private Map<Teams, List<Player>> playersTeam = new HashMap<Teams, List<Player>>();
	private Map<Teams, Location> teamsLocation = new HashMap<Teams, Location>();

	private Integer teamCount, teamSize;

	/**
	 * Générer la class Teams.
	 * 
	 * @param teamCount
	 * @param teamSize
	 */
	public TeamManager(Integer teamCount, Integer teamSize, boolean teamEnable) {
		this.teamCount = teamCount;
		if ((teamCount > 1) || (teamEnable)) {
			if (this.teamCount > (Teams.values().length - 2))
				this.teamCount = Teams.values().length - 2;
			this.teamSize = teamSize;
			int teamFilled = 0;
			for (Teams teams : Teams.values()) {
				if (teamFilled >= teamCount)
					break;
				this.teamList.add(teams);
				this.playersTeam.put(teams, new ArrayList<Player>());
				teamFilled++;
			}
		}
	}

	/**
	 * Ajouter le Joueur dans une Team.
	 * 
	 * @param player
	 * @param teams
	 */
	public void addPlayerTeam(Player player, Teams teams) {
		if (playersTeam.get(teams) == null)
			playersTeam.put(teams, new ArrayList<Player>());
		playersTeam.get(teams).add(player);
		TeamTagsSign.setNameTag(player, teams.getName(), teams.getPrefix() + " ");
	}

	/**
	 * Supprimer le Joueur d'une Team.
	 * 
	 * @param player
	 * @param teams
	 */
	public void removePlayerTeam(Player player, Teams teams) {
		playersTeam.get(teams).remove(player);
		TeamTagsSign.setNameTag(player, player.getName(), ChatColor.WHITE + "");
	}

	/**
	 * Supprimer le Joueur de toutes les Teams.
	 * 
	 * @param player
	 */
	public void removePlayerFromAll(Player player) {
		for (Teams teams : teamList)
			removePlayerTeam(player, teams);
	}

	/**
	 * Vérifier si une Team est Complète.
	 * 
	 * @param teams
	 */
	public boolean isTeamFull(Teams teams) {
		return playersTeam.get(teams).size() >= teamSize;
	}

	/**
	 * Vérifier si un Joueur est dans une Team.
	 * 
	 * @param player
	 * @param teams
	 */
	public boolean isInTeamPlayer(Player player, Teams teams) {
		return playersTeam.get(teams).contains(player);
	}

	/**
	 * Vérifier si un Joueur à une Team.
	 * 
	 * @param player
	 */
	public boolean hasTeamPlayer(Player player) {
		for (Teams teams : Teams.values())
			if (isInTeamPlayer(player, teams))
				return true;
		return false;
	}

	/**
	 * Récupérer la Team d'un Joueur.
	 * 
	 * @param player
	 */
	public Teams getPlayerTeam(Player player) {
		for (Teams teams : teamList)
			if (isInTeamPlayer(player, teams))
				return teams;
		return null;
	}

	/**
	 * Vérifier si le Joueur est dans une Team.
	 * 
	 * @param player
	 * @param target
	 */
	public boolean isInSameTeam(Player player, Player target) {
		if ((EndariumAPI.getGameSetting().isSpectatorEnable()) && (this.getPlayerTeam(player) != null)
				&& (this.getPlayerTeam(target) != null)
				&& (this.getPlayerTeam(target).equals(this.getPlayerTeam(player))))
			return true;
		return false;
	}

	/**
	 * Récupérer le Nombre de Joueurs dans une Teams.
	 * 
	 * @param teams
	 */
	public Integer countTeam(Teams teams) {
		return playersTeam.get(teams).size();
	}

	/**
	 * Mettre un Joueur Spectateur.
	 * 
	 * @param player
	 */
	public void setSpectator(Player player) {
		removePlayerFromAll(player);
		TeamTagsSign.setNameTag(player, "Spectateur", ChatColor.GRAY + "[SPEC] ");
	}

	/**
	 * Ajouter un Joueur dans une Team Aléatoire.
	 * 
	 * @param player
	 */
	public void addInRandomTeam(Player player) {
		Teams teamToJoin = null;
		for (Teams teams : teamList)
			if ((!(isTeamFull(teams))) && ((teamToJoin == null) || (countTeam(teams)) < countTeam(teamToJoin)))
				teamToJoin = teams;
		if (this.getPlayerTeam(player) == null)
			addPlayerTeam(player, teamToJoin);
	}

	/**
	 * Vérifier si la Teams est accessible.
	 * 
	 * @param teamsSelect
	 */
	public boolean canAccesTeam(Teams teamsSelect) {
		if (this.isTeamFull(teamsSelect))
			return false;
		if (this.getTeamCount() >= 8)
			return true;
		if (this.countTeam(teamsSelect) > (Bukkit.getOnlinePlayers().size() / this.getTeamCount()))
			return false;
		if ((this.getTeamCount() <= 2) && (Bukkit.getOnlinePlayers().size() <= 2) && (this.countTeam(teamsSelect) == 1))
			return false;
		return true;
	}

	/**
	 * Définir le nombre de Teams.
	 * 
	 * @param teamCount
	 */
	public void setTeamCount(Integer teamCount) {
		this.teamCount = teamCount;
		if (this.teamCount > (Teams.values().length - 2))
			this.teamCount = Teams.values().length - 2;
		Bukkit.getOnlinePlayers().forEach(players -> removePlayerFromAll(players));
		this.teamList.clear();
		this.playersTeam.clear();
		int teamFilled = 0;
		for (Teams teams : Teams.values()) {
			if (teamFilled >= teamCount)
				break;
			this.teamList.add(teams);
			this.playersTeam.put(teams, new ArrayList<Player>());
			teamFilled++;
		}
	}

	/**
	 * Récupérer les Joueurs d'une Team.
	 * 
	 * @param teams
	 */
	public List<Player> getTeamPlayerList(Teams teams) {
		if (this.playersTeam.get(teams) == null)
			return new ArrayList<Player>();
		return this.playersTeam.get(teams);
	}

	/**
	 * Définir la taille des Teams.
	 * 
	 * @param teamSize
	 */
	public void setTeamSize(Integer teamSize) {
		this.teamSize = teamSize;
	}

	/**
	 * Ajouter le spawn des Teams.
	 * 
	 * @param teams
	 * @param location
	 */
	public void setTeamLocation(Teams teams, Location location) {
		this.teamsLocation.put(teams, location);
	}

	/**
	 * Récupérer le spawn des Teams.
	 * 
	 * @param teams
	 */
	public Location getTeamLocation(Teams teams) {
		if (this.teamsLocation.get(teams) != null)
			return this.teamsLocation.get(teams);
		return EndariumAPI.getGameSetting().getLobbyLocation();
	}

	public Integer getTeamCount() {
		return teamCount;
	}

	public Integer getTeamSize() {
		return teamSize;
	}

	public List<Teams> getTeamList() {
		return teamList;
	}

	public Map<Teams, List<Player>> getPlayersTeam() {
		return playersTeam;
	}

	public Map<Teams, Location> getTeamsLocation() {
		return teamsLocation;
	}
}