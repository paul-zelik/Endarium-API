package net.endarium.api.utils.builders.inventories;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class Menu implements InventoryHolder {

    protected Player player;
    protected Inventory inventory;

    public Menu(Player player) {
        this.player = player;
    }

    public abstract String getMenuName();

    public abstract int getSlots();

    public abstract void handleMenu(InventoryClickEvent e);

    public abstract void closeMenu(InventoryCloseEvent e);

    public abstract void setMenuItems();

    public void open() {
        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());
        this.setMenuItems();
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void setOutlines(ItemStack items) {

        for (int i = 0; i < 9; i++) {
            if(inventory.getItem(i) == null) {
                inventory.setItem(i, items);
            }
        }
        if(inventory.getSize() >= 54) {
            for (int i = 45; i < 54; i++) {
                if(inventory.getItem(i) == null) {
                    inventory.setItem(i, items);
                }
            }
            for (int i = 0; i < 54; i++) {
                if (i % 9 == 0 || (i + 1) % 9 == 0) {
                    if(inventory.getItem(i) == null) {
                        inventory.setItem(i, items);
                    }
                }
            }
        }

    }

    public void fillMenu(ItemStack item) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, item);
            }
        }
    }

}
