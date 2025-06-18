package net.endarium.api.utils.builders.items;

import java.util.Arrays;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

/**
 * Cette classe a pour but de simplifier la creation d'Itemstack.
 */
public class ItemFactory {

	private ItemStack item;

	public ItemFactory(ItemStack item) {
		this.item = item;
	}

	public ItemFactory(Material mat) {
		item = new ItemStack(mat);
	}

	public ItemFactory(Material mat, int amount) {
		item = new ItemStack(mat, amount);
	}

	/**
	 * Changer le nom de l'Item.
	 */
	public ItemFactory withName(String name) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return this;
	}

	/**
	 * Changer la description de l'Item.
	 */
	public ItemFactory withLore(String... lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setLore(Arrays.asList(lore));
		item.setItemMeta(meta);
		return this;
	}

	/**
	 * Changer la couleur de l'Item.
	 */
	@SuppressWarnings("deprecation")
	public ItemFactory withColor(DyeColor color) {
		item.setDurability(color.getData());
		return this;
	}

	/**
	 * Changer le propietaire du Crâne.
	 */
	public ItemFactory withOwner(String owner) {
		if (item.getType().equals(Material.SKULL_ITEM)) {
			item.setDurability((short) 3);
			SkullMeta m = (SkullMeta) item.getItemMeta();
			m.setOwner(owner);
			item.setItemMeta(m);
		}
		return this;
	}

	/**
	 * Changer la quantité d'Item.
	 */
	public ItemFactory withAmount(int amount) {
		item.setAmount(amount);
		return this;
	}

	/**
     * Ajouter un enchantement a l'Item.
     */
	public ItemFactory withEnchant(Enchantment e, int lvl) {
		ItemMeta m = item.getItemMeta();
		m.addEnchant(e, lvl, true);
		item.setItemMeta(m);
		return this;
	}

	/**
	 * Ajouter un effet a la Potion.
	 */
	public ItemFactory withEffect(PotionEffect e) {
		if (!(item.getType().equals(Material.POTION)))
			return this;

		PotionMeta pm = (PotionMeta) item.getItemMeta();
		pm.addCustomEffect(e, true);
		item.setItemMeta(pm);

		return this;
	}

	/**
	 * Ajouter un flag a l'Item.
	 */
	public ItemFactory addFlag(ItemFlag... f) {
		ItemMeta m = item.getItemMeta();
		m.addItemFlags(f);
		item.setItemMeta(m);
		return this;
	}

	/**
	 * Ajouter un effet de brillant sur l'Item.
	 */
	public ItemFactory withGlowEffect() {
		Glow.registerGlow();
		withEnchant(new Glow(70), 1);
		return this;
	}

	/**
	 * Valider la construction de l'Item.
	 */
	public ItemStack done() {
		return item;
	}

	/**
	 * Construire une armure en cuire de couleur.
	 * 
	 * @param material
	 * @param color
	 * @return
	 */
	public static ItemStack buildLeatherColorArmor(Material material, Color color) {
		ItemStack item = new ItemStack(material);
		LeatherArmorMeta itemm = (LeatherArmorMeta) item.getItemMeta();
		itemm.setColor(color);
		item.setItemMeta(itemm);
		return item;
	}
}