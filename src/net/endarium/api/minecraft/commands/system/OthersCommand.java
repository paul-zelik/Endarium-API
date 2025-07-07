package net.endarium.api.minecraft.commands.system;

import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.login.LoginManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.EndariumAPI;
import net.endarium.api.utils.Messages;
import net.endarium.api.utils.builders.titles.ActionBarBuilder;
import net.endarium.api.utils.commands.Command;
import net.endarium.api.utils.commands.Command.SenderType;
import net.endarium.api.utils.tools.SoundUtils;
import net.md_5.bungee.api.ChatColor;

public class OthersCommand {

	@Command(name = { "heal", "heals" }, minimumRank = Rank.MODERATOR, senderType = SenderType.ONLY_PLAYER)
	public void onCommandRankList(Player player, String[] args) {

		EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());
		if (!(endaPlayer.isLogged())) {
			return;
		}

		// Vérifier si la commande est activée dans un Hub
		if (!(EndariumAPI.getGameSetting().isGameServer())) {
			player.sendMessage(Messages.UNKNOW_COMMAND);
			SoundUtils.sendSound(player, Sound.VILLAGER_NO);
			return;
		}

		// Vérifier si sa vie n'est pas déjà pleine.
		if ((player.getHealth() >= player.getMaxHealth()) && (player.getFoodLevel() >= 20)) {
			player.sendMessage(Messages.ENDARIUM_PREFIX + ChatColor.RED + "Votre vie est déjà à son maximum.");
			SoundUtils.sendSound(player, Sound.VILLAGER_NO);
			return;
		}

		// Effectuer un Heal du Joueur
		player.setHealth(player.getMaxHealth());
		player.setFoodLevel(20);
		SoundUtils.sendSound(player, Sound.LEVEL_UP);
		new ActionBarBuilder(
				ChatColor.WHITE + "Changement du niveau de vie : " + ChatColor.RED + "" + ChatColor.BOLD + "Heal ❤")
				.sendTo(player);
	}
}