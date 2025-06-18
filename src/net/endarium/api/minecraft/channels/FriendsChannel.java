package net.endarium.api.minecraft.channels;

import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import net.endarium.api.EndariumCommons;
import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.friends.FriendChannelType;
import net.endarium.api.players.friends.FriendChannelType.FriendChannelRequest;
import net.endarium.api.utils.GSONUtils;
import net.endarium.api.utils.Messages;
import net.endarium.api.utils.builders.JSONMessageBuilder;
import net.endarium.api.utils.tools.SoundUtils;
import net.md_5.bungee.api.ChatColor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class FriendsChannel implements Closeable {

	private static final String CHANNEL = "CHANNEL_FRIENDS_ENDARIUM";
	private final FriendsListener friendsListener;

	/**
	 * Constructure du Channel Friends.
	 */
	public FriendsChannel() {
		this.friendsListener = new FriendsListener();
		EndariumCommons.getInstance().getExecutorService().submit(() -> {
			try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
				jedis.subscribe(friendsListener, CHANNEL);
			}
		});
	}

	@Override
	public void close() throws IOException {
		try {
			this.friendsListener.unsubscribe();
		} catch (JedisConnectionException jedisConnectionException) {
			System.err.println(jedisConnectionException.getMessage());
		}
	}

	/**
	 * Envoyer un Channel de Friend.
	 * 
	 * @param friendChannelType
	 * @param uuid
	 * @param targetUUID
	 */
	public void sendFriendChannel(FriendChannelType friendChannelType, UUID uuid, UUID targetUUID, String name,
			String targetName) {
		FriendChannelRequest friendChannelRequest = new FriendChannelRequest(friendChannelType, uuid, targetUUID, name,
				targetName);
		EndariumCommons.getInstance().getExecutorService().execute(() -> {
			try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
				jedis.publish(CHANNEL, GSONUtils.getGson().toJson(friendChannelRequest));
			}
		});
	}

	/**
	 * Class d'écoute du Listener Friends.
	 */
	protected class FriendsListener extends JedisPubSub {

		@Override
		public void onMessage(String channel, String message) {
			if (channel == null || message == null)
				return;
			if (channel.equals(CHANNEL)) {
				FriendChannelRequest friendChannelRequest = GSONUtils.getGson().fromJson(message,
						FriendChannelRequest.class);
				if (friendChannelRequest == null)
					return;
				if (friendChannelRequest.getFriendChannelType() == null)
					return;
				switch (friendChannelRequest.getFriendChannelType()) {
				/** FRIEND : Notification Connexion */
				case CONNECT_NOTIFICATION:
					Bukkit.getOnlinePlayers().forEach(players -> {
						EndaPlayer endaPlayer = EndaPlayer.get(players.getUniqueId());
						if ((endaPlayer != null) && (endaPlayer.getFriends()).isFriend(friendChannelRequest.getUUID())
								&& (endaPlayer.getFriends().isNotifications())) {
							players.sendMessage(ChatColor.WHITE + "[" + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD
									+ "❤" + ChatColor.WHITE + "] " + ChatColor.YELLOW + friendChannelRequest.getName()
									+ ChatColor.GREEN + " s'est connecté.");
						}
					});
					break;

				/** FRIEND : Notification Deconnexion */
				case DISCONNECT_NOTIFICATION:
					Bukkit.getOnlinePlayers().forEach(players -> {
						EndaPlayer endaPlayer = EndaPlayer.get(players.getUniqueId());
						if ((endaPlayer != null) && (endaPlayer.getFriends()).isFriend(friendChannelRequest.getUUID())
								&& (endaPlayer.getFriends().isNotifications())) {
							players.sendMessage(ChatColor.WHITE + "[" + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD
									+ "❤" + ChatColor.WHITE + "] " + ChatColor.YELLOW + friendChannelRequest.getName()
									+ ChatColor.RED + " s'est déconnecté.");
						}
					});
					break;
				/** FRIEND : INVITATION */
				case INVITE_FRIEND:
					Bukkit.getOnlinePlayers().forEach(players -> {
						if (players.getUniqueId().equals(friendChannelRequest.getTargetUUID())) {
							players.sendMessage("");
							players.sendMessage(
									Messages.FRIENDS_PREFIX + ChatColor.YELLOW + friendChannelRequest.getName()
											+ ChatColor.WHITE + " souhaite devenir votre ami !");
							JSONMessageBuilder jsonMessageBuilder = new JSONMessageBuilder();
							jsonMessageBuilder.newJComp(ChatColor.GRAY + "Vous pouvez maintenant : ")
									.build(jsonMessageBuilder);
							jsonMessageBuilder.newJComp(ChatColor.GREEN + "" + ChatColor.BOLD + "[ACCEPTER]")
									.addHoverText(ChatColor.GRAY + "Accepter la demande d'ami.")
									.addCommandExecutor("/friend accept " + friendChannelRequest.getName())
									.build(jsonMessageBuilder);
							jsonMessageBuilder.newJComp(ChatColor.GRAY + " ou ").build(jsonMessageBuilder);
							jsonMessageBuilder.newJComp(ChatColor.RED + "" + ChatColor.BOLD + "[REFUSER]")
									.addHoverText(ChatColor.GRAY + "Refuser la demande d'ami.")
									.addCommandExecutor("/friend refuse " + friendChannelRequest.getName())
									.build(jsonMessageBuilder);
							jsonMessageBuilder.newJComp(ChatColor.GRAY + ".").build(jsonMessageBuilder);
							jsonMessageBuilder.send(players);
							players.sendMessage("");
							SoundUtils.sendSound(players, Sound.NOTE_PIANO);
						}
					});

					// Mise en place de la demande (Request)
					EndariumCommons.getInstance().getEndariumEntities().getFriendsManager()
							.makeFriendRequest(friendChannelRequest.getUUID(), friendChannelRequest.getTargetUUID());
					break;

				/** FRIEND : SUPPRESSION */
				case REMOVE_FRIEND:
					Bukkit.getOnlinePlayers().forEach(players -> {
						if (players.getUniqueId().equals(friendChannelRequest.getTargetUUID())) {
							players.sendMessage(
									Messages.FRIENDS_PREFIX + ChatColor.RED + "Vous n'êtes désormais plus ami avec : "
											+ ChatColor.YELLOW + friendChannelRequest.getName() + ChatColor.RED + ".");
							SoundUtils.sendSound(players, Sound.NOTE_BASS_DRUM);
						}
					});
					break;

				/** FRIEND : ACCEPTER */
				case ACCEPT_FRIEND:
					Bukkit.getOnlinePlayers().forEach(players -> {
						if (players.getUniqueId().equals(friendChannelRequest.getUUID())) {
							players.sendMessage(Messages.FRIENDS_PREFIX + ChatColor.WHITE
									+ "Vous êtes désormais ami avec : " + ChatColor.YELLOW
									+ friendChannelRequest.getTargetName() + ChatColor.WHITE + ".");
							SoundUtils.sendSound(players, Sound.LEVEL_UP);
						}
					});

					// Expiration de la demande (Request)
					EndariumCommons.getInstance().getEndariumEntities().getFriendsManager()
							.cancelFriendRequest(friendChannelRequest.getUUID(), friendChannelRequest.getTargetUUID());
					break;

				/** FRIEND : REFUSER */
				case REFUSE_FRIEND:
					Bukkit.getOnlinePlayers().forEach(players -> {
						if (players.getUniqueId().equals(friendChannelRequest.getUUID())) {
							players.sendMessage(
									Messages.FRIENDS_PREFIX + ChatColor.YELLOW + friendChannelRequest.getTargetName()
											+ ChatColor.RED + " a refusé votre demande d'ami.");
							SoundUtils.sendSound(players, Sound.NOTE_BASS_DRUM);
						}
					});

					// Expiration de la demande (Request)
					EndariumCommons.getInstance().getEndariumEntities().getFriendsManager()
							.cancelFriendRequest(friendChannelRequest.getUUID(), friendChannelRequest.getTargetUUID());
					break;

				default:
					break;
				}
			}
			super.onMessage(channel, message);
		}
	}
}