package net.endarium.api.minecraft.commands;

import net.endarium.api.players.login.LoginManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.endarium.api.games.GameStatus;
import net.endarium.api.games.servers.CrystaliserAPI;
import net.endarium.api.games.servers.CrystaliserServerManager;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.EndariumAPI;
import net.endarium.api.utils.Messages;
import net.endarium.api.utils.commands.Command;
import net.endarium.api.utils.commands.Command.SenderType;
import net.endarium.api.utils.tools.SoundUtils;
import net.endarium.crystaliser.servers.ServerType;

public class HubCommand {

	@Command(name = { "hub", "lobby" }, minimumRank = Rank.DEFAULT, senderType = SenderType.ONLY_PLAYER)
	public void onCommand(Player player, String[] args) {

		LoginManager loginManager = new LoginManager();
		if (!(loginManager.isLogged(player.getUniqueId()))) {
			return;
		}

		CrystaliserServerManager.sendPlayerToHub(player, false);
	}

	@Command(name = { "spawn" }, minimumRank = Rank.DEFAULT, senderType = SenderType.ONLY_PLAYER)
	public void onCommandSpawn(Player player, String[] args) {
		if ((CrystaliserAPI.getEndaServer().getServerType().equals(ServerType.HUB))
				|| (GameStatus.isStatus(GameStatus.LOBBY))) {
			LoginManager loginManager = new LoginManager();
			if (!(loginManager.isLogged(player.getUniqueId()))) {
				return;
			}

			player.teleport(new Location(player.getWorld(), 0.5 , 86 , -2.5,0,0));
			SoundUtils.sendSound(player, Sound.ENDERMAN_TELEPORT);
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0));
		} else {
			player.sendMessage(Messages.UNKNOW_COMMAND);
		}
	}
}