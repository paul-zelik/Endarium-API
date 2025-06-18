package net.endarium.api;

import net.endarium.api.players.box.BoxManager;
import net.endarium.api.players.friends.FriendsManager;
import net.endarium.api.players.language.LangManager;
import net.endarium.api.players.login.LoginManager;
import net.endarium.api.players.login.PreniumManager;
import net.endarium.api.players.moderation.ban.BanManager;
import net.endarium.api.players.moderation.mute.MuteManager;
import net.endarium.api.players.others.AntiBot;
import net.endarium.api.players.others.SponsorshipManager;
import net.endarium.api.players.rank.RankManager;
import net.endarium.api.players.rank.permissions.PermissionManager;
import net.endarium.api.players.report.ReportManager;
import net.endarium.api.players.wallets.WalletsManager;

public class EndariumEntities {

	private RankManager rankManager;
	private PermissionManager permissionManager;
	private WalletsManager walletsManager;



	private FriendsManager friendsManager;
	private LangManager langManager;

	private PreniumManager preniumManager;

	private SponsorshipManager sponsorshipManager;

	private BanManager banManager;

	private ReportManager reportManager;
	private MuteManager muteManager;
	private LoginManager loginManager;
	private BoxManager boxManager;

	private AntiBot antiBot;

	/**
	 * Récupérer des Entity de Endarium.
	 */
	public EndariumEntities() {
		this.rankManager = new RankManager();
		this.loginManager = new LoginManager();
		this.preniumManager = new PreniumManager();
		this.permissionManager = new PermissionManager();
		this.walletsManager = new WalletsManager();
		this.antiBot = new AntiBot();
		this.friendsManager = new FriendsManager();
		this.boxManager = new BoxManager();
		this.langManager = new LangManager();
		this.banManager = new BanManager();
		this.muteManager = new MuteManager();
		this.reportManager = new ReportManager();
		this.sponsorshipManager = new SponsorshipManager();
	}

	/**
	 * Entity : PlayerRank
	 */
	public RankManager getRankManager() {
		return rankManager;
	}

	public AntiBot getAntiBot() {return antiBot;}

	public LoginManager getLoginManager() {
		return loginManager;
	}

	public PreniumManager getPreniumManager() {return preniumManager;}

	/**
	 * Entity : PlayerPermissions
	 */
	public PermissionManager getPermissionManager() {
		return permissionManager;
	}

	public BoxManager getBoxManager() {return boxManager;}
	/**
	 * Entity : ReportManger
	 */
	public ReportManager getReportManager() {
		return reportManager;
	}

	/**
	 * Entity : PlayerWallets
	 */
	public WalletsManager getWalletsManager() {
		return walletsManager;
	}

	/**
	 * Entity : PlayerFriends
	 */
	public FriendsManager getFriendsManager() {
		return friendsManager;
	}

	/**
	 * Entity : PlayerLang
	 */
	public LangManager getLangManager() {
		return langManager;
	}


	/**
	 * Entity : PlayerBans
	 */
	public BanManager getBanManager() {
		return banManager;
	}

	/**
	 * Entity : PlayerMutes
	 */
	public MuteManager getMuteManager() {
		return muteManager;
	}

	public SponsorshipManager getSponsorshipManager() {
		return sponsorshipManager;
	}
}