package net.endarium.api.minecraft.channels;

import net.endarium.api.EndariumCommons;
import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.players.settings.SettingType;
import net.endarium.api.utils.GSONUtils;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.Closeable;
import java.io.IOException;

public class ServerBroadcasterChannel implements Closeable {

    private static final String CHANNEL = "EndariumServerBroadcasterChannel";
    private ServerBroadcasterListener serverBroadcasterListener;

    /**
     * Constructeur du Channel Broadcaster.
     */
    public ServerBroadcasterChannel() {
        this.serverBroadcasterListener = new ServerBroadcasterListener();
        EndariumCommons.getInstance().getExecutorService().submit(() -> {
            try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
                jedis.subscribe(serverBroadcasterListener, CHANNEL);
            }
        });
    }

    @Override
    public void close() throws IOException {
        try {
            this.serverBroadcasterListener.unsubscribe();
        } catch (JedisConnectionException jedisConnectionException) {
            System.err.println(jedisConnectionException.getMessage());
        }
    }

    /**
     * Envoyer un Message à tous les Joueurs.
     *
     * @param message
     */
    public void broadcastMessage(String message) {
        this.broadcast(Rank.DEFAULT, message, false);
    }

    /**
     * Envoyer un Message à un certain Role avec un Minimum de Power.
     *
     * @param role
     * @param message
     */
    public void broadcast(Rank role, String message, boolean staffchat) {
        Broadcast broadcast = new Broadcast(staffchat, message, role.getPower());
        String json = GSONUtils.getGson().toJson(broadcast);
        EndariumCommons.getInstance().getExecutorService().submit(() -> {
            try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
                jedis.publish(CHANNEL, json);
            }
        });
    }

    /**
     * Class d'écoute du Listener ServerBroadcaster.
     */
    protected class ServerBroadcasterListener extends JedisPubSub {
        @Override
        public void onMessage(String channel, String message) {
            if (channel == null || message == null)
                return;
            if (CHANNEL.equals(channel)) {
                Broadcast broadcast = GSONUtils.getGson().fromJson(message, Broadcast.class);
                if (broadcast == null)
                    return;
                if (broadcast.getMinPower() == 0) {
                    Bukkit.broadcastMessage(broadcast.getContent());
                } else {
                    Bukkit.getOnlinePlayers().forEach(playerOnline -> {
                        EndaPlayer endaPlayer = EndaPlayer.get(playerOnline.getUniqueId());
                        if ((endaPlayer != null) && (endaPlayer.getRank().getPower() >= broadcast.getMinPower())) {
                            if (broadcast.isStaffchat()) {
                                if (endaPlayer.getRank().getPower() >= Rank.STAFF.getPower()){
                                    playerOnline.sendMessage(broadcast.getContent());
                                }
                            } else {
                                playerOnline.sendMessage(broadcast.getContent());
                            }
                        }
                    });
                }
            }
        }
    }

    /**
     * Build an protected Broadcast.
     */
    protected class Broadcast {

        protected boolean staffchat;
        protected String content;
        protected int minPower;

        /**
         * Constructeur de l'Object Broadcast.
         *
         * @param staffchat
         * @param content
         * @param minPower
         */
        public Broadcast(boolean staffchat, String content, int minPower) {
            this.staffchat = staffchat;
            this.content = content;
            this.minPower = minPower;
        }

        public boolean isStaffchat() {
            return staffchat;
        }

        public void setStaffchat(boolean staffchat) {
            this.staffchat = staffchat;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getMinPower() {
            return minPower;
        }

        public void setMinPower(int minPower) {
            this.minPower = minPower;
        }
    }
}
