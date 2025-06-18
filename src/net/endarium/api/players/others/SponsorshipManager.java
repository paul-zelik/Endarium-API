package net.endarium.api.players.others;

import net.endarium.api.EndariumCommons;
import net.endarium.api.utils.EndariumAPI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SponsorshipManager {

    private final String TABLE = "user_sponsorship";

    /**
     * Constructeur du SponsorshipManager.
     */
    public SponsorshipManager() {
        this.clearSponsorship();
    }

    /**
     * Créer un parrainage en MySQL.
     *
     * @param uuid
     * @param uuidTarget
     * @param nameTarget
     */
    public void createSponsorship(UUID uuid, UUID uuidTarget, String nameTarget, int daysDelay) {
        EndariumCommons.getInstance().getExecutorService().submit(() -> {
            try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + TABLE
                        + " (uuid, sponsorship_uuid, sponsorship_name, sponsorship_delay, created_at) VALUES (?, ?, ?, DATE_ADD(NOW(), INTERVAL "
                        + daysDelay + " DAY), NOW())");
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setString(2, uuidTarget.toString());
                preparedStatement.setString(3, nameTarget);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                System.err.println(EndariumAPI.getPrefixAPI() + "Erreur : Impossible d'ajouter un parrainage via MySQL ("
                        + uuid.toString() + ")");
                e.printStackTrace();
            }
        });
    }

    /**
     * Récupérer les Parrainages d'un Utilisateur.
     *
     * @param uuid
     */
    public List<String> getSponsorshipNameList(UUID uuid) {
        List<String> sponsorshipList = new ArrayList<String>();
        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT sponsorship_name FROM " + TABLE + " WHERE uuid = ?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                sponsorshipList.add((rs.getString("sponsorship_name")));
            }
            preparedStatement.close();
        } catch (SQLException e) {
            System.err.println(EndariumAPI.getPrefixAPI() + "Erreur : Impossible de récupérer les parrainages via MySQL ("
                    + uuid.toString() + ")");
            e.printStackTrace();
        }
        return sponsorshipList;
    }

    /**
     * Faire expirer les Parrainages expirés.
     */
    private void clearSponsorship() {
        EndariumCommons.getInstance().getExecutorService().submit(() -> {
            try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
                PreparedStatement preparedStatement = connection
                        .prepareStatement("DELETE FROM " + TABLE + " WHERE sponsorship_delay <= NOW()");
                preparedStatement.executeUpdate();
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                System.err
                        .println(EndariumAPI.getPrefixAPI() + "Erreur : Impossible de clean les parrainages via MySQL !");
                e.printStackTrace();
            }
        });
    }

}
