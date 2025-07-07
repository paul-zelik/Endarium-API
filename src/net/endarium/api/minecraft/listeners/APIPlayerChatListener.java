package net.endarium.api.minecraft.listeners;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import net.endarium.api.players.login.LoginManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.endarium.api.games.GameStatus;
import net.endarium.api.games.teams.Teams;
import net.endarium.api.minecraft.commands.mods.SlowChatCommand;
import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.EndariumAPI;
import net.endarium.api.utils.Messages;
import net.endarium.api.utils.builders.titles.ActionBarBuilder;
import net.md_5.bungee.api.ChatColor;

public class APIPlayerChatListener implements Listener {

	private Map<String, Long> lastSpeakSlow = new HashMap<String, Long>();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {

		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		EndaPlayer endaPlayer = EndaPlayer.get(uuid);
		Rank playerRank = endaPlayer.getRank();

		String message = event.getMessage();
		event.setCancelled(true);

		if (endaPlayer.isLogged()) {

			// (t)Chat en ZMOD
			if (endaPlayer.isModeModeration()) {

				// Parler dans le serveur en entier
				if (message.startsWith("!")) {
					String newMessage = message.replaceFirst("!", "");
					Bukkit.getOnlinePlayers().forEach(playerOnline -> {
						if (newMessage.contains(playerOnline.getName()) && (!(player.equals(playerOnline)))) {
							playerOnline.sendMessage(endaPlayer.getRank().getChatColor() + endaPlayer.getRank().getPrefix()
									+ Messages.getRankSpaceConvention(endaPlayer.getRank()) + player.getName()
									+ ChatColor.GRAY + " » " + Messages.getRankChatMessageColor(playerRank)
									+ newMessage
									.replaceAll(playerOnline.getName(),
											ChatColor.GOLD + "@" + playerOnline.getName()
													+ Messages.getRankChatMessageColor(playerRank))
									.replace("<3",
											ChatColor.RED + "❤" + Messages.getRankChatMessageColor(playerRank)));
							this.sendAlertMention(playerOnline, player);
						} else {
							playerOnline.sendMessage(endaPlayer.getRank().getChatColor() + endaPlayer.getRank().getPrefix()
									+ Messages.getRankSpaceConvention(endaPlayer.getRank()) + player.getName()
									+ ChatColor.GRAY + " » " + Messages.getRankChatMessageColor(playerRank)
									+ newMessage.replace("<3",
									ChatColor.RED + "❤" + Messages.getRankChatMessageColor(playerRank)));
						}
					});
					return;
				}

				// Parler dans le ZMOD
				Bukkit.getOnlinePlayers().forEach(playersOnline -> {
					EndaPlayer endaPlayerOnline = EndaPlayer.get(playersOnline.getUniqueId());
					if ((endaPlayerOnline != null) && (endaPlayerOnline.isModeModeration())) {
						playersOnline.sendMessage(ChatColor.BLUE + "[ZMod Chat] " + endaPlayer.getRank().getPrefix()
								+ Messages.getRankSpaceConvention(endaPlayer.getRank()) + player.getName() + ChatColor.GRAY
								+ " » " + ChatColor.AQUA + message);
					}
				});
				return;
			}

			// MANAGEMENT : Mute
			if (endaPlayer.getMuteInfos() != null) {
				player.sendMessage(ChatColor.GOLD + "[Mute] " + ChatColor.WHITE
						+ "Vous êtes mute de ce serveur pour la raison suivante : " + ChatColor.YELLOW
						+ endaPlayer.getMuteInfos().getReason() + ChatColor.WHITE + ".");
				DateFormat shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT,
						new Locale("fr"));
				player.sendMessage(ChatColor.GRAY + "Votre sanction sera levée le : " + ChatColor.AQUA
						+ shortDateFormat.format(endaPlayer.getMuteInfos().getExpiryDate()));
				return;
			}

			// MANAGEMENT : Gestion du SlowChat
			if (SlowChatCommand.currentSlow > 0) {
				if (playerRank.getPower() < Rank.FAMOUS.getPower()) {
					Long lastSpeakStamp = lastSpeakSlow.get(player.getName());
					if (lastSpeakStamp == null)
						lastSpeakStamp = 0L;
					long result = System.currentTimeMillis() - lastSpeakStamp;
					if (result < (SlowChatCommand.currentSlow * 1000)) {
						event.setCancelled(true);
						player.sendMessage(SlowChatCommand.PREFIX + "" + ChatColor.WHITE + "Un message toutes les "
								+ ChatColor.RED + SlowChatCommand.currentSlow + " seconde(s)" + ChatColor.WHITE + ".");
						return;
					}
					lastSpeakSlow.put(player.getName(), System.currentTimeMillis());
				}
			}

			// Gestion du (t)Chat des Spectateurs
			if ((endaPlayer.isSpectator()) && (GameStatus.isStatus(GameStatus.GAME))) {
				Bukkit.getOnlinePlayers().forEach(playerOnline -> {
					EndaPlayer endaPlayerTarget = EndaPlayer.get(playerOnline.getUniqueId());
					if ((playerOnline != null) && (endaPlayerTarget != null)) {
						if (endaPlayerTarget.isSpectator()) {
							if (message.contains(playerOnline.getName())) {
								playerOnline
										.sendMessage(ChatColor.GRAY + "[Spectateur] " + endaPlayer.getRank().getChatColor()
												+ player.getName() + ChatColor.GRAY + " » " + ChatColor.GRAY
												+ message
												.replaceAll(playerOnline.getName(),
														ChatColor.GOLD + "@" + playerOnline.getName()
																+ ChatColor.GRAY)
												.replace("<3", ChatColor.RED + "❤" + ChatColor.GRAY));
								this.sendAlertMention(playerOnline, player);
							} else {
								playerOnline.sendMessage(ChatColor.GRAY + "[Spectateur] "
										+ endaPlayer.getRank().getChatColor() + player.getName() + ChatColor.GRAY + " » "
										+ ChatColor.GRAY + message.replace("<3", ChatColor.RED + "❤" + ChatColor.GRAY));
							}
						}
					}
				});
				return;
			}

			// MANAGEMENT : Gestion des GAMES
			if ((EndariumAPI.getGameSetting().isTeamEnable())
					&& (EndariumAPI.getGameSetting().getTeamManager().getPlayerTeam(player) != null)
					&& (GameStatus.isStatus(GameStatus.GAME))) {
				Teams teams = EndariumAPI.getGameSetting().getTeamManager().getPlayerTeam(player);

				// Gestion si le Joueur parler en Général
				if (message.startsWith("!")) {
					Bukkit.getOnlinePlayers().forEach(playerOnline -> {
						if (message.contains(playerOnline.getName())) {
							playerOnline
									.sendMessage(ChatColor.GOLD + "[All] " + teams.getPrefix() + " " + player.getName()
											+ ChatColor.GRAY + " » " + Messages.getRankChatMessageColor(playerRank)
											+ message.replaceFirst("!", "").replaceAll(playerOnline.getName(),
													ChatColor.GOLD + "@" + playerOnline.getName()
															+ Messages.getRankChatMessageColor(playerRank))
											.replace("<3", ChatColor.RED + "❤"
													+ Messages.getRankChatMessageColor(playerRank)));
							this.sendAlertMention(playerOnline, player);
						} else {
							playerOnline.sendMessage(ChatColor.GOLD + "[All] " + teams.getPrefix() + " " + player.getName()
									+ ChatColor.GRAY + " » " + Messages.getRankChatMessageColor(playerRank)
									+ message.replaceFirst("!", "").replace("<3",
									ChatColor.RED + "❤" + Messages.getRankChatMessageColor(playerRank)));
						}
					});
					return;
				}

				// Envoie d'un Message à la Team du Joueur
				for (Player playerTeam : EndariumAPI.getGameSetting().getTeamManager().getTeamPlayerList(teams)) {
					if (message.contains(playerTeam.getName())) {
						playerTeam.sendMessage(teams.getPrefix() + " " + player.getName() + ChatColor.GRAY + " » "
								+ Messages.getRankChatMessageColor(playerRank)
								+ message
								.replaceAll(playerTeam.getName(),
										ChatColor.GOLD + "@" + playerTeam.getName()
												+ Messages.getRankChatMessageColor(playerRank))
								.replace("<3", ChatColor.RED + "❤" + Messages.getRankChatMessageColor(playerRank)));
						this.sendAlertMention(playerTeam, player);
					} else {
						playerTeam.sendMessage(teams.getPrefix() + " " + player.getName() + ChatColor.GRAY + " » "
								+ Messages.getRankChatMessageColor(playerRank) + message.replace("<3",
								ChatColor.RED + "❤" + Messages.getRankChatMessageColor(playerRank)));
					}
				}
				return;
			}

			// Gestion du (t)Chat dans les Hubs
			Bukkit.getOnlinePlayers().forEach(playerOnline -> {
				if (message.contains(playerOnline.getName()) && (!(player.equals(playerOnline)))) {
					playerOnline.sendMessage(endaPlayer.getRank().getChatColor() + endaPlayer.getRank().getPrefix()
							+ Messages.getRankSpaceConvention(endaPlayer.getRank()) + player.getName() + ChatColor.GRAY
							+ " » " + Messages.getRankChatMessageColor(playerRank)
							+ message
							.replaceAll(playerOnline.getName(),
									ChatColor.GOLD + "@" + playerOnline.getName()
											+ Messages.getRankChatMessageColor(playerRank))
							.replace("<3", ChatColor.RED + "❤" + Messages.getRankChatMessageColor(playerRank)));
					this.sendAlertMention(playerOnline, player);
				} else {
					playerOnline.sendMessage(endaPlayer.getRank().getChatColor() + endaPlayer.getRank().getPrefix()
							+ Messages.getRankSpaceConvention(endaPlayer.getRank()) + player.getName() + ChatColor.GRAY
							+ " » " + Messages.getRankChatMessageColor(playerRank)
							+ message.replace("<3", ChatColor.RED + "❤" + Messages.getRankChatMessageColor(playerRank)));
				}
			});
		} else {
			player.sendMessage(Messages.ENDARIUM_PREFIX + org.bukkit.ChatColor.YELLOW + "Tu dois te login.");
		}
	}

	/**
	 * Envoyer une alerte de mention à un Joueur.
	 * 
	 * @param player
	 * @param target
	 */
	private void sendAlertMention(Player player, Player target) {
		new ActionBarBuilder(
				ChatColor.GOLD + "" + ChatColor.BOLD + "@" + target.getName() + ChatColor.YELLOW + " vous a mentionné.")
				.sendTo(player);
		player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1.0f, 1.0f);
	}
}