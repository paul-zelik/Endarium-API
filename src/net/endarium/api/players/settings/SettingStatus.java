package net.endarium.api.players.settings;

import net.md_5.bungee.api.ChatColor;

public enum SettingStatus {

    TRUE("Activé", true, ChatColor.GREEN),
    FALSE("Désactivé", false, ChatColor.RED),
    FRIENDS("Amis Uniquement", true, ChatColor.LIGHT_PURPLE);

    private String name;
    private boolean enable;
    private ChatColor chatColor;

    /**
     * Status des Paramètres du Joueur.
     *
     * @param name
     * @param enable
     * @param chatColor
     */
    private SettingStatus(String name, boolean enable, ChatColor chatColor) {
        this.name = name;
        this.enable = enable;
        this.chatColor = chatColor;
    }

    public String getName() {
        return name;
    }

    public boolean isEnable() {
        return enable;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }
}