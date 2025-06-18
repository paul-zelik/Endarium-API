package net.endarium.api.utils.world;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class LocationsUtils {

	/**
	 * Récupérer des coordonnées autour d'un Cercle.
	 * 
	 * @param center
	 * @param radius
	 * @param angleInRadian
	 */
	public static Location getLocationAroundCircle(Location center, double radius, double angleInRadian) {
		double x = center.getX() + radius * Math.cos(angleInRadian);
		double z = center.getZ() + radius * Math.sin(angleInRadian);

		Location location = new Location(center.getWorld(), x, 64, z);
		Vector difference = center.toVector().clone().subtract(location.toVector());
		location.setDirection(difference);

		return location;
	}

	/**
	 * Téléporation plus optimisée vers une Coordonnée.
	 * 
	 * @param x
	 * @param z
	 * @param player
	 * @param world
	 */
	public static void teleportOptimizedTo(int x, int z, Player player, String world) {
		int y = 250;
		Location baseT = new Location(Bukkit.getWorld(world), x, y, z);
		Block b = baseT.getBlock();

		do {
			y--;
			baseT.setY(y);
			b = baseT.getBlock();
			if (!(b.getType().equals(Material.AIR) || b.getType().equals(Material.LOG)
					|| b.getType().equals(Material.LEAVES) || b.getType().equals(Material.LOG_2)
					|| b.getType().equals(Material.LEAVES_2))) {
				Location base = new Location(Bukkit.getWorld(world), x, y + 3, z);

				player.teleport(base);

			}
		} while (b.getType().equals(Material.AIR) || b.getType().equals(Material.LOG)
				|| b.getType().equals(Material.LEAVES) || b.getType().equals(Material.LOG_2)
				|| b.getType().equals(Material.LEAVES_2));
	}

	/**
	 * Récupérer une Location optimisée.
	 * 
	 * @param x
	 * @param z
	 * @param world
	 */
	public static Location getOptimizedLocation(int x, int z, String world) {
		int y = 250;
		Location baseT = new Location(Bukkit.getWorld(world), x, y, z);
		Block b = baseT.getBlock();

		do {
			y--;
			baseT.setY(y);
			b = baseT.getBlock();
			if (!(b.getType().equals(Material.AIR) || b.getType().equals(Material.LOG)
					|| b.getType().equals(Material.LEAVES) || b.getType().equals(Material.LOG_2)
					|| b.getType().equals(Material.LEAVES_2))) {
				Location base = new Location(Bukkit.getWorld(world), x, y + 3, z);
				return base;
			}
		} while (b.getType().equals(Material.AIR) || b.getType().equals(Material.LOG)
				|| b.getType().equals(Material.LEAVES) || b.getType().equals(Material.LOG_2)
				|| b.getType().equals(Material.LEAVES_2));

		return baseT;
	}

	/**
	 * Définir une Parteforme.
	 * 
	 * @param center
	 * @param radius
	 * @param material
	 */
	public static void setFloor(Location center, int radius, Material material, boolean allowBorder) {
		for (int xMod = -radius; xMod <= radius; xMod++) {
			for (int zMod = -radius; zMod <= radius; zMod++) {
				Block theBlock = center.getBlock().getRelative(xMod, 0, zMod);
				theBlock.setType(material);
			}
		}
	}
}
