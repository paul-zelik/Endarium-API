package net.endarium.api.utils;

import net.endarium.api.games.GameSetting;

public class EndariumAPI {

	private static GameSetting gameSetting;

	/**
	 * Récupérer le préfix de l'API.
	 */
	public static String getPrefixAPI() {
		return "[EndariumAPI] ";
	}

	/**
	 * Récupérer les paramètres d'un serveur de Jeu.
	 */
	public static GameSetting getGameSetting() {
		if (gameSetting == null)
			gameSetting = new GameSetting("Unknow", "world", -1, 1, false, false, false, false);
		return gameSetting;
	}

	/**
	 * Définir un nouveau GameSetting.
	 * 
	 * @param gameSetting
	 */
	public static void setGameSetting(GameSetting gameSetting) {
		EndariumAPI.gameSetting = gameSetting;
	}
}