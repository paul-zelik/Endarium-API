package net.endarium.api.players.login;

import net.endarium.api.EndariumCommons;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class LoginManager {

    private final String TABLE = "authme";

    public LoginManager() {
        this.checkConnectedPlayers();
    }

    private void checkConnectedPlayers() {
        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT uuid FROM " + TABLE + " WHERE connected = true");
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /*
     * Créer un compte avec l'UUID et le mot de passe spécifiés.
     */
    public void createAccount(UUID uuid, String password) {
        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO " + TABLE + " (uuid, password, created_at, connected) VALUES (?, ?, NOW(), false)");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /*
     * Récupérer le mot de passe d'un compte à partir de son UUID.
     */
    public String getPassword(UUID uuid) {
        String password = null;
        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT password FROM " + TABLE + " WHERE uuid = ? LIMIT 1");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                password = resultSet.getString("password");
            }
            preparedStatement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return password;
    }

    /*
     * Vérifie si un joueur avec l'UUID spécifié existe dans la base de données.
     */
    public boolean loginPlayerExists(UUID uuid) {
        boolean exists = false;
        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT COUNT(*) AS count FROM " + TABLE + " WHERE uuid = ?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                exists = count > 0;
            }
            preparedStatement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return exists;
    }

    /*
     * Vérifier si un compte est connecté à partir de son UUID.
     */
    public boolean isLogged(UUID uuid) {
        boolean connected = false;
        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT connected FROM " + TABLE + " WHERE uuid = ? LIMIT 1");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                connected = resultSet.getBoolean("connected");
            }
            preparedStatement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return connected;
    }

    /*
     * Déconnecter un compte à partir de son UUID.
     */
    public void makeDisconnected(UUID uuid) {
        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE " + TABLE + " SET connected = false WHERE uuid = ?");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /*
     * Connecter un compte à partir de son UUID.
     */
    public void makeConnected(UUID uuid) {
        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE " + TABLE + " SET connected = true WHERE uuid = ?");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }


}
