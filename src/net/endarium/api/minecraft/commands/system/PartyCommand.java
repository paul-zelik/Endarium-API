package net.endarium.api.minecraft.commands.system;

import net.endarium.api.minecraft.channels.PrivateMessageChannel;
import net.endarium.api.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.endarium.api.players.party.Party;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.commands.Command;


public class PartyCommand {

    /*
    private String PREFIX = Messages.PARTY_PREFIX;
    @Command(name = {"party","p","g","group"}, minimumRank = Rank.ADMINISTRATOR, senderType = Command.SenderType.ONLY_PLAYER)
    public void onCommand(Player sender, String[] args) {
        if (args.length == 0 || args.length > 2) {
            this.sendHelp(sender);
            return;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("create")) {

                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    PartyManager partyManager = new PartyManager();
                    if (partyManager.getPlayerParty(player.getUniqueId()) == null) {
                        partyManager.createParty(player.getUniqueId(), player.getName() + "-group");
                        player.sendMessage(PREFIX + ChatColor.AQUA + "La Party a été crée avec succée");
                    } else {
                        player.sendMessage(PREFIX + ChatColor.AQUA + "Vous êtes déjà dans une party");
                    }
                } else {
                    sender.sendMessage(PREFIX + ChatColor.AQUA + "Seule une joueur peux utilisé cette commande");
                }

            }

            else if (args[0].equalsIgnoreCase("delete")) {

                PartyManager partyManager = new PartyManager();
                Party party = partyManager.getPlayerParty(sender.getUniqueId());

                if (party != null) {
                    if (party.isOwner(sender.getUniqueId())) {
                        if (partyManager.destroyParty(party)) {
                            sender.sendMessage(PREFIX + ChatColor.AQUA + "Votre party a été suprimer!");
                        } else {
                            sender.sendMessage(PREFIX + ChatColor.AQUA + "Oups, il y a eu un problème de supression");
                        }
                    } else {
                        sender.sendMessage(PREFIX + ChatColor.AQUA + "Il faut être Chef de la party pour la suprimer");
                    }
                } else {
                    sender.sendMessage(PREFIX + ChatColor.AQUA + "Vous n'êtes pas dabs une partie.");
                }

            } else if (args[0].equalsIgnoreCase("leave")) {

                PartyManager partyManager = new PartyManager();
                Party party = partyManager.getPlayerParty(sender.getUniqueId());

                if (party != null) {
                    if (!party.isOwner(sender.getUniqueId())) {
                        party.removeMember(sender.getUniqueId(), sender.getName());
                        sender.sendMessage(PREFIX + ChatColor.AQUA + "Vous avez quitté votre partie");
                    } else {
                        sender.sendMessage(PREFIX + ChatColor.AQUA + "Vous êtes le chef de votre partie, il faut la suprimer.");
                    }
                } else {
                    sender.sendMessage(PREFIX + ChatColor.AQUA + "Vous n'êtes pas dans une partie.");
                }


            }else if (args[0].equalsIgnoreCase("list")){

                PartyManager partyManager = new PartyManager();
                Party party = partyManager.getPlayerParty(sender.getUniqueId());

                if (party != null) {

                    String listPlayer = "";

                    for (String player : party.getMembersName()) {

                        listPlayer = listPlayer + player.toString() + ", ";

                    }

                    sender.sendMessage(PREFIX + ChatColor.AQUA + "Il y a dans votre partie : " + listPlayer);

                } else {
                    sender.sendMessage(PREFIX + ChatColor.AQUA + "Vous n'êtes pas dans une partie.");
                }

            } else if (args[0].equalsIgnoreCase("accept")) {

                PartyManager partyManager = new PartyManager();
                Party party = partyManager.getPlayerParty(sender.getUniqueId());
                PrivateMessageChannel privateMessageChannel = new PrivateMessageChannel();

                if (party == null) {

                    party.acceptInvitationRequest(sender);
                    privateMessageChannel.sendMessage(sender.getUniqueId(), party.getOwner(),PREFIX + ChatColor.AQUA + sender + "a accepté votre invitations.");

                } else {
                    sender.sendMessage(PREFIX + ChatColor.AQUA + "Vous êtes déjà dans une partie.");
                }

            }

            else {
                sendHelp(sender);
            }
        }

        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove")) {

                Player target = Bukkit.getPlayer(args[1]);
                if (target == null || !target.isOnline()) {
                    sender.sendMessage(PREFIX + ChatColor.AQUA + "Le joueur n'est pas en ligne!");
                    return;
                }

                PartyManager partyManager = new PartyManager();
                Party party = partyManager.getPlayerParty(sender.getUniqueId());
                if (party != null) {
                    if (party.isOwner(sender.getUniqueId())) {
                        party.removeMember(target.getUniqueId(), target.getName());
                        sender.sendMessage(PREFIX + ChatColor.AQUA + "Le joueur :  " + target.getName() + " a été suprimé de la party!");
                    } else {
                        sender.sendMessage(PREFIX + ChatColor.AQUA + "Seule le créateur de la party peut suprimer le joueur");
                    }
                } else {
                    sender.sendMessage(PREFIX + ChatColor.AQUA + "Vous n'êtes pas un joueur :/");
                }
            }

            if (args[0].equalsIgnoreCase("add")) {
                Player target = Bukkit.getPlayer(args[1]);

                PartyManager partyManager = new PartyManager();
                Party party = partyManager.getPlayerParty(sender.getUniqueId());
                PrivateMessageChannel privateMessageChannel = new PrivateMessageChannel();

                if (party.isOwner(sender.getUniqueId())) {
                    if (target == null || !target.isOnline()) {
                        sender.sendMessage(PREFIX + ChatColor.AQUA + "Le joueur n'est pas en ligne");
                        return;
                    }

                    if (party != null) {
                        sender.sendMessage(PREFIX + ChatColor.AQUA + "Une demande à été envoyé à" + target.getName() + "!");
                    } else {
                        sender.sendMessage(PREFIX + ChatColor.AQUA + "Vous n'êtes pas dans une party");
                    }
                } else {
                    sender.sendMessage(PREFIX + ChatColor.AQUA + "Vous devez être le chef de ma party.");
                }
            }
        } else {

            sendHelp(sender);

        }




    }

    private void sendHelp(Player player) {
        player.sendMessage("");
        player.sendMessage(" " + net.md_5.bungee.api.ChatColor.GRAY + "» " + net.md_5.bungee.api.ChatColor.YELLOW + "" + net.md_5.bungee.api.ChatColor.BOLD + "Aide"
                + net.md_5.bungee.api.ChatColor.WHITE + "│ " + net.md_5.bungee.api.ChatColor.YELLOW + "" + net.md_5.bungee.api.ChatColor.BOLD + "Party");
        player.sendMessage("");
        player.sendMessage(" " + net.md_5.bungee.api.ChatColor.GRAY + "" + net.md_5.bungee.api.ChatColor.BOLD + "■ " + net.md_5.bungee.api.ChatColor.GOLD + "/party add [pseudo] "
                + net.md_5.bungee.api.ChatColor.WHITE + "» " + net.md_5.bungee.api.ChatColor.AQUA + "Ajouter un Joueur a une party.");
        player.sendMessage("");
        player.sendMessage(" " + net.md_5.bungee.api.ChatColor.GRAY + "" + net.md_5.bungee.api.ChatColor.BOLD + "■ " + net.md_5.bungee.api.ChatColor.GOLD + "/party remove [pseudo] "
                + net.md_5.bungee.api.ChatColor.WHITE + "» " + net.md_5.bungee.api.ChatColor.AQUA + "Suprimer un Joueur d'une party.");
        player.sendMessage("");
        player.sendMessage(" " + net.md_5.bungee.api.ChatColor.GRAY + "" + net.md_5.bungee.api.ChatColor.BOLD + "■ " + net.md_5.bungee.api.ChatColor.GOLD + "/party create"
                + net.md_5.bungee.api.ChatColor.WHITE + "» " + net.md_5.bungee.api.ChatColor.AQUA + "Crée une party");
        player.sendMessage("");
        player.sendMessage(" " + net.md_5.bungee.api.ChatColor.GRAY + "" + net.md_5.bungee.api.ChatColor.BOLD + "■ " + net.md_5.bungee.api.ChatColor.GOLD + "/party delete"
                + net.md_5.bungee.api.ChatColor.WHITE + "» " + net.md_5.bungee.api.ChatColor.AQUA + "Suprimer une Party");
        player.sendMessage("");
        player.sendMessage(" " + net.md_5.bungee.api.ChatColor.GRAY + "" + net.md_5.bungee.api.ChatColor.BOLD + "■ " + net.md_5.bungee.api.ChatColor.GOLD + "/party leave"
                + net.md_5.bungee.api.ChatColor.WHITE + "» " + net.md_5.bungee.api.ChatColor.AQUA + "Quiter votre Party");
        player.sendMessage("");
        player.sendMessage(" " + net.md_5.bungee.api.ChatColor.GRAY + "" + net.md_5.bungee.api.ChatColor.BOLD + "■ " + net.md_5.bungee.api.ChatColor.GOLD + "/party list"
                + net.md_5.bungee.api.ChatColor.WHITE + "» " + net.md_5.bungee.api.ChatColor.AQUA + "Liste des membres de votre Party");
        player.sendMessage("");
    }
    */
}
