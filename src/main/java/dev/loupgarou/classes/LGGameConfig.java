package dev.loupgarou.classes;

import java.util.HashMap;
import java.util.Map;

import dev.loupgarou.MainLg;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class LGGameConfig {
	
	@Getter private final Map<String, Integer> roles = new HashMap<String, Integer>();
	@Getter @Setter private boolean hideRole = false;
	@Getter @Setter private boolean hideVote = false;
	@Getter @Setter private boolean hideVoteExtra = false;
	@Getter @Setter private int timerDayPerPlayer = 15;
	
	{
		for(String roleName : MainLg.getInstance().getRoles().keySet())
			this.roles.put(roleName, 0);
	}
}
