package net.endarium.api.bungeecord;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.io.ByteStreams;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;

import net.endarium.api.EndariumCommons;
import net.endarium.api.bungeecord.channels.RedisBungeeChannel;
import net.endarium.api.bungeecord.channels.RedisBungeeListener;
import net.endarium.api.bungeecord.commands.ListPlayersCommand;
import net.endarium.api.bungeecord.commands.player.PartyCommand;
import net.endarium.api.bungeecord.commands.staff.AntibotCommand;
import net.endarium.api.bungeecord.commands.staff.StaffChatCommand;
import net.endarium.api.bungeecord.listeners.BungeeBalancingHubListener;
import net.endarium.api.bungeecord.listeners.BungeePlayerConnectListener;
import net.endarium.api.bungeecord.listeners.BungeeServerListPingListener;
import net.endarium.api.config.MySQLConfig;
import net.endarium.api.config.RedisConfig;
import net.endarium.api.players.party.Party;
import net.endarium.api.players.party.PartyManager;
import net.endarium.api.utils.EndariumAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

public class EndariumBungeeCord extends Plugin {

	private static EndariumBungeeCord instance;

	private EndariumCommons endariumCommons;
	private Configuration configuration;

	private static RedisBungeeAPI redisBungeeAPI;
	private PartyManager partyManager;

	private int slots = 300;
	private Boolean antibotstatue = false;

	@Override
	public void onLoad() {

		// Démarrer les Commons de l'API
		instance = this;
		this.loadConfig();
		MySQLConfig mySQLConfig = new MySQLConfig(configuration.getString("database.mysql.hostname"),
				configuration.getInt("database.mysql.port"), configuration.getString("database.mysql.database"),
				configuration.getString("database.mysql.username"), configuration.getString("database.mysql.password"),
				configuration.getBoolean("database.enable_mysql"));
		RedisConfig redisConfig = new RedisConfig(configuration.getString("database.redis.hostname"),
				configuration.getInt("database.redis.port"), configuration.getInt("database.redis.database"),
				configuration.getString("database.redis.password"), configuration.getBoolean("database.enable_redis"));
		this.endariumCommons = new EndariumCommons(mySQLConfig, redisConfig, configuration.getBoolean("developer"));

		super.onLoad();
	}


	@Override
	public void onEnable() {

		// Enregistrement des Channels & Messaging
		redisBungeeAPI = RedisBungee.getApi();
		this.partyManager = new PartyManager();
		List<String> channelList = new ArrayList<String>();
		for (RedisBungeeChannel redisBungeeChannel : RedisBungeeChannel.values()) {
			channelList.add(redisBungeeChannel.getName());
			ProxyServer.getInstance().registerChannel(redisBungeeChannel.getName());
		}
		String[] channelArray = channelList.toArray(new String[0]);
		redisBungeeAPI.registerPubSubChannels(channelArray);

		// Enregistrement des Events/Listeners/Commands
		PluginManager pluginManager = ProxyServer.getInstance().getPluginManager();

		pluginManager.registerListener(instance, new RedisBungeeListener());

		pluginManager.registerListener(instance, new BungeePlayerConnectListener());
		pluginManager.registerListener(instance, new BungeeBalancingHubListener());
		pluginManager.registerListener(instance, new BungeeServerListPingListener());

		pluginManager.registerCommand(instance, new ListPlayersCommand());
		pluginManager.registerCommand(instance, new StaffChatCommand());
		pluginManager.registerCommand(instance, new AntibotCommand());
		pluginManager.registerCommand(instance, new PartyCommand());

		// Gestion des différentes fonctionnalitées BungeeCord
		this.updatePlayersTabList();

		super.onEnable();
	}

	@Override
	public void onDisable() {
		try {
			List<String> channelList = new ArrayList<String>();
			for (RedisBungeeChannel redisBungeeChannel : RedisBungeeChannel.values()) {
				channelList.add(redisBungeeChannel.getName());
				ProxyServer.getInstance().unregisterChannel(redisBungeeChannel.getName());
			}
			String[] channelArray = channelList.toArray(new String[0]);
			redisBungeeAPI.unregisterPubSubChannels(channelArray);
			redisBungeeAPI = null;
			this.endariumCommons.close();
		} catch (Exception exception) {
			System.err.println(EndariumAPI.getPrefixAPI() + "Impossible de fermer correctement le proxy...");
		}

		super.onDisable();
	}

	public PartyManager getPartyManager() {
		return this.partyManager;
	}

	/**
	 * Envoyer le Tablist à un Joueur.
	 */
	public void sendTablist(ProxiedPlayer proxiedPlayer) {
		proxiedPlayer.setTabHeader(
				new TextComponent(ChatColor.AQUA + "Vous jouez sur " + ChatColor.GOLD + "" + ChatColor.BOLD + "Endarium"
						+ ChatColor.AQUA + " avec " + ChatColor.YELLOW + "" + ChatColor.BOLD
						+ EndariumBungeeCord.getRedisBungeeAPI().getPlayerCount() + " Joueurs" + ChatColor.AQUA + ".\n"
						+ ChatColor.WHITE + "▸ " + ChatColor.GREEN + "" + ChatColor.BOLD + "PLAY.ENDARIUM.NET"
						+ ChatColor.WHITE + " ◂\n§0"),
				new TextComponent("§8\n" + ChatColor.WHITE + "Discord : " + ChatColor.YELLOW + "discord.gg/endarium\n"
						+ ChatColor.GREEN + "Grades, boosters et plus " + ChatColor.WHITE + "➼ " + ChatColor.AQUA
						+ "www.endarium.net"));
	}

	/**
	 * Mettre à jour les informations liées BungeeCord.
	 */
	private void updatePlayersTabList() {
		ProxyServer.getInstance().getScheduler().schedule(this, new Runnable() {
			@Override
			public void run() {
				ProxyServer.getInstance().getPlayers().forEach(playerOnline -> sendTablist(playerOnline));
			}
		}, 1, 1, TimeUnit.MINUTES);
	}

	/**
	 * Charger la configuration BungeeCord.
	 */
	private void loadConfig() {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		File configFile = new File(getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
				try (InputStream is = getResourceAsStream("config.yml");
						OutputStream os = new FileOutputStream(configFile)) {
					ByteStreams.copy(is, os);
				}
			} catch (IOException e) {
				e.printStackTrace();
				getLogger().severe("Erreur lors de la copie de la config par default!");
			}
		}

		try {
			configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
		} catch (IOException e) {
			e.printStackTrace();
			getLogger().severe("Erreur lors du chargement de la config!");
		}
	}

	/**
	 * Récupérer les Slots du Network.
	 */
	public int getSlots() {
		return slots;
	}

	public Boolean getAntibotstatue() { return antibotstatue;}

	public void setAntibotstatue(Boolean statue) {
		antibotstatue = statue;
	}

	public EndariumCommons getEndariumCommons() {
		return endariumCommons;
	}

	public static RedisBungeeAPI getRedisBungeeAPI() {
		return redisBungeeAPI;
	}

	public static EndariumBungeeCord getInstance() {
		return instance;
	}
}