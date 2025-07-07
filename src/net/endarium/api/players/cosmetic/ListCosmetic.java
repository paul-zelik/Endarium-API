package net.endarium.api.players.cosmetic;

import net.endarium.api.utils.builders.Particles;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum ListCosmetic {

    PARTICLE_RED("Particule de l'enfer", Particles.FLAME, Color.RED, new ItemStack(Material.BLAZE_POWDER), CosmeticType.PARTICULE, 1000, CosmeticRarete.COMMUN),
    HAT_WIZARD("Chapeau de sorcier", null, Color.BLUE, new ItemStack(Material.LEATHER_HELMET), CosmeticType.CHAPEAU, 1500, CosmeticRarete.RARE),
    CLOAK_ELVEN("Manteau elfique", null, Color.GREEN, new ItemStack(Material.LEATHER_CHESTPLATE), CosmeticType.MANTEAU, 2000, CosmeticRarete.EPIC),
    PANTS_DRAGON("Pantalon de dragon", null, Color.BLACK, new ItemStack(Material.LEATHER_LEGGINGS), CosmeticType.PANTALON, 1750, CosmeticRarete.LEGENDAIRE),
    SHOES_SPEED("Chaussures de vitesse", null, Color.YELLOW, new ItemStack(Material.LEATHER_BOOTS), CosmeticType.CHAUSSURE, 1250, CosmeticRarete.COMMUN),
    PARTICLE_MAGIC("Particule de l'amour", Particles.PORTAL, Color.RED, new ItemStack(Material.BLAZE_POWDER), CosmeticType.PARTICULE, 1000, CosmeticRarete.COMMUN),
    PARTICLE_HEART("Particule de cœur", Particles.HEART, Color.RED, new ItemStack(Material.APPLE), CosmeticType.PARTICULE, 1500, CosmeticRarete.RARE),
    PARTICLE_CLOUD("Particule de nuage", Particles.CLOUD, Color.WHITE, new ItemStack(Material.FEATHER), CosmeticType.PARTICULE, 1200, CosmeticRarete.COMMUN),
    PARTICLE_SMOKE("Particule de fumée", Particles.SMOKE_LARGE, Color.GRAY, new ItemStack(Material.COAL), CosmeticType.PARTICULE, 1300, CosmeticRarete.EPIC),
    PARTICLE_LAVA("Particule de lave", Particles.LAVA, Color.ORANGE, new ItemStack(Material.LAVA_BUCKET), CosmeticType.PARTICULE, 2000, CosmeticRarete.LEGENDAIRE);


    private String name;
    private Particles  particle;
    private Color color;
    private ItemStack item;
    private CosmeticType type;
    private int cout;
    private CosmeticRarete cosmeticRarete;

    ListCosmetic(String name, Particles particles, Color color, ItemStack item, CosmeticType type, int cout, CosmeticRarete cosmeticRarete) {

        this.name = name;
        this.particle = particles;
        this.color = color;
        this.item = item;
        this.type = type;
        this.cout = cout;
        this.cosmeticRarete = cosmeticRarete;


    }

    public Particles getParticle() {
        return particle;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public CosmeticRarete getCosmeticRarete() {
        return cosmeticRarete;
    }

    public CosmeticType getType() {
        return type;
    }

    public int getCout() {
        return cout;
    }

    public ItemStack getItem() {
        return item;
    }
}
