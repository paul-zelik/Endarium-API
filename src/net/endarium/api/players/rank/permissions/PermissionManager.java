package net.endarium.api.players.rank.permissions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.endarium.api.EndariumCommons;

public class PermissionManager {

	private final String TABLE = "player_permissions";

	public PermissionManager() {
		this.checkExpiriesPermissions();
	}

	/**
	 * Ajouter une Permission Permanente à un Joueur.
	 * 
	 * @param uuid
	 * @param permission
	 */
	public void addPermission(UUID uuid, String permission) {
		EndariumCommons.getInstance().getExecutorService().submit(() -> {
			try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
				PreparedStatement preparedStatement = connection.prepareStatement(
						"INSERT INTO " + TABLE + " (uuid, permission, created_at) VALUES (?, ?, NOW())");
				preparedStatement.setString(1, uuid.toString());
				preparedStatement.setString(2, permission);
				preparedStatement.executeUpdate();
				preparedStatement.close();
				connection.close();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		});
	}

	/**
	 * Ajouter une Permission avec un Délai à un Joueur.
	 * 
	 * @param uuid
	 * @param permission
	 * @param hoursDelay
	 */
	public void addPermissionTemporary(UUID uuid, String permission, int hoursDelay) {
		EndariumCommons.getInstance().getExecutorService().submit(() -> {
			try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
				PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + TABLE
						+ " (uuid, permission, permission_expiry, created_at) VALUES (?, ?, DATE_ADD(NOW(), INTERVAL "
						+ hoursDelay + " HOUR), NOW())");
				preparedStatement.setString(1, uuid.toString());
				preparedStatement.setString(2, permission);
				preparedStatement.executeUpdate();
				preparedStatement.close();
				connection.close();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		});
	}

	/**
	 * Supprimer une Permission d'un Joueur.
	 * 
	 * @param uuid
	 * @param permission
	 */
	public void removePermission(UUID uuid, String permission) {
		EndariumCommons.getInstance().getExecutorService().submit(() -> {
			try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
				PreparedStatement preparedStatement = connection
						.prepareStatement("DELETE FROM " + TABLE + " WHERE " + "uuid = ? and permission = ?");
				preparedStatement.setString(1, uuid.toString());
				preparedStatement.setString(2, permission);
				preparedStatement.executeUpdate();
				preparedStatement.close();
				connection.close();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		});
	}

	/**
	 * Récupérer les Permissions d'un Joueur.
	 * 
	 * @param uuid
	 * @return
	 */
	public List<String> getPermissions(UUID uuid) {
		List<String> permissions = new ArrayList<String>();
		try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
			PreparedStatement preparedStatement = connection
					.prepareStatement("SELECT permission FROM " + TABLE + " WHERE uuid = ?");
			preparedStatement.setString(1, uuid.toString());
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				permissions.add((resultSet.getString("permission")));
			}
			preparedStatement.close();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return permissions;
	}

	/**
	 * Faire expirer les grades dépassés de date.
	 */
	private void checkExpiriesPermissions() {
		EndariumCommons.getInstance().getExecutorService().submit(() -> {
			try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
				PreparedStatement preparedStatement = connection
						.prepareStatement("DELETE FROM " + TABLE + " WHERE permission_expiry <= NOW()");
				preparedStatement.executeUpdate();
				preparedStatement.close();
				connection.close();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		});
	}
}