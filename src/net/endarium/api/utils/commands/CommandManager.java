package net.endarium.api.utils.commands;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.endarium.api.players.login.LoginManager;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.SimplePluginManager;

import net.endarium.api.minecraft.EndariumBukkit;
import net.endarium.api.players.EndaPlayer;
import net.endarium.api.utils.Messages;
import net.endarium.api.utils.commands.Command.SenderType;
import net.md_5.bungee.api.ChatColor;

public class CommandManager implements CommandExecutor {

	private CommandMap commandMap;
	private Map<String, Pair<Object, Method>> commands = new HashMap<>();

	/**
	 * Constructeur du CommandManager.
	 */
	public CommandManager() {
		SimplePluginManager manager = (SimplePluginManager) Bukkit.getPluginManager();
		Field field;
		try {
			field = SimplePluginManager.class.getDeclaredField("commandMap");
			field.setAccessible(true);
			commandMap = (CommandMap) field.get(manager);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Methode de construction de la Commande.
	 */
	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {

		for (int i = args.length; i >= 0; i--) {
			StringBuilder buffer = new StringBuilder();
			buffer.append(label.toLowerCase());
			for (int x = 0; x < i; x++) {
				buffer.append("." + args[x].toLowerCase());
			}

			String cmdLabel = buffer.toString();
			if (commands.containsKey(cmdLabel)) {
				Method method = commands.get(cmdLabel).getRight();
				Object methodObject = commands.get(cmdLabel).getLeft();
				return handleCommand(i, sender, method, methodObject, args);
			}
		}
		return false;
	}

	/**
	 * Sécurisation de la Commande / Contrôle.
	 * 
	 * @param i
	 * @param sender
	 * @param method
	 * @param methodObject
	 * @param args
	 * @return
	 */
	private boolean handleCommand(int i, CommandSender sender, Method method, Object methodObject, String[] args) {
		Command command = method.getAnnotation(Command.class);

		if ((command.senderType().equals(SenderType.ONLY_PLAYER)) && (!(sender instanceof Player))) {
			return false;
		}

		Object[] params = new Object[method.getParameterTypes().length];

		// Creation of the arguments of the method
		if (params.length >= 1) {
			if (!command.senderType().getAssociatedType().isInstance(sender)) {
				sender.sendMessage(ChatColor.RED + "Error, only the CONSOLE can use this Command.");
				return true;
			}

			params[0] = command.senderType().getAssociatedType().cast(sender);

			if (params.length >= 2) {
				String[] newArgs = new String[Math.max(0, args.length - i)];
				if (newArgs.length != 0)
					System.arraycopy(args, i, newArgs, 0, newArgs.length);
				params[1] = newArgs;
			}
		}

		if (sender instanceof Player) {
			EndaPlayer endaPlayer = EndaPlayer.get(((Player) sender).getUniqueId());

			boolean allowed = false;



			// Gérer le Système de Permission Interne
			for (String permission : command.permission()) {
				if (endaPlayer.hasPermission(permission))
					allowed = true;
			}

			if ((!(allowed)) && (command.minimumRank().getPower() > endaPlayer.getRank().getPower())) {
				sender.sendMessage(Messages.UNKNOW_COMMAND);
				return true;
			}
		}

		try {
			method.invoke(methodObject, params);
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Enregistrement des différentes Commands.
	 * 
	 * @param obj
	 */
	public void registercommand(Object obj) {
		for (Method method : obj.getClass().getDeclaredMethods()) {
			if (method.isAnnotationPresent(Command.class)) {
				Command cmd = method.getAnnotation(Command.class);
				Validate.notNull(cmd.minimumRank());
				Validate.notNull(cmd.name());
				Validate.notEmpty(cmd.name());
				Validate.notEmpty(cmd.name());
				Class<?> clz = cmd.senderType().getAssociatedType();
				Validate.isTrue(method.getParameterTypes().length > 0 && method.getParameterTypes()[0].equals(clz),
						"The first argument of the command " + cmd.name() + "  must be of the type "
								+ clz.getSimpleName());

				for (String cmdName : cmd.name()) {
					commands.put(cmdName, new Pair<>(obj, method));
					String label = cmdName.contains(".") ? cmdName.split("[.]")[0] : cmdName;
					if (commandMap.getCommand(label) == null) {
						org.bukkit.command.Command bukkitCmd = new CustomCommand(label, this);
						commandMap.register(EndariumBukkit.getPlugin().getName(), bukkitCmd);
					}
				}
			}
		}
	}
}
