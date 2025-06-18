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

/**
 * Gestion du Compteur de Kill.
 */
public class KillBoardUtils implements ScoreboardManager {

	public static Map<Player, KillBoardUtils> scoreboardtabkill = new HashMap<>();
	public Player player;
	public Scoreboard scoreboard;
	public Objective objective;
	private String name = "kill";

	public KillBoardUtils(Player player) {
		this.player = player;
		this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		if (scoreboardtabkill.containsKey(player)) {
			return;
		}
		scoreboardtabkill.put(player, this);
		Random random = new Random();
		this.name = ("kill." + random.nextInt(1000000000));
		this.objective = this.scoreboard.registerNewObjective(this.name, "playerKillCount");
		this.objective = this.scoreboard.getObjective(this.name);
		this.objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
	}

	public static KillBoardUtils getScoreboard(Player player) {
		return (KillBoardUtils) scoreboardtabkill.get(player);
	}

	public Scoreboard getMainScoreboard() {
		return this.scoreboard;
	}

	public Scoreboard getNewScoreboard() {
		return null;
	}
}