package net.endarium.api.minecraft.commands.mods;

import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.login.LoginManager;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.players.report.Report;
import net.endarium.api.players.report.ReportManager;
import net.endarium.api.utils.Messages;
import net.endarium.api.utils.builders.inventories.PaginatedMenu;
import net.endarium.api.utils.builders.items.ItemFactory;
import net.endarium.api.utils.builders.items.heads.CustomSkull;
import net.endarium.api.utils.commands.Command;
import net.endarium.api.utils.mojang.UUIDEndaFetcher;
import net.endarium.api.utils.mojang.UUIDFetcher;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ZReportCommand {

    private String PREFIX = Messages.ZMODS_PREFIX;
    private List<ItemStack> reportItems = new ArrayList<>();

    @Command(name = {"zreport"}, minimumRank = Rank.MODERATOR, senderType = Command.SenderType.ONLY_PLAYER)
    public void onCommandZReport(Player player, String[] args) {
        // Ouvrir le Menu avec la liste des Serveurs
        LoginManager loginManager = new LoginManager();
        if (!(loginManager.isLogged(player.getUniqueId()))) {
            return;
        }

        new ReportGUI(player);
    }

    public class ReportGUI extends PaginatedMenu {

        public ReportGUI(Player player) {
            super(player);
        }

        @Override
        public String getMenuName() {
            return "ZReport » Report";
        }

        @Override
        public int getSlots() {
            return 54;
        }

        @Override
        public void handleMenu(InventoryClickEvent event) {
            if (!(event.getWhoClicked() instanceof Player))
                return;

            Player player = (Player) event.getWhoClicked();
            EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());

            if (this.player.equals(player)) {
                event.setCancelled(true);

                if (event.getInventory() == null)
                    return;
                if (event.getClickedInventory() == null)
                    return;
                if (event.getCurrentItem() == null)
                    return;
                if (event.getCurrentItem().getType().equals(Material.AIR))
                    return;
                if (!(event.getSlotType().equals(InventoryType.SlotType.CONTAINER)))
                    return;
                if ((event.getCurrentItem() == null) || (!(event.getCurrentItem().hasItemMeta()))
                        || (event.getCurrentItem().getItemMeta() == null)) {
                    return;
                }

                // Intéraction avec des Items Aléatoire
                if ((event.getCurrentItem().getType().equals(Material.STAINED_GLASS_PANE))
                        || (event.getCurrentItem().getType().equals(Material.EXPLOSIVE_MINECART)))
                    return;

                // Fermer le Menu de ZPLAY
                if (event.getCurrentItem().getType().equals(Material.ARROW)) {
                    player.closeInventory();
                    return;
                }

                // Vérifier si le Joueur est déjà sur le Serveur
                if (event.getCurrentItem().getType().equals(Material.BARRIER)) {
                    player.sendMessage(PREFIX + ChatColor.RED + "Vous êtes déjà connecté sur ce serveur du joueur.");
                    player.closeInventory();
                    return;
                }

                // Effectuer la Téléportation vers le Serveur
                if (event.getCurrentItem().getType().equals(Material.SKULL)) {

                    // Mise en place du ZMOD
                    if (!(endaPlayer.isModeModeration()))
                        player.getServer().dispatchCommand(player, "zmod");

                    ReportManager reportManager = new ReportManager();
                    // Effectuer la Téléportation du Joueur
                    String playerName = event.getCurrentItem().getItemMeta().getDisplayName().replace("§b", "");

                    player.getServer().dispatchCommand(player, "ztp " + event.getCurrentItem().getItemMeta().getDisplayName());
                    player.sendMessage(Messages.ENDARIUM_PREFIX + ChatColor.WHITE + ChatColor.WHITE
                            + "Téléportation en cours vers : " + ChatColor.AQUA + playerName);
                    player.sendMessage(event.getCurrentItem().getItemMeta().getLore().toString());
                    player.closeInventory();
                }
            }
        }

        @Override
        public void closeMenu(InventoryCloseEvent e) {
        }

        @Override
        public void setMenuItems() {
            for (int i = 45; i < 54; i++) {
                getInventory().setItem(i,
                        new ItemFactory(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 8)).withName("§0").done());
            }

            ReportManager reportManager = new ReportManager();

            // Afficher la liste des Serveurs
            for (Report report : reportManager.getReports()) {
                if (report != null) {

                    ItemStack itemStack = new ItemStack(CustomSkull.getPlayerSkull(UUIDEndaFetcher.getPlayerName(report.getReporterUUID())));
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setDisplayName("§b" + UUIDEndaFetcher.getPlayerName(report.getReporterUUID()));
                    System.out.println(report);

                    // Gestion de la Description
                    List<String> lores = new ArrayList<>();
                    lores.add("");
                    lores.add(ChatColor.WHITE + "Le reporteur : " + ChatColor.YELLOW + UUIDEndaFetcher.getPlayerName(report.getReportedPlayerUUID()));
                    lores.add(ChatColor.WHITE + "Joueur Report : " + ChatColor.RED + UUIDEndaFetcher.getPlayerName(report.getReporterUUID()));
                    lores.add(ChatColor.WHITE + "Motif : " + ChatColor.AQUA + report.getReason());
                    lores.add("");
                    itemMeta.setLore(lores);

                    itemStack.setItemMeta(itemMeta);
                    reportItems.add(itemStack);
                }
            }

            setItemList(reportItems);
        }

        @Override
        public ItemStack previousPage() {
            return new ItemFactory(Material.PAPER).withAmount(page - 1 < 0 ? page : page - 1).withName("Page Précédente").done();
        }

        @Override
        public ItemStack nextPage() {
            return new ItemFactory(Material.PAPER).withAmount(page + 1).withName("Page Suivante").done();
        }

        @Override
        public ItemStack closeMenu() {
            return new ItemFactory(Material.ARROW).withName(ChatColor.WHITE + "Fermer").done();
        }
    }
}
