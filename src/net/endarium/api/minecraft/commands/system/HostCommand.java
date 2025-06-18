package net.endarium.api.minecraft.commands.system;

import net.endarium.api.games.servers.CrystaliserAPI;
import net.endarium.api.games.servers.CrystaliserServerManager;
import net.endarium.api.minecraft.EndariumBukkit;
import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.login.LoginManager;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.Messages;
import net.endarium.api.utils.commands.Command;
import net.endarium.crystaliser.servers.EndaServer;
import net.endarium.crystaliser.servers.ServerType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class HostCommand {

    private String PREFIX = Messages.HOST_PREFIX;

    @Command(name = { "host", "h" }, minimumRank = Rank.DEFAULT, senderType = Command.SenderType.ONLY_PLAYER)
    public void onCommand(Player player, String[] args) {

        LoginManager loginManager = new LoginManager();
        if (!(loginManager.isLogged(player.getUniqueId()))) {
            return;
        }

        EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());
        EndaServer endaServer = CrystaliserAPI.getEndaServer();

        // Contrôler des permissions du Joueur
        if (endaPlayer == null || endaServer == null)
            return;
        if (!(endaServer.getServerType().equals(ServerType.HOST))) {
            player.sendMessage(Messages.UNKNOW_COMMAND);
            return;
        }
        if (!(endaServer.getHostUUID().toString().equalsIgnoreCase(player.getUniqueId().toString())) || endaPlayer.getRank().getPower() >= 70) {
            player.sendMessage(PREFIX + ChatColor.RED + "Vous n'êtes pas Host de ce serveur...");
            return;
        }

        // Envoyer les Help
        if (args.length < 1) {
            this.sendHelp(player);
            return;
        }

        // HOST : Announce
        if (args[0].equalsIgnoreCase("annonce")) {
            player.sendMessage(PREFIX + ChatColor.AQUA + "Votre annonce à été envoyé avec succès...");
            EndariumBukkit.getPlugin().getHostAnnounceChannel().sendHostAnnounceServer(endaServer);
            return;
        } else {
            this.sendHelp(player);
        }

        return;
    }

    @Command(name = { "gserv", "h" }, minimumRank = Rank.DEFAULT, senderType = Command.SenderType.ONLY_PLAYER)
    public void onCommandGServ(Player player, String[] args) {

        // Envoyer les Help
        if (args.length != 1) {
            player.sendMessage(Messages.UNKNOW_COMMAND);
            return;
        }

        String serverName = args[0];
        if (serverName.equalsIgnoreCase("build") || serverName.equalsIgnoreCase("minecraft"))
            return;
        CrystaliserServerManager.sendToServer(player, serverName);
        return;
    }

    /**
     * Message d'Aide de la Commande.
     */
    private void sendHelp(Player player) {
        player.sendMessage("");
        player.sendMessage(" " + ChatColor.GRAY + "» " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Aide"
                + ChatColor.WHITE + "│ " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Host");
        player.sendMessage("");
        player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/host annonce "
                + ChatColor.WHITE + "» " + ChatColor.AQUA + "Annoncer un Host dans les hubs.");
        player.sendMessage("");

    }
}