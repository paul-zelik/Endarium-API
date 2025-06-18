package net.endarium.api.utils.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.endarium.api.players.rank.Rank;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

	public String[] name();

	public Rank minimumRank() default Rank.ADMINISTRATOR;

	public SenderType senderType() default SenderType.ONLY_PLAYER;

	public String[] permission() default {};

	public enum SenderType {

		ONLY_PLAYER(Player.class), ONLY_CONSOLE(ConsoleCommandSender.class), PLAYER_AND_CONSOLE(CommandSender.class);

		Class<?> associated;

		private SenderType(Class<?> associated) {
			this.associated = associated;
		}

		public Class<?> getAssociatedType() {
			return associated;
		}
	}
}