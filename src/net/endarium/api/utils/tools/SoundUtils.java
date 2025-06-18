package net.endarium.api.utils.tools;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Gestion des Sons Minecraft.
 */
public class SoundUtils {

	/**
	 * Send sound to a player
	 * 
	 * @param player
	 *            Player to send sound
	 * @param sound
	 *            Sound to send
	 */
	public static void sendSound(Player player, Sound sound) {
		player.playSound(player.getEyeLocation(), sound, 1F, 1F);
	}

	/**
	 * Send sound to a player
	 * 
	 * @param player
	 *            Player to send sound
	 * @param sound
	 *            Sound to send
	 * @param volume
	 *            Float volume
	 * @param pitch
	 *            Float pitch
	 */
	public static void sendSound(Player player, Sound sound, float volume, float pitch) {
		player.playSound(player.getEyeLocation(), sound, volume, pitch);
	}

	/**
	 * Send sound to a player at specific location
	 * 
	 * @param player
	 *            Player to play sound
	 * @param location
	 *            Location to play sound
	 * @param sound
	 *            Sound to send
	 */
	public static void sendSound(Player player, Location location, Sound sound) {
		location.getWorld().playSound(location, sound, 1F, 1F);
	}

	/**
	 * Send sound to a player at specific location
	 * 
	 * @param player
	 *            Player to send sound
	 * @param location
	 *            Location to play sound
	 * @param sound
	 *            Sound to send
	 * @param volume
	 *            Float volume
	 * @param pitch
	 *            Float pitch
	 */
	public static void sendSound(Player player, Location location, Sound sound, float volume, float pitch) {
		player.playSound(location, sound, volume, pitch);
	}

	/**
	 * Send sound for all players
	 * 
	 * @param sound
	 *            Sound to send
	 */
	public static void sendSoundForAll(Sound sound) {
		Bukkit.getOnlinePlayers().forEach(playerOnline -> SoundUtils.sendSound(playerOnline, sound));
	}

	/**
	 * Send sound for all players
	 * 
	 * @param sound
	 *            Sound to send
	 * @param volume
	 *            Float volume
	 * @param pitch
	 *            Float pitch
	 */
	public static void sendSoundForAll(Sound sound, float volume, float pitch) {
		Bukkit.getOnlinePlayers().forEach(playerOnline -> SoundUtils.sendSound(playerOnline, sound, volume, pitch));
	}

	/**
	 * Send sound at specific location
	 * 
	 * @param location
	 *            Location to send sound
	 * @param sound
	 *            Sound to send
	 */
	public static void sendSoundAt(Location location, Sound sound) {
		location.getWorld().playSound(location, sound, 1F, 1F);
	}

	/**
	 * Send sound at specific location
	 * 
	 * @param location
	 *            Location to send sound
	 * @param sound
	 *            Sound to send
	 * @param volume
	 *            Float volume
	 * @param pitch
	 *            Float pitch
	 */
	public static void sendSoundAt(Location location, Sound sound, float volume, float pitch) {
		location.getWorld().playSound(location, sound, volume, pitch);
	}
}