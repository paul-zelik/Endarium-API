package net.endarium.api.minecraft.commands.administrator;

import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.login.LoginManager;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.players.wallets.Currency;
import net.endarium.api.utils.commands.Command;
import net.endarium.api.utils.mojang.UUIDEndaFetcher;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static java.lang.Integer.parseInt;

public class CoinsCommand{

    @Command(name = {"zcoins"}, minimumRank= Rank.ADMINISTRATOR, senderType = Command.SenderType.PLAYER_AND_CONSOLE)
    public void onCommand(CommandSender sender, String[] args) {

        LoginManager loginManager = new LoginManager();
        if (sender instanceof Player) {
            if (!(loginManager.isLogged(((Player) sender).getUniqueId()))) {
                return;
            }
        }


        if (args.length == 4) {
            if (args[1].equalsIgnoreCase("token")) {
                EndaPlayer endaPlayer = new EndaPlayer(UUIDEndaFetcher.getPlayerUUID(args[0]));
                if (args[2].equalsIgnoreCase("add")) {
                    endaPlayer.setCurrency(Currency.TOKENS, endaPlayer.getCurrency(Currency.TOKENS) + parseInt(args[4]));
                    sender.sendMessage("L'utilisateur a ressue les tokens");
                } else if (args[2].equalsIgnoreCase("take")) {
                    if (endaPlayer.getCurrency(Currency.TOKENS) > parseInt(args[4])) {
                        endaPlayer.setCurrency(Currency.TOKENS, endaPlayer.getCurrency(Currency.TOKENS) - parseInt(args[4]));
                        sender.sendMessage("Action réussie");
                    } else {
                        sender.sendMessage("Le joueur n'a pas asser de token");
                    }
                } else {
                    sendHelp(sender);
                }
            } else if (args[1].equalsIgnoreCase("coins")) {
                EndaPlayer endaPlayer = new EndaPlayer(UUIDEndaFetcher.getPlayerUUID(args[0]));
                if (args[2].equalsIgnoreCase("add")) {
                    endaPlayer.setCurrency(Currency.COINS, endaPlayer.getCurrency(Currency.COINS) + parseInt(args[4]));
                    sender.sendMessage("L'utilisateur a ressue les coins");
                } else if (args[2].equalsIgnoreCase("take")) {
                    if (endaPlayer.getCurrency(Currency.COINS) > parseInt(args[4])) {
                        endaPlayer.setCurrency(Currency.COINS, endaPlayer.getCurrency(Currency.COINS) - parseInt(args[4]));
                        sender.sendMessage("Action réussie");
                    } else {
                        sender.sendMessage("Le joueur n'a pas asser de coins");
                    }
                } else {
                    sendHelp(sender);
                }
            } else {
                sendHelp(sender);
            }
        } else {
            sendHelp(sender);
        }

    }

    private void sendHelp (CommandSender sender) {
        sender.sendMessage("// Message Interdit au staff");
        sender.sendMessage("/zcoins [player] [token/coins] [add/take] [coins]");


    }

}
