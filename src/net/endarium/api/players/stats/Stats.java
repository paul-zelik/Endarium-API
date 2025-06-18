package net.endarium.api.players.stats;

import org.bukkit.Material;

public enum Stats {

    COINSWIN("Coins gagnés", "coinswin", 0.0f, Material.GOLD_INGOT),
    GAMEPLAYED("Parties jouées", "gameplayed", 0, Material.COMPASS),
    WINS("Victoires", "wins", 0, Material.FIREWORK),
    FISH("Poissons pêchés", "fish", 0, Material.FISHING_ROD),
    KILLS("Ennemis tues", "kills", 0, Material.IRON_SWORD),
    DEATHS("Morts", "deaths", 0, Material.BONE),
    LAST_KIT("Dernier Kit", "lastkit", null, Material.NAME_TAG),
    CHESTS_OPENED("Coffres ouverts", "chestsopened", 0, Material.CHEST),
    ZONE_PLAYED("Zons affectées", "zoneplayed", 0, Material.MAP),
    CRYSTAL_KILLS("Crystal tués", "crystalkill", 0, Material.IRON_INGOT),
    PLAYERS_PUNCH("Joueurs expulsés", "playerspunch", 0, Material.FEATHER),
    BOW_KILLS("Expulsés à l'Arc", "bowkills", 0, Material.BOW);

    private String name, column;
    private Object defaultValue;
    private Material itemIcon;

    /**
     * Gestion des Statistiques.
     *
     * @param name
     * @param column
     * @param defaultValue
     * @param itemIcon
     */
    private Stats(String name, String column, Object defaultValue, Material itemIcon) {
        this.name = name;
        this.column = column;
        this.defaultValue = defaultValue;
        this.itemIcon = itemIcon;
    }

    public String getName() {
        return name;
    }

    public String getColumn() {
        return column;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public Material getItemIcon() {
        return itemIcon;
    }

    /**
     * Récupérer une Statistiques par son nom de Column.
     *
     * @param column
     * @return
     */
    public static Stats getStatByColumn(String column) {
        for (Stats stats : Stats.values())
            if (stats.getColumn().equalsIgnoreCase(column))
                return stats;
        return null;
    }
}