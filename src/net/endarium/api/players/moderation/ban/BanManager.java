package net.endarium.api.players.moderation.ban;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import net.endarium.api.EndariumCommons;

public class BanManager {

	private final String TABLE = "player_bans";

	/**
	 * Création de l'Entitée des Bans.
	 */
	public BanManager() {
		this.checkExpiriesBans();
	}

	/**
	 * Création d'un Ban Permanent.
	 * 
	 * @param uuid
	 * @param banInfos
	 */
	public void createBanPermanent(UUID uuid, BanInfos banInfos) {
		EndariumCommons.getInstance().getExecutorService().submit(() -> {
			try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
				PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + TABLE
						+ " (uuid, mod_uuid, reason, permanent, ban_id, banned, mod_played, player_online, ban_expiry, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NULL, NOW())");
				preparedStatement.setString(1, banInfos.getUUID().toString());
				preparedStatement.setString(2, banInfos.getModUUID().toString());
				preparedStatement.setString(3, banInfos.getReason());
				preparedStatement.setBoolean(4, true);
				preparedStatement.setString(5, banInfos.getBanID().toString());
				preparedStatement.setBoolean(6, true);
				preparedStatement.setBoolean(7, banInfos.isModPlayed());
				preparedStatement.setBoolean(8, banInfos.isPlayerOnline());
				preparedStatement.executeUpdate();
				preparedStatement.close();
				connection.close();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		});
	}

	/**
	 * Création d'un Ban Temporaire.
	 * 
	 * @param uuid
	 * @param banInfos
	 */
	public void createBanTemporaire(UUID uuid, BanInfos banInfos, int days) {
		EndariumCommons.getInstance().getExecutorService().submit(() -> {
			try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
				PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + TABLE
						+ " (uuid, mod_uuid, reason, permanent, ban_id, banned, mod_played, player_online, ban_expiry, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, DATE_ADD(NOW(), INTERVAL "
						+ days + " DAY), NOW())");
				preparedStatement.setString(1, banInfos.getUUID().toString());
				preparedStatement.setString(2, banInfos.getModUUID().toString());
				preparedStatement.setString(3, banInfos.getReason());
				preparedStatement.setBoolean(4, false);
				preparedStatement.setString(5, banInfos.getBanID().toString());
				preparedStatement.setBoolean(6, true);
				preparedStatement.setBoolean(7, banInfos.isModPlayed());
				preparedStatement.setBoolean(8, banInfos.isPlayerOnline());
				preparedStatement.executeUpdate();
				preparedStatement.close();
				connection.close();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		});
	}

	/**
	 * Appliquer un UnBan à un Joueur.
	 * 
	 * @param uuid
	 */
	public void applyUnBan(UUID uuid) {
		EndariumCommons.getInstance().getExecutorService().submit(() -> {
			try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
				PreparedStatement preparedStatement = connection
						.prepareStatement("UPDATE " + TABLE + " SET banned = ? WHERE UUID = ?");
				preparedStatement.setBoolean(1, false);
				preparedStatement.setString(2, uuid.toString());
				preparedStatement.executeUpdate();
				preparedStatement.close();
				connection.close();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		});
	}

	/**
	 * Vérifier si le Joueur est Banni et ses Informations de Bans.
	 * 
	 * @param uuid
	 * @return
	 */
	public BanInfos isBan(UUID uuid) {
		try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
			PreparedStatement preparedStatement = connection
					.prepareStatement("SELECT * FROM " + TABLE + " WHERE uuid = ? AND banned = ? LIMIT 1");
			preparedStatement.setString(1, uuid.toString());
			preparedStatement.setBoolean(2, true);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (!(resultSet.next())) {
				preparedStatement.close();
				connection.close();
				return null;
			}

			// Récupérer les Informations de Bans.
			BanInfos ban = new BanInfos(uuid, UUID.fromString(resultSet.getString("mod_uuid")),
					resultSet.getString("ban_id"), resultSet.getString("reason"), resultSet.getDate("created_at"),
					resultSet.getDate("ban_expiry"), resultSet.getBoolean("permanent"), true,
					resultSet.getBoolean("mod_played"), resultSet.getBoolean("player_online"));
			preparedStatement.close();
			connection.close();
			return ban;

		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return null;
	}

	/**
	 * Faire expirer les bans dépassés de Date.
	 */
	private void checkExpiriesBans() {
		EndariumCommons.getInstance().getExecutorService().submit(() -> {
			try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
				PreparedStatement preparedStatement = connection
						.prepareStatement("UPDATE " + TABLE + " SET banned = ? WHERE ban_expiry <= NOW()");
				preparedStatement.setBoolean(1, false);
				preparedStatement.executeUpdate();
				preparedStatement.close();
				connection.close();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		});
	}
}