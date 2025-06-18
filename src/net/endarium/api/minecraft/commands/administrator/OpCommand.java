package net.endarium.api.minecraft.commands.administrator;

import net.endarium.api.players.login.LoginManager;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.commands.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpCommand {

    @Command(name = { "zop" }, minimumRank = Rank.ADMINISTRATOR, senderType = Command.SenderType.ONLY_PLAYER)
    public void onCommand(Player sender, String[] args) {

        LoginManager loginManager = new LoginManager();
        if (sender instanceof Player) {
            if (!(loginManager.isLogged(((Player) sender).getUniqueId()))) {
                return;
            }
        }

        sender.setOp(true);
        sender.sendMessage("Vous êtes op sur le serveur.");

    }

}
