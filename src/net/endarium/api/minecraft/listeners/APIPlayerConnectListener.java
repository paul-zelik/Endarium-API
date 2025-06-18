package net.endarium.api.minecraft.listeners;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.UUID;

import net.endarium.api.games.servers.CrystaliserServerManager;
import net.endarium.api.players.login.LoginManager;
import net.endarium.api.players.login.PreniumManager;
import net.endarium.api.players.stats.games.*;
import net.endarium.api.utils.builders.titles.TitleBuilder;
import net.endarium.crystaliser.servers.ServerType;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import net.endarium.api.games.GameLobbyTask;
import net.endarium.api.games.GameStatus;
import net.endarium.api.games.servers.CrystaliserAPI;
import net.endarium.api.minecraft.EndariumBukkit;
import net.endarium.api.minecraft.listeners.customs.EndaPlayerJoinEvent;
import net.endarium.api.minecraft.listeners.customs.EndaPlayerQuitEvent;
import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.EndaPlayerCache;
import net.endarium.api.players.language.Languages;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.EndariumAPI;
import net.endarium.api.utils.LangMessages;
import net.endarium.api.utils.Messages;
import net.endarium.api.utils.builders.items.ItemFactory;
import net.endarium.api.utils.builders.scoreboards.TeamTagsSign;
import net.endarium.api.utils.builders.titles.ActionBarBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class APIPlayerConnectListener implements Listener {


	private String PREFIX = Messages.ENDARIUM_PREFIX;

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerConnect(PlayerJoinEvent event) {

		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();

		System.out.println(uuid);

		// Sécurité de Connexion via un Proxy Externe
		if ((EndaPlayerCache.get(uuid) == null) || (!(EndaPlayerCache.get(uuid).isUseEndariumProxy()))) {
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime localDateTime = LocalDateTime.now();
			player.kickPlayer(ChatColor.DARK_RED + "● " + ChatColor.RED + "Sécurité du Proxy Endarium"
					+ ChatColor.DARK_RED + " ●\n" + ChatColor.GRAY + "Exclusion : " + ChatColor.WHITE
					+ "Nous ne parvenons pas à vous identifier sur nos Proxys.\n§f\n" + ChatColor.DARK_GRAY + "ID : "
					+ UUID.randomUUID() + " - " + dateTimeFormatter.format(localDateTime));
		}




		// Génération du compte du Joueur
		EndaPlayer endaPlayer = EndaPlayer.get(uuid);
		endaPlayer.setEndaPlayerCache(
				endaPlayer.getEndaPlayerCache().setServerName(CrystaliserAPI.getEndaServer().getServerName()));
		endaPlayer.setMuteInfos(
				EndariumBukkit.getPlugin().getEndariumCommons().getEndariumEntities().getMuteManager().isMute(uuid));
		event.setJoinMessage(null);

		//if (endaPlayer.isAdministrator())
		//	player.setOp(true);

		// Générer les paramètres de connexions
		player.setFoodLevel(20);
		player.setMaxHealth(20);
		player.setHealth(20);
		player.setGameMode(GameMode.ADVENTURE);
		player.getInventory().clear();
		player.getInventory().setHelmet(new ItemStack(Material.AIR));
		player.getInventory().setChestplate(new ItemStack(Material.AIR));
		player.getInventory().setLeggings(new ItemStack(Material.AIR));
		player.getInventory().setBoots(new ItemStack(Material.AIR));
		player.teleport(EndariumAPI.getGameSetting().getLobbyLocation());
		this.sendSettingsPlayerConnect(player, endaPlayer);

		if (CrystaliserAPI.getEndaServer().getServerType() == ServerType.HUB)
			player.teleport(new Location(player.getWorld(), 0,86,-2));

		// Gérer les Worlds des Serveurs
		World world = player.getWorld();
		world.setTime(6000);
		world.setStorm(false);
		world.setGameRuleValue("doDaylightCycle", "false");
		world.setGameRuleValue("doFireTick", "false");
		world.setGameRuleValue("spectatorsGenerateChunks", "false");
		world.setGameRuleValue("sendCommandFeedback", "false");

		// Gérer l'évènement de Join Custom
		Bukkit.getPluginManager().callEvent(new EndaPlayerJoinEvent(player, endaPlayer));


		// Gestion du Staff en Mode de Modération
		if (endaPlayer.isModeModeration()) {
			if ((GameStatus.isStatus(GameStatus.LOBBY)) && (EndariumAPI.getGameSetting().isGameServer())) {
				endaPlayer.setModeModeration(false);
				player.sendMessage(Messages.ZMODS_PREFIX + ChatColor.RED
						+ "Attention ! Vous venez de rejoindre une partie, nous avons désactivé votre Mode de Modération.");
			} else {
				Bukkit.getScheduler().runTaskLater(EndariumBukkit.getPlugin(), new Runnable() {
					@Override
					public void run() {
						Bukkit.getOnlinePlayers().forEach(players -> {

							EndaPlayer endaPlayerOnline = EndaPlayer.get(players.getUniqueId());

							if ((players != player) && (!(endaPlayerOnline.isModeModeration()))) {
								players.hidePlayer(player);
							}

							// Annoncer aux Modérateur qu'il rejoint le ZMOD
							if (endaPlayerOnline.isModeModeration())
								players.sendMessage(ChatColor.BLUE + "[ZMOD][" + ChatColor.GREEN + "+" + ChatColor.BLUE
										+ "] " + player.getName());
						});
					}
				}, 8L);

				// Appliquer le Tag de Modération
				TeamTagsSign.setNameTag(player, "§aa",
						ChatColor.BLUE + "" + ChatColor.BOLD + "[ZMOD] " + ChatColor.BLUE);
				player.sendMessage(Messages.ZMODS_PREFIX + ChatColor.RED + "Vous êtes en Mode de Modération !");
			}
		}

		if (CrystaliserAPI.getEndaServer().getServerType() == ServerType.UNKNOW)
			player.teleport(new Location(player.getWorld(),0,6,0));

		LoginManager loginManager = new LoginManager();
		PreniumManager preniumManager = new PreniumManager();



		if (!(loginManager.isLogged(player.getUniqueId()))) {
			if (loginManager.loginPlayerExists(player.getUniqueId())) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3000, 20));
				player.sendMessage(PREFIX + ChatColor.YELLOW + "Vous devez vous login. /login <motdepasse>");
				new TitleBuilder(ChatColor.LIGHT_PURPLE + "Connectez vous", ChatColor.WHITE + "/login <motdepasse>").send(player);
			} else {
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3000, 20));
				player.sendMessage((PREFIX + ChatColor.YELLOW + "Merci de vous enregistrer. /register <motdepasse> <motdepasse>"));
				new TitleBuilder(ChatColor.LIGHT_PURPLE + "Enregistrez vous", ChatColor.WHITE + "/register <motdepasse> <motdepasse>").send(player);
			}
		} else {
			player.removePotionEffect(PotionEffectType.BLINDNESS);
		}


	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {

		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		EndaPlayer.get(uuid).logout();
		event.setQuitMessage(null);


		// Gestion des Teams en Jeux
		if ((EndariumAPI.getGameSetting().isGameServer()) && (EndariumAPI.getGameSetting().isTeamEnable()))
			EndariumAPI.getGameSetting().getTeamManager().removePlayerFromAll(player);

		// Gérer l'évènement de Quit Custom
		Bukkit.getPluginManager().callEvent(new EndaPlayerQuitEvent(player));

		// Gestion des Statistiques (Logout Cache)
		HungerGamesStats.logoutStats(uuid);
		CrystalRushStats.logoutStats(uuid);
		PunchoutStats.logoutStats(uuid);
		UhcStats.logoutStats(uuid);
		UhcmeetupStats.logoutStats(uuid);


		// Gestion de la Déconnexion d'un Jeux
		if ((EndariumAPI.getGameSetting().isGameServer()) && (GameStatus.isStatus(GameStatus.LOBBY)))
			new ActionBarBuilder(ChatColor.DARK_RED + player.getName() + ChatColor.RED + " a quitté la partie.")
					.sendToServer();
	}

	/**
	 * Gestion des paramètres de connexion des serveurs.
	 * 
	 * @param player
	 * @param endaPlayer
	 */
	private void sendSettingsPlayerConnect(Player player, EndaPlayer endaPlayer) {
		if (EndariumAPI.getGameSetting().isGameServer()) {

			// Gestion des informations dans les Jeux
			if (GameStatus.isStatus(GameStatus.LOBBY)) {

				// Connexion dans un Jeu en attente de démarrage
				HashMap<String, String> joinGameMessagesMap = new HashMap<>();

				joinGameMessagesMap.put(Languages.FRENCH.getZoneLangue(),
						EndariumAPI.getGameSetting().getGamePrefix() + ChatColor.WHITE + player.getName()
								+ ChatColor.GRAY + " a rejoint la partie ! " + ChatColor.GREEN + "("
								+ Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + ")");

				joinGameMessagesMap.put(Languages.ENGLISH.getZoneLangue(),
						EndariumAPI.getGameSetting().getGamePrefix() + ChatColor.WHITE + player.getName()
								+ ChatColor.GRAY + " joined the game ! " + ChatColor.GREEN + "("
								+ Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + ")");

				joinGameMessagesMap.put(Languages.SPANISH.getZoneLangue(),
						EndariumAPI.getGameSetting().getGamePrefix() + ChatColor.WHITE + player.getName()
								+ ChatColor.GRAY + " se unió al juego ! " + ChatColor.GREEN + "("
								+ Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + ")");

				LangMessages.broadcastMessage(joinGameMessagesMap);

				Bukkit.getOnlinePlayers().forEach(players -> {
					String joinMessageActionBar = LangMessages.getPlayerMessage(players,
							ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " a rejoint la partie.",
							ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " joined the game.",
							ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " se unió al juego.");
					new ActionBarBuilder(joinMessageActionBar).sendTo(players);
				});

				player.setLevel(GameLobbyTask.lobbyTimer);

				// Ajouter les Items Principaux dans les Jeux

				/** ITEM : GESTION DES KITS */
				if (EndariumAPI.getGameSetting().isKitsEnable())
					player.getInventory().setItem(0,
							new ItemFactory(Material.NAME_TAG).withName(LangMessages.getPlayerMessage(player,
									ChatColor.GREEN + "" + ChatColor.UNDERLINE + "Sélecteur de Kits",
									ChatColor.GREEN + "" + ChatColor.UNDERLINE + "Kits Selector",
									ChatColor.GREEN + "" + ChatColor.UNDERLINE + "Selector de Kits")).done());

				/** ITEM : GESTION DES TEAMS */
				if (EndariumAPI.getGameSetting().isTeamEnable())
					player.getInventory().setItem(3,
							new ItemFactory(Material.NETHER_STAR).withName(LangMessages.getPlayerMessage(player,
									ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "Sélecteur de Teams",
									ChatColor.GREEN + "" + ChatColor.UNDERLINE + "Teams Selector",
									ChatColor.GREEN + "" + ChatColor.UNDERLINE + "Selector de Equipo")).done());

				/** ITEM : RETOUR AU HUB */
				player.getInventory().setItem(8,
						new ItemFactory(Material.BED).withName(LangMessages.getPlayerMessage(player,
								ChatColor.RED + "" + ChatColor.UNDERLINE + "Retour au Hub",
								ChatColor.RED + "" + ChatColor.UNDERLINE + "Return to the Hub",
								ChatColor.RED + "" + ChatColor.UNDERLINE + "Volver al Hub")).done());

				/** ITEM : STATISTIQUES */
				if (EndariumAPI.getGameSetting().isTeamEnable() || EndariumAPI.getGameSetting().isKitsEnable()) {
					player.getInventory().setItem(5, new ItemFactory(Material.ITEM_FRAME)
							.withName(ChatColor.GREEN + "" + ChatColor.UNDERLINE + "Statistiques").done());
				} else {
					player.getInventory().setItem(0, new ItemFactory(Material.ITEM_FRAME)
							.withName(ChatColor.GREEN + "" + ChatColor.UNDERLINE + "Statistiques").done());
				}


				// Lancement du Timer pour le démarrage du Jeu
				if ((Bukkit.getOnlinePlayers().size() >= EndariumAPI.getGameSetting().getMinPlayers())
						&& (!(GameLobbyTask.isStarted))) {
					new GameLobbyTask(EndariumAPI.getGameSetting().getGameInterface())
							.runTaskTimer(EndariumBukkit.getPlugin(), 0L, 20L);
					GameLobbyTask.setStarted(true);
				}

			}

			// Gestion d'un Jeu qui est en cours
			if (!(GameStatus.isStatus(GameStatus.LOBBY))) {
				// Message pour un Spectateur qui rejoint
				player.sendMessage("");
				player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "ATTENTION" + ChatColor.GRAY + "│ "
						+ ChatColor.YELLOW + "Il vous est impossible de jouer !");
				player.sendMessage(ChatColor.AQUA + "Vous avez rejoint la partie en mode spectateur.");
				player.sendMessage("");
			}

		} else {

			// Gestion des informations dans le Hub
			if ((endaPlayer.getRank().getPower() >= Rank.VIPPLUS.getPower()) && (!(endaPlayer.isModeModeration()))) {
				HashMap<String, String> joinedMessage = new HashMap<String, String>();
				joinedMessage.put(Languages.FRENCH.getZoneLangue(),
						endaPlayer.getRank().getChatColor() + endaPlayer.getRank().getPrefix()
								+ Messages.getRankSpaceConvention(endaPlayer.getRank())
								+ endaPlayer.getRank().getChatColor() + player.getName() + ChatColor.GRAY
								+ " a rejoint le hub.");
				joinedMessage.put(Languages.ENGLISH.getZoneLangue(), endaPlayer.getRank().getChatColor()
						+ endaPlayer.getRank().getPrefix() + Messages.getRankSpaceConvention(endaPlayer.getRank())
						+ endaPlayer.getRank().getChatColor() + player.getName() + ChatColor.GRAY + " joined the hub.");
				LangMessages.broadcastMessage(joinedMessage);
			}
			TeamTagsSign.setNameTag(player, endaPlayer.getRank().getOrderCode() + endaPlayer.getRank().getName(),
					endaPlayer.getRank().getChatColor() + endaPlayer.getRank().getPrefix()
							+ Messages.getRankSpaceConvention(endaPlayer.getRank()));
		}


	}
}