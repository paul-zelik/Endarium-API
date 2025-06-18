package net.endarium.api.utils.mojang;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import net.endarium.api.EndariumCommons;

public class UUIDEndaFetcher {

	private static final String TABLE = "uuid_fetcher";

	/**
	 * Récupérer l'UUID d'un Joueur par son Compte.
	 * 
	 * @param name
	 * @return
	 */
	public static UUID getPlayerUUID(String name) {
		UUID uuid = null;
		try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
			PreparedStatement preparedStatement = connection
					.prepareStatement("SELECT uuid FROM " + TABLE + " WHERE name = ? LIMIT 1");
			preparedStatement.setString(1, name);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (!(resultSet.next())) {
				preparedStatement.close();
				connection.close();

				UUID uuidTarget = UUIDFetcher.getUUID(name);
				UUIDEndaFetcher.updateFetch(uuidTarget, name);

				return uuidTarget;
			}
			uuid = UUID.fromString(resultSet.getString("uuid"));
			preparedStatement.close();
			connection.close();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return uuid;
	}

	/**
	 * Récupérer le Pseudo du Joueur par son UUID.
	 * 
	 * @param uuid
	 * @return
	 */
	public static String getPlayerName(UUID uuid) {
		String name = "";
		try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
			PreparedStatement preparedStatement = connection
					.prepareStatement("SELECT name FROM " + TABLE + " WHERE uuid = ? LIMIT 1");
			preparedStatement.setString(1, uuid.toString());
			ResultSet resultSet = preparedStatement.executeQuery();
			if (!(resultSet.next())) {
				preparedStatement.close();
				connection.close();

				String nameTarget = UUIDFetcher.getName(uuid);
				UUIDEndaFetcher.updateFetch(uuid, nameTarget);

				return nameTarget;
			}
			name = resultSet.getString("name");
			preparedStatement.close();
			connection.close();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return name;
	}

	/**
	 * Sauvegarder le Fetcher d'un Joueur.
	 * 
	 * @param uuid
	 * @param name
	 */
	public static void updateFetch(UUID uuid, String name) {
		EndariumCommons.getInstance().getExecutorService().submit(() -> {
			try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
				PreparedStatement preparedStatement = connection
						.prepareStatement("SELECT name FROM " + TABLE + " WHERE uuid = ? LIMIT 1");
				preparedStatement.setString(1, uuid.toString());
				ResultSet resultSet = preparedStatement.executeQuery();
				if (!(resultSet.next())) {

					// Création d'un compte Fetcher au Joueur
					preparedStatement.close();
					preparedStatement = connection.prepareStatement("INSERT INTO " + TABLE
							+ " (uuid, name, updated_at, created_at) VALUES (?, ?, NOW(), NOW())");
					preparedStatement.setString(1, uuid.toString());
					preparedStatement.setString(2, name);

					preparedStatement.executeUpdate();

					preparedStatement.close();
					connection.close();

				} else if (!(resultSet.getString("name").equals(name))) {

					// Mettre à jour le compte Fetcher d'un Joueur
					preparedStatement.close();
					preparedStatement = connection
							.prepareStatement("UPDATE " + TABLE + " SET name = ?, updated_at = NOW() WHERE uuid = ?");
					preparedStatement.setString(1, name);
					preparedStatement.setString(2, uuid.toString());

					preparedStatement.executeUpdate();

					preparedStatement.close();
					connection.close();

				} else {
					preparedStatement.close();
					connection.close();
				}
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		});
	}

	/**
	 * Voir si un joueur à un compte sur Endarium.
	 * 
	 * @param uuid
	 */
	public static boolean hasAccount(String name) {
		if (UUIDEndaFetcher.getPlayerUUID(name) == null)
			return false;
		return true;
	}
}
