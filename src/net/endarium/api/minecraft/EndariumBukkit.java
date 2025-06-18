package net.endarium.api.minecraft;

import java.io.IOException;

import net.endarium.api.minecraft.channels.ServerBroadcasterChannel;
import net.endarium.api.minecraft.channels.host.HostAnnounceChannel;
import net.endarium.api.minecraft.channels.host.HostTeleportChannel;
import net.endarium.api.minecraft.commands.administrator.BroadcastCommand;
import net.endarium.api.minecraft.commands.administrator.BuildServerTPCommand;
import net.endarium.api.minecraft.commands.administrator.OpCommand;
import net.endarium.api.minecraft.commands.system.LagCommand;
import net.endarium.api.minecraft.commands.mods.*;
import net.endarium.api.minecraft.commands.system.*;
import net.endarium.api.minecraft.listeners.*;
import net.endarium.api.players.moderation.ban.BanInfos;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.endarium.api.EndariumCommons;
import net.endarium.api.config.MySQLConfig;
import net.endarium.api.config.RedisConfig;
import net.endarium.api.games.GameSetting;
import net.endarium.api.games.GameStatus;
import net.endarium.api.games.servers.CrystaliserAPI;
import net.endarium.api.minecraft.channels.FriendsChannel;
import net.endarium.api.minecraft.channels.PrivateMessageChannel;
import net.endarium.api.minecraft.channels.staff.RankChangeChannel;
import net.endarium.api.minecraft.commands.system.HostCommand;
import net.endarium.api.minecraft.commands.HubCommand;
import net.endarium.api.minecraft.commands.PlayerProcessCommandListener;
import net.endarium.api.minecraft.commands.administrator.RankCommand;
import net.endarium.api.minecraft.listeners.games.GameListener;
import net.endarium.api.minecraft.runnables.EndariumRunnable;
import net.endarium.api.utils.EndariumAPI;
import net.endarium.api.utils.commands.CommandManager;
import net.endarium.crystaliser.servers.ServerStatus;

public class EndariumBukkit extends JavaPlugin {

	private EndariumCommons endariumCommons;
	private CommandManager commandManager;

	private HostTeleportChannel hostTeleportChannel;
	private HostAnnounceChannel hostAnnounceChannel;

	private ServerBroadcasterChannel serverBroadcasterChannel;

	private RankChangeChannel rankChangeChannel;

	private FriendsChannel friendsChannel;
	private PrivateMessageChannel privateMessageChannel;

	@Override
	public void onLoad() {

		// Démarrer les Commons de l'API
		this.saveDefaultConfig();
		MySQLConfig mySQLConfig = new MySQLConfig(getConfig().getString("database.mysql.hostname"),
				getConfig().getInt("database.mysql.port"), getConfig().getString("database.mysql.database"),
				getConfig().getString("database.mysql.username"), getConfig().getString("database.mysql.password"),
				getConfig().getBoolean("database.enable_mysql"));
		RedisConfig redisConfig = new RedisConfig(getConfig().getString("database.redis.hostname"),
				getConfig().getInt("database.redis.port"), getConfig().getInt("database.redis.database"),
				getConfig().getString("database.redis.password"), getConfig().getBoolean("database.enable_redis"));

		this.endariumCommons = new EndariumCommons(mySQLConfig, redisConfig, true);
		this.commandManager = new CommandManager();

		// Gestion d'enregistrement du Serveur
		this.initGameServerInfos(
				getConfig().get("gameServer.enable_game") != null ? getConfig().getBoolean("gameServer.enable_game")
						: false);

		super.onLoad();
	}

	@Override
	public void onEnable() {

		// Enregistrement des Channels & Messaging
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.rankChangeChannel = new RankChangeChannel();
		this.friendsChannel = new FriendsChannel();
		this.privateMessageChannel = new PrivateMessageChannel();
		this.serverBroadcasterChannel = new ServerBroadcasterChannel();
		this.hostTeleportChannel = new HostTeleportChannel();
		this.hostAnnounceChannel = new HostAnnounceChannel();

		// Enregistrement des Listeners
		PluginManager pluginManager = Bukkit.getPluginManager();

		pluginManager.registerEvents(new APIPlayerConnectListener(), this);
		pluginManager.registerEvents(new APIPlayerChatListener(), this);
		pluginManager.registerEvents(new APIRankChangeListener(), this);
		pluginManager.registerEvents(new WorldListener(), this);
		pluginManager.registerEvents(new APIPlayerMove(), this);
	//	pluginManager.registerEvents(new APIPlayerInteractListener(), this);

		pluginManager.registerEvents(new PlayerProcessCommandListener(), this);

		pluginManager.registerEvents(new GameListener(), this);

		// Enregistrement des Commands
		commandManager.registercommand(new RankCommand());

		commandManager.registercommand(new ModsCommands());
		commandManager.registercommand(new KickCommand());
		commandManager.registercommand(new BanCommand());
		commandManager.registercommand(new MuteCommand());
		commandManager.registercommand(new SlowChatCommand());
		commandManager.registercommand(new BuildServerTPCommand());
		commandManager.registercommand(new ZPlayCommand());
		commandManager.registercommand(new ZReportCommand());
		commandManager.registercommand(new FishCommand());
		commandManager.registercommand(new OpCommand());
		commandManager.registercommand(new GameStartCommand());

		commandManager.registercommand(new SponsorshipCommand());
		commandManager.registercommand(new HostCommand());
		commandManager.registercommand(new LoginCommand());
		commandManager.registercommand(new RegisterCommand());
		commandManager.registercommand(new ReplayCommand());


		commandManager.registercommand(new HubCommand());
		commandManager.registercommand(new FriendsCommand());
		commandManager.registercommand(new ReportCommand());
		commandManager.registercommand(new MessagesPrivatesCommand());
		commandManager.registercommand(new OthersCommand());
		commandManager.registercommand(new LagCommand());
		commandManager.registercommand(new LangCommand());
		commandManager.registercommand(new BroadcastCommand());
		commandManager.registercommand(new MineCommand());
		commandManager.registercommand(new JumpCommand());

		// Enregistrement des Runnables
		new EndariumRunnable().runTaskTimer(EndariumBukkit.getPlugin(), 0L, 20L);

		// Enregistrement des informations de Host
		this.initHostGameInfos();

		super.onEnable();
	}

	@Override
	public void onDisable() {
		try {

			this.endariumCommons.close();

			// Gestion des Channels/Redis
			this.rankChangeChannel.close();
			this.friendsChannel.close();

		} catch (IOException exception) {
		}
		super.onDisable();
	}

	/**
	 * Initialiser les paramètres d'un serveur.
	 * 
	 * @param gameServer
	 */
	private void initGameServerInfos(boolean gameServer) {

		// Définir les Settings Crystaliser
		CrystaliserAPI.getEndaServer().setGameServer(gameServer);

		// Gérer les paramètres d'un serveur de Jeu
		if (gameServer) {

			// Initialiser les paramètres de la Partie
			String gameName = CrystaliserAPI.getEndaServer().getGameType().getDisplayName();
			String worldName = getConfig().getString("gameServer.world_name");
			CrystaliserAPI.getEndaServer().setMapName(CrystaliserAPI.getEndaServer().getMapInfos().getDisplayName());

			// Gestion des paramètres de Teams
			boolean spectatorEnable = getConfig().getBoolean("gameServer.enable_spectator");
			CrystaliserAPI.getEndaServer().setSpectator(spectatorEnable);
			boolean kitsEnable = getConfig().getBoolean("gameServer.enable_kits");
			boolean teamEnable = getConfig().getBoolean("gameServer.teams.enable_team");
			int teamNumber = getConfig().getInt("gameServer.teams.team_number");
			int minPlayers = getConfig().getInt("gameServer.min_players");

			CrystaliserAPI.getEndaServer().setServerStatus(ServerStatus.PUBLIC);

			// Construction de l'Object GameSetting et Initialisation
			GameSetting gameSetting = new GameSetting(gameName, worldName, minPlayers, teamNumber, gameServer,
					teamEnable, kitsEnable, spectatorEnable);
			EndariumAPI.setGameSetting(gameSetting);
			GameStatus.setStatus(GameStatus.LOBBY);
			System.out.print(EndariumAPI.getPrefixAPI() + "Vous venez d'initialiser un serveur de jeu : " + gameName);

		} else {
			System.out.print(EndariumAPI.getPrefixAPI() + "Ce serveur n'est pas un serveur de jeu...");
		}
	}

	/**
	 * Initialiser les paramètres de Host.
	 */
	private void initHostGameInfos() {
		// Détection d'un Type Host
		if (CrystaliserAPI.isHostServer()) {
			CrystaliserAPI.setServerStatus(ServerStatus.HOST);
			Bukkit.getScheduler().runTaskLater(EndariumBukkit.getPlugin(), new Runnable() {
				@Override
				public void run() {
					EndariumBukkit.getPlugin().getHostTeleportChannel()
							.sendHostPlayerServer(CrystaliserAPI.getEndaServer());
				}
			}, 10 * 20L);
		}
	}

	public ServerBroadcasterChannel getServerBroadcasterChannel() {
		return serverBroadcasterChannel;
	}

	public EndariumCommons getEndariumCommons() {
		return endariumCommons;
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public RankChangeChannel getRankChangeChannel() {
		return rankChangeChannel;
	}

	public FriendsChannel getFriendsChannel() {
		return friendsChannel;
	}

	public PrivateMessageChannel getPrivateMessageChannel() {
		return privateMessageChannel;
	}

	public HostTeleportChannel getHostTeleportChannel() {
		return hostTeleportChannel;
	}

	public HostAnnounceChannel getHostAnnounceChannel() {
		return hostAnnounceChannel;
	}

	public static EndariumBukkit getPlugin() {
		return getPlugin(EndariumBukkit.class);
	}
}