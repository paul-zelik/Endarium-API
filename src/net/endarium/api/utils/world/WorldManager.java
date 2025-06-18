package net.endarium.api.utils.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;

import net.endarium.api.games.servers.CrystaliserAPI;
import net.endarium.api.minecraft.EndariumBukkit;
import net.endarium.api.utils.EndariumAPI;
import net.endarium.api.utils.builders.FileManager;
import net.endarium.api.utils.builders.FileManager.Config;
import net.endarium.crystaliser.servers.MapInfos;

public class WorldManager {

	/**
	 * Faire une copie de Fichier.
	 * 
	 * @param worldName
	 */
	public static void copyFolder(File src, File dest) throws IOException {

		if (src.isDirectory()) {

			if (!dest.exists()) {
				dest.mkdir();
			}

			String files[] = src.list();

			for (String file : files) {
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				copyFolder(srcFile, destFile);
			}

		} else {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.close();
		}
	}

	/**
	 * Supprimer un monde par son Fichier.
	 * 
	 * @param worldName
	 */
	public static boolean deleteWorld(File path) {
		if (path.exists()) {
			File files[] = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteWorld(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	/**
	 * Supprimer un monde par son Nom.
	 * 
	 * @param worldName
	 */
	public static void deleteWorld(String worldName) {
		World world = Bukkit.getWorld(worldName);
		File f = new File(worldName);
		if (f == null || world == null)
			return;
		Bukkit.unloadWorld(world, false);
		try {
			FileUtils.deleteDirectory(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Générer les Maps d'un Serveur de Jeux.
	 * 
	 * @param gameType
	 * @return
	 */
	public static Config loadGameServerMaps(MapInfos mapInfos) {

		// Gestion de la Carte et des Configurations
		Bukkit.unloadWorld("world", false);

		// Detection de la Validité de la Carte
		if ((mapInfos == null) || (mapInfos.equals(MapInfos.UNKNOW))) {
			System.err.println(EndariumAPI.getPrefixAPI() + "Erreur: Le serveur n'arrive pas à copier le fichier de config de la Map : "
					+ mapInfos.getName());
//			Bukkit.getServer().shutdown();
		}

		// Copy de la Map et Validation de la Configuration
		CrystaliserAPI.setMapInfos(mapInfos);
		FileManager fileManager = new FileManager(EndariumBukkit.getPlugin());
		Config mapConfig = fileManager.getConfig("maps/" + mapInfos.getName() + ".yml");
		mapConfig.copyDefaults(true).save();
		WorldManager.deleteWorld(new File("world"));
		File from = new File("maps/" + mapInfos.getName());
		File to = new File("world");
		try {
			WorldManager.copyFolder(from, to);
			return mapConfig;
		} catch (Exception e) {
			System.err.println(EndariumAPI.getPrefixAPI() + "Erreur: Le serveur n'arrive pas à copier la Map : "
					+ mapInfos.getName());
			Bukkit.getServer().shutdown();
			return null;
		}
	}
}