package net.endarium.api.minecraft.commands.administrator;

import net.endarium.api.minecraft.EndariumBukkit;
import net.endarium.api.players.login.LoginManager;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.commands.Command;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class BroadcastCommand {

    @Command(name = { "annonce" }, minimumRank = Rank.ADMINISTRATOR, senderType = Command.SenderType.ONLY_PLAYER)
    public void onCommand(Player player, String[] args) {

        LoginManager loginManager = new LoginManager();
        if (!(loginManager.isLogged(player.getUniqueId()))) {
            return;
        }

        // Envoyer les Help
        if (args.length < 1) {
            this.sendHelp(player);
            return;
        }

        // Récupérer le Message
        String message = "";
        for (int i = 0; i < args.length; i++) {
            message = message + " " + args[i];
        }
        message.trim();

        // Envoie du Broadcast
        message = ChatColor.GOLD + "[Annonce] " + ChatColor.RED + "" + ChatColor.BOLD + player.getName()
                + ChatColor.GRAY + " »" + ChatColor.WHITE + message;
        EndariumBukkit.getPlugin().getServerBroadcasterChannel().broadcastMessage(message);
    }

    /**
     * Message d'Aide de la Commande.
     */
    private void sendHelp(Player player) {
        player.sendMessage("");
        player.sendMessage(" " + ChatColor.GRAY + "» " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Aide"
                + ChatColor.WHITE + "│ " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Annonce/Broadcast");
        player.sendMessage("");
        player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/annonce [message] "
                + ChatColor.WHITE + "» " + ChatColor.AQUA + "Faire une annonce sur le Network.");
        player.sendMessage("");

    }
}
