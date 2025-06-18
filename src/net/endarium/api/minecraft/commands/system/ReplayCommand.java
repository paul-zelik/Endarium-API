package net.endarium.api.minecraft.commands.system;

import net.endarium.api.players.login.LoginManager;
import org.bukkit.entity.Player;

import net.endarium.api.games.GameStatus;
import net.endarium.api.games.servers.CrystaliserAPI;
import net.endarium.api.games.servers.CrystaliserServerManager;
import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.Messages;
import net.endarium.api.utils.commands.Command;
import net.endarium.api.utils.commands.Command.SenderType;
import net.endarium.crystaliser.servers.EndaServer;
import net.endarium.crystaliser.servers.ServerType;
import net.md_5.bungee.api.ChatColor;

public class ReplayCommand {

	@Command(name = { "replay", "rejouer", "re" }, minimumRank = Rank.DEFAULT, senderType = SenderType.ONLY_PLAYER)
	public void onCommand(Player player, String[] args) {


		LoginManager loginManager = new LoginManager();
		if (!(loginManager.isLogged(player.getUniqueId()))) {
			return;
		}

		EndaServer endaServer = CrystaliserAPI.getEndaServer();

		// Vérifier si le serveur est valide
		if (endaServer == null) {
			player.sendMessage(Messages.ENDARIUM_PREFIX + ChatColor.RED
					+ "Impossible d'utiliser cette commande, contactez un administrateur.");
			return;
		}

		// Vérifier si le Serveur n'est pas un Hub
		if (endaServer.getServerType().equals(ServerType.HUB)) {
			player.sendMessage(Messages.UNKNOW_COMMAND);
			return;
		}

		// Vérifier si il est déjà dans un serveur en Attente
		if (GameStatus.isStatus(GameStatus.LOBBY)) {
			player.sendMessage(Messages.ENDARIUM_PREFIX + ChatColor.RED + "Vous vous apprêtez déjà à jouer sur nos "
					+ endaServer.getGameType().getDisplayName() + ".");
			return;
		}

		// Si le Joueur n'est pas en Spectateur
		if ((GameStatus.isStatus(GameStatus.GAME))) {
			EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());
			if ((endaPlayer != null) && (!(endaPlayer.isSpectator()))) {
				player.sendMessage(
						Messages.ENDARIUM_PREFIX + ChatColor.RED + "Erreur, vous êtes déjà en train de jouer.");
				return;
			}
		}

		// Trouver un Serveur & Vérifier sa disponibilité & Téléporter
		CrystaliserServerManager.sendPlayerToGame(player, endaServer.getGameType());
		return;
	}
}