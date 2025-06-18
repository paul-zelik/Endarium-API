package net.endarium.api.players.settings;

public enum SettingType {

    AFK("AFK", SettingStatus.FALSE, false),
    STAFFCHAT("StaffChat", SettingStatus.TRUE, false),
    MODE_MODERATION("Mode Modération", SettingStatus.FALSE, false),

    MESSAGES_PRIVATES("Messages Privés", SettingStatus.TRUE, true),
    MESSAGES_PRIVATES_SOUND("Notifications des Messages Privés", SettingStatus.TRUE, false),

    FRIENDS_REQUEST("Reqûetes d'Amis", SettingStatus.TRUE, false),
    FRIENDS_SOUND("Notifications/Sons des Amis", SettingStatus.TRUE, false),
    FRIENDS_LOGIN_NOTIFICATIONS("Notifications de connexion des Amis", SettingStatus.TRUE, false),

    PARTY_REQUEST("Requêtes de Groupes", SettingStatus.TRUE, true),
    PARTY_SOUND("Notifications/Sons des Groupes", SettingStatus.TRUE, false),
    PARTY_FOLLOW("Suivre le Chef du Groupe", SettingStatus.TRUE, false),

    CHAT_MENTIONS("Mentions Tchat", SettingStatus.TRUE, true),
    CHAT_HUB_VISIBLE("Visibilité du Tchat", SettingStatus.TRUE, false),

    GAME_EFFECT("Effets de Jeux", SettingStatus.TRUE, false),
    GAME_AUTOREPLAY("AutoReplay", SettingStatus.FALSE, false),
    GAME_WAITING_REQUESTGAME("File d'attente Automatique", SettingStatus.FALSE, false),

    HUB_SHOWPLAYERS("Visibilité des Joueurs", SettingStatus.TRUE, true),
    COMMAND_LOBBY_PROTECTION("Protection du /hub", SettingStatus.FALSE, false),

    HOST_INVITATIONS("Invitations des Hosts", SettingStatus.TRUE, true),
    HOST_NOTIFICATIONS("Notifications des Hosts", SettingStatus.TRUE, false),

    ANTICHEAT_NOTIFICATIONS("Notifcations de l'AntiCheat", SettingStatus.TRUE, false);

    private String name;
    private SettingStatus defaultValue;
    private boolean acceptFriends;

    /**
     * Récupérer les différents Types de paramètres.
     *
     * @param name
     * @param defaultValue
     * @param acceptFriends
     */
    private SettingType(String name, SettingStatus defaultValue, boolean acceptFriends) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.acceptFriends = acceptFriends;
    }

    public String getName() {
        return name;
    }

    public SettingStatus getDefaultValue() {
        return defaultValue;
    }

    public boolean isAcceptFriends() {
        return acceptFriends;
    }

    /**
     * Récupérer un Settings par son Nom.
     */
    public static SettingType getSettingByName(String settingName) {
        for (SettingType settingType : SettingType.values())
            if (settingType.getName().equalsIgnoreCase(settingName))
                return settingType;
        return null;
    }
}