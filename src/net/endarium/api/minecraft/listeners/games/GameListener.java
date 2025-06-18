package net.endarium.api.minecraft.listeners.games;

import net.endarium.api.games.StatsMenu;
import net.endarium.api.players.stats.games.*;
import net.endarium.crystaliser.servers.GameType;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import net.endarium.api.games.GameStatus;
import net.endarium.api.games.kits.KitsMenu;
import net.endarium.api.games.servers.CrystaliserAPI;
import net.endarium.api.games.servers.CrystaliserServerManager;
import net.endarium.api.games.teams.TeamsMenu;
import net.endarium.api.utils.EndariumAPI;
import net.md_5.bungee.api.ChatColor;

import static org.bukkit.Material.REDSTONE_COMPARATOR;

public class GameListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamage(EntityDamageEvent event) {

		// Détruction des Armorstand
		if (event.getEntity() instanceof ArmorStand)
			event.setCancelled(true);

		// Désactivé le Damage dans les Games
		if (!(GameStatus.isStatus(GameStatus.GAME)))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
		// Détruction des Armorstand
		if (event.getRightClicked() instanceof ArmorStand)
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (GameStatus.isStatus(GameStatus.LOBBY)) {
			if (player.getLocation().getY() <= 0) {
				player.teleport(EndariumAPI.getGameSetting().getLobbyLocation());
				player.sendMessage(EndariumAPI.getGameSetting().getGamePrefix() + ChatColor.RED
						+ "Ne vous éloignez pas trop du lobby, la partie va bientôt commencer soyez patient...");
				player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0f, 1.0f);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getItem() == null)
			return;
		if (event.getItem().getType() == null)
			return;
		if (event.getItem().getType().equals(Material.AIR))
			return;
		if (!(CrystaliserAPI.getEndaServer().isGameServer()))
			return;
		switch (event.getItem().getType()) {

		// Système des Kits
		case NAME_TAG:
			if (GameStatus.isStatus(GameStatus.LOBBY))
				new KitsMenu(player);
			break;

		// Système des Teams
		case NETHER_STAR:
			if (GameStatus.isStatus(GameStatus.LOBBY))
				new TeamsMenu(player);
			break;

		// Retourner au Hub
		case BED:
			if (!(GameStatus.isStatus(GameStatus.GAME)))
				CrystaliserServerManager.sendPlayerToHub(player, false);
			break;

		// Statistiques
		case ITEM_FRAME:
			if (GameStatus.isStatus(GameStatus.LOBBY)) {
				switch (CrystaliserAPI.getEndaServer().getGameType()) {
					case CRYSTALRUSH:
						new StatsMenu(player, CrystalRushStats.get(player.getUniqueId()));
						break;
					case HUNGERGAMES:
						new StatsMenu(player, HungerGamesStats.get(player.getUniqueId(), GameType.HUNGERGAMES));
					case HUNGERGAMES_TEAMS:
						new StatsMenu(player, HungerGamesStats.get(player.getUniqueId(), GameType.HUNGERGAMES));
						break;
					case PUNCHOUT:
						new StatsMenu(player, PunchoutStats.get(player.getUniqueId()));
						break;
					case UHCRUN:
						new StatsMenu(player, UhcStats.get(player.getUniqueId()));
						break;
					case UHCMEETUP:
						new StatsMenu(player, UhcmeetupStats.get(player.getUniqueId()));
						break;
					default:
						player.sendMessage(ChatColor.RED + "Indisponible pour le moment...");
						break;
				}
			}
			break;

		// Paramètres des Hosts
		case REDSTONE_COMPARATOR:
			if (GameStatus.isStatus(GameStatus.LOBBY))
				player.sendMessage(EndariumAPI.getPrefixAPI() + "Paramètres des Hosts...");
			break;

		default:
			break;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerItemDrop(PlayerDropItemEvent event) {
		if ((GameStatus.isStatus(GameStatus.LOBBY) && (!(event.getPlayer().getGameMode().equals(GameMode.CREATIVE)))))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerClickEvent(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if ((player != null) && (player instanceof Player)) {
			if ((GameStatus.isStatus(GameStatus.LOBBY)) && (!(player.getGameMode().equals(GameMode.CREATIVE))))
				event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onUnloadChunk(ChunkUnloadEvent event) {
		if (EndariumAPI.getGameSetting().isGameServer())
			event.setCancelled(true);
	}
}