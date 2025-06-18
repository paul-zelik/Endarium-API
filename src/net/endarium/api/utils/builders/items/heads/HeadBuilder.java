package net.endarium.api.utils.builders.items.heads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * Cette classe a pour but de simplifier la creation de Skull.
 */
public class HeadBuilder {

	private int amount;
	private String name;
	private String head;
	private List<String> lore;
	private Map<Enchantment, Integer> enchantments;
	private List<ItemFlag> flags;
	private boolean unbreakable;

	public HeadBuilder() {
		this(1);
	}

	/**
	 * Constructeur de Skull.
	 */
	public HeadBuilder(int amount) {
		this.amount = amount;
		this.lore = new ArrayList<>();
		this.enchantments = new HashMap<>();
		this.flags = new ArrayList<>();
	}

	public HeadBuilder setAmount(int amount) {
		this.amount = amount;
		return this;
	}

	public HeadBuilder setName(String name) {
		this.name = name;
		return this;
	}

	public HeadBuilder setHead(String head) {
		this.head = head;
		return this;
	}

	public HeadBuilder setLore(List<String> lore) {
		this.lore = lore;
		return this;
	}

	public HeadBuilder setLore(String... lore) {
		this.lore = Arrays.asList(lore);
		return this;
	}

	public HeadBuilder setEnchantments(Map<Enchantment, Integer> enchantments) {
		this.enchantments = enchantments;
		return this;
	}

	public HeadBuilder addEnchantment(Enchantment enchantment, int level) {
		this.enchantments.put(enchantment, level);
		return this;
	}

	public HeadBuilder setFlags(List<ItemFlag> flags) {
		this.flags = flags;
		return this;
	}

	public HeadBuilder setFlags(ItemFlag... flags) {
		this.flags = Arrays.asList(flags);
		return this;
	}

	public HeadBuilder addFlag(ItemFlag flag) {
		this.flags.add(flag);
		return this;
	}

	public HeadBuilder setUnbreakable(boolean unbreakable) {
		this.unbreakable = unbreakable;
		return this;
	}

	/**
	 * Valider la construction de la Head.
	 */
	public ItemStack build() {
		@SuppressWarnings("deprecation")
		ItemStack item = new ItemStack(Material.SKULL_ITEM, amount, (short) 3, (byte) SkullType.PLAYER.ordinal());

		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setDisplayName(name);
		meta.setOwner(head);
		meta.setLore(lore);
		enchantments.entrySet().forEach(entry -> meta.addEnchant(entry.getKey(), entry.getValue(), true));
		flags.forEach(entry -> meta.addItemFlags(entry));
		meta.spigot().setUnbreakable(unbreakable);

		item.setItemMeta(meta);
		return item;
	}
}