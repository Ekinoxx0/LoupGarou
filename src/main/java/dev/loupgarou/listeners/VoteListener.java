package dev.loupgarou.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;

import dev.loupgarou.classes.LGPlayer;

public class VoteListener implements Listener{
	@EventHandler
	public void onClick(PlayerAnimationEvent e) {
		LGPlayer lgp = LGPlayer.thePlayer(e.getPlayer());
		if(e.getAnimationType() == PlayerAnimationType.ARM_SWING)
			lgp.chooseAction();
		
		if(lgp.getGame() != null)
			e.setCancelled(lgp.getGame().getConfig().isHideVoteExtra() || lgp.getGame().getConfig().isHideVote());
	}
}
