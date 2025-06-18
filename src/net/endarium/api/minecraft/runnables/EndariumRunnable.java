package net.endarium.api.minecraft.runnables;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import net.endarium.api.players.EndaPlayer;
import net.endarium.api.utils.builders.titles.ActionBarBuilder;
import net.md_5.bungee.api.ChatColor;

public class EndariumRunnable extends BukkitRunnable {

	@Override
	public void run() {
		Bukkit.getOnlinePlayers().forEach(players -> {
			EndaPlayer endaPlayer = EndaPlayer.get(players.getUniqueId());
			// Gestion du Mode de Modération
			if ((endaPlayer.getEndaPlayerCache() != null) && (endaPlayer.isModeModeration()))
				new ActionBarBuilder(
						ChatColor.WHITE + "Mode Modération : " + ChatColor.GREEN + "" + ChatColor.BOLD + "Activé")
						.sendTo(players);
		});
	}
}