package net.endarium.api.players.party;

import java.util.ArrayList;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Party {
	
	private static ArrayList<Party> PartyPlayer = new ArrayList<>();
	private ArrayList<ProxiedPlayer> players = new ArrayList<>();
	private String name;
	private ProxiedPlayer createur;
	
	public Party(ProxiedPlayer createur){
		this.createur = createur;
		this.name = createur.getName();
		PartyPlayer.add(this);
		this.players.add(createur);
	}
	
	public void addPlayer(ProxiedPlayer pp){
		this.players.add(pp);
	}
	
	public void removePlayer(ProxiedPlayer pp){
		this.players.remove(pp);
	}
	
	public void removeParty(){
		for(ProxiedPlayer pp : this.players){
			pp.sendMessage(new TextComponent("�cVotre party vient d'�tre dissoute !"));
		}
		this.players.clear();
		PartyPlayer.remove(this);
	}
	
	public ArrayList<ProxiedPlayer> getPlayers(){
		return this.players;
	}
	
	public String getName(){
		return this.name;
	}
	
	public ProxiedPlayer getCreateur(){
		return this.createur;
	}
	
	public boolean isCreator(ProxiedPlayer pp){
		return pp == this.createur;
	}
	
	public static Party getParty(ProxiedPlayer pp){
		for(Party p : PartyPlayer){
			if(p.getPlayers().contains(pp)){
				return p;
			}
		}
		return null;
	}
	
	public static boolean hasParty(ProxiedPlayer pp){
		return getParty(pp) != null;
	}

}
