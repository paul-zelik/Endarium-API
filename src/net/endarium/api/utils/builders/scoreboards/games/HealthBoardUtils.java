package net.endarium.api.utils.builders.scoreboards.games;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import net.md_5.bungee.api.ChatColor;

/**
 * Afficher la vie du joueur dans le tablist.
 */
public class HealthBoardUtils implements ScoreboardManager {

	public static Map<Player, HealthBoardUtils> scoreboardtabkill = new HashMap<>();
	public Player player;
	public Scoreboard scoreboard;
	public Objective objective;
	private String name = "hearth";

	public HealthBoardUtils(Player player) {
		this.player = player;
		this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		if (scoreboardtabkill.containsKey(player)) {
			return;
		}
		scoreboardtabkill.put(player, this);
		Random random = new Random();
		this.name = ("health." + random.nextInt(1000000000));
		this.objective = this.scoreboard.registerNewObjective(this.name, "health");
		this.objective = this.scoreboard.getObjective(this.name);
		this.objective.setDisplayName(ChatColor.RED + "❤");
		this.objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
	}

	public static HealthBoardUtils getScoreboard(Player player) {
		return (HealthBoardUtils) scoreboardtabkill.get(player);
	}

	public Scoreboard getMainScoreboard() {
		return this.scoreboard;
	}

	public Scoreboard getNewScoreboard() {
		return null;
	}
}
