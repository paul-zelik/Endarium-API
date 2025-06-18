package net.endarium.api.minecraft.channels.host;

import net.endarium.api.EndariumCommons;
import net.endarium.api.games.servers.CrystaliserServerManager;
import net.endarium.api.utils.GSONUtils;
import net.endarium.api.utils.Messages;
import net.endarium.crystaliser.servers.EndaServer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;

public class HostTeleportChannel implements Closeable {

    private static final String CHANNEL = "GolemaHostTeleportChannel";
    private HostTeleportListener hostTeleportListener;

    /**
     * Gestion du Channel de Téléportation dans les Hosts.
     */
    public HostTeleportChannel() {
        this.hostTeleportListener = new HostTeleportListener();
        EndariumCommons.getInstance().getExecutorService().submit(() -> {
            try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
                jedis.subscribe(hostTeleportListener, CHANNEL);
            }
        });
    }

    @Override
    public void close() throws IOException {
        try {
            this.hostTeleportListener.unsubscribe();
        } catch (JedisConnectionException jedisConnectionException) {
            System.err.println(jedisConnectionException.getMessage());
        }
    }

    /**
     * Envoyer un Host sur son Serveur quand il est prêt.
     *
     * @param endaServer
     */
    public void sendHostPlayerServer(EndaServer endaServer) {
        TeleportHost teleportHost = new TeleportHost(endaServer.getHostUUID(), endaServer);
        String json = GSONUtils.getGson().toJson(teleportHost);
        EndariumCommons.getInstance().getExecutorService().submit(() -> {
            try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
                jedis.publish(CHANNEL, json);
            }
        });
    }

    /**
     * Class d'écoute du Listener Host.
     */
    protected class HostTeleportListener extends JedisPubSub {
        @Override
        public void onMessage(String channel, String message) {
            if (channel == null || message == null)
                return;
            if (channel.equals(CHANNEL)) {
                TeleportHost teleportHost = GSONUtils.getGson().fromJson(message, TeleportHost.class);
                if (teleportHost == null)
                    return;
                Bukkit.getOnlinePlayers().forEach(playerOnline -> {
                    if ((playerOnline != null) && (teleportHost.getUUID().toString()
                            .equalsIgnoreCase(playerOnline.getUniqueId().toString()))) {
                        playerOnline.sendMessage(Messages.HOST_PREFIX + ChatColor.YELLOW
                                + "Votre serveur vient d'être livré, téléportation en cours...");
                        CrystaliserServerManager.sendToServer(playerOnline, teleportHost.getEndaServer().getServerName());
                    }
                });
            }
        }
    }

    /**
     * Build an protected TeleportHost.
     */
    protected class TeleportHost {

        protected UUID uuid;
        protected EndaServer endaServer;

        /**
         * Construire l'objet de demande de Téléportation.
         */
        public TeleportHost(UUID uuid, EndaServer endaServer) {
            this.uuid = uuid;
            this.endaServer = endaServer;
        }

        public UUID getUUID() {
            return uuid;
        }

        public void setUUID(UUID uuid) {
            this.uuid = uuid;
        }

        public EndaServer getEndaServer() {
            return endaServer;
        }

        public void setEndaServer(EndaServer endaServer) {
            this.endaServer = endaServer;
        }
    }
}
