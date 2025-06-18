package net.endarium.api.minecraft.commands.administrator;

import net.endarium.api.games.servers.CrystaliserServerManager;
import net.endarium.api.players.login.LoginManager;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.Messages;
import net.endarium.api.utils.commands.Command;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class BuildServerTPCommand {

    @Command(name = { "build" }, minimumRank = Rank.MODERATOR, senderType = Command.SenderType.ONLY_PLAYER)
    public void onCommand(Player player, String[] args) {

        LoginManager loginManager = new LoginManager();
        if (!(loginManager.isLogged(player.getUniqueId()))) {
            return;
        }
        // Envoie du Message
        player.sendMessage(Messages.ENDARIUM_PREFIX + ChatColor.WHITE + "Téléportation vers le serveur de "
                + ChatColor.GOLD + "construction" + ChatColor.WHITE + "...");
        CrystaliserServerManager.sendToServer(player, "build");
        return;
    }
}