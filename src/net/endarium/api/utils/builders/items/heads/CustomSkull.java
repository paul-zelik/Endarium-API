package net.endarium.api.utils.builders.items.heads;

import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import net.endarium.api.utils.ReflectionUtils;

public enum CustomSkull {

	EXEMPLE_HEAD("EXEMPLE_HEAD");

	private static final Base64 base64 = new Base64();
	private String id;

	private CustomSkull(String id) {
		this.id = id;
	}

	/**
	 * Récupérer la Custom Skull.
	 */
	public ItemStack getSkull() {
		ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
		meta.setOwner(id);
		itemStack.setItemMeta(meta);
		return itemStack;
	}

	/**
	 * Récupérer une Skull à partir de la NMS.
	 * 
	 * @param url
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static ItemStack getCustomSkull(String url) {
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		PropertyMap propertyMap = profile.getProperties();
		if (propertyMap == null) {
			throw new IllegalStateException("Profile doesn't contain a property map");
		}
		byte[] encodedData = base64.encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
		propertyMap.put("textures", new Property("textures", new String(encodedData)));
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		ItemMeta headMeta = head.getItemMeta();
		Class<?> headMetaClass = headMeta.getClass();
		try {
			ReflectionUtils.getField((Class) headMetaClass, true, "profile").set(headMeta, profile);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		head.setItemMeta(headMeta);
		return head;
	}

	/**
	 * Récupérer la tête d'un Joueur.
	 * 
	 * @param name
	 * @return
	 */
	public static ItemStack getPlayerSkull(String name) {
		ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
		meta.setOwner(name);
		itemStack.setItemMeta(meta);
		return itemStack;
	}
}