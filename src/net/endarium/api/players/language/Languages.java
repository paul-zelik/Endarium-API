package net.endarium.api.players.language;

public enum Languages {

	FRENCH("Français", "FR_fr"), ENGLISH("English", "EN_en"), SPANISH("Spanish", "ES_es");

	private String name, zoneLangue;

	/**
	 * Système de Langue du serveur/network.
	 * 
	 * @param name
	 * @param zoneLangue
	 */
	private Languages(String name, String zoneLangue) {
		this.name = name;
		this.zoneLangue = zoneLangue;
	}

	public String getName() {
		return name;
	}

	public String getZoneLangue() {
		return zoneLangue;
	}

	/**
	 * Récupérer une Langue par sa Zone.
	 * 
	 * @param zone
	 * @return
	 */
	public static Languages getLangueByZone(String zone) {
		for (Languages lang : Languages.values())
			if (lang.getZoneLangue().equalsIgnoreCase(zone))
				return lang;
		return Languages.FRENCH;
	}
}