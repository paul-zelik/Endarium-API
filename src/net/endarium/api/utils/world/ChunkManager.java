package net.endarium.api.utils.world;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public class ChunkManager {

	/**
	 * Générer un Chunk par son Chunk.
	 * 
	 * @param world
	 * @param chunk
	 */
	public static void generateChunkbyChunk(World world, Chunk chunk) {
		int x = chunk.getX() - Bukkit.getViewDistance();
		int toX = x + Bukkit.getViewDistance() * 2;
		int toZ = x + Bukkit.getViewDistance() * 2;
		while (x < toX) {
			int z = chunk.getZ() - Bukkit.getViewDistance();
			while (z < toZ) {
				world.loadChunk(x, z);
				z++;
			}
			x++;
		}
	}

	/**
	 * Générer un Chunk via une Location.
	 * 
	 * @param world
	 * @param location
	 */
	public static void generateChunkbyLocation(World world, Location location) {
		int x = (int) location.getX();
		int z = (int) location.getZ();
		world.getChunkAt(x, z).load(true);
	}
}