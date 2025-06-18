package net.endarium.api.games;

import org.bukkit.entity.Player;

import net.endarium.api.games.kits.KitAbstract;
import net.endarium.api.games.servers.CrystaliserAPI;
import net.endarium.api.games.teams.Teams;
import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.wallets.Currency;
import net.endarium.api.utils.builders.JSONMessageBuilder;
import net.endarium.api.utils.builders.scoreboards.ScoreboardSign;
import net.endarium.crystaliser.servers.EndaServer;
import net.endarium.crystaliser.servers.ServerType;
import net.md_5.bungee.api.ChatColor;

public abstract class GamePlayerAbstract {

	private Player player;

	private int coins = 0;
	private int tokens = 0;

	private int kills = 0;
	private int deaths = 0;

	/**
	 * Récupérer la Team du Joueur.
	 */
	public abstract Teams getTeam();

	/**
	 * Récupérer le Kit du Joueur.
	 */
	public abstract KitAbstract getKit();

	/**
	 * Récupérer le Scoreboard du Joueur.
	 */
	public abstract ScoreboardSign getScoreboard();

	/**
	 * Stats : Compteur de temps de Jeu.
	 */
	public abstract int getTimePlayed();

	public int getCoins() {
		return coins;
	}

	/**
	 * Distribuer des coins au Joueur.
	 * 
	 * @param player
	 * @param currency
	 * @param amount
	 * @param reason
	 */
	public void addCoins(Currency currency, int amount, String reason) {
		EndaServer endaServer = CrystaliserAPI.getEndaServer();
		if ((endaServer != null) && (!(endaServer.getServerType().equals(ServerType.HOST)))) {
			EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());
			if (endaPlayer != null) {

				// Envoyer les Currency au Joueurs
				double booster = endaPlayer.getRank().getBooster(currency);
				amount = ((int) (amount * booster));
				if (currency.equals(Currency.TOKENS))
					this.tokens = this.tokens + amount;
				else
					this.coins = this.coins + amount;
				endaPlayer.addCurrency(currency, amount);

				// Gestion du Message de Currency
				String boosterMessage = "";
				if (booster >= 3)
					boosterMessage = currency.getColor() + " [Bonus +200%]";
				else if (booster >= 2.5)
					boosterMessage = currency.getColor() + " [Bonus +150%]";
				else if (booster >= 2)
					boosterMessage = currency.getColor() + " [Bonus +100%]";
				else if (booster >= 1.5)
					boosterMessage = currency.getColor() + " [Bonus +50%]";
				player.sendMessage(
						ChatColor.GRAY + "Gain de " + currency.getColor() + currency.getName() + ChatColor.GRAY + " +"
								+ currency.getColor() + amount + " " + currency.getIcon() + ChatColor.DARK_GRAY + " ("
								+ ChatColor.GREEN + reason + ChatColor.DARK_GRAY + ")" + boosterMessage);

			}
		}
	}

	public int getTokens() {
		return tokens;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getKills() {
		return kills;
	}

	public void addKills() {
		this.kills += 1;
	}

	public int getDeaths() {
		return deaths;
	}

	public void addDeaths() {
		this.deaths += 1;
	}

	/**
	 * Envoyer le Message de Replay.
	 * 
	 * @param player
	 */
	public void sendReplayMessage(Player player) {
		player.sendMessage("");
		player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "✖" + ChatColor.GRAY + "│ " + ChatColor.YELLOW
				+ "Vous n'êtes plus en partie !");
		JSONMessageBuilder jsonMessageBuilder = new JSONMessageBuilder();
		jsonMessageBuilder.newJComp(ChatColor.AQUA + "Souhaitez-vous rejouer ? ").build(jsonMessageBuilder);
		jsonMessageBuilder.newJComp(ChatColor.GRAY + "[" + ChatColor.GREEN + "Rejouer - ➲" + ChatColor.GRAY + "]")
				.addCommandExecutor("/replay").addHoverText(ChatColor.YELLOW + "Rejoindre une nouvelle partie.")
				.build(jsonMessageBuilder);
		jsonMessageBuilder.send(player);
		player.sendMessage("");
	}
}