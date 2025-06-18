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
import java.util.Map;
import java.util.UUID;

public class UhcStats extends IStats {

    private static Map<UUID, UhcStats> uhcPlayerStats = new HashMap<>();
    private UUID uuid;

    /**
     * Créer les Uhcrun Stats d'un Joueur.
     *
     * @param uuid
     */
    public UhcStats(UUID uuid) {

        // Définir le Type de Jeu
        this.uuid = uuid;
        this.gameType = GameType.UHCRUN;
        this.statsList = new ArrayList<Stats>();
        this.statsMap = new HashMap<Stats, Object>();

        // Liste des Statistiques liées au Jeu
        this.getStatsList().add(Stats.COINSWIN);
        this.getStatsList().add(Stats.GAMEPLAYED);
        this.getStatsList().add(Stats.WINS);
        this.getStatsList().add(Stats.KILLS);
        this.getStatsList().add(Stats.DEATHS);


        // Générer les Informaitons du Comptes
        if (this.createStatsAccount(uuid, "coinswin, gameplayed, wins, kills, deaths",
                "?, ?, ?, ?, ?")) {
            for (Stats statsInfo : this.getStatsList())
                this.getStatsMap().put(statsInfo, statsInfo.getDefaultValue());
        } else {
            this.statsMap = this.getStatsSQL(uuid);
        }
    }

    /**
     * Mettre à jour les Statistiques.
     */
    public static void updateUhcrunStats(UhcStats uhcStats, UUID uuid) {
        EndariumCommons.getInstance().getExecutorService().submit(() -> {
            try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE stats_"
                        + uhcStats.gameType.getName().toLowerCase()
                        + " SET coinswin = ?, gameplayed = ?, wins = ?, kills = ?, deaths = ?, updated_at = NOW() WHERE uuid = ?");
                preparedStatement.setFloat(1, uhcStats.getCoinsWin());
                preparedStatement.setInt(2, uhcStats.getStats(Stats.GAMEPLAYED));
                preparedStatement.setInt(3, uhcStats.getStats(Stats.WINS));
                preparedStatement.setInt(4, uhcStats.getStats(Stats.KILLS));
                preparedStatement.setInt(5, uhcStats.getStats(Stats.DEATHS));
                preparedStatement.setString(6, uuid.toString());
                preparedStatement.executeUpdate();
                preparedStatement.close();
                connection.close();
            } catch (SQLException exception) {
                System.err.println(EndariumAPI.getPrefixAPI()
                        + "Erreur : Impossible de mettre à jour les Stats UHCRun via MySQL (" + uuid.toString() + ")");
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
        if (uhcPlayerStats.get(uuid) != null)
            uhcPlayerStats.remove(uuid);
    }

    /**
     * Récupérer le Profil de Stats SkyRush d'un Joueur.
     *
     * @param uuid
     */
    public static UhcStats get(UUID uuid) {
        if (uhcPlayerStats.get(uuid) == null)
            uhcPlayerStats.put(uuid, new UhcStats(uuid));
        return uhcPlayerStats.get(uuid);
    }

}
