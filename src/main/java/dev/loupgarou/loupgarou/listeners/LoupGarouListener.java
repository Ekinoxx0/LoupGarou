package dev.loupgarou.loupgarou.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.comphenix.protocol.wrappers.EnumWrappers.ScoreboardAction;

import dev.loupgarou.com.comphenix.packetwrapper.WrapperPlayServerScoreboardScore;
import dev.loupgarou.com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import dev.loupgarou.loupgarou.events.LGGameJoinEvent;

public class LoupGarouListener implements Listener {
	@EventHandler
	public void onGameJoin(LGGameJoinEvent e) {
		//Tous les loups-garous
		WrapperPlayServerScoreboardTeam teamDelete = new WrapperPlayServerScoreboardTeam();
		teamDelete.setMode(1);
		teamDelete.setName("loup_garou_list");
		
		teamDelete.sendPacket(e.getPlayer().getPlayer());
		
		//Loup-Garou noir
		WrapperPlayServerScoreboardScore score = new WrapperPlayServerScoreboardScore();
		score.setObjectiveName("lg_scoreboard");
		score.setValue(0);
		score.setScoreName("été");
		score.setScoreboardAction(ScoreboardAction.REMOVE);
		score.sendPacket(e.getPlayer().getPlayer());
	}
}
