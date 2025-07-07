package net.endarium.api.bungeecord.commands.player;

import net.endarium.api.bungeecord.EndariumBungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Command;
import net.endarium.api.players.party.Party;
import net.endarium.api.players.party.PartyManager;

import static org.bukkit.Bukkit.getPlayer;

public class PartyCommand extends Command {

    private String PREFIX = ChatColor.GOLD + "[Party] ";
    private PartyManager partyManager;

    public PartyCommand() {
        super("party", null, new String[] { "p" });
        this.partyManager = new PartyManager();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Seul un joueur peut exécuter cette commande.");
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length == 0) {
            sendHelp(player);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                handleCreate(player);
                break;
            case "leave":
                handleLeave(player);
                break;
            case "destroy":
                handleDestroy(player);
                break;
            case "add":
                if (args.length > 1) {
                    handleAdd(player, args[1]);
                } else {
                    player.sendMessage(PREFIX + ChatColor.RED + "Usage: /p add <nomd'unjoueur>");
                }
                break;
            case "join":
                handleJoin(player);
                break;
            case "chat":
                if (args.length > 1) {
                    String message = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
                    handleChat(player, message);
                } else {
                    player.sendMessage(PREFIX + ChatColor.RED + "Usage: /p chat <message>");
                }
                break;
            default:
                sendHelp(player);
        }
    }

    private void handleCreate(ProxiedPlayer player) {
        Party playerParty = partyManager.findPartyByPlayer(player);
        if (playerParty == null) {
            Party newParty = new Party(player);
            partyManager.addParty(newParty);
            player.sendMessage(PREFIX + ChatColor.GREEN + "Votre partie a été créée avec succès.");
        } else {
            player.sendMessage(PREFIX + ChatColor.RED + "Vous êtes déjà dans une partie.");
        }
    }

    private void handleLeave(ProxiedPlayer player) {
        Party playerParty = partyManager.findPartyByPlayer(player);
        if (playerParty != null) {
            if (!playerParty.getPlayerOwner().equals(player)) {
                playerParty.removePlayer(player);
                player.sendMessage(PREFIX + ChatColor.GREEN + "Vous avez quitté la partie.");
            } else {
                player.sendMessage(PREFIX + ChatColor.RED + "Vous êtes le propriétaire de la partie. Utilisez /p destroy pour la supprimer.");
            }
        } else {
            player.sendMessage(PREFIX + ChatColor.RED + "Vous n'êtes pas dans une partie.");
        }
    }

    private void handleDestroy(ProxiedPlayer player) {
        Party playerParty = partyManager.findPartyByPlayer(player);
        if (playerParty != null && playerParty.getPlayerOwner().equals(player)) {
            partyManager.removeParty(playerParty);
            player.sendMessage(PREFIX + ChatColor.GREEN + "Votre partie a été détruite avec succès.");
        } else {
            player.sendMessage(PREFIX + ChatColor.RED + "Vous devez être le propriétaire de la partie pour la détruire.");
        }
    }

    private void handleAdd(ProxiedPlayer player, String targetPlayerName) {
        ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(targetPlayerName);
        if (targetPlayer != null) {
            Party playerParty = partyManager.findPartyByPlayer(player);
            if (playerParty != null && playerParty.getPlayerOwner().equals(player)) {
                playerParty.invitePlayer(targetPlayer);
                player.sendMessage(PREFIX + ChatColor.GREEN + "Invitation envoyée à " + targetPlayerName);
                targetPlayer.sendMessage(PREFIX + ChatColor.GREEN + "Vous avez été invité à rejoindre une partie par " + player.getName());
            } else {
                player.sendMessage(PREFIX + ChatColor.RED + "Vous devez être le propriétaire d'une partie pour inviter des joueurs.");
            }
        } else {
            player.sendMessage(PREFIX + ChatColor.RED + "Joueur introuvable.");
        }
    }

    private void handleJoin(ProxiedPlayer player) {
        Party playerParty = partyManager.findPartyByPlayer(player);
        if (playerParty != null && playerParty.hasInvitation(player)) {
            if (playerParty.acceptInvitation(player)) {
                player.sendMessage(PREFIX + ChatColor.GREEN + "Vous avez rejoint la partie.");
            } else {
                player.sendMessage(PREFIX + ChatColor.RED + "Impossible de rejoindre la partie.");
            }
        } else {
            player.sendMessage(PREFIX + ChatColor.RED + "Vous n'avez pas d'invitation en attente.");
        }
    }

    private void handleChat(ProxiedPlayer player, String message) {
        Party playerParty = partyManager.findPartyByPlayer(player);
        if (playerParty != null) {
            for (ProxiedPlayer member : playerParty.getPlayers()) {
                member.sendMessage(PREFIX + ChatColor.AQUA + player.getName() + ": " + message);
            }
        } else {
            player.sendMessage(PREFIX + ChatColor.RED + "Vous n'êtes pas dans une partie.");
        }
    }

    private void sendHelp(ProxiedPlayer player) {
        player.sendMessage(PREFIX + ChatColor.YELLOW + "Commandes de la partie:");
        player.sendMessage("/p create: " + ChatColor.WHITE + "Crée votre partie.");
        player.sendMessage("/p leave: " + ChatColor.WHITE + "Quitte votre partie.");
        player.sendMessage("/p destroy: " + ChatColor.WHITE + "Détruit votre partie.");
        player.sendMessage("/p add <nomd'unjoueur>: " + ChatColor.WHITE + "Invite un joueur à rejoindre votre partie.");
        player.sendMessage("/p join: " + ChatColor.WHITE + "Accepte une invitation à rejoindre une partie.");
        player.sendMessage("/p chat <message>: " + ChatColor.WHITE + "Envoie un message à tous les membres de votre partie.");
    }
}
