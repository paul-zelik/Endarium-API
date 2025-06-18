package net.endarium.api.games;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.endarium.api.utils.builders.titles.ActionBarBuilder;
import net.endarium.api.utils.tools.SoundUtils;
import net.md_5.bungee.api.ChatColor;

/**
 * Gestion des Messages de Parties.
 */
public class GameMessages {

	public static String ENOUGHT_PLAYERS = ChatColor.RED + "Il n'y a pas assez de joueurs pour démarrer la partie.";

	/**
	 * Envoyer un message d'information aux Joueurs.
	 */
	public static void sendHelpMessage(String message) {
		Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "?"
				+ ChatColor.WHITE + "] " + message);
	}

	/**
	 * Envoyer un message d'alerte aux Joueurs.
	 */
	public static void sendWarningMessage(String message) {
		Bukkit.broadcastMessage(
				ChatColor.WHITE + "[" + ChatColor.RED + "" + ChatColor.BOLD + "!" + ChatColor.WHITE + "] " + message);
	}

	/**
	 * Récupérer l'orthographe du Timer pour les Secondes.
	 */
	public static String getSeconds(Integer timer) {
		return timer == 1 ? " seconde" : " secondes";
	}

	/**
	 * Récupérer l'orthographe du Timer pour les Minutes.
	 */
	public static String getMinutes(Integer timer) {
		return timer == 1 ? " minute" : " minutes";
	}

	/**
	 * Alerter un Joueur d'un cassage de blocs.
	 */
	public static void warnBreakBlock(Player player) {
		new ActionBarBuilder(ChatColor.DARK_RED + "" + ChatColor.BOLD + "ATTENTION" + ChatColor.GRAY + "│ "
				+ ChatColor.RED + "Vous ne pouvez détruire ces blocs-là...").sendTo(player);
		SoundUtils.sendSound(player, Sound.VILLAGER_NO);
	}
}