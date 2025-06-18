package net.endarium.api.players.stats.games;

import net.endarium.api.EndariumCommons;
import net.endarium.api.players.stats.IStats;
import net.endarium.api.players.stats.Stats;
import net.endarium.api.utils.EndariumAPI;
import net.endarium.crystaliser.servers.GameType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class PunchoutStats extends IStats {
    private static Map<UUID, PunchoutStats> punchoutPlayers = new HashMap();
    private UUID uuid;

    public PunchoutStats(UUID uuid) {
        this.uuid = uuid;
        this.gameType = GameType.PUNCHOUT;
        this.statsList = new ArrayList();
        this.statsMap = new HashMap();
        this.getStatsList().add(Stats.COINSWIN);
        this.getStatsList().add(Stats.GAMEPLAYED);
        this.getStatsList().add(Stats.WINS);
        this.getStatsList().add(Stats.KILLS);
        this.getStatsList().add(Stats.PLAYERS_PUNCH);
        this.getStatsList().add(Stats.DEATHS);
        this.getStatsList().add(Stats.BOW_KILLS);
        if (this.createStatsAccount(uuid, "coinswin, gameplayed, wins, kills, playerspunch, deaths, bowkills", "?, ?, ?, ?, ?, ?, ?")) {
            Iterator var2 = this.getStatsList().iterator();

            while(var2.hasNext()) {
                Stats statsInfo = (Stats)var2.next();
                this.getStatsMap().put(statsInfo, statsInfo.getDefaultValue());
            }
        } else {
            this.statsMap = this.getStatsSQL(uuid);
        }

    }

    public static void updatePunchoutStatsAccount(PunchoutStats punchoutStats, UUID uuid) {
        EndariumCommons.getInstance().getExecutorService().submit(() -> {
            try {
                Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection();
                Throwable var3 = null;

                try {
                    PreparedStatement preparedStatement = connection.prepareStatement("UPDATE stats_" + punchoutStats.gameType.getName().toLowerCase() + " SET coinswin = ?, gameplayed = ?, wins = ?, kills = ?, playerspunch = ?, deaths = ?, bowkills = ?, updated_at = NOW() WHERE uuid = ?");
                    preparedStatement.setFloat(1, punchoutStats.getCoinsWin());
                    preparedStatement.setInt(2, punchoutStats.getStats(Stats.GAMEPLAYED));
                    preparedStatement.setInt(3, punchoutStats.getStats(Stats.WINS));
                    preparedStatement.setInt(4, punchoutStats.getStats(Stats.KILLS));
                    preparedStatement.setInt(5, punchoutStats.getStats(Stats.PLAYERS_PUNCH));
                    preparedStatement.setInt(6, punchoutStats.getStats(Stats.DEATHS));
                    preparedStatement.setInt(7, punchoutStats.getStats(Stats.BOW_KILLS));
                    preparedStatement.setString(8, uuid.toString());
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                    connection.close();
                } catch (Throwable var13) {
                    var3 = var13;
                    throw var13;
                } finally {
                    if (connection != null) {
                        if (var3 != null) {
                            try {
                                connection.close();
                            } catch (Throwable var12) {
                                var3.addSuppressed(var12);
                            }
                        } else {
                            connection.close();
                        }
                    }

                }
            } catch (SQLException var15) {
                System.err.println(EndariumAPI.getPrefixAPI() + "Erreur : Impossible de mettre à jour les Stats SkyRush via MySQL (" + uuid.toString() + ")");
                var15.printStackTrace();
            }

        });
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public static void logoutStats(UUID uuid) {
        if (punchoutPlayers.get(uuid) != null) {
            punchoutPlayers.remove(uuid);
        }

    }

    public static PunchoutStats get(UUID uuid) {
        if (punchoutPlayers.get(uuid) == null) {
            punchoutPlayers.put(uuid, new PunchoutStats(uuid));
        }

        return (PunchoutStats)punchoutPlayers.get(uuid);
    }
}
