package net.endarium.api.players.login;

import net.endarium.api.EndariumCommons;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static net.endarium.api.utils.mojang.UUIDFetcher.getUUID;

public class PreniumManager {

    private final String TABLE = "prenium";



    public PreniumManager() {
        this.checkPreniumPlayer();
    }

    private void checkPreniumPlayer() {
        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM " + TABLE);
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /*
     * Crée un compte premium dans la base de données.
     * Vérifie d'abord si le joueur a un compte premium sur Mojang.
     * Ensuite, insère le joueur dans la base de données avec son UUID, son IP et son statut premium.
     */
    public void createAccountPrenium(Player player, Boolean wantBePrenium) {
        try {
            boolean isPrenium = isPrenium(player);
            if (isPrenium) {
                UUID uuid = player.getUniqueId();
                String ip = player.getAddress().getAddress().getHostAddress();
                insertAccountIntoDatabase(uuid, ip, wantBePrenium);
            } else {
                UUID uuid = player.getUniqueId();
                String ip = player.getAddress().getAddress().getHostAddress();
                insertAccountIntoDatabase(uuid, ip, wantBePrenium);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Vérifie si le joueur avec cet UUID est déjà enregistré dans la base de données.
     */
    public boolean isPlayerRegistered(UUID uuid) {
        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM " + TABLE + " WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // Si une ligne est retournée, cela signifie que le joueur est enregistré.
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    /*
     * Vérifie si le joueur a un compte premium.
     * Utilise l'API de Mojang pour vérifier si le joueur possède un compte premium sous ce nom.
     */
    public boolean isPrenium(Player player) {
        try {
            String playerName = player.getName();
            UUID uuid = getUUID(playerName);
            if (uuid != null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
     * Vérifie si le joueur avec cet UUID a un compte premium enregistré dans la base de données.
     */
    public boolean hasAccountPrenium(Player player) {
        UUID uuid = player.getUniqueId();
        String ip = player.getAddress().getAddress().getHostAddress();
        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM " + TABLE + " WHERE uuid = ? AND ip = ? AND prenium = true")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, ip);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // Si une ligne est retournée, cela signifie que le compte premium existe.
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    /*
     * Méthode utilitaire pour insérer un compte premium dans la base de données.
     */
    private void insertAccountIntoDatabase(UUID uuid, String ip, boolean isPrenium) {
        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO " + TABLE + " (uuid, ip, prenium) VALUES (?, ?, ?)")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, ip);
            preparedStatement.setBoolean(3, isPrenium);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

}
