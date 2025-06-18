package net.endarium.api.players.friends;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import net.endarium.api.EndariumCommons;
import net.endarium.api.players.friends.FriendChannelType.FriendChannelRequest;
import net.endarium.api.utils.GSONUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

public class FriendsManager {

	private final String TABLE = "player_friends";

	/**
	 * Sauvegarder les amis dans la base données.
	 * 
	 * @param uuid
	 * @param friends
	 */
	public void setFriends(UUID uuid, Friends friends) {
		EndariumCommons.getInstance().getExecutorService().submit(() -> {
			try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
				PreparedStatement preparedStatement = connection
						.prepareStatement("UPDATE " + TABLE + " SET friends = ?, updated_at = NOW() WHERE uuid = ?");
				preparedStatement.setString(1, GSONUtils.getGson().toJson(friends));
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
	 * Récupérer son profil d'Amis.
	 * 
	 * @param uuid
	 * @return
	 */
	public Friends getFriends(UUID uuid) {
		Friends friends = new Friends(uuid);
		try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
			PreparedStatement preparedStatement = connection
					.prepareStatement("SELECT friends FROM " + TABLE + " WHERE uuid = ? LIMIT 1");
			preparedStatement.setString(1, uuid.toString());
			ResultSet resultSet = preparedStatement.executeQuery();
			if (!(resultSet.next())) {

				// Création d'un compte Friends au Joueur
				preparedStatement.close();
				preparedStatement = connection.prepareStatement(
						"INSERT INTO " + TABLE + " (uuid, friends, updated_at, created_at) VALUES(?, ?, NOW(), NOW())");
				preparedStatement.setString(1, uuid.toString());
				preparedStatement.setString(2, GSONUtils.getGson().toJson(friends));
				preparedStatement.executeUpdate();

				preparedStatement.close();
				connection.close();

				return friends;
			}
			friends = GSONUtils.getGson().fromJson(resultSet.getString("friends"), Friends.class);
			preparedStatement.close();
			connection.close();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return friends;
	}

	/** ############### REDIS CACHE ############### **/

	/**
	 * Sauvegarder le cache des Amis sur Redis.
	 * 
	 * @param friends
	 */
	public void saveRedisFriends(Friends friends) {
		EndariumCommons.getInstance().getExecutorService().submit(() -> {
			try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
				String jsonFriends = GSONUtils.getGson().toJson(friends);
				if (jsonFriends != null) {
					String pathFriendsRedis = TABLE + ":" + friends.getUUID().toString();
					jedis.set(pathFriendsRedis, jsonFriends);
					jedis.expire(pathFriendsRedis, 10800);
					this.setFriends(friends.getUUID(), friends);
				}
			} catch (JedisException exception) {
				exception.printStackTrace();
			}
		});
	}

	/**
	 * Récupérer le cache des Amis via Redis.
	 * 
	 * @param uuid
	 * @return
	 */
	public Friends getRedisFriends(UUID uuid) {
		try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
			String pathFriendsRedis = TABLE + ":" + uuid.toString();
			if (!(jedis.exists(pathFriendsRedis))) {
				Friends friends = this.getFriends(uuid);
				this.saveRedisFriends(friends);
				return friends;
			}
			return GSONUtils.getGson().fromJson(jedis.get(pathFriendsRedis), Friends.class);
		} catch (JedisException exception) {
			exception.printStackTrace();
		}
		return null;
	}

	/** ############### REDIS REQUEST ############### **/

	/**
	 * Envoyer une requête d'Ami.
	 * 
	 * @param uuid
	 * @param targetUUID
	 */
	public void makeFriendRequest(UUID uuid, UUID targetUUID) {
		EndariumCommons.getInstance().getExecutorService().submit(() -> {
			try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
				String request = TABLE + ":friendRequest:" + uuid.toString() + ":" + targetUUID.toString();
				if (!(jedis.exists(request))) {
					jedis.set(request, GSONUtils.getGson().toJson(new FriendChannelRequest(uuid, targetUUID)));
					jedis.expire(request, 300);
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		});
	}

	/**
	 * Supprimer une requête d'Ami.
	 * 
	 * @param uuid
	 * @param targetUUID
	 */
	public void cancelFriendRequest(UUID uuid, UUID targetUUID) {
		EndariumCommons.getInstance().getExecutorService().submit(() -> {
			try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
				String request = TABLE + ":friendRequest:" + uuid.toString() + ":" + targetUUID.toString();
				if (jedis.exists(request))
					jedis.del(request);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		});
	}

	/**
	 * Vérifier si une requête d'ami existe.
	 * 
	 * @param uuid
	 * @param targetUUID
	 */
	public boolean hasFriendRequest(UUID uuid, UUID targetUUID) {
		try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
			String request = TABLE + ":friendRequest:" + uuid.toString() + ":" + targetUUID.toString();
			return jedis.exists(request);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return false;
	}
}