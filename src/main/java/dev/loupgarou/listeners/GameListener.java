package dev.loupgarou.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import dev.loupgarou.classes.LGCustomItems.LGCustomItemsConstraints;
import dev.loupgarou.classes.LGCustomSkin;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.events.other.LGCustomItemChangeEvent;
import dev.loupgarou.events.other.LGSkinLoadEvent;
import dev.loupgarou.menu.MainMenu;
import dev.loupgarou.utils.VariousUtils;

public class GameListener implements Listener {
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(!e.getItem().equals(VariousUtils.getLOBBY_ITEM())) return;
		MainMenu.openMenu(LGPlayer.thePlayer(e.getPlayer()));
	}
	
	@EventHandler
	public void onCustomItemChange(LGCustomItemChangeEvent e) {
		if(e.getGame().getMayor() == e.getPlayer())
			e.getConstraints().add(LGCustomItemsConstraints.MAYOR);
		
		if(e.getPlayer().isDead())
			e.getConstraints().add(LGCustomItemsConstraints.DEAD);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onSkinChange(LGSkinLoadEvent e) {
		e.getProfile().getProperties().removeAll("textures");
		if(e.getGame().getMayor() == e.getPlayer())
			e.getProfile().getProperties().put("textures", LGCustomSkin.MAYOR.getProperty());
		else
			e.getProfile().getProperties().put("textures", LGCustomSkin.VILLAGER.getProperty());
	}
	
}
