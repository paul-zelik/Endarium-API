package net.endarium.api.players.rank.permissions;

public enum Permission {

	DEFAULT("endarium.default"), COMMAND_GAMESTART("endarium.command.gamestart"), MODS_MUTE("endarium.mods.mute"),
	HOST_START("endarium.host.start"),
	MODS_UNMUTE("endarium.mods.unmute"), MODS_UNBAN("endarium.mods.unban"), SUPER_MODO("endarium.mods.*");

	private String permission;

	/**
	 * Gestion des permissions usuelles.
	 * 
	 * @param permission
	 */
	private Permission(String permission) {
		this.permission = permission;
	}

	public String getPermission() {
		return permission;
	}
}