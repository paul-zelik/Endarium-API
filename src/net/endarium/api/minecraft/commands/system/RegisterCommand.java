package net.endarium.api.minecraft.commands.system;

import net.endarium.api.games.servers.CrystaliserServerManager;
import net.endarium.api.players.login.LoginManager;
import net.endarium.api.players.login.PreniumManager;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.Messages;
import net.endarium.api.utils.builders.inventories.InventoryBuilder;
import net.endarium.api.utils.builders.items.ItemFactory;
import net.endarium.api.utils.builders.titles.TitleBuilder;
import net.endarium.api.utils.commands.Command;
import net.md_5.bungee.protocol.packet.Chat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class RegisterCommand implements Listener {

    private String PREFIX = Messages.ENDARIUM_PREFIX;
    private String inventoryName = "Es-tu prenium ?";

    @Command(name = { "register" }, minimumRank = Rank.DEFAULT, senderType = Command.SenderType.ONLY_PLAYER)
    public void onCommand(Player player, String[] args) {
        LoginManager loginManager = new LoginManager();


        if (loginManager.isLogged(player.getUniqueId())) {
            player.sendMessage(PREFIX + ChatColor.YELLOW + "Vous êtes déjà connecté.");
        } else {
            if (loginManager.loginPlayerExists(player.getUniqueId())){
                player.sendMessage(PREFIX + ChatColor.YELLOW + "Vous avez déjà un compte crée sur le serveur.");
            } else {
                if (args.length == 2) {
                    if (args[0].equalsIgnoreCase(args[1])) {
                        loginManager.createAccount(player.getUniqueId(), args[0]);
                        loginManager.makeConnected(player.getUniqueId());
                        player.sendMessage(PREFIX + ChatColor.YELLOW + "Vous êtes enregistré. Bon jeu :).");
                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                        new TitleBuilder(ChatColor.GREEN + "Connecté", ChatColor.WHITE + "Bon jeu").send(player);



                    } else {
                        player.sendMessage(PREFIX + ChatColor.YELLOW + "Vous n'avez pas écrit le même mot de passe.");
                    }
                } else {
                    player.sendMessage(PREFIX + ChatColor.YELLOW + "Utilisation : /register <motdepasse> <motdepasse>");
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PreniumManager preniumManager = new PreniumManager();
        if (event.getInventory() == null)
            return;
        if (event.getCurrentItem() == null)
            return;
        if (event.getCurrentItem().getType().equals(Material.AIR))
            return;
        if (!(event.getInventory().getName().equalsIgnoreCase(inventoryName)))
            return;
        event.setCancelled(true);
        switch (event.getCurrentItem().getType()) {
            case COMMAND:
                preniumManager.createAccountPrenium(player, true);
                player.sendMessage(PREFIX + ChatColor.YELLOW + "Vous êtes sur une connection Prenium. Pour tous problèmes rencontrés, merci de contacté un administrateur. Bon jeu :)");
                player.closeInventory();
                CrystaliserServerManager.sendPlayerToHub(player, false);

            case BARRIER:
                preniumManager.createAccountPrenium(player, false);
                player.sendMessage(PREFIX + ChatColor.YELLOW + "Vous êtes sur une connection crack. Pour tous problèmes rencontrés, merci de contacté un administrateur. Bon jeu :)");
                player.closeInventory();
                CrystaliserServerManager.sendPlayerToHub(player, false);

        }

    }

}
