package dev.loupgarou.scoreboard;

import java.util.Arrays;

import com.comphenix.protocol.wrappers.EnumWrappers.ScoreboardAction;

import dev.loupgarou.packetwrapper.WrapperPlayServerScoreboardScore;
import dev.loupgarou.packetwrapper.WrapperPlayServerScoreboardTeam;
import dev.loupgarou.packetwrapper.WrapperPlayServerScoreboardTeam.Mode;
import dev.loupgarou.utils.VariousUtils;

import com.comphenix.protocol.wrappers.WrappedChatComponent;

import lombok.Getter;
import lombok.Setter;

public class CustomScoreboardEntry {
	private static WrappedChatComponent nullComponent = WrappedChatComponent.fromText("");
	
	//setter car flemme de modifier le systeme pour le rendre plus logique
	@Getter @Setter private int score;
	private final String name;
	private final CustomScoreboard scoreboard;
	private WrappedChatComponent prefix, suffix;

	public CustomScoreboardEntry(int score, CustomScoreboard scoreboard) {
		this.score = score;
		this.scoreboard = scoreboard;
		this.name = "§"+VariousUtils.toHex(score);
	}
	
	public void show() {
		if(prefix == null) return;
		
		WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam();
		team.setPlayers(Arrays.asList(name));
		team.setName(name);
		team.setMode(Mode.TEAM_CREATED);
		team.setPrefix(prefix);
		if(suffix != null)
			team.setSuffix(suffix);
		team.sendPacket(scoreboard.getPlayer().getPlayer());
		
		WrapperPlayServerScoreboardScore score = new WrapperPlayServerScoreboardScore();
		score.setObjectiveName(scoreboard.getName());
		score.setScoreboardAction(ScoreboardAction.CHANGE);
		score.setScoreName(name);
		score.setValue(this.score);
		score.sendPacket(scoreboard.getPlayer().getPlayer());
	}
	
	public void setDisplayName(String displayName) {
		boolean spawn = prefix == null;
		if(displayName.length() > 16) {
			char colorCode = 'f';
			int limit = displayName.charAt(14) == '§' && displayName.charAt(13) != '§' ? 14 : displayName.charAt(15) == '§' ? 15 : 16;
			String prefixStr = displayName.substring(0, limit);
			
			prefix = WrappedChatComponent.fromText(prefixStr);
			
			if(limit == 16) {
				boolean storeColorCode = false;
				for(char c : prefixStr.toCharArray())
					if(storeColorCode) {
						storeColorCode = false;
						colorCode = c;
					}else
						if(c == '§')
							storeColorCode = true;
				suffix = WrappedChatComponent.fromText("§"+colorCode+displayName.substring(limit));
			}else
				suffix = WrappedChatComponent.fromText(displayName.substring(limit));
		} else {
			prefix = WrappedChatComponent.fromText(displayName);
			suffix = nullComponent;
		}
		
		if(scoreboard.isShown()) {
			if(spawn)
				show();
			else {
				WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam();
				team.setPlayers(Arrays.asList(name));
				team.setName(name);
				team.setMode(Mode.TEAM_UPDATED);
				team.setPrefix(prefix);
				if(suffix != null)
					team.setSuffix(suffix);
				team.sendPacket(scoreboard.getPlayer().getPlayer());
			}
		}
	}
	public void delete() {
		hide();
		prefix = null;
	}
	public void hide() {
		if(prefix != null && scoreboard.isShown()) {
			WrapperPlayServerScoreboardScore score = new WrapperPlayServerScoreboardScore();
			score.setObjectiveName(scoreboard.getName());
			score.setScoreboardAction(ScoreboardAction.REMOVE);
			score.setScoreName(name);
			score.sendPacket(scoreboard.getPlayer().getPlayer());
			
			WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam();
			team.setName(name);
			team.setMode(Mode.TEAM_REMOVED);
			team.sendPacket(scoreboard.getPlayer().getPlayer());
		}
	}

}
