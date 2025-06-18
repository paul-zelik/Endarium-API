package net.endarium.api.players.wallets;

import net.md_5.bungee.api.ChatColor;

public enum Currency {

	COINS("Coins", ChatColor.YELLOW, "coins", "⛃"), TOKENS("Tokens", ChatColor.AQUA, "tokens", "⛂");

	private String name, data, icon;
	private ChatColor color;

	private Currency(String name, ChatColor color, String data, String icon) {
		this.name = name;
		this.color = color;
		this.data = data;
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public ChatColor getColor() {
		return color;
	}

	public String getData() {
		return data;
	}

	public String getIcon() {
		return icon;
	}

	/**
	 * Récupérer un type de monnaie par son nom.
	 * 
	 * @param currencyName
	 * @return
	 */
	public static Currency getCurrencyByName(String currencyName) {
		for (Currency currency : Currency.values())
			if (currency.getName().equalsIgnoreCase(currencyName))
				return currency;
		return Currency.COINS;
	}
}