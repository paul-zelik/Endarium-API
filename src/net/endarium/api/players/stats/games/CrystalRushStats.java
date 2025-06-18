package net.endarium.api.players.stats.games;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import net.endarium.api.EndariumCommons;
import net.endarium.api.players.stats.IStats;
import net.endarium.api.players.stats.Stats;
import net.endarium.api.utils.EndariumAPI;
import net.endarium.crystaliser.servers.GameType;
public class CrystalRushStats extends IStats {

    private static Map<UUID, CrystalRushStats> crystalRushStatsMap = new HashMap<>();
    private UUID uuid;

    /**
     * Créer les SkyRush Stats d'un Joueur.
     *
     * @param uuid
     */
    public CrystalRushStats(UUID uuid) {

        // Définir le Type de Jeu
        this.uuid = uuid;
        this.gameType = GameType.CRYSTALRUSH;
        this.statsList = new ArrayList<Stats>();
        this.statsMap = new HashMap<Stats, Object>();

        // Liste des Statistiques liées au Jeu
        this.getStatsList().add(Stats.COINSWIN);
        this.getStatsList().add(Stats.GAMEPLAYED);
        this.getStatsList().add(Stats.WINS);
        this.getStatsList().add(Stats.KILLS);
        this.getStatsList().add(Stats.DEATHS);
        this.getStatsList().add(Stats.CRYSTAL_KILLS);
        this.getStatsList().add(Stats.LAST_KIT);

        // Générer les Informaitons du Comptes
        if (this.createStatsAccount(uuid, "coinswin, gameplayed, wins, kills, deaths, crystalkill, lastkit",
                "?, ?, ?, ?, ?, ?, ?")) {
            for (Stats statsInfo : this.getStatsList())
                this.getStatsMap().put(statsInfo, statsInfo.getDefaultValue());
        } else {
            this.statsMap = this.getStatsSQL(uuid);
        }
    }

    /**
     * Mettre à jour les Statistiques.
     */
    public static void updateSkyRushStatsAccount(CrystalRushStats crystalRushStats, UUID uuid) {
        EndariumCommons.getInstance().getExecutorService().submit(() -> {
            try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE stats_"
                        + crystalRushStats.gameType.getName().toLowerCase()
                        + " SET coinswin = ?, gameplayed = ?, wins = ?, kills = ?, deaths = ?, crystalkill = ?, lastkit = ?, updated_at = NOW() WHERE uuid = ?");
                preparedStatement.setFloat(1, crystalRushStats.getCoinsWin());
                preparedStatement.setInt(2, crystalRushStats.getStats(Stats.GAMEPLAYED));
                preparedStatement.setInt(3, crystalRushStats.getStats(Stats.WINS));
                preparedStatement.setInt(4, crystalRushStats.getStats(Stats.KILLS));
                preparedStatement.setInt(5, crystalRushStats.getStats(Stats.DEATHS));
                preparedStatement.setInt(6, crystalRushStats.getStats(Stats.CRYSTAL_KILLS));
                preparedStatement.setString(7,
                        crystalRushStats.getLastKit() == null ? null : crystalRushStats.getLastKit().getName());
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
        if (crystalRushStatsMap.get(uuid) != null)
            crystalRushStatsMap.remove(uuid);
    }


    /**
     * Récupérer le Profil de Stats SkyRush d'un Joueur.
     *
     * @param uuid
     */
    public static CrystalRushStats get(UUID uuid) {
        if (crystalRushStatsMap.get(uuid) == null)
            crystalRushStatsMap.put(uuid, new CrystalRushStats(uuid));
        return crystalRushStatsMap.get(uuid);
    }
}