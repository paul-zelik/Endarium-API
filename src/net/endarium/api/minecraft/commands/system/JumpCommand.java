package net.endarium.api.minecraft.commands.system;

import net.endarium.api.games.servers.CrystaliserAPI;
import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.login.LoginManager;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.commands.Command;
import net.endarium.api.utils.tools.SoundUtils;
import net.endarium.crystaliser.servers.ServerType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.Sound.LEVEL_UP;

public class JumpCommand{

    @Command(name = { "jump" }, minimumRank = Rank.DEFAULT, senderType = Command.SenderType.ONLY_PLAYER)
    public void onCommand(Player player, String[] args) {

        EndaPlayer endaPlayer =  EndaPlayer.get(player.getUniqueId());
        if (!(endaPlayer.isLogged())) {
            return;
        }

        if (CrystaliserAPI.getEndaServer().getServerType() == ServerType.HUB) {
            player.teleport(new Location(player.getWorld(), -58.5,107,64.5, -0,-0));
            player.sendMessage(ChatColor.DARK_GRAY + "(" + ChatColor.RED + "!" + ChatColor.DARK_GRAY + ")" + ChatColor.RED +"Le jump dans Minecraft : une petite impulsion, un grand saut vers l'aventure. - ChatGPT (02/04/2024 22:59)");
        }

    }

}
