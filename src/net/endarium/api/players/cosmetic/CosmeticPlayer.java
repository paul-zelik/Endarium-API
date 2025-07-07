package net.endarium.api.players.cosmetic;

import net.endarium.api.EndariumCommons;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CosmeticPlayer {

    private CosmeticPlayer cosmeticPlayer;
    private static Map<UUID, CosmeticPlayer> cosmeticPlayerMap = new HashMap<UUID, CosmeticPlayer>();

    private ListCosmetic chapeau;
    private ListCosmetic manteau;
    private ListCosmetic pantalon;
    private ListCosmetic botte;
    private ListCosmetic particule;


    private UUID uuid;

    private List<ListCosmetic> listCosmetics;

    public CosmeticPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    private void loadEquippedCosmetics() {
        String query = "SELECT type, cosmetic_name FROM player_equipped_cosmetics WHERE player_uuid = ?";

        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, this.uuid.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String type = rs.getString("type");
                    String cosmeticName = rs.getString("cosmetic_name");

                    // Retrieve the ListCosmetic enum based on the cosmetic name
                    ListCosmetic cosmetic = ListCosmetic.valueOf(cosmeticName);

                    // Set the equipped cosmetic based on its type
                    switch (type) {
                        case "CHAPEAU":
                            this.setChapeau(cosmetic);
                            break;
                        case "MANTEAU":
                            this.setManteau(cosmetic);
                            break;
                        case "PANTALON":
                            this.setPantalon(cosmetic);
                            break;
                        case "CHAUSSURE":
                            this.setBotte(cosmetic);
                            break;
                        case "PARTICULE":
                            this.setParticule(cosmetic);
                            break;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadOwnedCosmetics() {
        String query = "SELECT cosmetic_name FROM player_owned_cosmetics WHERE player_uuid = ?";

        try (Connection connection = EndariumCommons.getInstance().getHikariDataSource().getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, this.uuid.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String cosmeticName = rs.getString("cosmetic_name");

                    // Retrieve the ListCosmetic enum based on the cosmetic name
                    ListCosmetic cosmetic = ListCosmetic.valueOf(cosmeticName);
                    this.listCosmetics.add(cosmetic);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ListCosmetic> getListCosmetics() {
        return listCosmetics;
    }

    public UUID getUuid() {
        return uuid;
    }

    public ListCosmetic getParticule() {
        return particule;
    }

    public void setParticule(ListCosmetic particule) {
        this.particule = particule;
    }

    public ListCosmetic getBotte() {
        return botte;
    }

    public ListCosmetic getChapeau() {
        return chapeau;
    }

    public ListCosmetic getManteau() {
        return manteau;
    }

    public ListCosmetic getPantalon() {
        return pantalon;
    }

    public void setChapeau(ListCosmetic chapeau) {
        this.chapeau = chapeau;
    }

    public void setBotte(ListCosmetic botte) {
        this.botte = botte;
    }

    public void setManteau(ListCosmetic manteau) {
        this.manteau = manteau;
    }

    public void setPantalon(ListCosmetic pantalon) {
        this.pantalon = pantalon;
    }

    public void addCosmetic(ListCosmetic listCosmetic) {
        listCosmetics.add(listCosmetic);
    }

    // Déconnecter le Cache du Joueur.
    public void logoutCosmeticPlayer() {
        // Déconnecter un Joueur
        if (cosmeticPlayerMap.get(uuid) != null)
            cosmeticPlayerMap.remove(uuid);
    }

    /**
     * Récupérer un EndaPlayer/Compte Endarium.
     *
     * @param uuid
     * @return
     */
    public static CosmeticPlayer get(UUID uuid) {
        if (cosmeticPlayerMap.get(uuid) == null)
            cosmeticPlayerMap.put(uuid, new CosmeticPlayer(uuid));
        return cosmeticPlayerMap.get(uuid);
    }
}
