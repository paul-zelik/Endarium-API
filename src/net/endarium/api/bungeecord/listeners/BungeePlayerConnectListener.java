package net.endarium.api.bungeecord.listeners;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.UUID;

import com.imaginarycode.minecraft.redisbungee.events.PlayerChangedServerNetworkEvent;
import net.endarium.api.bungeecord.EndariumBungeeCord;
import net.endarium.api.bungeecord.channels.RedisBungeeChannel;
import net.endarium.api.games.servers.CrystaliserServerManager;
import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.EndaPlayerCache;
import net.endarium.api.players.friends.FriendChannelType;
import net.endarium.api.players.friends.FriendChannelType.FriendChannelRequest;
import net.endarium.api.players.login.LoginManager;
import net.endarium.api.players.moderation.ban.BanInfos;
import net.endarium.api.players.others.AntiBot;
import net.endarium.api.players.party.Party;
import net.endarium.api.players.party.PartyManager;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.GSONUtils;
import net.endarium.api.utils.Messages;
import net.endarium.api.utils.mojang.UUIDEndaFetcher;
import net.endarium.crystaliser.servers.EndaServer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.bukkit.entity.Player;

public class BungeePlayerConnectListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(LoginEvent event) {

		UUID uuid = event.getConnection().getUniqueId();
		String addressip = event.getConnection().getAddress().getHostString();


		// Vérifiction de la Maintenance
		if (EndariumBungeeCord.getInstance().getEndariumCommons().isDeveloper()) {
			Rank rankPlayer = EndariumBungeeCord.getInstance().getEndariumCommons().getEndariumEntities()
					.getRankManager().getRank(uuid, false);
			if (rankPlayer == null || rankPlayer.getPower() < Rank.FRIEND.getPower()) {
				event.setCancelled(true);
				event.setCancelReason(Messages.ENDARIUM_PREFIX + ChatColor.RED + "Le serveur est en maitenance.\n"
						+ ChatColor.WHITE + "Discord : " + ChatColor.DARK_AQUA + "discord.endarium.net");
				return;
			}
		}



		// Vérification si le Joueur est Banni
		BanInfos banInfos = EndariumBungeeCord.getInstance().getEndariumCommons().getEndariumEntities().getBanManager()
				.isBan(uuid);
		if (banInfos != null) {
			event.setCancelled(true);

			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			String kickMessage = ChatColor.DARK_RED + "● " + ChatColor.RED + "Vous êtes banni d'Endarium"
					+ ChatColor.DARK_RED + " ●\n" + ChatColor.GRAY + "Raison : " + ChatColor.WHITE
					+ banInfos.getReason() + "\n"
					+ (banInfos.isPermanent()
							? ChatColor.GRAY + "Durée : " + ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "PERMANENT"
							: ChatColor.GRAY + "Jusqu'au : " + ChatColor.DARK_AQUA + "" + ChatColor.BOLD
									+ dateFormat.format(banInfos.getExpiryDate()))
					+ "\n§f\n" + ChatColor.DARK_GRAY + "ID : #" + banInfos.getBanID() + " - "
					+ dateFormat.format(banInfos.getDate());

			event.setCancelReason(kickMessage);
			return;
		}

		// Vérifier si le Serveur est accessible au VIP Uniquement
		if (EndariumBungeeCord.getRedisBungeeAPI().getPlayersOnline()
				.size() >= (EndariumBungeeCord.getInstance().getSlots() - 25)) {
			Rank rankPlayer = EndariumBungeeCord.getInstance().getEndariumCommons().getEndariumEntities()
					.getRankManager().getRank(uuid, false);
			if (rankPlayer == null || rankPlayer.getPower() < Rank.MINIVIP.getPower()) {
				event.setCancelled(true);
				event.setCancelReason(Messages.ENDARIUM_PREFIX + ChatColor.RED + "Serveur Plein ! " + ChatColor.WHITE
						+ "L'accès est réservé au " + ChatColor.YELLOW + "" + ChatColor.BOLD + "VIP " + ChatColor.WHITE
						+ "!\n" + ChatColor.WHITE + "Boutique : " + ChatColor.GREEN + "https://www.endarium.net/");
				return;
			}
		}


	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerConnect(PostLoginEvent event) {

		ProxiedPlayer proxiedPlayer = event.getPlayer();
		EndaPlayer endaPlayer = EndaPlayer.get(proxiedPlayer.getUniqueId());
		UUIDEndaFetcher.updateFetch(proxiedPlayer.getUniqueId(), proxiedPlayer.getName());

		// Chargement du compte du Joueur en Cache
		EndaPlayerCache endaPlayerCache = new EndaPlayerCache("", false, true);
		System.out.println(proxiedPlayer.getUniqueId().toString());
		endaPlayer.setEndaPlayerCache(endaPlayerCache);
		EndariumBungeeCord.getInstance().sendTablist(proxiedPlayer);

		LoginManager loginManager = new LoginManager();
		if (loginManager.loginPlayerExists(proxiedPlayer.getUniqueId())) {
			loginManager.makeConnected(proxiedPlayer.getUniqueId());
		} else {
			loginManager.createAccount(proxiedPlayer.getUniqueId(), "EndaProxi");
			loginManager.makeConnected(proxiedPlayer.getUniqueId());
		}


		// AMI : Connexion
		FriendChannelRequest friendChannelRequest = new FriendChannelRequest(FriendChannelType.CONNECT_NOTIFICATION,
				proxiedPlayer.getUniqueId(), proxiedPlayer.getUniqueId(), proxiedPlayer.getName(),
				proxiedPlayer.getName());
		EndariumBungeeCord.getRedisBungeeAPI().sendChannelMessage(RedisBungeeChannel.CHANNEL_FRIEND.getName(),
				GSONUtils.getGson().toJson(friendChannelRequest));



	}


	@EventHandler
	public void onPlayerChangeServer(ServerSwitchEvent event) {
		ProxiedPlayer proxiedPlayer = event.getPlayer();

		PartyManager partyManager = EndariumBungeeCord.getInstance().getPartyManager();
		if (partyManager.findPartyByPlayer(proxiedPlayer) != null) {
			Party party = partyManager.findPartyByPlayer(proxiedPlayer);
			if (party.getPlayerOwner().equals(proxiedPlayer)) {
				Server server = event.getPlayer().getServer();
				for (ProxiedPlayer player : party.getPlayers()) {
					player.connect(server.getInfo());
					player.sendMessage("Vous avez été tp sur un nouveau serveur.");
				}
			}
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogout(PlayerDisconnectEvent event) {

		ProxiedPlayer proxiedPlayer = event.getPlayer();
		EndaPlayer endaPlayer = EndaPlayer.get(proxiedPlayer.getUniqueId());

		String ipadress = proxiedPlayer.getAddress().getHostName();
		AntiBot antiBot = new AntiBot();

		// AntiBot

		antiBot.removeConnectionIp(ipadress);

		// AMI : Deconnexion
		FriendChannelRequest friendChannelRequest = new FriendChannelRequest(FriendChannelType.DISCONNECT_NOTIFICATION,
				proxiedPlayer.getUniqueId(), proxiedPlayer.getUniqueId(), proxiedPlayer.getName(),
				proxiedPlayer.getName());
		EndariumBungeeCord.getRedisBungeeAPI().sendChannelMessage(RedisBungeeChannel.CHANNEL_FRIEND.getName(),
				GSONUtils.getGson().toJson(friendChannelRequest));
		endaPlayer.setFriends(endaPlayer.getFriends().setPrivateMessageLastTarget(null));

		LoginManager loginManager = new LoginManager();
		loginManager.makeDisconnected(proxiedPlayer.getUniqueId());

		endaPlayer.setDisconnected();
		endaPlayer.logoutEndaPlayerCache();
	}

	@EventHandler
	public void onLeave(PlayerDisconnectEvent event){
		ProxiedPlayer pp = event.getPlayer();
		PartyManager partyManager = EndariumBungeeCord.getInstance().getPartyManager();
		if (partyManager.findPartyByPlayer(pp) != null) {

			Party party = partyManager.findPartyByPlayer(pp);
			if (party.getPlayerOwner().equals(pp.getUniqueId())) {
				for (ProxiedPlayer player : party.getPlayers()) {
					party.removePlayer(player);
					player.sendMessage(ChatColor.GOLD + "[Party] " + ChatColor.RED + "Votre groupe a été suprimé.");
				}

				partyManager.removeParty(party);
			} else {
				party.removePlayer(pp);
			}

		}
	}


}