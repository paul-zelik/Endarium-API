package net.endarium.api.bungeecord.channels;

import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;

import net.endarium.api.players.EndaPlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class RedisBungeeListener implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onRedisMessage(PubSubMessageEvent event) {

		if (event.getMessage() == null)
			return;

		Iterable<ProxiedPlayer> playerOnProxied = ProxyServer.getInstance().getPlayers();

		switch (event.getChannel()) {
		case "CHANNEL_STAFFCHAT":
			playerOnProxied.forEach(player -> {
				EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());
				if (endaPlayer == null)
					return;
				if ((!(endaPlayer.isStaff())) || (!(endaPlayer.isStaffChat())))
					return;
				player.sendMessage(event.getMessage());
			});
			break;
		default:
			break;
		}
	}
}