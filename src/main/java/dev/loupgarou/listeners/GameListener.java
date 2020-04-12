package dev.loupgarou.listeners;

import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import dev.loupgarou.classes.LGCustomItems.LGCustomItemsConstraints;
import dev.loupgarou.classes.LGCustomSkin;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.events.other.LGCustomItemChangeEvent;
import dev.loupgarou.events.other.LGSkinLoadEvent;
import dev.loupgarou.menu.MainMenu;
import dev.loupgarou.utils.VariousUtils;

public class GameListener implements Listener {
	
	@EventHandler
	public void onInteractBlock(PlayerInteractEvent e) {
		if(e.getAction() != Action.LEFT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(e.getClickedBlock() == null) return;
		if(!(e.getClickedBlock().getState() instanceof Sign)) return;
		Sign sign = (Sign) e.getClickedBlock().getState();
		
		for(String line : sign.getLines())
			if(line.contains("Menu"))
				MainMenu.openMenu(LGPlayer.thePlayer(e.getPlayer()));
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getItem() == null) return;
		if(e.getItem().equals(VariousUtils.getLOBBY_ITEM())) {
			MainMenu.openMenu(LGPlayer.thePlayer(e.getPlayer()));
		} else if(e.getItem().equals(LGGame.getWAITING_ITEM())) {
			LGPlayer.thePlayer(e.getPlayer()).getGame().getPartieMenu().openPartieMenu(LGPlayer.thePlayer(e.getPlayer()));
		}
	}
	
	@EventHandler
	public void onCustomItemChange(LGCustomItemChangeEvent e) {
		if(e.getGame() == null) return;
		if(e.getGame().getMayor() == e.getPlayer() && !e.getPlayer().isDead())
			e.getConstraints().add(LGCustomItemsConstraints.MAYOR);
		
		if(e.getPlayer().isDead())
			e.getConstraints().add(LGCustomItemsConstraints.DEAD);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onSkinChange(LGSkinLoadEvent e) {
		if(e.getPlayer().getSkin() == null && e.getProfile().getProperties().containsKey("textures") && e.getProfile().getProperties().get("textures").size() >= 1)
			e.getPlayer().setSkin(e.getProfile().getProperties().get("textures").iterator().next());

		e.getProfile().getProperties().removeAll("textures");
		
		if(e.getGame() == null || !e.getGame().isStarted()) {
			if(e.getPlayer().getSkin() != null)
				e.getProfile().getProperties().put("textures", e.getPlayer().getSkin());
			return;
		}
		
		if(e.getGame().getMayor() == e.getPlayer())
			e.getProfile().getProperties().put("textures", LGCustomSkin.MAYOR.getProperty());
		else
			e.getProfile().getProperties().put("textures", LGCustomSkin.VILLAGER.getProperty());
	}
	
}
