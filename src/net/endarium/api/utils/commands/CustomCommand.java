package net.endarium.api.utils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Gestionnaire des Commandes customisées.
 */
public class CustomCommand extends Command {

	private CommandExecutor handle;

	public CustomCommand(String name, CommandExecutor handle) {
		super(name);
		this.handle = handle;
	}

	@Override
	public boolean execute(CommandSender arg0, String arg1, String[] arg2) {
		return handle.onCommand(arg0, this, arg1, arg2);
	}
}