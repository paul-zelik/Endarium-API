package net.endarium.api.minecraft.commands.system;

import java.util.List;
import java.util.UUID;

import net.endarium.api.EndariumCommons;
import net.endarium.api.minecraft.EndariumBukkit;
import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.login.LoginManager;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.commands.Command;
import net.endarium.api.utils.mojang.UUIDFetcher;
import net.endarium.api.utils.tools.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class SponsorshipCommand {

    private String PREFIX = ChatColor.GOLD + "[Parrainage] ";

    @Command(name = { "parrain", "parrainage" }, minimumRank = Rank.DEFAULT, senderType = Command.SenderType.ONLY_PLAYER)
    public void onCommand(Player player, String[] args) {


        LoginManager loginManager = new LoginManager();
        if (!(loginManager.isLogged(player.getUniqueId()))) {
            return;
        }

        // Vérifier la permission du Joueur
        EndaPlayer golemaPlayer = EndaPlayer.get(player.getUniqueId());
        if (golemaPlayer == null)
            return;
        if (golemaPlayer.getRank().getPower() < Rank.VIP.getPower()) {
            player.sendMessage(PREFIX + ChatColor.RED + "Vous devez être " + ChatColor.YELLOW + "VIP " + ChatColor.WHITE
                    + "pour parrainer des joueurs.");
            return;
        }

        // Envoyer les Help
        if (args.length != 1) {
            this.sendHelp(player);
            return;
        }

        List<String> sponsorshipList = EndariumBukkit.getPlugin().getEndariumCommons().getEndariumEntities()
                .getSponsorshipManager().getSponsorshipNameList(player.getUniqueId());
        if (sponsorshipList == null)
            return;

        // Vérifier si il veut Afficher ses parrainages
        if (args[0].equalsIgnoreCase("list")) {
            if ((sponsorshipList.isEmpty()) || (sponsorshipList.size() == 0)) {
                player.sendMessage(PREFIX + ChatColor.RED + "Vous n'avez encore parrainé aucun joueur...");
                return;
            }
            String members = "";
            for (String playerName : sponsorshipList)
                members = members + ChatColor.YELLOW + playerName + ChatColor.GRAY + ", ";
            members = members.substring(0, members.length() - 2);
            player.sendMessage(PREFIX + ChatColor.WHITE + "Liste de vos parrainages : " + members);
            return;
        }

        // Limite des parrainages
        if (sponsorshipList.size() >= golemaPlayer.getRank().getMaxSponsorship()) {
            player.sendMessage(PREFIX + ChatColor.RED + "Vous avez atteint votre limite de parrainage...");
            return;
        }

        // Parrainage d'un Joueur
        String playerName = args[0];
        UUID targetUUID = UUIDFetcher.getUUID(playerName);
        if (targetUUID == null) {
            player.sendMessage(
                    PREFIX + ChatColor.RED + "Le Joueur '" + playerName + "' n'est pas un compte Minecraft valide.");
            return;
        }

        // Vérifier si le joueur possède un compte sur Endarium
        Rank targetRole = EndariumCommons.getInstance().getEndariumEntities().getRankManager().getRank(targetUUID);
        if (targetRole == null) {
            player.sendMessage(
                    PREFIX + ChatColor.RED + "Le Joueur '" + playerName + "' ne possède pas de compte sur Endarium.");
            return;
        }

        // Vérifier si le Joueur n'est pas déjà VIP
        if (targetRole.getPower() > Rank.DEFAULT.getPower()) {
            player.sendMessage(PREFIX + ChatColor.RED + "Vous ne pouvez pas parrainer ce joueur.");
            return;
        }

        // Appliquer le Parrainager
        EndariumBukkit.getPlugin().getEndariumCommons().getEndariumEntities().getRankManager().setRankTemporary(targetUUID,
                Rank.MINIVIP, 20160);
        EndariumBukkit.getPlugin().getEndariumCommons().getEndariumEntities().getSponsorshipManager()
                .createSponsorship(player.getUniqueId(), targetUUID, playerName, 31);
        player.sendMessage(PREFIX + ChatColor.WHITE + "Vous venez de parrainer le joueur : " + ChatColor.YELLOW
                + playerName + ChatColor.WHITE + ".");
        Bukkit.broadcastMessage(PREFIX + golemaPlayer.getRank().getChatColor() + player.getName() + ChatColor.GRAY
                + " vient de parrainer le joueur " + ChatColor.WHITE + playerName + ChatColor.GRAY + ".");
        SoundUtils.sendSound(player, Sound.LEVEL_UP);
        return;
    }

    /**
     * Message d'Aide de la Commande.
     */
    private void sendHelp(Player player) {
        player.sendMessage("");
        player.sendMessage(" " + ChatColor.GRAY + "» " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Aide"
                + ChatColor.WHITE + "│ " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Parrainage");
        player.sendMessage("");
        player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/parrain [pseudo] "
                + ChatColor.WHITE + "» " + ChatColor.AQUA + "Parrainer un Joueur.");
        player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/parrain list "
                + ChatColor.WHITE + "» " + ChatColor.AQUA + "Afficher vos parrainages.");
        player.sendMessage(ChatColor.GRAY + "Les parrainages sont valides pendant " + ChatColor.YELLOW + "2 semaines"
                + ChatColor.GRAY + " et vous ne pouvez utiliser vos parrainages que tous les " + ChatColor.RED
                + "30 jours" + ChatColor.GRAY + ".");
        player.sendMessage("");

    }
}