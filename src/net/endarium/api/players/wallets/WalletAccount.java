package net.endarium.api.players.wallets;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.endarium.api.EndariumCommons;

public class WalletAccount {

	private Map<Currency, Integer> wallets = new HashMap<Currency, Integer>();

	private UUID uuid;

	/**
	 * WalletAccount d'un Joueur.
	 * 
	 * @param uuid
	 */
	public WalletAccount(UUID uuid) {
		this.uuid = uuid;
		for (Currency currency : Currency.values())
			wallets.put(currency, EndariumCommons.getInstance().getEndariumEntities().getWalletsManager()
					.getCurrency(uuid, currency));
	}

	public UUID getUUID() {
		return uuid;
	}

	/**
	 * Ajouter des Currency à un Joueur.
	 * 
	 * @param currency
	 * @param value
	 */
	public WalletAccount addCurrencyWallet(Currency currency, int value) {
		this.setCurrencyWallet(currency, this.getCurrencyWallet(currency) + value);
		return this;
	}

	/**
	 * Supprimer des Currency à un Joueur.
	 * 
	 * @param currency
	 * @param value
	 */
	public WalletAccount removeCurrencyWallet(Currency currency, int value) {
		if (this.getCurrencyWallet(currency) - value <= 0)
			this.setCurrencyWallet(currency, 0);
		else
			this.setCurrencyWallet(currency, this.getCurrencyWallet(currency) - value);
		return this;
	}

	/**
	 * Définir des Currency à un Joueur.
	 * 
	 * @param currency
	 * @param value
	 */
	public WalletAccount setCurrencyWallet(Currency currency, int value) {
		if (wallets.containsKey(currency))
			wallets.remove(currency);
		wallets.put(currency, value);
		return this;
	}

	/**
	 * Récupérer les Currency d'un Joueur.
	 * 
	 * @param currency
	 */
	public Integer getCurrencyWallet(Currency currency) {
		if (wallets.containsKey(currency))
			return wallets.get(currency);
		return 0;
	}
}