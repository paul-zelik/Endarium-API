package net.endarium.api.minecraft.listeners;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.endarium.api.games.servers.CrystaliserAPI;
import net.endarium.api.minecraft.listeners.customs.RankChangeEvent;
import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.Messages;
import net.endarium.api.utils.builders.scoreboards.TeamTagsSign;
import net.endarium.api.utils.builders.titles.ActionBarBuilder;
import net.endarium.api.utils.tools.SoundUtils;
import net.endarium.crystaliser.servers.GameType;
import net.md_5.bungee.api.ChatColor;

public class APIRankChangeListener implements Listener {

	@EventHandler
	public void onRankChange(RankChangeEvent event) {

		Player player = event.getPlayer();
		EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());
		Rank rank = event.getRank();

		endaPlayer.setRank(rank);
		player.sendMessage(Messages.ENDARIUM_PREFIX + ChatColor.WHITE
				+ "Changement de grade, vous possédez maintenant le grade " + rank.getChatColor() + rank.getPrefix()
				+ Messages.getRankSpaceConvention(rank) + player.getName() + ChatColor.WHITE + ".");
		new ActionBarBuilder(ChatColor.WHITE + "Bravo ! Vous possédez maintenant le grade : " + rank.getChatColor()
				+ rank.getPrefix() + Messages.getRankSpaceConvention(rank) + player.getName() + ChatColor.WHITE + ".")
				.sendTo(player);
		SoundUtils.sendSound(player, Sound.LEVEL_UP);

		// Editer le Tab das les Hubs
		if (CrystaliserAPI.getEndaServer().getGameType().equals(GameType.HUB))
			TeamTagsSign.setNameTag(player, endaPlayer.getRank().getOrderCode() + endaPlayer.getRank().getName(),
					endaPlayer.getRank().getChatColor() + endaPlayer.getRank().getPrefix()
							+ Messages.getRankSpaceConvention(endaPlayer.getRank()));
	}
}