package net.endarium.api.utils.builders.inventories;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import net.endarium.api.minecraft.EndariumBukkit;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;

public abstract class VirtualMenu implements Listener {

	protected Player player;
	protected String menuName;
	protected int menuRow;
	protected Inventory menuInventory;
	protected HashMap<Integer, VirtualItem> eventClick = new HashMap<>();

	/**
	 * Générer un Menu Virtual.
	 * 
	 * @param player
	 * @param menuName
	 * @param menuRow
	 */
	public VirtualMenu(Player player, String menuName, int menuRow) {
		this.player = player;
		this.menuName = menuName;
		this.menuRow = menuRow;
		this.menuInventory = Bukkit.createInventory(null, this.menuRow * 9, this.menuName);
		this.eventClick.clear();
		Bukkit.getPluginManager().registerEvents(this, EndariumBukkit.getPlugin());
	}

	/**
	 * Ouvrir le Menu d'un Joueur.
	 */
	public void open() {
		this.player.openInventory(this.menuInventory);
	}

	/**
	 * Détruire le Menu du Joueur.
	 */
	public void destroy() {
		HandlerList.unregisterAll(this);
		this.eventClick.clear();
		this.menuInventory.clear();
	}

	public void setItem(VirtualItem item, int slot) {
		org.bukkit.inventory.ItemStack itemStack = item.itemStack;
		if (item.enchant)
			itemStack = addGlow(itemStack);

		this.eventClick.put(Integer.valueOf(slot), item);
		this.menuInventory.setItem(slot, itemStack);
	}

	private org.bukkit.inventory.ItemStack addGlow(org.bukkit.inventory.ItemStack item) {
		net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tag = null;

		if (!nmsStack.hasTag()) {
			tag = new NBTTagCompound();
			nmsStack.setTag(tag);
		}

		if (tag == null) {
			tag = nmsStack.getTag();
		}

		NBTTagList ench = new NBTTagList();
		tag.set("ench", ench);
		nmsStack.setTag(tag);

		return CraftItemStack.asCraftMirror(nmsStack);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		Inventory inv = event.getInventory();
		if (event.getInventory() == null)
			return;
		if (menuInventory == null)
			return;
		if (menuInventory.equals(event.getInventory()))
			destroy();
		if ((this.menuName.equals(inv.getName())) && (this.player.equals(player)))
			destroy();
	}
}