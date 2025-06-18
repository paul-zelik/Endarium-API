package net.endarium.api.bungeecord.commands.staff;

import net.endarium.api.bungeecord.EndariumBungeeCord;
import net.endarium.api.bungeecord.channels.RedisBungeeChannel;
import net.endarium.api.players.EndaPlayer;
import net.endarium.api.utils.Messages;
import net.endarium.api.utils.mojang.UUIDEndaFetcher;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.event.EventHandler;

public class AntibotCommand extends Command {

    public AntibotCommand() {
        super("antibot", null, new String[] { "bot", "endabot" });
    }

    @SuppressWarnings("deprecation")
    @Override
    public void execute(CommandSender sender, String[] args) {
        EndaPlayer endaPlayer = EndaPlayer.get(UUIDEndaFetcher.getPlayerUUID(sender.getName()));

        if (!(sender instanceof ProxiedPlayer))
            return;

        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;

        if (endaPlayer.isAdministrator()) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("on")) {
                    proxiedPlayer.sendMessage(Messages.ENDARIUM_PREFIX + ChatColor.YELLOW + " Antibot Activée");
                    EndariumBungeeCord.getInstance().setAntibotstatue(true);
                } else if (args[0].equalsIgnoreCase("off")) {
                    proxiedPlayer.sendMessage(Messages.ENDARIUM_PREFIX + ChatColor.YELLOW + " Antibot Désactivée");
                    EndariumBungeeCord.getInstance().setAntibotstatue(false);
                } else {
                    sendHelp(proxiedPlayer);
                }
            }
        } else {
            sender.sendMessage("Commande inconnue.");
        }
    }

    private void sendHelp(ProxiedPlayer proxiedPlayer) {
        proxiedPlayer.sendMessage("");
        proxiedPlayer.sendMessage(" " + ChatColor.GRAY + "» " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Aide"
                + ChatColor.WHITE + "│ " + ChatColor.YELLOW + "" + ChatColor.BOLD + "AntiBot");
        proxiedPlayer.sendMessage("");
        proxiedPlayer.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD
                + "/antibot [on:off] " + ChatColor.WHITE + "» " + ChatColor.AQUA + "Activé ou désactivé l'antibot");
        proxiedPlayer.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD);
    }
}


