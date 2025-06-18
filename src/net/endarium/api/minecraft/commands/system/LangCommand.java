package net.endarium.api.minecraft.commands.system;

import net.endarium.api.players.EndaPlayer;
import net.endarium.api.players.language.Languages;
import net.endarium.api.players.login.LoginManager;
import net.endarium.api.players.rank.Rank;
import net.endarium.api.utils.Messages;
import net.endarium.api.utils.commands.Command;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class LangCommand {

    @Command(name = { "lang", "lague", "language",
            "langage" }, minimumRank = Rank.DEFAULT, senderType = Command.SenderType.ONLY_PLAYER)
    public void onCommand(Player player, String[] args) {

        LoginManager loginManager = new LoginManager();
        if (!(loginManager.isLogged(player.getUniqueId()))) {
            return;
        }

        // Envoyer les Help
        if (args.length != 1) {
            this.sendHelp(player);
            return;
        }

        String langName = args[0];
        Languages languages = Languages.getLangueByZone(langName);

        // Vérifier si la Langue existe
        if (languages == null) {
            player.sendMessage(Messages.ENDARIUM_PREFIX + ChatColor.RED + "La langue '" + langName + "' n'existe pas.");
            player.sendMessage(ChatColor.GRAY + "Les langages disponibles : Français, English, Espanol.");
            return;
        }

        // Effectuer le changement de Langue a l'Utilisateur
        EndaPlayer endaPlayer = EndaPlayer.get(player.getUniqueId());
        endaPlayer.setLanguages(languages);
        player.sendMessage(Messages.ENDARIUM_PREFIX + ChatColor.WHITE + "Changement de langage en " + ChatColor.YELLOW
                + languages.getName() + ChatColor.WHITE + ".");

        return;
    }

    /**
     * Message d'Aide de la Commande.
     */
    private void sendHelp(Player player) {
        player.sendMessage("");
        player.sendMessage(" " + ChatColor.GRAY + "» " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Aide"
                + ChatColor.WHITE + "│ " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Langues");
        player.sendMessage("");
        player.sendMessage(" " + ChatColor.GRAY + "" + ChatColor.BOLD + "■ " + ChatColor.GOLD + "/lang [langage] "
                + ChatColor.WHITE + "» " + ChatColor.AQUA + "Changer votre Langue sur le serveur.");
        player.sendMessage(ChatColor.GRAY + "Les langages disponibles : Français, English, Espanol.");
        player.sendMessage("");
    }
}
