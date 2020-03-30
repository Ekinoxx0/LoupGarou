package dev.loupgarou.roles;

import java.util.Arrays;
import java.util.List;

import org.bukkit.event.EventHandler;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.events.roles.LGRoleTurnEndEvent;
import dev.loupgarou.roles.utils.Role;
import dev.loupgarou.roles.utils.RoleType;
import dev.loupgarou.roles.utils.RoleWinType;

public class RPetiteFille extends Role{
	public RPetiteFille(LGGame game) {
		super(game);
	}
	@Override
	public RoleType getType() {
		return RoleType.VILLAGER;
	}
	@Override
	public RoleWinType getWinType() {
		return RoleWinType.VILLAGE;
	}
	@Override
	public String getColor() {
		return "§a";
	}
	@Override
	public String getName() {
		return getColor() + "§lPetite Fille";
	}
	@Override
	public String getFriendlyName() {
		return "de la "+getName();
	}
	@Override
	public String getShortDescription() {
		return "Tu gagnes avec le §a§lVillage";
	}
	@Override
	public String getDescription() {
		return "Tu gagnes avec le §a§lVillage§f. Chaque nuit, tu peux espionner les §c§lLoups§f.";
	}
	@Override
	public String getTask() {
		return "";
	}
	@Override
	public String getBroadcastedTask() {
		return "";
	}
	@Override
	public int getTimeout() {
		return -1;
	}
	
	List<String> customNames = Arrays.asList("Loup Glouton", "Loup Méchant", "Loup Burlesque", "Loup Peureux", "Loup Malingre", "Loup Gentil", "Loup Tueur", "Loup Énervé", "Loup Docteur");
	
	@EventHandler
	public void onChangeRole(LGRoleTurnEndEvent e) {
		if(e.getGame() == getGame()) {
			if(e.getNewRole() instanceof RLoupGarou)
				for(Role role : getGame().getRoles())
					if(role instanceof RLoupGarou) {
						RLoupGarou lgRole = (RLoupGarou)role;
						for(LGPlayer player : getPlayers())
							if(!player.getCache().getBoolean("infected"))
								player.joinChat(lgRole.getChat(), (sender, message)->{
									return "§c"+customNames.get(lgRole.getPlayers().indexOf(sender))+" §6» §f"+message;
								}, true);
						break;
					}
			if(e.getPreviousRole() instanceof RLoupGarou)
				for(LGPlayer player : getPlayers())
					if(!player.getCache().getBoolean("infected"))
						player.leaveChat();
		}
	}
}
