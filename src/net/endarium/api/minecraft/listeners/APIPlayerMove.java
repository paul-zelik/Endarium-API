package net.endarium.api.minecraft.listeners;

import net.endarium.api.games.GameStatus;
import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.login.LoginManager;
import net.endarium.api.players.wallets.Currency;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class APIPlayerMove implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        LoginManager loginManager = new LoginManager();

        // Bloquer le Mouvement des Joueurs
        if (!(loginManager.isLogged(player.getUniqueId()))) {
            Location from = event.getFrom();
            Location to = event.getTo();
            double x = Math.floor(from.getX());
            double z = Math.floor(from.getZ());
            double y = Math.floor(from.getY());
            if (Math.floor(to.getX()) != x || Math.floor(to.getZ()) != z || Math.floor(to.getY()) != y) {
                x += .5;
                y += .5;
                z += .5;
                player.teleport(new Location(from.getWorld(), x, y, z, from.getYaw(), from.getPitch()));
            }
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        EndaPlayer endaPlayer = new EndaPlayer(player.getUniqueId());

        Entity caughtEntity;
        caughtEntity = event.getCaught();

        if (caughtEntity != null && caughtEntity instanceof Item) {
            Item item = (Item) caughtEntity;
            ItemStack itemStack = item.getItemStack();

            // Vérifie si l'objet pêché est un poisson
            if (itemStack.getType() == Material.RAW_FISH) {
                // Obtient la durabilité de l'objet (qui correspond au type de poisson)
                short durability = itemStack.getDurability();

                // Imprime le type de poisson pêché en fonction de la durabilité
                switch (durability) {
                    case 0:
                        endaPlayer.addCurrency(Currency.COINS,10);
                        player.sendMessage(ChatColor.GRAY + "Gain de " + Currency.COINS.getColor() + Currency.COINS.getName() + ChatColor.GRAY + " +" + Currency.COINS.getColor() + " 10 " + Currency.COINS.getIcon() + ChatColor.DARK_GRAY + " (" + ChatColor.GREEN + "Va te laver les mains. Ton poisson est pas bon." + ChatColor.DARK_GRAY + ")");
                        break;
                    case 1:
                        endaPlayer.addCurrency(Currency.COINS,15);
                        player.sendMessage(ChatColor.GRAY + "Gain de " + Currency.COINS.getColor() + Currency.COINS.getName() + ChatColor.GRAY + " +" + Currency.COINS.getColor() + " 15 " + Currency.COINS.getIcon() + ChatColor.DARK_GRAY + " (" + ChatColor.GREEN + "SloWPr1 aime les sushi. On aime bien le saumon ici." + ChatColor.DARK_GRAY + ")");
                        break;
                    case 2:
                        endaPlayer.addCurrency(Currency.TOKENS,5);
                        player.sendMessage(ChatColor.GRAY + "Gain de " + Currency.TOKENS.getColor() + Currency.TOKENS.getName() + Currency.TOKENS + " +" + Currency.TOKENS.getColor() + " 5 " + Currency.TOKENS.getIcon() + ChatColor.DARK_GRAY + " (" + ChatColor.GREEN + "Tu pêche des poissons tropicaux. Sale Consomateur." + ChatColor.DARK_GRAY + ")");
                        break;
                    case 3:
                        endaPlayer.addCurrency(Currency.COINS,20);
                        player.sendMessage(ChatColor.GRAY + "Gain de " + Currency.COINS.getColor() + Currency.COINS.getName() + Currency.COINS + " +" + Currency.COINS.getColor() + " 20 " + Currency.COINS.getIcon() + ChatColor.DARK_GRAY + " (" + ChatColor.GREEN + "Toi aussi tu aimes les Simpsons?" + ChatColor.DARK_GRAY + ")");
                        break;
                    default:
                        System.out.println("Poisson pêché : Inconnu");
                }
            }
        }
    }

}
