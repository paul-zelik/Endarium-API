package net.endarium.api;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.endarium.api.config.MySQLConfig;
import net.endarium.api.config.RedisConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class EndariumCommons implements Closeable {

	private String prefixCommons = "[APICommons] ";
	private boolean developer = true;
	private static EndariumCommons instance;

	// Database & Services
	private ExecutorService executorService;

	private HikariDataSource hikariDataSource;
	private JedisPool jedisPool;

	private EndariumEntities endariumEntities;

	public EndariumCommons(MySQLConfig mySQLConfig, RedisConfig redisConfig, boolean developer) {
		instance = this;
		this.developer = developer;
		this.executorService = Executors.newCachedThreadPool();

		this.setupMySQL(mySQLConfig);
		this.setupRedis(redisConfig);

		this.endariumEntities = new EndariumEntities();

		System.out.print(this.prefixCommons + "Developer Mode : " + this.developer);
	}

	@Override
	public void close() throws IOException {
		this.executorService.shutdown();
		if (hikariDataSource != null)
			hikariDataSource.close();
		if (jedisPool != null)
			jedisPool.close();
		instance = null;
	}

	/**
	 * Déploiement de base de données (MYSQL)
	 * 
	 * @param mySQLConfig
	 */
	private void setupMySQL(MySQLConfig mySQLConfig) {
		if (mySQLConfig.isEnable()) {
			try {
				final HikariConfig hikariConfig = new HikariConfig();
				hikariConfig.setJdbcUrl("jdbc:mysql://" + mySQLConfig.getHost() + ":" + mySQLConfig.getPort() + "/"
						+ mySQLConfig.getDatabase() + "?useUnicode=yes");
				hikariConfig.setUsername(mySQLConfig.getUsername());
				hikariConfig.setPassword(mySQLConfig.getPassword());
				hikariConfig.setMaxLifetime(600000L);
				hikariConfig.setIdleTimeout(300000L);
				hikariConfig.setLeakDetectionThreshold(300000L);
				hikariConfig.setMaximumPoolSize(4);
				hikariConfig.setConnectionTimeout(5000L);
				hikariConfig.addDataSourceProperty("useSSL", false);
				this.hikariDataSource = new HikariDataSource(hikariConfig);
				System.out.println(
						this.prefixCommons + "La MySQL '" + mySQLConfig.getDatabase() + "' est prête à être utilisée.");
			} catch (Exception exception) {
				System.err.println(this.prefixCommons + "Erreur : Impossible de se connecter à la SQL : "
						+ mySQLConfig.getDatabase());
				this.hikariDataSource.close();
				this.hikariDataSource = null;
			}
		} else {
			System.out.println(this.prefixCommons + "L'option MySQL n'est pas activée pour cette instance.");
		}
	}

	/**
	 * Déploiement de base de données (REDIS)
	 * 
	 * @param redisConfig
	 */
	private void setupRedis(RedisConfig redisConfig) {
		if (redisConfig.isEnable()) {
			ClassLoader previous = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(Jedis.class.getClassLoader());
			JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
			this.jedisPool = new JedisPool(jedisPoolConfig, redisConfig.getHost(), redisConfig.getPort(), 3000,
					redisConfig.getPassword(), redisConfig.getDatabase());
			Thread.currentThread().setContextClassLoader(previous);
			try (Jedis jedisConnector = jedisPool.getResource()) {
				System.out.println(
						this.prefixCommons + "Redis est maintenant actif sur le port : " + redisConfig.getPort());
			} catch (Exception exception) {
				System.err.println(this.prefixCommons + "Erreur : Impossible de se connecter à Redis.");
				this.jedisPool.close();
				this.jedisPool = null;
			}
		} else {
			System.out.println(this.prefixCommons + "L'option Redis n'est pas activée pour cette instance.");
		}
	}

	public boolean isDeveloper() {
		return developer;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public HikariDataSource getHikariDataSource() {
		return hikariDataSource;
	}

	public JedisPool getJedisPool() {
		return jedisPool;
	}

	public EndariumEntities getEndariumEntities() {
		return endariumEntities;
	}

	public static EndariumCommons getInstance() {
		return instance;
	}
}
