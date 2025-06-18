package net.endarium.api.games.teams;

import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;

import net.endarium.api.utils.builders.items.heads.CustomSkull;
import net.endarium.api.utils.builders.items.heads.TextureHeadLink;
import net.md_5.bungee.api.ChatColor;

public enum Teams {

	RED("Rouge", ChatColor.RED + "[R]", ChatColor.RED, "§c", (short) 14, Color.RED,
			CustomSkull.getCustomSkull(TextureHeadLink.WOOL_RED.getLink())),
	BLUE("Bleu", ChatColor.BLUE + "[B]", ChatColor.BLUE, "§9", (short) 9, Color.BLUE,
			CustomSkull.getCustomSkull(TextureHeadLink.WOOL_BLUE.getLink())),
	GREEN("Vert", ChatColor.GREEN + "[V]", ChatColor.GREEN, "§a", (short) 5, Color.LIME,
			CustomSkull.getCustomSkull(TextureHeadLink.WOOL_GREEN.getLink())),
	YELLOW("Jaune", ChatColor.YELLOW + "[J]", ChatColor.YELLOW, "§e", (short) 4, Color.YELLOW,
			CustomSkull.getCustomSkull(TextureHeadLink.WOOL_YELLOW.getLink())),
	AQUA("Aqua", ChatColor.AQUA + "[A]", ChatColor.AQUA, "§b", (short) 3, Color.AQUA,
			CustomSkull.getCustomSkull(TextureHeadLink.WOOL_AQUA.getLink())),
	PINK("Rose", ChatColor.LIGHT_PURPLE + "[P]", ChatColor.LIGHT_PURPLE, "§d", (short) 2, Color.FUCHSIA,
			CustomSkull.getCustomSkull(TextureHeadLink.WOOL_PINK.getLink())),
	ORANGE("Orange", ChatColor.GOLD + "[O]", ChatColor.GOLD, "§6", (short) 1, Color.ORANGE,
			CustomSkull.getCustomSkull(TextureHeadLink.WOOL_ORANGE.getLink())),
	WHITE("Blanche", ChatColor.WHITE + "[B]", ChatColor.WHITE, "§f", (short) 0, Color.WHITE,
			CustomSkull.getCustomSkull(TextureHeadLink.WOOL_WHITE.getLink())),

	RED_HEART("❤ Rouge", ChatColor.RED + "[❤R]", ChatColor.RED, "§c", (short) 14, Color.RED,
			CustomSkull.getCustomSkull(TextureHeadLink.WOOL_RED_HEART.getLink())),
	BLUE_HEART("❤ Bleu", ChatColor.BLUE + "[❤B]", ChatColor.BLUE, "§9", (short) 9, Color.BLUE,
			CustomSkull.getCustomSkull(TextureHeadLink.WOOL_BLUE_HEART.getLink())),
	GREEN_HEART("❤ Vert", ChatColor.GREEN + "[❤V]", ChatColor.GREEN, "§a", (short) 5, Color.LIME,
			CustomSkull.getCustomSkull(TextureHeadLink.WOOL_GREEN_HEART.getLink())),
	YELLOW_HEART("❤ Jaune", ChatColor.YELLOW + "[❤J]", ChatColor.YELLOW, "§e", (short) 4, Color.YELLOW,
			CustomSkull.getCustomSkull(TextureHeadLink.WOOL_YELLOW_HEART.getLink())),
	AQUA_HEART("❤ Aqua", ChatColor.AQUA + "[❤A]", ChatColor.AQUA, "§b", (short) 3, Color.AQUA,
			CustomSkull.getCustomSkull(TextureHeadLink.WOOL_AQUA_HEART.getLink())),
	PINK_HEART("❤ Rose", ChatColor.LIGHT_PURPLE + "[❤P]", ChatColor.LIGHT_PURPLE, "§d", (short) 2, Color.FUCHSIA,
			CustomSkull.getCustomSkull(TextureHeadLink.WOOL_PINK_HEART.getLink())),
	ORANGE_HEART("❤ Orange", ChatColor.GOLD + "[❤O]", ChatColor.GOLD, "§6", (short) 1, Color.ORANGE,
			CustomSkull.getCustomSkull(TextureHeadLink.WOOL_ORANGE_HEART.getLink())),
	WHITE_HEART("❤ Blanche", ChatColor.WHITE + "[❤B]", ChatColor.WHITE, "§f", (short) 0, Color.WHITE,
			CustomSkull.getCustomSkull(TextureHeadLink.WOOL_WHITE_HEART.getLink()));

	private String name;
	private String prefix;
	private ChatColor chatColor;
	private String codeColor;
	private short data;
	private Color color;
	private ItemStack iconItem;

	/**
	 * Enumération du listing des Teams.
	 * 
	 * @param name
	 * @param prefix
	 * @param chatColor
	 * @param codeColor
	 * @param data
	 * @param color
	 * @param iconItem
	 */
	Teams(String name, String prefix, ChatColor chatColor, String codeColor, short data, Color color,
			ItemStack iconItem) {
		this.name = name;
		this.prefix = prefix;
		this.chatColor = chatColor;
		this.codeColor = codeColor;
		this.data = data;
		this.color = color;
		this.iconItem = iconItem;
	}

	public String getName() {
		return name;
	}

	public String getPrefix() {
		return prefix;
	}

	public ChatColor getChatColor() {
		return chatColor;
	}

	public String getCodeColor() {
		return codeColor;
	}

	public short getData() {
		return data;
	}

	public Color getColor() {
		return color;
	}

	public ItemStack getIconItem() {
		return iconItem;
	}

	/**
	 * Récupérer une Team par son Nom.
	 * 
	 * @param teamName
	 */
	public static Teams getTeam(String teamName) {
		for (Teams teams : Teams.values()) {
			if (teams.getName().equalsIgnoreCase(teamName)) {
				return teams;
			}
		}
		return null;
	}
}