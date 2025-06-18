package net.endarium.api.minecraft.listeners;

import net.endarium.api.EndariumCommons;
import net.endarium.api.games.servers.CrystaliserAPI;
import net.endarium.api.minecraft.EndariumBukkit;
import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.wallets.Currency;
import net.endarium.api.utils.EndariumAPI;
import net.endarium.crystaliser.servers.ServerType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Random;

public class APIPlayerInteractListener implements Listener {
    private static final Material REPLACEMENT_BLOCK = Material.BEDROCK;
    private static final Material[] ORES = {
            Material.STONE,
            Material.DIAMOND_ORE,
            Material.COAL_ORE,
            Material.EMERALD_ORE,
            Material.IRON_ORE,
            Material.GOLD_ORE
    };
    private static final int DURATION = 20 * 20; // 20 ticks per second * 20 seconds

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        event.setCancelled(true);

        if (CrystaliserAPI.getEndaServer().getServerType() == ServerType.HUB) {
            Block brokenBlock = event.getBlock();

            Material blockType = brokenBlock.getType();
            if (Arrays.asList(ORES).contains(blockType)) {

                if (blockType == Material.STONE && !(blockType.getMaxDurability() == 0)) {
                    event.setCancelled(true);
                }

                Player player = event.getPlayer();
                EndaPlayer endaPlayer = new EndaPlayer(player.getUniqueId());

                if (blockType == Material.STONE) {
                    endaPlayer.addCurrency(Currency.COINS, 1);
                    player.sendMessage(ChatColor.GRAY + "Gain de " + Currency.COINS.getColor() + Currency.COINS.getName() + " + 1 " + Currency.COINS.getIcon() + ChatColor.DARK_GRAY + "(" + ChatColor.GREEN + "Euh un racaillou?" + ChatColor.DARK_GRAY + ")");

                }
                else if (blockType == Material.DIAMOND_ORE) {
                    endaPlayer.addCurrency(Currency.COINS, 5);
                    player.sendMessage(ChatColor.GRAY + "Gain de " + Currency.COINS.getColor() + Currency.COINS.getName() + " + 5 " + Currency.COINS.getIcon() + ChatColor.DARK_GRAY + "(" + ChatColor.GREEN + "Hassoul le diamand c'est bien" + ChatColor.DARK_GRAY + ")");
                }
                else if (blockType == Material.COAL_ORE) {
                    endaPlayer.addCurrency(Currency.COINS, 2);
                    player.sendMessage(ChatColor.GRAY + "Gain de " + Currency.COINS.getColor() + Currency.COINS.getName() + " + 2 " + Currency.COINS.getIcon() + ChatColor.DARK_GRAY + "(" + ChatColor.GREEN + "Apowze a dit : Juste dans le jar que il y  3 maps setup et aucune des 3 et en maps (pour le punchout)" + ChatColor.DARK_GRAY + ")");

                }
                else if (blockType == Material.EMERALD_ORE) {
                    endaPlayer.addCurrency(Currency.TOKENS, 1);
                    player.sendMessage(ChatColor.GRAY + "Gain de " + Currency.TOKENS.getColor() + Currency.TOKENS.getName() + " + 5 " + Currency.TOKENS.getIcon() + ChatColor.DARK_GRAY + "(" + ChatColor.GREEN + "Dommage il n'y a pas de villageois" + ChatColor.DARK_GRAY + ")");
                }
                else if (blockType == Material.IRON_ORE) {
                    endaPlayer.addCurrency(Currency.COINS, 3);
                    player.sendMessage(ChatColor.GRAY + "Gain de " + Currency.TOKENS.getColor() + Currency.TOKENS.getName() + " + 5 " + Currency.TOKENS.getIcon() + ChatColor.DARK_GRAY + "(" + ChatColor.GREEN + "Faire faire du fer ça revient à faire du fer vert qui est fier." + ChatColor.DARK_GRAY + ")");
                }
                else if (blockType == Material.STONE) {
                    endaPlayer.addCurrency(Currency.COINS, 4);
                    player.sendMessage(ChatColor.GRAY + "Gain de " + Currency.TOKENS.getColor() + Currency.TOKENS.getName() + " + 5 " + Currency.TOKENS.getIcon() + ChatColor.DARK_GRAY + "(" + ChatColor.GREEN + "Un grand homme qui aimait les échecs a dit : Fais pas le calcul c'est des voleurs mdr" + ChatColor.DARK_GRAY + ")");
                } else {
                    player.sendMessage("Je crois que Fliser ne sait pas dev");
                }


                // Remplacement du bloc cassé par de la bedrock
                brokenBlock.setType(REPLACEMENT_BLOCK);

                // Planification du remplacement de la bedrock par un minerai après la durée spécifiée
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        replaceBedrockWithRandomOre(brokenBlock);
                    }
                }.runTaskLater(EndariumBukkit.getPlugin(), DURATION);
            }
        }
    }

    private void replaceBedrockWithRandomOre(Block block) {
        // Générer un index aléatoire pour choisir un minerai
        Random random = new Random();
        int index = random.nextInt(ORES.length);

        // Remplacer la bedrock par le minerai aléatoire
        block.setType(ORES[index]);
    }

}
