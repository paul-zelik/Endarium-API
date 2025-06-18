package net.endarium.api.minecraft.commands.system;

import net.endarium.api.games.servers.CrystaliserAPI;
import net.endarium.api.players.login.LoginManager;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.builders.items.ItemFactory;
import net.endarium.api.utils.commands.Command;
import net.endarium.api.utils.tools.SoundUtils;
import net.endarium.crystaliser.servers.ServerType;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static org.bukkit.Sound.LEVEL_UP;

public class MineCommand {

    @Command(name = { "mine" }, minimumRank = Rank.DEFAULT, senderType = Command.SenderType.ONLY_PLAYER)
    public void onCommand(Player player, String[] args) {

        LoginManager loginManager = new LoginManager();
        if (!(loginManager.isLogged(player.getUniqueId()))) {
            return;
        }

        if (CrystaliserAPI.getEndaServer().getServerType() == ServerType.HUB) {
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().setItem(4, new ItemFactory(Material.DIAMOND_PICKAXE).withName(ChatColor.AQUA + "Un truc").done());
            SoundUtils.sendSound(player, LEVEL_UP, 2.0f, 1.0f);
            player.sendMessage(ChatColor.DARK_GRAY + "(" + ChatColor.RED + "!" + ChatColor.DARK_GRAY + ")" + ChatColor.RED + " Miner dans une mine c'est l'art de miner une mine en minant dans une mine d'Amine qui ne mine pas dans ça mine - ChatGPT (01/24/2024 - 23:01)");
        }

    }

}
