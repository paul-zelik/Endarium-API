package net.endarium.api.players.settings;

import net.endarium.api.players.language.Languages;

import java.util.Map;
import java.util.UUID;

public class SettingAccount {

    private UUID uuid;
    private Map<SettingType, SettingStatus> settingMap;
    private Languages languages;

    /**
     * Récupérer le compte d'un Joueur.
     *
     * @param uuid
     * @param settingMap
     */
    public SettingAccount(UUID uuid, Map<SettingType, SettingStatus> settingMap, Languages languages) {
        this.uuid = uuid;
        this.settingMap = settingMap;
        this.languages = languages;
    }

    /**
     * Vérifier un paramètre.
     *
     * @param settingType
     */
    public boolean isSetting(SettingType settingType) {
        if (settingMap.get(settingType) == null)
            settingMap.put(settingType, settingType.getDefaultValue());
        return settingMap.get(settingType).isEnable();
    }

    /**
     * Définir un nouveau paramètre.
     *
     * @param settingType
     * @param settingStatus
     */
    public SettingAccount setSetting(SettingType settingType, SettingStatus settingStatus) {
        if (settingMap.get(settingType) != null)
            settingMap.remove(settingType);
        settingMap.put(settingType, settingStatus);
        return this;
    }

    /**
     * Récupérer le Setting Status d'un Setting.
     *
     * @param settingType
     */
    public SettingStatus getSettingStatus(SettingType settingType) {
        if (settingMap.get(settingType) == null)
            return settingType.getDefaultValue();
        return settingMap.get(settingType);
    }

    /**
     * Définir la Langue d'un Utilisateur.
     *
     * @param languages
     */
    public SettingAccount setLanguages(Languages languages) {
        this.languages = languages;
        return this;
    }

    /**
     * Récupérer la Langue d'un Joueur.
     */
    public Languages getLanguages() {
        if (languages == null)
            return Languages.FRENCH;
        return languages;
    }

    public UUID getUUID() {
        return uuid;
    }

    public Map<SettingType, SettingStatus> getSettingMap() {
        return settingMap;
    }
}