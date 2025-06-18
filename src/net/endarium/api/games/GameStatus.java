package net.endarium.api.games;

public enum GameStatus {

	LOBBY(true), GAME(false), FINISH(false);

	private boolean canJoin;
	private static GameStatus currentStatus;

	/**
	 * Gestion des Status des Games.
	 * 
	 * @param canJoin
	 */
	private GameStatus(boolean canJoin) {
		this.canJoin = canJoin;
	}

	public boolean canJoin() {
		return this.canJoin;
	}

	public static void setStatus(GameStatus status) {
		currentStatus = status;
	}

	public static boolean isStatus(GameStatus status) {
		return currentStatus == status;
	}

	public static GameStatus getStatus() {
		return currentStatus;
	}
}