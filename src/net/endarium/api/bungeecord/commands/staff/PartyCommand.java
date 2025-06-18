package net.endarium.api.bungeecord.commands.staff;

import java.util.HashMap;

import net.endarium.api.bungeecord.EndariumBungeeCord;
import net.endarium.api.players.party.Party;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PartyCommand extends Command{
	
	private HashMap <String, Party> playerParty = new HashMap<String, Party>();

	public PartyCommand(String name) {
		super(name);
		
	}
	
	public void execute(CommandSender sender, String[] args){
		if(sender instanceof ProxiedPlayer) {
			
			ProxiedPlayer pp = (ProxiedPlayer) sender;
			
			if(args.length == 0){
				pp.sendMessage(new TextComponent("�6-- Gestion des groupes --"));
				pp.sendMessage(new TextComponent("�e/party create �7> Cr�er une party"));
				pp.sendMessage(new TextComponent("�e/party have �7> Voir dans quelle party vous �tes"));
				pp.sendMessage(new TextComponent("�e/party invite <joueur> �7> Inviter un joueur"));
				pp.sendMessage(new TextComponent("�e/party accept �7> Accepter une party"));
				pp.sendMessage(new TextComponent("�e/party leave �7> Quitter une party"));
				pp.sendMessage(new TextComponent("�e/party kick <joueur> �7> Ejecter un joueur de la party"));
				pp.sendMessage(new TextComponent("�e/party disband �7> Fermer une party"));
				pp.sendMessage(new TextComponent("�e/party chat �7> Parler dans une party"));
				pp.sendMessage(new TextComponent("�e/party tp �7> Se t�l�porter au chef d'une party"));
				pp.sendMessage(new TextComponent("�e/party list �7> Lister les joueurs d'une party"));
			}
			
			if(args.length == 1){
				
				if(args[0].equalsIgnoreCase("create")){
					if(Party.hasParty(pp)){
						pp.sendMessage(new TextComponent("�7[�9Party�7] �eVous avez d�j� une party !"));
					}else{
						new Party(pp);
						pp.sendMessage(new TextComponent("�7[�9Party�7] �eVous venez de cr�er une party !"));
					}
				}
				
				if(args[0].equalsIgnoreCase("have")){
					if(Party.hasParty(pp)){
						pp.sendMessage(new TextComponent("�7[�9Party�7] �7Vous �tes dans la party de : �e" + Party.getParty(pp).getCreateur().getName()));
					}else{
						pp.sendMessage(new TextComponent("�7[�9Party�7] �cVous n'�tes dans aucune party !"));
					}
				}
				
				if(args[0].equalsIgnoreCase("list")){
					if(Party.hasParty(pp)){
						pp.sendMessage(new TextComponent("�7-- Joueurs dans la Party --"));
						for(ProxiedPlayer pp2 : Party.getParty(pp).getPlayers()){
							pp.sendMessage(new TextComponent("�e" + pp2.getName()));
						}
					}else{
						pp.sendMessage(new TextComponent("�7[�9Party�7] �cVous n'avez pas de party !"));
					}
				}
				
				if(args[0].equalsIgnoreCase("leave")){
					if(Party.hasParty(pp)){
						if(Party.getParty(pp).isCreator(pp)){
							pp.sendMessage(new TextComponent("�7[�9Party�7] �cVous devez utiliser : /party disband !"));
						}else{
							Party.getParty(pp).removePlayer(pp);
							pp.sendMessage(new TextComponent("�7[�9Party�7] �eVous venez de quitter la party !"));
						}
					}else{
						pp.sendMessage(new TextComponent("�7[�9Party�7] �cVous n'avez pas de party !"));
					}
				}
				
				if(args[0].equalsIgnoreCase("disband")){
					if(Party.hasParty(pp)){
						if(Party.getParty(pp).isCreator(pp)){
							Party.getParty(pp).removeParty();
						}else{
							pp.sendMessage(new TextComponent("�7[�9Party�7] �cVous n'�tes pas le cr�ateur de la party !"));
						}
					}else{
						pp.sendMessage(new TextComponent("�7[�9Party�7] �cVous n'avez pas de party !"));
					}
				}
				
				if(args[0].equalsIgnoreCase("tp")){
					if(Party.hasParty(pp)){
						if(Party.getParty(pp).isCreator(pp)){
							pp.sendMessage(new TextComponent("�7[�9Party�7] �cVous ne pouvez pas vous t�l�porter � vous m�me !"));
						}else{
							if(pp.getServer().getInfo() == Party.getParty(pp).getCreateur().getServer().getInfo()){
								pp.sendMessage(new TextComponent("�7[�9Party�7] �bVous �tes d�j� sur ce serveur !"));
							}else{
								pp.connect(Party.getParty(pp).getCreateur().getServer().getInfo());
							}
						}
					}else{
						pp.sendMessage(new TextComponent("�7[�9Party�7] �cVous n'�tes dans aucune party !"));
					}
				}
				
				if(args[0].equalsIgnoreCase("accept")){
					if(this.playerParty.containsKey(pp.getName())){
						if(Party.hasParty(pp)){
							pp.sendMessage(new TextComponent("�7[�9Party�7] �cVous �tes d�j� dans une party !"));
						}else{
							((Party)this.playerParty.get(pp.getName())).addPlayer(pp);
							pp.sendMessage(new TextComponent("�7[�9Party�7] �7Vous avez rejoint la party de �e" + Party.getParty(pp).getName()));
						}
					}
				}else{
					pp.sendMessage(new TextComponent("�7[�9Party�7] �cVous n'�tes invit� dans aucune party !"));
				}
			}
			
			if(args.length == 2){
				if(args[0].equalsIgnoreCase("invite")){
					if(Party.hasParty(pp)){
						if(Party.getParty(pp).isCreator(pp)){
							try{
								if(ProxyServer.getInstance().getPlayer(args[1]) == null){
									pp.sendMessage(new TextComponent("�7[�9Party�7] �cLe joueur n'est pas en ligne !"));
									return;
								}
								
								ProxiedPlayer pp2 = EndariumBungeeCord.getInstance().getProxy().getPlayer(args[0]);
								
								if(!Party.hasParty(pp2)){
									this.playerParty.put(pp2.getName(), Party.getParty(pp));
									pp.sendMessage(new TextComponent("�7[�9Party�7] �7Vous venez d'inviter le joueur : �a" + pp2.getName()));
									pp2.sendMessage(new TextComponent("�7[�9Party�7] �7Vous avez �t� invit� dans la party de : �a" + pp.getName()));
								}else{
									pp.sendMessage(new TextComponent("�7[�9Party�7] �cCe joueur est d�j� dans une party !"));
								}
								
							}catch(Error e){
								pp.sendMessage(new TextComponent("�7[�9Party�7] �cUne erreur est survenue !"));
								e.printStackTrace();
							}
						}else{
							pp.sendMessage(new TextComponent("�7[�9Party�7] �cVous n'�tes pas le cr�ateur de la party !"));
						}
					}else{
						pp.sendMessage(new TextComponent("�7[�9Party�7] �cVous n'�tes dans aucune party !"));
					}
				}
				
				if(args[0].equalsIgnoreCase("kick")){
					if(Party.hasParty(pp)){
						if(Party.getParty(pp).isCreator(pp)){
							try{
								if(!Party.hasParty(ProxyServer.getInstance().getPlayer(args[1]))){
									pp.sendMessage(new TextComponent("�7[�9Party�7] �cLe joueur n'est pas dans votre party !"));
									return;
								}else{
									if(ProxyServer.getInstance().getPlayer(args[1]) == null){
										pp.sendMessage(new TextComponent("�7[�9Party�7] �cLe joueur n'est pas en ligne !"));
										return;
									}
									
									Party.getParty(pp).removePlayer(ProxyServer.getInstance().getPlayer(args[1]));
									this.playerParty.clear();
									ProxyServer.getInstance().getPlayer(args[1]).sendMessage(new TextComponent("�7[�9Party�7] �7Vous avez �t� exclu de la party de �c" + Party.getParty(pp).getName()));
									
									for(ProxiedPlayer pp2 : Party.getParty(pp).getPlayers()){
										pp2.sendMessage(new TextComponent("�7[�9Party�7] �7Le joueur : �c" + EndariumBungeeCord.getInstance().getProxy().getPlayer(args[0]).getName() + " �7a �t� exclu de la party."));
									}
									
								}
							}catch(Error e){
								pp.sendMessage(new TextComponent("�7[�9Party�7] �cUne erreur est survenue !"));
								e.printStackTrace();
							}
						}else{
							pp.sendMessage(new TextComponent("�7[�9Party�7] �cVous n'�tes pas le cr�ateur de la party !"));
						}
					}else{
						pp.sendMessage(new TextComponent("�7[�9Party�7] �cVous n'�tes dans aucune party !"));
					}
				}
				
				if((args.length >= 2) && (args[0].equalsIgnoreCase("chat"))){
					if(Party.hasParty(pp)){
						String message = "";
						for(int i = 1; i < args.length; i++){
							message = message + " " + args[i];
						}
						message.trim();
						String messagefinal = message.replace('�', '&');
						for(ProxiedPlayer pp2 : Party.getParty(pp).getPlayers()){
							pp2.sendMessage(new TextComponent("�7[�9Party�7] �e" + pp.getName() + "�7> �f" + messagefinal));
						}
					}else{
						pp.sendMessage(new TextComponent("�7[�9Party�7] �cVous n'�tes dans aucune party !"));
					}
				}
				
			}
			
		}
		
	}

}
