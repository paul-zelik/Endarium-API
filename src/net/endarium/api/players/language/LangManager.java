package net.endarium.api.players.language;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import net.endarium.api.EndariumCommons;

public class LangManager {

	private final String TABLE = "player_lang";

	/*
	 * Définir la Langue d'un joueur.
	 */
	public void setLanguages(UUID uuid, Languages languages) {
		EndariumCommons.getInstance().getExecutorService().submit(() -> {
			try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
				PreparedStatement preparedStatement = connection.prepareStatement(
						"UPDATE " + TABLE + " SET language_zone = ?, updated_at = NOW() WHERE uuid = ?");
				preparedStatement.setString(1, languages.getZoneLangue());
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
	 * Récupérer la Langue d'un joueur.
	 * 
	 * @param uuid
	 * @return Languages
	 */
	public Languages getLanguages(UUID uuid) {
		Languages language = Languages.FRENCH;
		try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
			PreparedStatement preparedStatement = connection
					.prepareStatement("SELECT language_zone FROM " + TABLE + " WHERE uuid = ? LIMIT 1");
			preparedStatement.setString(1, uuid.toString());
			ResultSet resultSet = preparedStatement.executeQuery();
			if (!(resultSet.next())) {

				// Création d'un compte Languages au Joueur
				preparedStatement.close();
				preparedStatement = connection.prepareStatement("INSERT INTO " + TABLE
						+ " (uuid, language_zone, updated_at, created_at) VALUES (?, ?, NOW(), NOW())");
				preparedStatement.setString(1, uuid.toString());
				preparedStatement.setString(2, Languages.FRENCH.getZoneLangue());
				preparedStatement.executeUpdate();

				preparedStatement.close();
				connection.close();

				return language;
			}
			language = Languages.getLangueByZone(resultSet.getString("language_zone"));
			preparedStatement.close();
			connection.close();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return language;
	}
}