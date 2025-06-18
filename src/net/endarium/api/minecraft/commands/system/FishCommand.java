package net.endarium.api.minecraft.commands.system;

import net.endarium.api.games.servers.CrystaliserAPI;
import net.endarium.api.players.login.LoginManager;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.builders.items.ItemFactory;
import net.endarium.api.utils.builders.titles.ActionBarBuilder;
import net.endarium.api.utils.commands.Command;
import net.endarium.api.utils.tools.SoundUtils;
import net.endarium.crystaliser.servers.ServerType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.Sound.LEVEL_UP;

public class FishCommand {

    @Command(name = { "fish" }, minimumRank = Rank.DEFAULT, senderType = Command.SenderType.ONLY_PLAYER)
    public void onCommand(Player player, String[] args) {

        LoginManager loginManager = new LoginManager();
        if (!(loginManager.isLogged(player.getUniqueId()))) {
            return;
        }

        if (CrystaliserAPI.getEndaServer().getServerType() == ServerType.HUB) {
            player.getInventory().setItem(4, new ItemFactory(Material.FISHING_ROD).withName(ChatColor.AQUA + "Plouf").done());
            SoundUtils.sendSound(player, LEVEL_UP, 2.0f, 1.0f);
            player.sendMessage(ChatColor.DARK_GRAY + "(" + ChatColor.RED + "!" + ChatColor.DARK_GRAY + ")" + ChatColor.RED + " La canne à pêche, c'est l'art de tendre des lignes pour attraper des souvenirs aquatiques ! - ChatGPT (01/24/2024 - 23:01)");
        }

    }

}
