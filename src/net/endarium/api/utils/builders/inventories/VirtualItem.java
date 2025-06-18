package net.endarium.api.utils.builders.inventories;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class VirtualItem {

	public String itemName;
	public String itemDescription;
	public ItemStack itemStack;
	public boolean enchant = false;

	public VirtualItem(String itemName, String itemDescription, ItemStack itemStack) {
		this.itemName = itemName;
		this.itemDescription = itemDescription;
		this.itemStack = itemStack;
	}

	public VirtualItem(int i, String[] strings, ItemStack itemStack2) {
	}

	public abstract void onUse(Player paramPlayer);
}