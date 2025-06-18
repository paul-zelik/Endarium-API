package net.endarium.api.players.others;

import net.endarium.api.EndariumCommons;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AntiBot {


    private final String TABLE = "ip_antibot"; // Remplacez par le nom de votre table

    public AntiBot() {this.checkListIp();}

    private void checkListIp() {
        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT ip FROM " + TABLE);
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /*
     * Crée une entrée IP dans la base de données si elle n'existe pas déjà.
     */
    public void createAccountIP(String ip) {
        if (!getAccountIP(ip)) {
            try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) { // Remplacez YourDatabaseConnection par votre gestionnaire de connexion
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO " + TABLE + " (ip, connection, created_at) VALUES (?, 0, NOW())");
                preparedStatement.setString(1, ip);
                preparedStatement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    /*
     * Vérifie si une IP existe dans la base de données.
     */
    public boolean getAccountIP(String ip) {
        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) { // Remplacez YourDatabaseConnection par votre gestionnaire de connexion
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT COUNT(*) AS count FROM " + TABLE + " WHERE ip = ?");
            preparedStatement.setString(1, ip);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    /*
     * Récupère le nombre de connexions pour une IP donnée.
     */
    public int getIpConnection(String ip) {
        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) { // Remplacez YourDatabaseConnection par votre gestionnaire de connexion
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT connection FROM " + TABLE + " WHERE ip = ?");
            preparedStatement.setString(1, ip);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("connection");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    /*
     * Ajoute une connexion pour une IP donnée.
     */
    public void addConnectionIp(String ip) {
        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) { // Remplacez YourDatabaseConnection par votre gestionnaire de connexion
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE " + TABLE + " SET connection = connection + 1 WHERE ip = ?");
            preparedStatement.setString(1, ip);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /*
     * Supprime une connexion pour une IP donnée.
     */
    public void removeConnectionIp(String ip) {
        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) { // Remplacez YourDatabaseConnection par votre gestionnaire de connexion
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE " + TABLE + " SET connection = connection - 1 WHERE ip = ?");
            preparedStatement.setString(1, ip);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }


}
