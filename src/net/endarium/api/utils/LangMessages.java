package net.endarium.api.utils;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.language.Languages;

public class LangMessages {

	/**
	 * Envoyer un Broadcast dans le Chat en fonction de la Langue.
	 * 
	 * @param frenchMessage
	 * @param englishMessage
	 * @param spanishMessage
	 */
	public static void broadcastMessage(String frenchMessage, String englishMessage, String spanishMessage) {
		Bukkit.getOnlinePlayers().forEach(players -> {
			EndaPlayer endaPlayer = EndaPlayer.get(players.getUniqueId());
			switch (endaPlayer.getLanguages()) {
			case FRENCH:
				players.sendMessage(frenchMessage);
				break;
			case ENGLISH:
				if ((englishMessage == null) || (englishMessage.equalsIgnoreCase("")))
					players.sendMessage(frenchMessage);
				else
					players.sendMessage(englishMessage);
				break;
			case SPANISH:
				if ((spanishMessage == null) || (spanishMessage.equalsIgnoreCase("")))
					players.sendMessage(frenchMessage);
				else
					players.sendMessage(spanishMessage);
				break;
			default:
				players.sendMessage(frenchMessage);
				break;
			}
		});
	}

	/**
	 * Envoyer un Message à un Joueur en fonction de la Langue.
	 * 
	 * @param player
	 * @param frenchMessage
	 * @param englishMessage
	 * @param spanishMessage
	 */
	public static void sendPlayerMessage(Player player, String frenchMessage, String englishMessage,
			String spanishMessage) {
		EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());
		switch (endaPlayer.getLanguages()) {
		case FRENCH:
			player.sendMessage(frenchMessage);
			break;
		case ENGLISH:
			if ((englishMessage == null) || (englishMessage.equalsIgnoreCase("")))
				player.sendMessage(frenchMessage);
			else
				player.sendMessage(englishMessage);
			break;
		case SPANISH:
			if ((spanishMessage == null) || (spanishMessage.equalsIgnoreCase("")))
				player.sendMessage(frenchMessage);
			else
				player.sendMessage(spanishMessage);
			break;
		default:
			player.sendMessage(frenchMessage);
			break;
		}
	}

	/**
	 * Récupérer une chaîne de caractères avec une Langue.
	 * 
	 * @param player
	 * @param frenchMessage
	 * @param englishMessage
	 * @param spanishMessage
	 * @return
	 */
	public static String getPlayerMessage(Player player, String frenchMessage, String englishMessage,
			String spanishMessage) {
		EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());
		switch (endaPlayer.getLanguages()) {
		case FRENCH:
			return frenchMessage;
		case ENGLISH:
			if ((englishMessage == null) || (englishMessage.equalsIgnoreCase("")))
				return frenchMessage;
			else
				return englishMessage;
		case SPANISH:
			if ((spanishMessage == null) || (spanishMessage.equalsIgnoreCase("")))
				return frenchMessage;
			else
				return spanishMessage;
		default:
			return frenchMessage;
		}
	}

	/**
	 * Broadcast un Message qui possède une Langue.
	 * 
	 * @param messagesMap
	 */
	public static void broadcastMessage(HashMap<String, String> messagesMap) {
		Bukkit.getOnlinePlayers().forEach(players -> {
			EndaPlayer endaPlayer = EndaPlayer.get(players.getUniqueId());
			String message = "";
			if (messagesMap.containsKey(endaPlayer.getLanguages().getZoneLangue()))
				message = messagesMap.get(endaPlayer.getLanguages().getZoneLangue());
			else
				message = messagesMap.get(Languages.FRENCH.getZoneLangue());
			players.sendMessage(message);
		});
	}

	/**
	 * Envoyer un Message à un Joueur qui possède une Langue.
	 * 
	 * @param player
	 * @param messagesMap
	 */
	public static void sendPlayerMessage(Player player, HashMap<String, String> messagesMap) {
		EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());
		String message = "";
		if (messagesMap.containsKey(endaPlayer.getLanguages().getZoneLangue()))
			message = messagesMap.get(endaPlayer.getLanguages().getZoneLangue());
		else
			message = messagesMap.get(Languages.FRENCH.getZoneLangue());
		player.sendMessage(message);
	}

	/**
	 * Récupérer la chaîne de caractères qui correspond à la Langue.
	 * 
	 * @param player
	 * @param messagesMap
	 */
	public static String getPlayerMessage(Player player, HashMap<String, String> messagesMap) {
		EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());
		String message = "";
		if (messagesMap.containsKey(endaPlayer.getLanguages().getZoneLangue()))
			message = messagesMap.get(endaPlayer.getLanguages().getZoneLangue());
		else
			message = messagesMap.get(Languages.FRENCH.getZoneLangue());
		return message;
	}
}