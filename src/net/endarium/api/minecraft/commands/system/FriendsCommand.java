package net.endarium.api.minecraft.commands.system;

import java.util.UUID;

import net.endarium.api.players.login.LoginManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.endarium.api.EndariumCommons;
import net.endarium.api.minecraft.EndariumBukkit;
import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.friends.FriendChannelType;
import net.endarium.api.players.friends.Friends;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.Messages;
import net.endarium.api.utils.commands.Command;
import net.endarium.api.utils.commands.Command.SenderType;
import net.endarium.api.utils.mojang.UUIDEndaFetcher;
import net.endarium.api.utils.tools.SoundUtils;
import net.md_5.bungee.api.ChatColor;

public class FriendsCommand {

	private String PREFIX = Messages.FRIENDS_PREFIX;


	@Command(name = { "friends", "friend", "f", "amis",
			"ami" }, minimumRank = Rank.DEFAULT, senderType = SenderType.ONLY_PLAYER)
	public void onCommand(Player player, String[] args) {

		EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());
		if (!(endaPlayer.isLogged())) {
			return;
		}

		if (args.length < 1 || args.length > 2) {
			this.sendHelp(player);
			return;
		}

		Friends friends = endaPlayer.getFriends();

		/** Commande avec 1 Argument */
		if (args.length == 1) {

			/** FRIEND : ON **/
			if (args[0].equalsIgnoreCase("on")) {

				if (friends.isActive()) {
					player.sendMessage(PREFIX + ChatColor.RED + "Vous avez déjà activé vos demandes d'amis.");
					return;
				}

				player.sendMessage(PREFIX + ChatColor.WHITE + "Vous venez d'" + ChatColor.GREEN + "activer"
						+ ChatColor.WHITE + " vos demandes d'amis.");
				endaPlayer.setFriends(friends.setActive(true));
				return;

				/** FRIEND : OFF **/
			} else if (args[0].equalsIgnoreCase("off")) {

				if (!(friends.isActive())) {
					player.sendMessage(PREFIX + ChatColor.RED + "Vous avez déjà désactivé vos demandes d'amis.");
					return;
				}

				player.sendMessage(PREFIX + ChatColor.WHITE + "Vous venez de " + ChatColor.RED + "désactiver"
						+ ChatColor.WHITE + " vos demandes d'amis.");
				endaPlayer.setFriends(friends.setActive(false));
				return;

				/** FRIEND : NOTIFICATIONS **/
			} else if ((args[0].equalsIgnoreCase("notifications")) && (args[0].equalsIgnoreCase("notifs"))) {

				if (friends.isNotifications()) {
					player.sendMessage(PREFIX + ChatColor.WHITE + "Vous venez de " + ChatColor.RED + "désactiver"
							+ ChatColor.WHITE + " vos notifications d'amis.");
					endaPlayer.setFriends(friends.setNotifications(false));
				} else {
					player.sendMessage(PREFIX + ChatColor.WHITE + "Vous venez d'" + ChatColor.GREEN + "activer"
							+ ChatColor.WHITE + " vos notifications d'amis.");
					endaPlayer.setFriends(friends.setNotifications(true));
				}

				/** FRIEND : LISTE **/
			} else if (args[0].equalsIgnoreCase("list")) {

				if (friends.countFriends() <= 0) {
					player.sendMessage(PREFIX + ChatColor.RED + "Vous n'avez pas d'amis ! :(");
					SoundUtils.sendSound(player, Sound.VILLAGER_NO);
					return;
				}
				String friendList = "";
				for (UUID uuid : endaPlayer.getFriends().getFriends()) {
					friendList = friendList + ChatColor.WHITE + ", "
							+ (EndaPlayer.isConnected(uuid) ? ChatColor.GREEN : ChatColor.RED)
							+ UUIDEndaFetcher.getPlayerName(uuid);
				}
				player.sendMessage(PREFIX + ChatColor.WHITE + "Liste de vos amis : " + friendList.replaceFirst(", ", "")
						+ ChatColor.WHITE + ".");
				return;

			} else {
				this.sendHelp(player);
				return;
			}
		}

		/** Commande avec 2 Arguments */
		if (args.length == 2) {

			// Vérifier si le Joueur existe
			String target = args[1];
			UUID targetUUID = UUIDEndaFetcher.getPlayerUUID(target);
			if (targetUUID == null) {
				player.sendMessage(PREFIX + ChatColor.RED + "Le joueur '" + target + "' est introuvable.");
				return;
			}

			/** FRIEND : AJOUTER **/
			if ((args[0].equalsIgnoreCase("add")) || (args[0].equalsIgnoreCase("invite"))) {

				// Vérifier si le joueur s'invite lui-même
				if (player.getName().equalsIgnoreCase(target)) {
					player.sendMessage(PREFIX + ChatColor.RED + "Vous ne pouvez pas vous inviter vous-même.");
					return;
				}

				// Vérifier si le joueur n'est pas déjà son ami
				if (friends.isFriend(targetUUID)) {
					player.sendMessage(PREFIX + ChatColor.RED + "Ce joueur est déjà votre ami.");
					return;
				}

				// Vérifier si il a déjà atteint la limite de friends
				if (friends.countFriends() >= endaPlayer.getRank().getMaxFriends()) {
					player.sendMessage(PREFIX + ChatColor.RED + "Vous avez atteint votre limite d'amis.");
					return;
				}

				// Vérifier si une requête n'est pas déjà en cours
				if ((EndariumCommons.getInstance().getEndariumEntities().getFriendsManager()
						.hasFriendRequest(player.getUniqueId(), targetUUID))
						|| (EndariumCommons.getInstance().getEndariumEntities().getFriendsManager()
								.hasFriendRequest(targetUUID, player.getUniqueId()))) {
					player.performCommand("friend accept " + target);
					return;
				}

				// Vérifier si le joueur n'est pas connecté
				if (!(EndaPlayer.isConnected(targetUUID))) {
					player.sendMessage(PREFIX + ChatColor.RED + target + ChatColor.WHITE + " n'est pas en ligne.");
					return;
				}

				// Vérifier si le joueur accepte les demandes d'amis
				EndaPlayer endaTarget = EndaPlayer.get(targetUUID);
				if (!(endaTarget.getFriends().isActive())) {
					player.sendMessage(
							PREFIX + ChatColor.RED + "Le joueur '" + target + "' n'accepte pas les demandes d'amis.");
					return;
				}

				// Vérifier si le joueur possède assez de place das ses amis
				if (endaTarget.getFriends().countFriends() >= endaTarget.getRank().getMaxFriends()) {
					player.sendMessage(PREFIX + ChatColor.RED + target + " a déjà trop d'amis dans sa liste.");
					return;
				}

				// Envoyer la demande d'amis
				player.sendMessage(Messages.FRIENDS_PREFIX + ChatColor.WHITE + "Demande d'amitié envoyée à "
						+ ChatColor.YELLOW + target + ChatColor.WHITE + ".");
				SoundUtils.sendSound(player, Sound.NOTE_PIANO);
				if (Bukkit.getPlayer(targetUUID) == null)
					endaTarget.logout();

				EndariumBukkit.getPlugin().getFriendsChannel().sendFriendChannel(FriendChannelType.INVITE_FRIEND,
						player.getUniqueId(), targetUUID, player.getName(), target);
				return;

				/** FRIEND : SUPPRIMER **/
			} else if (args[0].equalsIgnoreCase("remove")) {

				// Vérifier si le joueur se supprimer lui-même
				if (player.getName().equalsIgnoreCase(target)) {
					player.sendMessage(PREFIX + ChatColor.RED + "Vous ne pouvez pas vous supprimer vous-même.");
					return;
				}

				// Vérifier si le joueur n'est pas déjà son ami
				if (!(friends.isFriend(targetUUID))) {
					player.sendMessage(PREFIX + ChatColor.RED + "Ce joueur n'est pas votre ami.");
					return;
				}

				// Effectuer la suppression de l'ami
				player.sendMessage("");
				player.sendMessage(Messages.FRIENDS_PREFIX + ChatColor.WHITE + "Vous venez de " + ChatColor.RED
						+ "supprimer" + ChatColor.WHITE + " un de vos amis.");
				player.sendMessage(ChatColor.RED + "Vous n'êtes désormais plus ami avec : " + ChatColor.YELLOW + target
						+ ChatColor.RED + ".");
				player.sendMessage("");
				SoundUtils.sendSound(player, Sound.NOTE_BASS_DRUM);

				endaPlayer.setFriends(friends.removeFriend(targetUUID));
				EndaPlayer endaTarget = EndaPlayer.get(targetUUID);
				endaTarget.setFriends(endaTarget.getFriends().removeFriend(player.getUniqueId()));
				if (Bukkit.getPlayer(targetUUID) == null)
					endaTarget.logout();
				EndariumBukkit.getPlugin().getFriendsChannel().sendFriendChannel(FriendChannelType.REMOVE_FRIEND,
						player.getUniqueId(), targetUUID, player.getName(), target);
				return;

				/** FRIEND : ACCEPTER **/
			} else if (args[0].equalsIgnoreCase("accept")) {

				// Vérifier si le Jouer lui a fait une demande
				if (!(EndariumCommons.getInstance().getEndariumEntities().getFriendsManager()
						.hasFriendRequest(targetUUID, player.getUniqueId()))) {
					player.sendMessage(PREFIX + ChatColor.RED + "Cette demande d'ami n'existe pas ou a expiré.");
					return;
				}

				// Envoyer la demande d'acceptation
				player.sendMessage("");
				player.sendMessage(Messages.FRIENDS_PREFIX + ChatColor.WHITE + "Vous avez " + ChatColor.GREEN
						+ "accepté" + ChatColor.WHITE + " la demande d'ami.");
				player.sendMessage(ChatColor.WHITE + "Vous êtes désormais ami avec : " + ChatColor.YELLOW + target
						+ ChatColor.WHITE + ".");
				player.sendMessage("");
				SoundUtils.sendSound(player, Sound.LEVEL_UP);

				endaPlayer.setFriends(friends.addFriend(targetUUID));
				EndaPlayer endaTarget = EndaPlayer.get(targetUUID);
				endaTarget.setFriends(endaTarget.getFriends().addFriend(player.getUniqueId()));
				if (Bukkit.getPlayer(targetUUID) == null)
					endaTarget.logout();
				EndariumBukkit.getPlugin().getFriendsChannel().sendFriendChannel(FriendChannelType.ACCEPT_FRIEND,
						targetUUID, player.getUniqueId(), target, player.getName());
				return;

				/** FRIEND : REFUSER **/
			} else if (args[0].equalsIgnoreCase("refuse")) {

				// Vérifier si le Jouer lui a fait une demande
				if (!(EndariumCommons.getInstance().getEndariumEntities().getFriendsManager()
						.hasFriendRequest(targetUUID, player.getUniqueId()))) {
					player.sendMessage(PREFIX + ChatColor.RED + "Cette demande d'ami n'existe pas ou a expiré.");
					return;
				}

				// Envoyer la demande de refus
				player.sendMessage(Messages.FRIENDS_PREFIX + ChatColor.WHITE + "Vous avez " + ChatColor.RED + "refusé"
						+ ChatColor.WHITE + " la demande d'ami de " + ChatColor.YELLOW + target + ChatColor.WHITE
						+ ".");
				SoundUtils.sendSound(player, Sound.NOTE_BASS_DRUM);

				EndariumBukkit.getPlugin().getFriendsChannel().sendFriendChannel(FriendChannelType.REFUSE_FRIEND,
						targetUUID, player.getUniqueId(), target, player.getName());
				return;

			} else {
				this.sendHelp(player);
				return;
			}
		}
	}

	/**
	 * Message d'Aide de la Commande.
	 */
	private void sendHelp(Player player) {
		player.sendMessage("");
		player.sendMessage(" " + ChatColor.GRAY + "» " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Aide"
				+ ChatColor.WHITE + "│ " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Amis");
		player.sendMessage("");
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/friends add [pseudo] "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Ajouter un ami.");
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD
				+ "/friends remove [pseudo] " + ChatColor.WHITE + "» " + ChatColor.AQUA + "Supprimer un ami.");
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/friends list "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Afficher vos amis.");
		player.sendMessage(
				" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/friends notifications "
						+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Gérer les notifications d'amis.");
		player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/friends [on|off] "
				+ ChatColor.WHITE + "» " + ChatColor.AQUA + "Gérer les requêtes d'amis.");
		player.sendMessage("");
	}
}