package net.endarium.api.utils.builders.items;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

class Glow extends Enchantment {

	protected Glow(int id) {
		super(id);
	}

	@Override
	public boolean canEnchantItem(ItemStack itemStack) {
		return false;
	}

	@Override
	public boolean conflictsWith(Enchantment enchantement) {
		return false;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return null;
	}

	@Override
	public int getMaxLevel() {
		return 0;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public int getStartLevel() {
		return 0;
	}

	private static AtomicBoolean registred = new AtomicBoolean(false);

	public static void registerGlow() {
		if (registred.compareAndSet(false, true)) {
			try {
				Field f = Enchantment.class.getDeclaredField("acceptingNew");
				f.setAccessible(true);
				f.set(null, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Glow glow = new Glow(70);
				Enchantment.registerEnchantment(glow);
			} catch (IllegalArgumentException e) {
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}