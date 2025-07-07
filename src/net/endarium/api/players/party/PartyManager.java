package net.endarium.api.players.party;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

public class PartyManager {
    private List<Party> parties;

    public PartyManager() {
        this.parties = new ArrayList<>();
    }

    // Ajoute une nouvelle partie à la liste des parties
    public void addParty(Party party) {
        if (!parties.contains(party)) {
            parties.add(party);
        }
    }

    // Supprime une partie de la liste des parties
    public void removeParty(Party party) {
        parties.remove(party);
    }

    // Trouve et retourne la partie à laquelle un joueur appartient
    public Party findPartyByPlayer(ProxiedPlayer player) {
        for (Party party : parties) {
            if (party.getPlayers().contains(player)) {
                return party;
            }
        }
        return null; // Retourne null si le joueur n'est dans aucune partie
    }
}
