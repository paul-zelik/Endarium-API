package net.endarium.api.players.stats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import net.endarium.api.EndariumCommons;
import net.endarium.api.utils.EndariumAPI;
import net.endarium.api.EndariumCommons;
import net.endarium.api.games.kits.KitsInfos;
import net.endarium.api.utils.EndariumAPI;
import net.endarium.crystaliser.servers.GameType;

public abstract class IStats {

    public GameType gameType;

    protected List<Stats> statsList;
    protected Map<Stats, Object> statsMap;

    /**
     * Récupérer les Coins d'un Joueur.
     */
    public float getCoinsWin() {
        return (float) this.statsMap.get(Stats.COINSWIN);
    }

    /**
     * Ajouter d'un Coins à un Joueur.
     *
     * @param coins
     */
    public void addCoinsWin(float coins) {
        this.statsMap.put(Stats.COINSWIN, this.getCoinsWin() + coins);
    }

    /**
     * Récupérer les Stats d'un Joueur.
     *
     * @param stats
     */
    public int getStats(Stats stats) {
        if (this.statsMap.get(stats) == null)
            return 0;
        return (int) this.statsMap.get(stats);
    }

    /**
     * Ajouter les Stats d'un Joueur.
     *
     * @param stats
     * @param value
     */
    public void addStats(Stats stats, int value) {
        if (this.statsMap.get(stats) != null)
            this.statsMap.put(stats, this.getStats(stats) + value);
    }

    /**
     * Récuper le dernier Kit d'un Joueur.
     */
    public KitsInfos getLastKit() {
        return KitsInfos.getKitByName((String) this.statsMap.get(Stats.LAST_KIT), gameType);
    }

    /**
     * Définir le dernier Kit d'un Joueur.
     *
     * @param kitsInfos
     */
    public void setLastKit(KitsInfos kitsInfos) {
        this.statsMap.put(Stats.LAST_KIT, kitsInfos.getName());
    }

    /**
     * Créer un compte de Statistiques aux Joueurs.
     *
     * @param uuid
     * @param value
     * @param type
     */
    public boolean createStatsAccount(UUID uuid, String value, String type) {
        String TABLE = "stats_" + gameType.getName().toLowerCase();
        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT updated_at FROM " + TABLE + " WHERE uuid = ? LIMIT 1");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!(resultSet.next())) {
                preparedStatement.close();
                preparedStatement = connection.prepareStatement("INSERT INTO " + TABLE + " (uuid, " + value
                        + ", updated_at, created_at) VALUES(?, " + type + ", NOW(), NOW())");
                int statementIndex = 1;
                preparedStatement.setString(statementIndex, uuid.toString());
                for (Stats stats : this.statsList) {
                    statementIndex++;
                    preparedStatement.setObject(statementIndex, stats.getDefaultValue());
                }
                preparedStatement.executeUpdate();
                preparedStatement.close();
                connection.close();
                System.out.println(EndariumAPI.getPrefixAPI() + "Compte de Stats (" + uuid.toString()
                        + ") a bien été créer pour : " + gameType.getName() + ".");
                return true;
            }
            return false;
        } catch (SQLException exception) {
            System.err.println(EndariumAPI.getPrefixAPI() + "Erreur : Impossible de créer les stats via MySQL ("
                    + uuid.toString() + ") de : " + gameType.getName());
            exception.printStackTrace();
        }
        return false;
    }

    public List<UUID> getLeaderboard(GameType gameType) {
        List<UUID> leaderboard = null;
        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT uuid FROM stats_" + gameType.getName().toLowerCase() + " ORDER BY wins DESC LIMIT 10"
            );
            ResultSet resultSet = preparedStatement.executeQuery();
            leaderboard = new ArrayList<>();
            while (resultSet.next()) {
                leaderboard.add(UUID.fromString(resultSet.getString("uuid")));
            }
            preparedStatement.close();
            connection.close();
        } catch (SQLException exception) {
            System.err.println(EndariumAPI.getPrefixAPI() + "Erreur : Impossible de récupérer le leaderboard (" + exception.getMessage() + ")");
            exception.printStackTrace();
        }
        return leaderboard;
    }

    /**
     * Récupérer les Stats d'un Joueur.
     *
     * @param uuid
     */
    public Map<Stats, Object> getStatsSQL(UUID uuid) {
        Map<Stats, Object> statsMapSQL = new HashMap<Stats, Object>();
        String TABLE = "stats_" + gameType.getName().toLowerCase();
        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT * FROM " + TABLE + " WHERE uuid = ? LIMIT 1");
            preparedStatement.setString(1, uuid.toString());
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                for (Stats statsInfo : statsList)
                    statsMapSQL.put(statsInfo, rs.getObject(statsInfo.getColumn()));
            } else {
                for (Stats statsInfo : statsList)
                    statsMapSQL.put(statsInfo, statsInfo.getDefaultValue());
            }
            while (rs.next())
                System.out.println(rs.getCursorName() + " Cursor Name");
            preparedStatement.close();
        } catch (SQLException e) {
            System.err.println(EndariumAPI.getPrefixAPI()
                    + "Erreur : Impossible de récupérer les statistiques via MySQL (" + uuid.toString() + ")");
            e.printStackTrace();
        }
        return statsMapSQL;
    }

    public List<Stats> getStatsList() {
        return statsList;
    }

    public Map<Stats, Object> getStatsMap() {
        return statsMap;
    }
}