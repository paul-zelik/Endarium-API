package net.endarium.api.players.rank;

import net.endarium.api.players.wallets.Currency;
import net.md_5.bungee.api.ChatColor;

public enum Rank {

	ADMINISTRATOR(100, "§1", "ADMIN", "Admin", "Admin", ChatColor.RED, 45,4),

	MODERATOR(70, "§2", "MODERATOR", "Modo", "Modo", ChatColor.BLUE, 45,4),
	HELPER(60, "§3", "HELPER", "Helper", "Helper", ChatColor.DARK_AQUA, 45,4),
	STAFF(50, "§4", "STAFF", "Staff", "Staff", ChatColor.DARK_GREEN, 45,4),

	FRIEND(45, "§5", "FRIEND", "Friend", "Friend", ChatColor.LIGHT_PURPLE, 45,1),
	FAMOUS(40, "§6", "FAMOUS", "Famous", "Famous", ChatColor.LIGHT_PURPLE, 45,1),

	ULTRAVIP(30, "§7", "ULTRAVIP", "UltraVIP", "UltraVIP", ChatColor.AQUA, 45,1),
	VIPPLUS(20, "§8", "VIPPLUS", "VIP+", "VIP+", ChatColor.GREEN, 36,1),
	VIP(10, "§9", "VIP", "VIP", "VIP", ChatColor.YELLOW, 27,1),
	MINIVIP(5, "§a", "MINIVIP", "MiniVIP", "MiniVIP", ChatColor.WHITE, 18,0),
	DEFAULT(0, "§b", "DEFAULT", "Joueur", "", ChatColor.GRAY, 9, 0);

	private int power;

	private String orderCode, identificatorName, name, prefix;
	private ChatColor chatColor;
	private int maxFriends, maxSponsorship;

	/**
	 * Enumération de gestion des Ranks.
	 * 
	 * @param power
	 * @param orderCode
	 * @param identificatorName
	 * @param name
	 * @param prefix
	 * @param chatColor
	 * @param maxSponsorship
	 */
	private Rank(int power, String orderCode, String identificatorName, String name, String prefix, ChatColor chatColor,
			int maxFriends, int maxSponsorship) {
		this.power = power;
		this.orderCode = orderCode;
		this.identificatorName = identificatorName;
		this.maxSponsorship = maxSponsorship;
		this.name = name;
		this.prefix = prefix;
		this.chatColor = chatColor;
		this.maxFriends = maxFriends;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getIdentificatorName() {
		return identificatorName;
	}

	public int getMaxSponsorship() {
		return maxSponsorship;
	}

	public void setIdentificatorName(String identificatorName) {
		this.identificatorName = identificatorName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public ChatColor getChatColor() {
		return chatColor;
	}

	public void setChatColor(ChatColor chatColor) {
		this.chatColor = chatColor;
	}

	public int getMaxFriends() {
		return maxFriends;
	}

	public void setMaxFriends(int maxFriends) {
		this.maxFriends = maxFriends;
	}

	/**
	 * Récupérer le Booster d'un Joueur.
	 * 
	 * @param currency
	 */
	public double getBooster(Currency currency) {
		double value = 0.0d;
		if (currency.equals(Currency.TOKENS)) {
			// Booster de Tokens | Rank >= VIP+
			if (this.getPower() >= 20)
				value = 2.0d;
			// Booster de Tokens | Rank >= VIP
			else if (this.getPower() >= 10)
				value = 1.5d;
			// Booster de Tokens | Rank >= DEFAULT
			else
				value = 1.0d;
		} else {
			// Booster de Coins | Rank >= VIP+
			if (this.getPower() >= 20)
				value = 2.5d;
			// Booster de Coins | Rank >= VIP
			else if (this.getPower() >= 10)
				value = 2.0d;
			// Booster de Coins | Rank >= MiniVIP
			else if (this.getPower() >= 5)
				value = 1.5d;
			// Booster de Coins | Rank >= DEFAULT
			else
				value = 1.0d;
		}
		return value;
	}

	/**
	 * Récupérer un Rank par son Nom d'Identification.
	 * 
	 * @param rankIdName
	 * @return Rank
	 */
	public static Rank getUserRank(String rankIdName, boolean nullable) {
		for (Rank rank : Rank.values())
			if (rank.getIdentificatorName().equalsIgnoreCase(rankIdName))
				return rank;
		return nullable ? null : Rank.DEFAULT;
	}
}