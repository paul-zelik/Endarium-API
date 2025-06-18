package net.endarium.api.minecraft.channels.host;

import net.endarium.api.EndariumCommons;
import net.endarium.api.games.servers.CrystaliserAPI;
import net.endarium.api.utils.GSONUtils;
import net.endarium.api.utils.builders.JSONMessageBuilder;
import net.endarium.api.utils.tools.SoundUtils;
import net.endarium.crystaliser.servers.EndaServer;
import net.endarium.crystaliser.servers.ServerType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.Closeable;
import java.io.IOException;

public class HostAnnounceChannel implements Closeable {

    private static final String CHANNEL_HOST_ANNOUNCE = "GolemaHostAnnounceChannel";

    private HostAnnounceListener hostAnnounceListener;

    public HostAnnounceChannel() {
        this.hostAnnounceListener = new HostAnnounceListener();
        EndariumCommons.getInstance().getExecutorService().submit(() -> {
            try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
                jedis.subscribe(hostAnnounceListener, CHANNEL_HOST_ANNOUNCE);
            }
        });
    }

    @Override
    public void close() throws IOException {
        try {
            this.hostAnnounceListener.unsubscribe();
        } catch (JedisConnectionException jedisConnectionException) {
            System.err.println(jedisConnectionException.getMessage());
        }
    }

    /**
     * Faire une annonce du Host dans les Hubs.
     *
     * @param golemaServer
     */
    public void sendHostAnnounceServer(EndaServer golemaServer) {
        String json = GSONUtils.getGson().toJson(golemaServer);
        EndariumCommons.getInstance().getExecutorService().submit(() -> {
            try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
                jedis.publish(CHANNEL_HOST_ANNOUNCE, json);
            }
        });
    }

    /**
     * Class d'écoute du Listener Host.
     */
    protected class HostAnnounceListener extends JedisPubSub {
        @Override
        public void onMessage(String channel, String message) {
            if (channel == null || message == null)
                return;
            switch (channel) {
                case CHANNEL_HOST_ANNOUNCE:
                    EndaServer golemaServerHost = GSONUtils.getGson().fromJson(message, EndaServer.class);
                    EndaServer golemaServerActual = CrystaliserAPI.getEndaServer();
                    if (golemaServerHost == null || golemaServerActual == null)
                        return;
                    if (!(golemaServerActual.getServerType().equals(ServerType.HUB)))
                        return;
                    for (Player playerOnline : Bukkit.getOnlinePlayers()) {
                        playerOnline.sendMessage("");
                        playerOnline.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Host" + ChatColor.GRAY
                                + "│ " + ChatColor.GOLD + "" + ChatColor.BOLD + golemaServerHost.getHostName()
                                + ChatColor.WHITE + " vient de lancer un Host !");
                        JSONMessageBuilder jsonMessageBuilder = new JSONMessageBuilder();
                        jsonMessageBuilder.newJComp(ChatColor.GRAY + "Cliquez sur ").build(jsonMessageBuilder);
                        jsonMessageBuilder.newJComp(ChatColor.AQUA + "" + ChatColor.BOLD + "[ Jouer - ➲]")
                                .addCommandExecutor("/gserv " + golemaServerHost.getServerName())
                                .addHoverText(
                                        ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Host" + ChatColor.GRAY + "│ "
                                                + ChatColor.GOLD + "" + ChatColor.BOLD + golemaServerHost.getHostName(),
                                        "",
                                        ChatColor.GRAY + "Jeu : " + ChatColor.WHITE
                                                + golemaServerHost.getGameType().getName(),
                                        ChatColor.GRAY + "Carte : " + ChatColor.WHITE + golemaServerHost.getMapName(), "",
                                        ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "» " + ChatColor.GREEN
                                                + "Rejoignez dès maintenant")
                                .build(jsonMessageBuilder);
                        jsonMessageBuilder.newJComp(ChatColor.GRAY + " pour le rejoindre.").build(jsonMessageBuilder);
                        jsonMessageBuilder.send(playerOnline);
                        playerOnline.sendMessage("");
                        SoundUtils.sendSound(playerOnline, Sound.LEVEL_UP);
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
