package net.endarium.api.games.kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import net.endarium.crystaliser.servers.GameType;
import net.md_5.bungee.api.ChatColor;

public enum KitsInfos {

	HUNGERGAMES_TONITRUS(GameType.HUNGERGAMES, "Tonitrus", Material.GOLD_AXE, "hungergames.kits.tonitrus", 5000, true,
			new String[] { "En prenant ce kit tu te verras",
					"attribuer le pouvoir de " + ChatColor.GOLD + "Thor" + ChatColor.WHITE + "." },
			new String[] { "Hache de Thor" }),
	HUNGERGAMES_VIPERA(GameType.HUNGERGAMES, "Vipera", Material.FERMENTED_SPIDER_EYE, "hungergames.kits.vipera", 6000,
			true,
			new String[] { "Vous possédez " + ChatColor.RED + "15% de chance", "d'empoissoner votre ennemi en",
					"le frappant.", ChatColor.AQUA + "De plus, vous êtes immunisé",
					ChatColor.AQUA + "aux zones empoisonnées." },
			new String[] { "Epée en bois", "Potion de Poison I" }),
	HUNGERGAMES_SWITCHER(GameType.HUNGERGAMES, "Switcher", Material.SNOW_BALL, "hungergames.kits.switcher", 4000, true,
			new String[] { "Tu possèdes des boules magiques", "qui te permettent d'inverser ta",
					"position avec l'ennemi touché." },
			new String[] { "Epée en bois", "8 Boules Magiques" }),
	HUNGERGAMES_FIREMAN(GameType.HUNGERGAMES, "Fireman", Material.BLAZE_POWDER, "hungergames.kits.fireman", 4000, true,
			new String[] { "Obtenez le pouvoir de contrôler", "le feu autour de vous.",
					ChatColor.RED + "Cependant, méfiez-vous de l'eau..." },
			new String[] { "Seau de lave", "3 Boules de feu" }),
	HUNGERGAMES_VAMPIRE(GameType.HUNGERGAMES, "Vampire", Material.REDSTONE, "hungergames.kits.vampire", 5000, true,
			new String[] { "Récupérez de la vie à chaque", "fois que vous tuez un ennemi.",
					ChatColor.AQUA + "De plus, votre vitesse s'améliore." },
			new String[] { "Epée en pierre" }),
	HUNGERGAMES_KANGAROO(GameType.HUNGERGAMES, "Kangaroo", Material.FIREWORK, "hungergames.kits.kangaroo", 6000, true,
			new String[] { "Battez-vous en vous mettant", "dans la peau d'un Kangourou." },
			new String[] { "Epée en bois", "Fusée Magique" });

	private GameType gameType;
	private String name;
	private Material iconItem;
	private String permission;
	private int price;
	private boolean free;
	private String[] description, items;

	/***
	 * Liste des Kits des différents Jeux avec Items.
	 * 
	 * @param gameType
	 * @param name
	 * @param iconItem
	 * @param permission
	 * @param price
	 * @param free
	 * @param description
	 * @param items
	 */
	private KitsInfos(GameType gameType, String name, Material iconItem, String permission, int price, boolean free,
			String[] description, String[] items) {
		this.gameType = gameType;
		this.name = name;
		this.iconItem = iconItem;
		this.permission = permission;
		this.price = price;
		this.free = free;
		this.description = description;
		this.items = items;
	}

	/***
	 * Liste des Kits des différents Jeux.
	 * 
	 * @param gameType
	 * @param name
	 * @param iconItem
	 * @param permission
	 * @param price
	 * @param description
	 */
	private KitsInfos(GameType gameType, String name, Material iconItem, String permission, int price,
			String[] description) {
		this.gameType = gameType;
		this.name = name;
		this.iconItem = iconItem;
		this.permission = permission;
		this.price = price;
		this.description = description;
		this.items = new String[] {};
	}

	public GameType getGameType() {
		return gameType;
	}

	public String getName() {
		return name;
	}

	public Material getIconItem() {
		return iconItem;
	}

	public String getPermission() {
		return permission;
	}

	public int getPrice() {
		return price;
	}

	public boolean isFree() {
		return free;
	}

	public String[] getDescription() {
		return description;
	}

	public String[] getItems() {
		return items;
	}

	/**
	 * Convertire le tableau des Descriptions en une Liste.
	 */
	public List<String> getDescriptionsList() {
		List<String> descriptionList = new ArrayList<>();
		for (String lores : this.getDescription())
			descriptionList.add(lores);
		return descriptionList;
	}

	/**
	 * Convertire le tableau des Items en une Liste.
	 */
	public List<String> getItemsList() {
		List<String> itemsList = new ArrayList<>();
		for (String items : this.getItems())
			itemsList.add(items);
		return itemsList;
	}

	/**
	 * Rétourner un Kit par son Material.
	 * 
	 * @param name
	 * @param gameType
	 */
	public static KitsInfos getKitByMaterial(Material material, GameType gameType) {
		for (KitsInfos kitsInfos : KitsInfos.values())
			if ((kitsInfos.getIconItem().equals(material)) && (kitsInfos.getGameType().equals(gameType)))
				return kitsInfos;
		return null;
	}

	/**
	 * Retourner un Kit par son Nom.
	 * 
	 * @param name
	 * @param gameType
	 */
	public static KitsInfos getKitByName(String name, GameType gameType) {
		for (KitsInfos kitsInfos : KitsInfos.values())
			if ((kitsInfos.getName().equalsIgnoreCase(name)) && (kitsInfos.getGameType().equals(gameType)))
				return kitsInfos;
		return null;
	}
}