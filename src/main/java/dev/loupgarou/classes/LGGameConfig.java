package dev.loupgarou.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGMaps.LGMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class LGGameConfig {
	
	@Getter private final Map<String, Integer> roles = new HashMap<String, Integer>();
	@Getter @Setter private boolean hideRole = false;
	@Getter @Setter private boolean hideVote = false;
	@Getter @Setter private boolean hideVoteExtra = false;
	@Getter @Setter private int timerDayPerPlayer = 15;
	@Getter @Setter @NonNull private CommunicationType com = CommunicationType.TEXT;
	
	@Getter @NonNull private final LGMap map;
	@Getter private final boolean privateGame;
	@Getter private final List<String> banned = new ArrayList<String>();
	
	{
		for(String roleName : MainLg.getInstance().getRoles().keySet())
			this.roles.put(roleName, 0);
	}
	
	public enum CommunicationType {
		TEXT,
		DISCORD;
	}
}
