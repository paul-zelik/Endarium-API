package net.endarium.api.players.stats.games;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.endarium.api.EndariumCommons;
import net.endarium.api.players.stats.IStats;
import net.endarium.api.players.stats.Stats;
import net.endarium.api.utils.EndariumAPI;
import net.endarium.crystaliser.servers.GameType;

public class HungerGamesStats extends IStats {

    private static Map<UUID, HungerGamesStats> hungerGamesStatsPlayers = new HashMap<>();
    private UUID uuid;

    /**
     * Créer les HungerGames Stats d'un Joueur.
     *
     * @param uuid
     * @param gameType
     */
    public HungerGamesStats(UUID uuid, GameType gameType) {

        // Définir le Type de Jeu
        this.uuid = uuid;
        this.gameType = gameType;
        this.statsList = new ArrayList<Stats>();
        this.statsMap = new HashMap<Stats, Object>();

        // Liste des Statistiques liées au Jeu
        this.getStatsList().add(Stats.COINSWIN);
        this.getStatsList().add(Stats.GAMEPLAYED);
        this.getStatsList().add(Stats.WINS);
        this.getStatsList().add(Stats.KILLS);
        this.getStatsList().add(Stats.CHESTS_OPENED);
        this.getStatsList().add(Stats.ZONE_PLAYED);
        this.getStatsList().add(Stats.LAST_KIT);

        // Générer les Informaitons du Comptes
        if (this.createStatsAccount(uuid, "coinswin, gameplayed, wins, kills, chestsopened, zoneplayed, lastkit",
                "?, ?, ?, ?, ?, ?, ?")) {
            for (Stats statsInfo : this.getStatsList())
                this.getStatsMap().put(statsInfo, statsInfo.getDefaultValue());
        } else {
            this.statsMap = this.getStatsSQL(uuid);
        }
    }

    /**
     * Update les Stats HungerGames des Joueurs.
     *
     * @param hungerGamesStats
     * @param uuid
     */
    public static void updateHungerGamesStatsAccount(HungerGamesStats hungerGamesStats, UUID uuid) {
        EndariumCommons.getInstance().getExecutorService().submit(() -> {
            try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE stats_"
                        + hungerGamesStats.gameType.getName().toLowerCase()
                        + " SET coinswin = ?, gameplayed = ?, wins = ?, kills = ?, chestsopened = ?, zoneplayed = ?, lastkit = ?, updated_at = NOW() WHERE uuid = ?");
                preparedStatement.setFloat(1, hungerGamesStats.getCoinsWin());
                preparedStatement.setInt(2, hungerGamesStats.getStats(Stats.GAMEPLAYED));
                preparedStatement.setInt(3, hungerGamesStats.getStats(Stats.WINS));
                preparedStatement.setInt(4, hungerGamesStats.getStats(Stats.KILLS));
                preparedStatement.setInt(5, hungerGamesStats.getStats(Stats.CHESTS_OPENED));
                preparedStatement.setInt(6, hungerGamesStats.getStats(Stats.ZONE_PLAYED));
                preparedStatement.setString(7,
                        hungerGamesStats.getLastKit() == null ? null : hungerGamesStats.getLastKit().getName());
                preparedStatement.setString(8, uuid.toString());
                preparedStatement.executeUpdate();
                preparedStatement.close();
                connection.close();
            } catch (SQLException exception) {
                System.err.println(EndariumAPI.getPrefixAPI()
                        + "Erreur : Impossible de mettre à jour les Stats SkyRush via MySQL (" + uuid.toString() + ")");
                exception.printStackTrace();
            }
        });
    }

    public UUID getUUID() {
        return uuid;
    }

    /**
     * Déconnecter un Joueur des Stats.
     *
     * @param uuid
     */
    public static void logoutStats(UUID uuid) {
        if (hungerGamesStatsPlayers.get(uuid) != null)
            hungerGamesStatsPlayers.remove(uuid);
    }

    /**
     * Récupérer le Profil de Stats HungerGames d'un Joueur.
     *
     * @param uuid
     * @param gameType
     */
    public static HungerGamesStats get(UUID uuid, GameType gameType) {
        if (hungerGamesStatsPlayers.get(uuid) == null)
            hungerGamesStatsPlayers.put(uuid, new HungerGamesStats(uuid, gameType));
        return hungerGamesStatsPlayers.get(uuid);
    }
}
