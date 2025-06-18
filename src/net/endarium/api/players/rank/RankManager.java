package net.endarium.api.players.rank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import net.endarium.api.EndariumCommons;

public class RankManager {

	private final String TABLE = "player_rank";

	public RankManager() {
		this.checkExpiriesRanks();
	}

	/*
	 * Définir le Rank d'un joueur de façon permanente.
	 */
	public void setRank(UUID uuid, Rank rank) {
		EndariumCommons.getInstance().getExecutorService().submit(() -> {
			try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
				PreparedStatement preparedStatement = connection.prepareStatement("UPDATE " + TABLE
						+ " SET rank = ?, lasted_rank = ?, rank_expiry = ?, updated_at = NOW() WHERE uuid = ?");
				preparedStatement.setString(1, rank.getIdentificatorName());
				preparedStatement.setString(2, rank.getIdentificatorName());
				preparedStatement.setDate(3, null);
				preparedStatement.setString(4, uuid.toString());
				preparedStatement.executeUpdate();
				preparedStatement.close();
				connection.close();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		});
	}

	/*
	 * Définir le Rank d'un joueur avec un délai.
	 */
	public void setRankTemporary(UUID uuid, Rank rank, int minutes) {
		EndariumCommons.getInstance().getExecutorService().submit(() -> {
			try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
				PreparedStatement preparedStatement = connection
						.prepareStatement("UPDATE " + TABLE + " SET rank = ?, rank_expiry = DATE_ADD(NOW(), INTERVAL "
								+ minutes + " MINUTE), updated_at = NOW() WHERE uuid = ?");
				preparedStatement.setString(1, rank.getIdentificatorName());
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
	 * Récupérer le Rank d'un joueur.
	 * 
	 * @param uuid
	 * @return Rank
	 */
	public Rank getRank(UUID uuid, boolean nullable) {
		Rank rank = Rank.DEFAULT;
		try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
			PreparedStatement preparedStatement = connection
					.prepareStatement("SELECT rank FROM " + TABLE + " WHERE uuid = ? LIMIT 1");
			preparedStatement.setString(1, uuid.toString());
			ResultSet resultSet = preparedStatement.executeQuery();
			if (!(resultSet.next())) {

				// Vérifier si la méthode est 'Nullable'
				if (nullable) {
					preparedStatement.close();
					connection.close();
					return null;
				}

				// Création d'un compte Rank au Joueur
				preparedStatement.close();
				preparedStatement = connection.prepareStatement("INSERT INTO " + TABLE
						+ " (uuid, rank, lasted_rank, rank_expiry, updated_at, created_at) VALUES (?, ?, ?, ?, NOW(), NOW())");
				preparedStatement.setString(1, uuid.toString());
				preparedStatement.setString(2, Rank.DEFAULT.getIdentificatorName());
				preparedStatement.setString(3, Rank.DEFAULT.getIdentificatorName());
				preparedStatement.setDate(4, null);
				preparedStatement.executeUpdate();

				preparedStatement.close();
				connection.close();

				return rank;
			}
			rank = Rank.getUserRank(resultSet.getString("rank"), false);
			preparedStatement.close();
			connection.close();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return rank;
	}

	/**
	 * Récupérer le Grade d'un Joueur.
	 * 
	 * @param uuid
	 * @return
	 */
	public Rank getRank(UUID uuid) {
		return this.getRank(uuid, false);
	}

	/**
	 * Faire expirer les grades dépassés de date.
	 */
	private void checkExpiriesRanks() {
		EndariumCommons.getInstance().getExecutorService().submit(() -> {
			try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
				PreparedStatement preparedStatement = connection.prepareStatement(
						"UPDATE " + TABLE + " SET rank = lasted_rank, rank_expiry = ? WHERE rank_expiry <= NOW()");
				preparedStatement.setDate(1, null);
				preparedStatement.executeUpdate();
				preparedStatement.close();
				connection.close();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		});
	}
}