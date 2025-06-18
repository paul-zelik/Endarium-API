package net.endarium.api.utils.builders.inventories;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class PaginatedMenu extends Menu {

    protected int page = 0;
    protected int maxItemPerPage = 28;
    protected int index = 0;
    protected int itemSize = 0;

    public PaginatedMenu(Player player) {
        super(player);
    }

    public void addMenuBorder(ItemStack borderItemstack) {
        inventory.setItem(48, previousPage());
        inventory.setItem(50, nextPage());
        inventory.setItem(49, closeMenu());

        setOutlines(borderItemstack);
    }

    public abstract ItemStack previousPage();

    public abstract ItemStack nextPage();

    public abstract ItemStack closeMenu();

    public void setItemList(List<ItemStack> itemsList) {
        if (itemsList != null && !itemsList.isEmpty()) {
            itemSize = itemsList.size();
            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if (index >= itemsList.size()) break;
                if (itemsList.get(index) != null) {
                    inventory.addItem(itemsList.get(index));
                }
            }
        }
    }

    public void runEvent(InventoryClickEvent event) {

        Player p = (Player) event.getWhoClicked();

        if (event.getCurrentItem().equals(closeMenu())) {
            p.closeInventory();
        } else if (event.getCurrentItem().equals(previousPage())) {
            if (page == 0) {
                p.sendMessage(ChatColor.GRAY + "Vous êtes déjà sur la première page !");
            } else {
                page = page - 1;
                super.open();
            }
        } else if (event.getCurrentItem().equals(nextPage())) {
            if (!((index + 1) >= itemSize)) {
                page = page + 1;
                super.open();
            } else {
                p.sendMessage(ChatColor.GRAY + "Vous êtes sur la dernière page.");
            }
        }

    }

    public int getMaxItemsPerPage() {
        return maxItemPerPage;
    }

    public boolean isBorderItem(ItemStack item) {
        return item.equals(closeMenu()) || item.equals(previousPage()) || item.equals(nextPage());
    }


}
