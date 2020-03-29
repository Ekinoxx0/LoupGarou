package dev.loupgarou.roles;

import org.bukkit.event.EventHandler;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.classes.chat.LGChat;
import dev.loupgarou.events.LGNightStartEvent;
import dev.loupgarou.events.LGPreDayStartEvent;
import dev.loupgarou.events.LGRoleTurnEndEvent;
import dev.loupgarou.roles.utils.Role;
import dev.loupgarou.roles.utils.RoleType;
import dev.loupgarou.roles.utils.RoleWinType;

public class RMedium extends Role{
	public RMedium(LGGame game) {
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
		return getColor() + "§lMédium";
	}
	@Override
	public String getFriendlyName() {
		return "du "+getName();
	}
	@Override
	public String getShortDescription() {
		return "Tu gagnes avec le §a§lVillage";
	}
	@Override
	public String getDescription() {
		return "Tu gagnes avec le §a§lVillage§f. Chaque nuit, tu peux communiquer avec les morts pour tenter de récupérer des informations cruciales.";
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
	
	@EventHandler
	public void onNightStart(LGNightStartEvent e) {
		if(e.getGame() == getGame())
			for(LGPlayer lgp : getPlayers()) {
				lgp.sendMessage("§8§oTu entres en contact avec le monde des morts...");
				joinChat(lgp);
			}
	}
	
	
	private void joinChat(LGPlayer lgp) {
		lgp.joinChat(getGame().getSpectatorChat(), new LGChat.LGChatCallback() {

			@Override
			public String receive(LGPlayer sender, String message) {
				return "§7"+sender.getName()+"§6 » §f"+message;
			}
			
			@Override
			public String send(LGPlayer sender, String message) {
				return getName()+"§6 » §f"+message;
			}
			
		});
	}
	@EventHandler
	public void onRoleTurn(LGRoleTurnEndEvent e) {
		if(e.getGame() == getGame())
			if(e.getPreviousRole() instanceof RLoupGarou)
				for(LGPlayer lgp : getPlayers())
					if(lgp.getChat() != getGame().getSpectatorChat()) {
						lgp.sendMessage("§6§oTu peux de nouveau parler aux morts...");
						joinChat(lgp);
					}
	}
	
	@EventHandler
	public void onDay(LGPreDayStartEvent e) {
		if(e.getGame() == getGame())
			for(LGPlayer lgp : getPlayers()) {
				lgp.sendMessage("§8§oTu perds le contact avec les morts...");
				lgp.leaveChat();
			}
	}
}
