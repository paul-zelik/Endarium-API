package net.endarium.api.games;

import net.endarium.api.minecraft.EndariumBukkit;
import net.endarium.api.players.stats.IStats;
import net.endarium.api.players.stats.Stats;
import net.endarium.api.utils.builders.items.ItemFactory;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class StatsMenu implements Listener {

    public Map<Player, Inventory> menuInventoryMap = new HashMap<Player, Inventory>();
    private String inventoryName = "";

    /**
     * Constructeur de Menu de Stats.
     *
     * @param player
     */
    public StatsMenu(Player player, IStats iStats) {
        menuInventoryMap.remove(player);
        Bukkit.getPluginManager().registerEvents(this, EndariumBukkit.getPlugin());
        this.inventoryName = "Stats - " + iStats.gameType.getName();
        Inventory inventory = Bukkit.createInventory(null, 3 * 9, inventoryName);
        for (int i = 18; i < 26; i++)
            inventory.setItem(i,
                    new ItemFactory(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5)).withName("§1").done());
        inventory.setItem(26, new ItemFactory(Material.ARROW).withName(ChatColor.WHITE + "Fermer").done());
        for (Stats stats : iStats.getStatsList())
            inventory.addItem(new ItemFactory(stats.getItemIcon()).withName(
                            ChatColor.WHITE + stats.getName() + " :" + ChatColor.YELLOW + " " + iStats.getStatsMap().get(stats))
                    .done());
        this.menuInventoryMap.put(player, inventory);
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getInventory() == null)
            return;
        if (event.getCurrentItem() == null)
            return;
        if (event.getCurrentItem().getType().equals(Material.AIR))
            return;
        if (!(event.getInventory().getName().equalsIgnoreCase(inventoryName)))
            return;
        if (!(event.getInventory().equals(menuInventoryMap.get(player))))
            return;
        event.setCancelled(true);
        switch (event.getCurrentItem().getType()) {
            case ARROW:
                player.closeInventory();
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (event.getInventory() == null)
            return;
        if (menuInventoryMap.get(player) == null)
            return;
        if (menuInventoryMap.get(player).equals(event.getInventory()))
            menuInventoryMap.remove(player);
    }
}
