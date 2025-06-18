package net.endarium.api.utils.builders.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class BookBuilder {

	private String title;
	private String author;
	private List<String> pages;
	private List<String> item_lore;
	private int amount;
	private boolean item_glow;

	/**
	 * Instance book with material
	 *
	 * @param title  String title of book
	 * @param author String author of book
	 */
	public BookBuilder(String title, String author) {
		this.title = title;
		this.author = author;
		this.pages = new ArrayList<>();
		this.item_lore = new ArrayList<>();
	}

	/**
	 * Set amount of Book
	 * 
	 * @param amount Integer amount
	 * @return ItemBuilder
	 */
	public BookBuilder amount(int amount) {
		this.amount = amount;
		return this;
	}

	/**
	 * Set lore of Book
	 * 
	 * @param lore List of String lore
	 * @return ItemBuilder
	 */
	public BookBuilder lore(List<String> lore) {
		this.item_lore = lore;
		return this;
	}

	/**
	 * Set glow of Book
	 * 
	 * @param value Boolean if it have a glow
	 * @return ItemBuilder
	 */
	public BookBuilder glow(boolean value) {
		this.item_glow = value;
		return this;
	}

	/**
	 * Set glow of Book
	 *
	 * @param pages List of String Set pages
	 * @return ItemBuilder
	 */
	public BookBuilder pages(List<String> pages) {
		this.pages = pages;
		return this;
	}

	/**
	 * Build itembuilder
	 * 
	 * @return ItemStack
	 */
	public ItemStack build() {
		ItemStack stack = new ItemStack(Material.WRITTEN_BOOK, amount);
		BookMeta meta = (BookMeta) stack.getItemMeta();
		meta.setAuthor(this.author);
		meta.setTitle(this.title);
		meta.setPages(this.pages);
		if (!this.item_lore.isEmpty())
			meta.setLore(this.item_lore);
		if (this.item_glow) {
			stack.addUnsafeEnchantment(Enchantment.LURE, 1);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		stack.setItemMeta(meta);
		return stack;
	}
}