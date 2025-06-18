package net.endarium.api.utils;

import net.endarium.api.players.rank.Rank;
import net.md_5.bungee.api.ChatColor;

public class Messages {

	public static String ENDARIUM_PREFIX = ChatColor.GOLD + "" + ChatColor.BOLD + "Endarium" + ChatColor.WHITE + ""
			+ ChatColor.BOLD + " » ";

	public static String HOST_PREFIX = ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Host" + ChatColor.WHITE + ""
			+ ChatColor.BOLD + " » ";

	public static String PARTY_PREFIX = ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Party" + ChatColor.WHITE + ""
			+ ChatColor.BOLD + " » ";
	public static String FRIENDS_PREFIX = ChatColor.GOLD + "[Amis] ";

	public static String REPORT_PREFIX = ChatColor.GOLD + "[Report] ";
	public static String ZMODS_PREFIX = ChatColor.GOLD + "[ZMod] ";
	public static String NO_PERMISSION = ChatColor.RED
			+ "Désolé, vous n'avez pas accès à cette commande. N'hésitez pas à contacter un Staff en cas de problème.";
	public static String UNKNOW_COMMAND = ChatColor.WHITE + "Commande inconnue.";

	/**
	 * Récupérer la couleur des messages du (t)chat en fonction du Rank.
	 */
	public static ChatColor getRankChatMessageColor(Rank rank) {
		return rank.equals(Rank.DEFAULT) ? ChatColor.GRAY : ChatColor.WHITE;
	}

	/**
	 * Récupérer l'espacement d'un Rank.
	 */
	public static String getRankSpaceConvention(Rank rank) {
		return rank.equals(Rank.DEFAULT) ? "" : " ";
	}

	/**
	 * Récupérer une chaîne de caractère centré.
	 */
	public static String centerText(String text) {
		int space = (int) Math.round((80.0D - 1.4D * text.length()) / 2.0D);
		return repeat(" ", space) + text;
	}

	private static String repeat(String text, int times) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < times; i++) {
			stringBuilder.append(text);
		}
		return stringBuilder.toString();
	}
}