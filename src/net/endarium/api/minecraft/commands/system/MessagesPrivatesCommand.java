package net.endarium.api.minecraft.commands.system;

import java.util.UUID;

import net.endarium.api.players.login.LoginManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.endarium.api.minecraft.EndariumBukkit;
import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.friends.Friends;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.Messages;
import net.endarium.api.utils.commands.Command;
import net.endarium.api.utils.commands.Command.SenderType;
import net.endarium.api.utils.mojang.UUIDEndaFetcher;
import net.md_5.bungee.api.ChatColor;

public class MessagesPrivatesCommand {

	private String PREFIX = ChatColor.GOLD + "[MP] ";

	@Command(name = { "message", "msg", "mp", "m", "tell", "whisper",
			"whispers" }, minimumRank = Rank.DEFAULT, senderType = SenderType.ONLY_PLAYER)
	public void onCommandMessage(Player player, String[] args) {

		LoginManager loginManager = new LoginManager();
		if (!(loginManager.isLogged(player.getUniqueId()))) {
			return;
		}

		if (args.length < 1) {
			this.sendHelp(player);
			return;
		}

		EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());
		Friends friends = endaPlayer.getFriends();

		/** Commande avec 1 Argument */
		if (args.length == 1) {

			/** MP : Notifications */
			if ((args[0].equalsIgnoreCase("notifications")) || (args[0].equalsIgnoreCase("notifs"))) {
				if (friends.isPrivateMessageNotifications()) {
					player.sendMessage(PREFIX + ChatColor.WHITE + "Vous venez de " + ChatColor.RED + "désactiver"
							+ ChatColor.WHITE + " les notifications de messages privés.");
					endaPlayer.setFriends(friends.setPrivateMessageNotifications(false));
				} else {
					player.sendMessage(PREFIX + ChatColor.WHITE + "Vous venez d'" + ChatColor.GREEN + "activer"
							+ ChatColor.WHITE + " les notifications de messages privés.");
					endaPlayer.setFriends(friends.setPrivateMessageNotifications(true));
				}
				return;

				/** MP : ON */
			} else if (args[0].equalsIgnoreCase("on")) {
				if (friends.getPrivateMessageReception() == 1) {
					player.sendMessage(PREFIX + ChatColor.RED + "Vous avez déjà activé vos messages privés.");
					return;
				}
				player.sendMessage(PREFIX + ChatColor.WHITE + "Vous venez d'" + ChatColor.GREEN + "activer"
						+ ChatColor.WHITE + " vos messages privés.");
				endaPlayer.setFriends(friends.setPrivateMessageReception(1));
				return;

				/** MP : OFF */
			} else if (args[0].equalsIgnoreCase("off")) {
				if (friends.getPrivateMessageReception() == 0) {
					player.sendMessage(PREFIX + ChatColor.RED + "Vous avez déjà désactivé vos messages privés.");
					return;
				}
				player.sendMessage(PREFIX + ChatColor.WHITE + "Vous venez de " + ChatColor.RED + "désactiver"
						+ ChatColor.WHITE + " vos messages privés.");
				endaPlayer.setFriends(friends.setPrivateMessageReception(0));
				return;

				/** MP : FRIENDS */
			} else if ((args[0].equalsIgnoreCase("friends")) || args[0].equalsIgnoreCase("friend")
					|| args[0].equalsIgnoreCase("amis") || args[0].equalsIgnoreCase("ami")) {
				if (friends.getPrivateMessageReception() == 2) {
					player.sendMessage(PREFIX + ChatColor.RED
							+ "Vous avez déjà autorisé vos messages privés seulement pour vos amis.");
					return;
				}
				player.sendMessage(PREFIX + ChatColor.WHITE + "Vous venez d'" + ChatColor.GREEN + "activer"
						+ ChatColor.WHITE + " vos messages privés pour vos " + ChatColor.LIGHT_PURPLE + "amis seulement"
						+ ChatColor.WHITE + ".");
				endaPlayer.setFriends(friends.setPrivateMessageReception(2));
				return;

			} else {
				this.sendHelp(player);
				return;
			}
		}

		/** Commande avec 2 Arguments et Plus */

		// Récupérer le Joueur concerné (Target)
		String target = args[0];

		// Récupérer le Message à Envoyer
		String message = "";
		for (int i = 1; i < args.length; i++) {
			message = message + " " + args[i];
		}
		message = message.trim();

		// Procéder à l'envoie du Message
		UUID targetUUID = UUIDEndaFetcher.getPlayerUUID(target);
		this.sendPrivateMessage(friends.getPrivateMessageReception(), player, target, targetUUID, message);
		return;
	}

	@Command(name = { "reply", "r" }, minimumRank = Rank.DEFAULT, senderType = SenderType.ONLY_PLAYER)
	public void onCommandMessageReply(Player player, String[] args) {

		if (args.length < 1) {
			this.sendHelp(player);
			return;
		}

		EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());
		Friends friends = endaPlayer.getFriends();

		// Vérification du Status de ses Messages
		if (friends.getPrivateMessageReception() == 0) {
			player.sendMessage(PREFIX + ChatColor.RED + "Vous devez activer vos messages privés.");
			return;
		}

		// Vérifier le dernier destinataire
		UUID targetUUID = friends.getPrivateMessageLastTarget();
		if (targetUUID == null) {
			player.sendMessage(PREFIX + ChatColor.RED + "Vous n'avez personne à qui répondre...");
			return;
		}

		// Récupérer le Message à Envoyer
		String message = "";
		for (int i = 0; i < args.length; i++) {
			message = message + " " + args[i];
		}
		message = message.trim();

		// Procéder à l'envoie du Message
		this.sendPrivateMessage(friends.getPrivateMessageReception(), player, UUIDEndaFetcher.getPlayerName(targetUUID),
				targetUUID, message);
		return;
	}

	/**
	 * Envoyer un Message Privé.
	 * 
	 * @param powerMessagePrivate
	 * @param player
	 * @param target
	 * @param targetUUID
	 * @param message
	 */
	private void sendPrivateMessage(int powerMessagePrivate, Player player, String target, UUID targetUUID,
			String message) {

		EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());

		// Vérification du Status de ses Messages
		if (powerMessagePrivate == 0) {
			player.sendMessage(PREFIX + ChatColor.RED + "Vous devez activer vos messages privés.");
			return;
		}

		// Vérifier si le Joueur s'écrit à lui-même
		if (player.getName().equalsIgnoreCase(target)) {
			player.sendMessage(PREFIX + ChatColor.RED + "T'écrire ne comblera pas ta solitude ! :'(");
			return;
		}

		// Vérifier la validité du Target
		if (targetUUID == null) {
			player.sendMessage(PREFIX + ChatColor.RED + "Le joueur '" + target + "' n'existe pas.");
			return;
		}

		// Vérifier si le Target est en Ligne
		target = UUIDEndaFetcher.getPlayerName(targetUUID);
		if (!(EndaPlayer.isConnected(targetUUID))) {
			player.sendMessage(PREFIX + ChatColor.RED + target + ChatColor.WHITE + " n'est pas en ligne.");
			return;
		}

		// Vérifier si le Joueur accepte les MP
		EndaPlayer endaTarget = EndaPlayer.get(targetUUID);
		if (endaTarget.getFriends().getPrivateMessageReception() == 0) {
			player.sendMessage(
					PREFIX + ChatColor.RED + "Le joueur '" + target + "' n'accepte pas les messages privés.");
			return;
		}

		// Vérifier si les MP sont seulement aux Amis && Target est Ami au Joueur
		if ((endaTarget.getFriends().getPrivateMessageReception() == 2)
				&& (!(endaTarget.getFriends().getFriends().contains(player.getUniqueId())))) {
			player.sendMessage(PREFIX + ChatColor.RED
					+ "Vous devez être ami avec cette personne pour pouvoir lui envoyer un message.");
			return;
		}

		// Gestion de l'envoie final du Message Privé
		endaPlayer.setFriends(endaPlayer.getFriends().setPrivateMessageLastTarget(targetUUID));
		player.sendMessage(ChatColor.AQUA + "Envoyé à " + endaTarget.getRank().getChatColor()
				+ endaTarget.getRank().getPrefix() + Messages.getRankSpaceConvention(endaTarget.getRank()) + target
				+ ChatColor.AQUA + " : " + ChatColor.GRAY + message);
		String messageTarget = ChatColor.AQUA + "Reçu de " + endaPlayer.getRank().getChatColor()
				+ endaPlayer.getRank().getPrefix() + Messages.getRankSpaceConvention(endaPlayer.getRank())
				+ player.getName() + ChatColor.AQUA + " : " + ChatColor.LIGHT_PURPLE + message;

		// Supprimer la création du TargetUUID
		if ((Bukkit.getPlayer(targetUUID) == null) || (Bukkit.getPlayer(target) == null))
			endaTarget.logout();

		EndariumBukkit.getPlugin().getPrivateMessageChannel().sendMessage(player.getUniqueId(), targetUUID,
				messageTarget);
		return;
	}

	/**
	 * Message d'Aide de la Commande.
	 */
	private void sendHelp(Player player) {
		player.sendMessage("");
		player.sendMessage(" " + ChatColor.GRAY + "» " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Aide"
				+ ChatColor.WHITE + "│ " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Messages Privés");
		player.sendMessage("");
		player.sendMessage(
				" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/msg [pseudo] [message] "
						+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Evoyer un message à un joueur.");
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/msg notifications "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Gérer les notifications des messages.");
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/msg [on|friends|off] "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Gérer les réceptions des messages.");
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/reply [message] "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Répondre au dernier destinataire.");
		player.sendMessage("");
	}
}