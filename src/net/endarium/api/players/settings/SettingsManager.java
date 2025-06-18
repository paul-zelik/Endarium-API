package net.endarium.api.players.settings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.endarium.api.EndariumCommons;
import net.endarium.api.players.language.Languages;
import net.endarium.api.utils.EndariumAPI;
import net.endarium.api.utils.GSONUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

/**
 * Entity : UserSettings | SettingsManager
 */
public class SettingsManager {

    private final String TABLE = "user_settings";

    /**
     * Créer un nouveau compte 'Setting' aux utilisateurs.
     *
     * @param uuid
     */
    public boolean createSettingAccount(UUID uuid) {
        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT settings FROM " + TABLE + " WHERE uuid = ? LIMIT 1");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!(resultSet.next())) {
                preparedStatement.close();
                preparedStatement = connection.prepareStatement("INSERT INTO " + TABLE
                        + " (uuid, settings, updated_at, created_at) VALUES(?, ?, NOW(), NOW())");
                preparedStatement.setString(1, uuid.toString());

                // Sauvegarder les paramètres par défaut
                Map<SettingType, SettingStatus> settingMap = new HashMap<SettingType, SettingStatus>();
                for (SettingType settingType : SettingType.values())
                    settingMap.put(settingType, settingType.getDefaultValue());
                SettingAccount settingAccount = new SettingAccount(uuid, settingMap, Languages.FRENCH);
                preparedStatement.setString(2, GSONUtils.getGson().toJson(settingAccount));

                preparedStatement.executeUpdate();
                preparedStatement.close();
                connection.close();
                System.out.println(
                        EndariumAPI.getPrefixAPI() + "Compte du Setting (" + uuid.toString() + ") a bien été créer.");
            }
            return true;
        } catch (SQLException exception) {
            System.err.println(EndariumAPI.getPrefixAPI() + "Erreur : Impossible de créer un Setting via MySQL ("
                    + uuid.toString() + ")");
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * Définir les Settings d'un utilisateur en MySQL.
     *
     * @param uuid
     * @param settingAccount
     */
    public void setSettingAccount(UUID uuid, SettingAccount settingAccount) {
        EndariumCommons.getInstance().getExecutorService().submit(() -> {
            try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
                PreparedStatement preparedStatement = connection
                        .prepareStatement("UPDATE " + TABLE + " SET settings = ?, updated_at = NOW() WHERE uuid = ?");
                preparedStatement.setString(1, GSONUtils.getGson().toJson(settingAccount));
                preparedStatement.setString(2, uuid.toString());
                preparedStatement.executeUpdate();
                preparedStatement.close();
                connection.close();
            } catch (SQLException exception) {
                System.err.println(EndariumAPI.getPrefixAPI()
                        + "Erreur : Impossible de définir les Settings d'un utilisateur via MySQL (" + uuid.toString()
                        + ")");
                exception.printStackTrace();
            }
        });
    }

    /**
     * Récuoérer un compte Setting d'un utilisateur.
     *
     * @param uuid
     */
    public SettingAccount getSettingAccount(UUID uuid) {
        SettingAccount settingAccount = new SettingAccount(uuid, new HashMap<SettingType, SettingStatus>(),
                Languages.FRENCH);
        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT settings FROM " + TABLE + " WHERE uuid = ? LIMIT 1");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                preparedStatement.close();
                connection.close();
                return settingAccount;
            }
            settingAccount = GSONUtils.getGson().fromJson(resultSet.getString("settings"), SettingAccount.class);
            preparedStatement.close();
            connection.close();
        } catch (SQLException exception) {
            System.err.println(EndariumAPI.getPrefixAPI()
                    + "Erreur : Impossible de récupérer les Settings d'un utilisateur via MySQL (" + uuid.toString()
                    + ")");
            exception.printStackTrace();
        }
        return settingAccount;
    }

    /** ############### REDIS CACHE ############### **/

    /**
     * Sauvegarder les Settings dans le Cache Redis.
     *
     * @param settingAccount
     */
    public void saveRedisSetting(SettingAccount settingAccount) {
        EndariumCommons.getInstance().getExecutorService().submit(() -> {
            try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
                String jsonSetting = GSONUtils.getGson().toJson(settingAccount);
                if (jsonSetting != null) {
                    String pathSettingCache = TABLE + ":" + settingAccount.getUUID().toString();
                    jedis.set(pathSettingCache, jsonSetting);
                    jedis.expire(pathSettingCache, 10800);
                }
            } catch (JedisException exception) {
                System.out.println(EndariumAPI.getPrefixAPI() + "Impossible de Sauvegarder les Settings d'un Joueur.");
                exception.printStackTrace();
            }
        });
    }

    /**
     * Récupérer les Settings depuis le Cache Redis.
     *
     * @param uuid
     */
    public SettingAccount getRedisSetting(UUID uuid) {
        try (Jedis jedis = EndariumCommons.getInstance().getJedisPool().getResource()) {
            String pathSettingCache = TABLE + ":" + uuid.toString();
            if (!(jedis.exists(pathSettingCache))) {
                SettingAccount settingAccount = this.getSettingAccount(uuid);
                this.saveRedisSetting(settingAccount);
                return settingAccount;
            }
            return GSONUtils.getGson().fromJson(jedis.get(pathSettingCache), SettingAccount.class);
        } catch (JedisException exception) {
            System.out.println(EndariumAPI.getPrefixAPI() + "Impossible de Récupérer les Settings d'un Joueur : ("
                    + uuid.toString() + ").");
            exception.printStackTrace();
        }
        return null;
    }
}