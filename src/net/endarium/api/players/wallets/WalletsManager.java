package net.endarium.api.players.wallets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import net.endarium.api.EndariumCommons;
import net.endarium.api.utils.GSONUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

public class WalletsManager {

	private final String TABLE = "player_wallets";

	/**
	 * Définir une monnaie dans un Wallet.
	 * 
	 * @param uuid
	 * @param currency
	 * @param value
	 */
	public void setCurrency(UUID uuid, Currency currency, int value) {
		EndariumCommons.getInstance().getExecutorService().submit(() -> {
			try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
				PreparedStatement preparedStatement = connection.prepareStatement(
						"UPDATE " + TABLE + " SET " + currency.getData() + " = ?, updated_at = NOW() WHERE uuid = ?");
				preparedStatement.setInt(1, value);
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
	 * Récupérer le Wallet d'un joueur.
	 * 
	 * @param uuid
	 * @param currency
	 * @return
	 */
	public int getCurrency(UUID uuid, Currency currency) {
		int valueCurrency = 0;
		try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
			PreparedStatement preparedStatement = connection
					.prepareStatement("SELECT " + currency.getData() + " FROM " + TABLE + " WHERE uuid = ? LIMIT 1");
			preparedStatement.setString(1, uuid.toString());
			ResultSet resultSet = preparedStatement.executeQuery();
			if (!(resultSet.next())) {

				// Création d'un compte Wallet au Joueur
				preparedStatement.close();
				preparedStatement = connection.prepareStatement("INSERT INTO " + TABLE
						+ " (uuid, coins, tokens, updated_at, created_at) VALUES(?, ?, ?, NOW(), NOW())");
				preparedStatement.setString(1, uuid.toString());
				preparedStatement.setInt(2, 0);
				preparedStatement.setInt(3, 0);
				preparedStatement.executeUpdate();

				preparedStatement.close();
				connection.close();

				return valueCurrency;
			}
			valueCurrency = resultSet.getInt(currency.getData());
			preparedStatement.close();
			connection.close();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return valueCurrency;
	}

	/** ############### REDIS CACHE ############### **/

	/**
	 * Sauvegarder un Wallet dans le Cache Redis.
	 * 
	 * @param walletAccount
	 */
	public void saveRedisWallet(WalletAccount walletAccount) {
		try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
			String jsonWallet = GSONUtils.getGson().toJson(walletAccount);
			if (jsonWallet != null) {
				String pathWalletRedis = TABLE + ":" + walletAccount.getUUID().toString();
				jedis.set(pathWalletRedis, jsonWallet);
				jedis.expire(pathWalletRedis, 21600);
			}
		} catch (JedisException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Récupérer un Wallet dans le Cache Redis.
	 * 
	 * @param uuid
	 * @return
	 */
	public WalletAccount getRedisWallet(UUID uuid) {
		try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
			String pathWalletRedis = TABLE + ":" + uuid.toString();
			if (!(jedis.exists(pathWalletRedis))) {
				WalletAccount walletAccount = new WalletAccount(uuid);
				this.saveRedisWallet(walletAccount);
				return walletAccount;
			}
			return GSONUtils.getGson().fromJson(jedis.get(pathWalletRedis), WalletAccount.class);
		} catch (JedisException exception) {
			exception.printStackTrace();
		}
		return null;
	}
}