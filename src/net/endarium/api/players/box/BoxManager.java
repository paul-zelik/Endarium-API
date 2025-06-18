package net.endarium.api.players.box;

import net.endarium.api.EndariumCommons;
import net.endarium.api.players.wallets.Currency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BoxManager {

    public BoxManager() {
        this.checkBoxPlayers();
    }

    private void checkBoxPlayers() {
        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT uuid FROM player_box");
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static void createAccountBox(UUID uuid) {
        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
            String query = "INSERT INTO player_box (uuid, countbox, updated_at, created_at) VALUES (?, 0, NOW(), NOW())";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean playerExists(UUID uuid) {
        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT COUNT(*) AS count FROM player_box WHERE uuid = ?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0; // Si count > 0, le joueur existe dans la table
            }
            preparedStatement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false; // Si une erreur se produit ou si le joueur n'est pas trouvé, renvoie false
    }

    public static int getBoxPlayer(UUID uuid) {
        if (!(playerExists(uuid))) {
            createAccountBox(uuid);
        }
        int countBox = 0;
        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
            String query = "SELECT countbox FROM player_box WHERE uuid = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                countBox = resultSet.getInt("countbox");
            }
        } catch (SQLException e) {
            createAccountBox(uuid);
        }
        return countBox;

    }

    public static void setBoxPlayer(UUID uuid, int countBox) {
    try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
        String query = "UPDATE player_box SET countbox = ?, updated_at = NOW() WHERE uuid = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, countBox);
        preparedStatement.setString(2, uuid.toString());
        preparedStatement.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

}
