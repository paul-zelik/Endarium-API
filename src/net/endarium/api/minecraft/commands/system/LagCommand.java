package net.endarium.api.minecraft.commands.system;

import net.endarium.api.games.servers.CrystaliserAPI;
import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.login.LoginManager;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.commands.Command;
import net.endarium.api.utils.commands.Command.SenderType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;

public class LagCommand {

    @Command(name = { "lag", "lags", "tps", "ping" }, minimumRank = Rank.DEFAULT, senderType = SenderType.ONLY_PLAYER)
    public void onCommand(Player player, String[] args) {

        EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());
        if (!(endaPlayer.isLogged())) {
            return;
        }

        // Envoie du Message
        player.sendMessage("");
        player.sendMessage(" " + ChatColor.GRAY + "» " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Informations"
                + ChatColor.WHITE + "│ " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Serveur");
        player.sendMessage("");
        player.sendMessage(" §7§l■ §6Serveur: §b" + CrystaliserAPI.getEndaServer().getServerName()
                + " §8| §6Joueur(s): §b" + Bukkit.getOnlinePlayers().size());
        player.sendMessage(
                " §7§l■ §6Date: §b" + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date()));
        player.sendMessage(" §7§l■ §6Latence: §a*20.0, *20.0, *20.0");
        player.sendMessage(" §7§l■ §6Ping: §a" + ((CraftPlayer) player).getHandle().ping + "ms");
        player.sendMessage("");
        return;
    }
}
